package ru.my.scents.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

@EnabledOnOs(OS.WINDOWS)
class WindowsProcessJobIntegrationTest {
    @TempDir Path directory;

    @Test
    void closingJobTerminatesTheSuspendedParentAndItsChild() throws Exception {
        Process process = WindowsProcessJob.start(List.of(
                javaExecutable().toString(), "-cp", System.getProperty("java.class.path"), HelperProcess.class.getName()), directory);
        try (BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            ExecutorService pidReader = Executors.newSingleThreadExecutor();
            String pidLine;
            try {
                pidLine = pidReader.submit(output::readLine).get(10, TimeUnit.SECONDS);
            } finally {
                pidReader.shutdownNow();
            }
            assertThat(pidLine).matches("\\d+ \\d+");
            String[] pids = pidLine.split(" ");
            long parentPid = Long.parseLong(pids[0]);
            long childPid = Long.parseLong(pids[1]);

            assertThat(ProcessHandle.of(parentPid)).isPresent();
            assertThat(ProcessHandle.of(parentPid).orElseThrow().isAlive()).isTrue();
            assertThat(ProcessHandle.of(childPid)).isPresent();
            assertThat(ProcessHandle.of(childPid).orElseThrow().isAlive()).isTrue();

            WindowsProcessJob.close(process);

            awaitTermination(parentPid);
            awaitTermination(childPid);
        } finally {
            WindowsProcessJob.close(process);
        }
    }

    private static Path javaExecutable() {
        return Path.of(System.getProperty("java.home"), "bin", "java.exe");
    }

    private static void awaitTermination(long pid) throws InterruptedException {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(10));
        while (ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false) && Instant.now().isBefore(deadline)) {
            Thread.sleep(50);
        }
        assertThat(ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false)).isFalse();
    }

    public static final class HelperProcess {
        public static void main(String[] args) throws IOException, InterruptedException {
            Process child = new ProcessBuilder(javaExecutable().toString(), "-cp", System.getProperty("java.class.path"),
                    HelperChild.class.getName()).start();
            System.out.println(ProcessHandle.current().pid() + " " + child.pid());
            System.out.flush();
            Thread.sleep(Duration.ofMinutes(5));
        }
    }

    public static final class HelperChild {
        public static void main(String[] args) throws InterruptedException {
            Thread.sleep(Duration.ofMinutes(5));
        }
    }
}
