package ru.my.scents.mcp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.sun.jna.Platform;

final class ProjectCheckTool {
    static final String NAME = "run_project_check";
    private static final int MAX_OUTPUT_BYTES = 8 * 1024;
    private static final Duration TIMEOUT = Duration.ofMinutes(5);
    private static final Duration READER_GRACE_PERIOD = Duration.ofSeconds(5);
    private static final long PROCESS_POLL_MILLIS = 100;
    private final Path repositoryRoot;
    private final ProcessLauncher launcher;
    private final Duration timeout;

    ProjectCheckTool(Path repositoryRoot) {
        this(repositoryRoot, (command, directory) -> Platform.isWindows()
                ? WindowsProcessJob.start(command, directory)
                : new ProcessBuilder(command).directory(directory.toFile()).redirectErrorStream(true).start(), TIMEOUT);
    }

    ProjectCheckTool(Path repositoryRoot, ProcessLauncher launcher) {
        this(repositoryRoot, launcher, TIMEOUT);
    }

    ProjectCheckTool(Path repositoryRoot, ProcessLauncher launcher, Duration timeout) {
        this.repositoryRoot = repositoryRoot;
        this.launcher = launcher;
        this.timeout = timeout;
    }

    ToolResult execute(String check) {
        ProcessSpec spec = specification(check);
        if (spec == null) {
            return ToolResult.failure(NAME, "INVALID_ARGUMENT", "Requested check is not available.");
        }
        Instant started = Instant.now();
        Process process = null;
        InputStream processOutput = null;
        Thread reader = null;
        try {
            process = launcher.start(spec.command, spec.directory);
            BoundedOutput output = new BoundedOutput();
            processOutput = process.getInputStream();
            InputStream outputStream = processOutput;
            reader = new Thread(() -> output.read(outputStream), "mcp-check-output");
            reader.setDaemon(true);
            reader.start();
            if (!waitForProcess(process, output, started)) {
                stopProcess(process, processOutput);
                reader.join(READER_GRACE_PERIOD.toMillis());
                if (output.limitExceeded()) {
                    return ToolResult.failure(NAME, "OUTPUT_LIMIT_EXCEEDED", "Project check produced too much output.");
                }
                return ToolResult.failure(NAME, "TIMEOUT", "Project check exceeded its allowed execution time.");
            }
            reader.join(READER_GRACE_PERIOD.toMillis());
            if (reader.isAlive()) {
                stopProcess(process, processOutput);
                reader.join(READER_GRACE_PERIOD.toMillis());
                return ToolResult.failure(NAME, "PROCESS_UNAVAILABLE", "Project check output could not be collected safely.");
            }
            if (output.readFailed()) {
                stopProcess(process, processOutput);
                return ToolResult.failure(NAME, "PROCESS_UNAVAILABLE", "Project check output could not be collected safely.");
            }
            if (output.limitExceeded()) {
                stopProcess(process, processOutput);
                return ToolResult.failure(NAME, "OUTPUT_LIMIT_EXCEEDED", "Project check produced too much output.");
            }
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                return ToolResult.failure(NAME, "CHECK_FAILED", "Project check completed with a non-zero exit code.");
            }
            return ToolResult.success(NAME, "Project check completed.", Map.of(
                    "check", check, "exit_code", exitCode,
                    "duration_ms", Duration.between(started, Instant.now()).toMillis(),
                    "output_bytes", output.count, "output_truncated", false));
        } catch (IOException exception) {
            return ToolResult.failure(NAME, "PROCESS_UNAVAILABLE", "Project check could not be started.");
        } catch (IllegalStateException exception) {
            stopAfterFailure(process, processOutput, reader);
            return ToolResult.failure(NAME, "PROCESS_UNAVAILABLE", "Project check could not be started safely.");
        } catch (InterruptedException exception) {
            stopAfterFailure(process, processOutput, reader);
            Thread.currentThread().interrupt();
            return ToolResult.failure(NAME, "INTERRUPTED", "Project check was interrupted.");
        } finally {
            WindowsProcessJob.close(process);
        }
    }

    private boolean waitForProcess(Process process, BoundedOutput output, Instant started) throws InterruptedException {
        while (Duration.between(started, Instant.now()).compareTo(timeout) < 0) {
            if (process.waitFor(PROCESS_POLL_MILLIS, TimeUnit.MILLISECONDS)) {
                return true;
            }
            if (output.limitExceeded()) {
                return false;
            }
        }
        return false;
    }

    private void stopProcess(Process process, InputStream output) throws IOException, InterruptedException {
        terminateTree(process);
        output.close();
    }

    private void stopAfterFailure(Process process, InputStream output, Thread reader) {
        try {
            if (process != null && output != null) {
                stopProcess(process, output);
            }
            if (reader != null) {
                reader.join(READER_GRACE_PERIOD.toMillis());
            }
        } catch (IOException | InterruptedException ignored) {
            if (process != null) {
                terminateTreeWithoutWaiting(process);
            }
        }
    }

    private void terminateTree(Process process) throws InterruptedException {
        List<ProcessHandle> descendants = descendants(process);
        descendants.forEach(ProcessHandle::destroy);
        process.destroy();
        process.waitFor(5, TimeUnit.SECONDS);
        descendants(process).forEach(handle -> {
            if (handle.isAlive()) {
                handle.destroyForcibly();
            }
        });
        descendants.stream().filter(ProcessHandle::isAlive).forEach(ProcessHandle::destroyForcibly);
        if (process.isAlive()) {
            process.destroyForcibly();
        }
        process.waitFor(5, TimeUnit.SECONDS);
    }

    private void terminateTreeWithoutWaiting(Process process) {
        descendants(process).forEach(ProcessHandle::destroyForcibly);
        process.destroyForcibly();
    }

    private List<ProcessHandle> descendants(Process process) {
        try {
            return process.toHandle().descendants().sorted(Comparator.comparingLong(ProcessHandle::pid).reversed()).toList();
        } catch (UnsupportedOperationException ignored) {
            return List.of();
        }
    }

    private ProcessSpec specification(String check) {
        if ("mcp_tests".equals(check)) {
            return new ProcessSpec(repositoryRoot.resolve("mcp-server"),
                    Platform.isWindows()
                            ? List.of("cmd.exe", "/d", "/s", "/c", "gradlew.bat", "test", "-Dmcp.skip.stdio.smoke=true")
                            : List.of("./gradlew", "test", "-Dmcp.skip.stdio.smoke=true"));
        }
        return null;
    }

    private static final class BoundedOutput {
        private volatile boolean limitExceeded;
        private volatile boolean readFailed;
        private int count;

        private void read(InputStream stream) {
            try (stream) {
                byte[] buffer = new byte[1024];
                for (int read; (read = stream.read(buffer)) != -1;) {
                    if (count + read > MAX_OUTPUT_BYTES) {
                        limitExceeded = true;
                        return;
                    }
                    count += read;
                }
            } catch (IOException ignored) {
                if (!limitExceeded) {
                    readFailed = true;
                }
            }
        }

        private boolean limitExceeded() {
            return limitExceeded;
        }

        private boolean readFailed() {
            return readFailed;
        }
    }

    private record ProcessSpec(Path directory, List<String> command) {
    }

    @FunctionalInterface
    interface ProcessLauncher {
        Process start(List<String> command, Path directory) throws IOException;
    }
}
