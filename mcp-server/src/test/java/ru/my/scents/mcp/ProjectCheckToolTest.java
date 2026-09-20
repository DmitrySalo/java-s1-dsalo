package ru.my.scents.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import com.sun.jna.Platform;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProjectCheckToolTest {
    @TempDir Path root;

    @Test
    void rejectsUnknownCheckWithoutStartingProcess() {
        ProjectCheckTool tool = new ProjectCheckTool(root, (command, directory) -> {
            throw new AssertionError("Process must not be started for an unknown check");
        });

        ToolResult result = tool.execute("backend_tests");

        assertThat(result.error()).isTrue();
        assertThat(result.structuredContent()).containsEntry("status", "error");
        assertThat(result.structuredContent().toString()).contains("INVALID_ARGUMENT");
    }

    @Test
    void returnsStructuredErrorForNonZeroExitCode() {
        ProjectCheckTool tool = new ProjectCheckTool(root, (command, directory) -> {
            assertThat(command).containsExactlyElementsOf(Platform.isWindows()
                    ? List.of("cmd.exe", "/d", "/s", "/c", "gradlew.bat", "test", "-Dmcp.skip.stdio.smoke=true")
                    : List.of("./gradlew", "test", "-Dmcp.skip.stdio.smoke=true"));
            assertThat(directory).isEqualTo(root.resolve("mcp-server"));
            return new CompletedProcess(1, "test failure");
        });

        ToolResult result = tool.execute("mcp_tests");

        assertThat(result.error()).isTrue();
        assertThat(result.structuredContent().toString()).contains("CHECK_FAILED");
    }

    @Test
    void acceptsValidCheckThroughMcpHandlerWithoutStartingGradle() {
        ProjectCheckTool tool = new ProjectCheckTool(root,
                (command, directory) -> new CompletedProcess(0, "BUILD SUCCESSFUL"));

        ToolResult result = McpServerApplication.executeCheck(tool, Map.of("check", "mcp_tests"));

        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent()).containsEntry("status", "success").containsEntry("tool", ProjectCheckTool.NAME);
    }

    @Test
    void returnsBoundedSuccessfulResult() {
        ProjectCheckTool tool = new ProjectCheckTool(root,
                (command, directory) -> new CompletedProcess(0, "BUILD SUCCESSFUL"));

        ToolResult result = tool.execute("mcp_tests");

        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent().toString()).contains("mcp_tests", "exit_code=0", "output_bytes=16")
                .doesNotContain("BUILD SUCCESSFUL");
    }

    @Test
    void terminatesProcessWhenOutputExceedsLimitWithoutReturningIt() {
        String marker = "sensitive-child-output";
        OutputLimitProcess process = new OutputLimitProcess(marker.repeat(1_000));
        ProjectCheckTool tool = new ProjectCheckTool(root,
                (command, directory) -> process, Duration.ofSeconds(1));

        ToolResult result = tool.execute("mcp_tests");

        assertThat(result.error()).isTrue();
        assertThat(process.destroyed).isTrue();
        assertThat(result.structuredContent().toString()).contains("OUTPUT_LIMIT_EXCEEDED")
                .doesNotContain(marker);
    }

    @Test
    void terminatesDescendantsWhenParentAlreadyExitedAfterOutputLimit() {
        String marker = "descendant-output";
        CompletedAfterLimitProcess process = new CompletedAfterLimitProcess(marker.repeat(1_000));
        ProjectCheckTool tool = new ProjectCheckTool(root,
                (command, directory) -> process, Duration.ofSeconds(1));

        ToolResult result = tool.execute("mcp_tests");

        assertThat(result.error()).isTrue();
        assertThat(process.destroyed).isTrue();
        assertThat(result.structuredContent().toString()).contains("OUTPUT_LIMIT_EXCEEDED")
                .doesNotContain(marker);
    }

    @Test
    void collectsOutputWithinTheLimitWithoutReturningIt() {
        String marker = "bounded-child-output";
        DrainingProcess process = new DrainingProcess(marker.repeat(100));
        ProjectCheckTool tool = new ProjectCheckTool(root, (command, directory) -> process);

        ToolResult result = tool.execute("mcp_tests");

        assertThat(result.error()).isFalse();
        assertThat(process.outputFullyRead).isTrue();
        assertThat(result.structuredContent().toString()).contains("output_bytes=2000", "output_truncated=false")
                .doesNotContain(marker);
    }

    @Test
    void returnsTimeoutAfterTerminatingTheProcess() {
        HangingProcess process = new HangingProcess();
        ProjectCheckTool tool = new ProjectCheckTool(root, (command, directory) -> process, Duration.ofMillis(1));

        ToolResult result = tool.execute("mcp_tests");

        assertThat(result.error()).isTrue();
        assertThat(result.structuredContent().toString()).contains("TIMEOUT");
        assertThat(process.destroyed).isTrue();
    }

    @Test
    void returnsWhenOutputReaderDoesNotReachEof() {
        BlockingOutputProcess process = new BlockingOutputProcess();
        ProjectCheckTool tool = new ProjectCheckTool(root, (command, directory) -> process);

        ToolResult result = tool.execute("mcp_tests");

        assertThat(result.error()).isTrue();
        assertThat(result.structuredContent().toString()).contains("PROCESS_UNAVAILABLE");
        assertThat(process.streamClosed).isTrue();
    }

    @Test
    void returnsUnavailableWhenOutputReaderFails() {
        ProjectCheckTool tool = new ProjectCheckTool(root,
                (command, directory) -> new ReaderFailureProcess());

        ToolResult result = tool.execute("mcp_tests");

        assertThat(result.error()).isTrue();
        assertThat(result.structuredContent().toString()).contains("PROCESS_UNAVAILABLE");
    }

    @Test
    void terminatesProcessWhenInterrupted() throws Exception {
        InterruptibleProcess process = new InterruptibleProcess();
        ProjectCheckTool tool = new ProjectCheckTool(root, (command, directory) -> process);
        AtomicReference<ToolResult> result = new AtomicReference<>();
        Thread execution = new Thread(() -> result.set(tool.execute("mcp_tests")));

        execution.start();
        assertThat(process.waiting.await(1, TimeUnit.SECONDS)).isTrue();
        execution.interrupt();
        execution.join(1_000);

        assertThat(execution.isAlive()).isFalse();
        assertThat(process.destroyed).isTrue();
        assertThat(result.get().structuredContent().toString()).contains("INTERRUPTED");
    }

    private static class CompletedProcess extends Process {
        private final int exitCode;
        private final InputStream output;

        private CompletedProcess(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = new ByteArrayInputStream(output.getBytes());
        }

        @Override public OutputStream getOutputStream() { return OutputStream.nullOutputStream(); }
        @Override public InputStream getInputStream() { return output; }
        @Override public InputStream getErrorStream() { return InputStream.nullInputStream(); }
        @Override public int waitFor() { return exitCode; }
        @Override public boolean waitFor(long timeout, java.util.concurrent.TimeUnit unit) throws InterruptedException { return true; }
        @Override public int exitValue() { return exitCode; }
        @Override public void destroy() { }
        @Override public Process destroyForcibly() { return this; }
        @Override public boolean isAlive() { return false; }
    }

    private static final class HangingProcess extends CompletedProcess {
        private boolean destroyed;

        private HangingProcess() {
            super(0, "");
        }

        @Override public boolean waitFor(long timeout, java.util.concurrent.TimeUnit unit) { return destroyed; }
        @Override public void destroy() { destroyed = true; }
        @Override public boolean isAlive() { return !destroyed; }
    }

    private static final class DrainingProcess extends CompletedProcess {
        private boolean outputFullyRead;

        private DrainingProcess(String output) {
            super(0, output);
        }

        @Override public InputStream getInputStream() {
            InputStream delegate = super.getInputStream();
            return new java.io.FilterInputStream(delegate) {
                @Override public int read(byte[] bytes, int offset, int length) throws java.io.IOException {
                    int read = super.read(bytes, offset, length);
                    if (read == -1) {
                        outputFullyRead = true;
                    }
                    return read;
                }
            };
        }
    }

    private static final class BlockingOutputProcess extends CompletedProcess {
        private volatile boolean streamClosed;

        private BlockingOutputProcess() {
            super(0, "");
        }

        @Override public InputStream getInputStream() {
            return new InputStream() {
                @Override public int read() {
                    while (!streamClosed) {
                        Thread.onSpinWait();
                    }
                    return -1;
                }

                @Override public void close() {
                    streamClosed = true;
                }
            };
        }
    }

    private static final class ReaderFailureProcess extends CompletedProcess {
        private ReaderFailureProcess() {
            super(0, "");
        }

        @Override public InputStream getInputStream() {
            return new InputStream() {
                @Override public int read() throws java.io.IOException {
                    throw new java.io.IOException("simulated reader failure");
                }
            };
        }
    }

    private static final class OutputLimitProcess extends CompletedProcess {
        private boolean destroyed;

        private OutputLimitProcess(String output) {
            super(0, output);
        }

        @Override public boolean waitFor(long timeout, java.util.concurrent.TimeUnit unit) { return destroyed; }
        @Override public void destroy() { destroyed = true; }
        @Override public boolean isAlive() { return !destroyed; }
    }

    private static final class CompletedAfterLimitProcess extends CompletedProcess {
        private boolean destroyed;

        private CompletedAfterLimitProcess(String output) {
            super(0, output);
        }

        @Override public void destroy() { destroyed = true; }
    }

    private static final class InterruptibleProcess extends CompletedProcess {
        private final CountDownLatch waiting = new CountDownLatch(1);
        private boolean destroyed;

        private InterruptibleProcess() {
            super(0, "");
        }

        @Override public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
            waiting.countDown();
            if (destroyed) {
                return true;
            }
            Thread.sleep(unit.toMillis(timeout));
            return destroyed;
        }

        @Override public void destroy() { destroyed = true; }
        @Override public Process destroyForcibly() { destroyed = true; return this; }
        @Override public boolean isAlive() { return !destroyed; }
    }
}
