package ru.my.scents.mcp;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** Windows containment boundary: the process is assigned before its primary thread can run. */
final class WindowsProcessJob {
    private static final int JOB_OBJECT_EXTENDED_LIMIT_INFORMATION = 9;
    private static final int JOB_OBJECT_LIMIT_KILL_ON_JOB_CLOSE = 0x00002000;
    private static final int WAIT_TIMEOUT = 258;
    private static final int ERROR_BROKEN_PIPE = 109;
    private static final long PIPE_POLL_MILLIS = 10;

    private WindowsProcessJob() {
    }

    static Process start(List<String> command, Path directory) throws IOException {
        if (!Platform.isWindows()) {
            throw new IllegalStateException("Windows process containment is unavailable.");
        }
        HANDLE job = createJob();
        HANDLEByReference inputRead = new HANDLEByReference();
        HANDLEByReference inputWrite = new HANDLEByReference();
        HANDLEByReference outputRead = new HANDLEByReference();
        HANDLEByReference outputWrite = new HANDLEByReference();
        WinBase.PROCESS_INFORMATION processInfo = new WinBase.PROCESS_INFORMATION();
        try {
            createPipe(inputRead, inputWrite, inputWrite);
            createPipe(outputRead, outputWrite, outputRead);
            WinBase.STARTUPINFO startupInfo = new WinBase.STARTUPINFO();
            startupInfo.dwFlags = WinBase.STARTF_USESTDHANDLES;
            startupInfo.hStdInput = inputRead.getValue();
            startupInfo.hStdOutput = outputWrite.getValue();
            startupInfo.hStdError = outputWrite.getValue();
            char[] commandLine = (commandLine(command) + "\0").toCharArray();
            if (!Kernel32.INSTANCE.CreateProcessW(null, commandLine, null, null, true,
                    new DWORD(WinBase.CREATE_SUSPENDED), null, directory.toAbsolutePath().toString(), startupInfo, processInfo)) {
                throw unavailable();
            }
            close(outputWrite.getValue());
            outputWrite.setValue(null);
            close(inputWrite.getValue());
            inputWrite.setValue(null);
            if (!JobApi.INSTANCE.AssignProcessToJobObject(job, processInfo.hProcess)) {
                Kernel32.INSTANCE.TerminateProcess(processInfo.hProcess, 1);
                throw unavailable();
            }
            if (JobApi.INSTANCE.ResumeThread(processInfo.hThread) == -1) {
                Kernel32.INSTANCE.TerminateProcess(processInfo.hProcess, 1);
                throw unavailable();
            }
            return new ContainedProcess(job, processInfo.hProcess, outputRead.getValue());
        } catch (RuntimeException | IOException exception) {
            close(inputRead.getValue());
            close(inputWrite.getValue());
            close(outputRead.getValue());
            close(outputWrite.getValue());
            close(processInfo.hProcess);
            close(job);
            throw exception;
        } finally {
            close(inputRead.getValue());
            close(processInfo.hThread);
        }
    }

    static void close(Process process) {
        if (process instanceof ContainedProcess containedProcess) {
            containedProcess.close();
        }
    }

    private static HANDLE createJob() {
        HANDLE job = JobApi.INSTANCE.CreateJobObjectW(null, null);
        if (job == null) {
            throw unavailable();
        }
        ExtendedLimitInformation limits = new ExtendedLimitInformation();
        limits.BasicLimitInformation.LimitFlags = JOB_OBJECT_LIMIT_KILL_ON_JOB_CLOSE;
        limits.write();
        if (!JobApi.INSTANCE.SetInformationJobObject(job, JOB_OBJECT_EXTENDED_LIMIT_INFORMATION, limits, limits.size())) {
            close(job);
            throw unavailable();
        }
        return job;
    }

    private static void createPipe(HANDLEByReference read, HANDLEByReference write, HANDLEByReference parentHandle) throws IOException {
        WinBase.SECURITY_ATTRIBUTES attributes = new WinBase.SECURITY_ATTRIBUTES();
        attributes.bInheritHandle = true;
        attributes.write();
        if (!Kernel32.INSTANCE.CreatePipe(read, write, attributes, 0)
                || !Kernel32.INSTANCE.SetHandleInformation(parentHandle.getValue(), WinBase.HANDLE_FLAG_INHERIT, 0)) {
            close(read.getValue());
            close(write.getValue());
            throw new IOException("Windows process output pipe is unavailable.");
        }
    }

    private static String commandLine(List<String> command) {
        return command.stream().map(WindowsProcessJob::quote).reduce((left, right) -> left + " " + right).orElseThrow();
    }

    private static String quote(String argument) {
        if (!argument.isEmpty() && !argument.contains(" ") && !argument.contains("\t") && !argument.contains("\"")) {
            return argument;
        }
        StringBuilder quoted = new StringBuilder("\"");
        int backslashes = 0;
        for (int index = 0; index < argument.length(); index++) {
            char character = argument.charAt(index);
            if (character == '\\') {
                backslashes++;
            } else if (character == '"') {
                quoted.append("\\".repeat(backslashes * 2 + 1)).append(character);
                backslashes = 0;
            } else {
                quoted.append("\\".repeat(backslashes)).append(character);
                backslashes = 0;
            }
        }
        return quoted.append("\\".repeat(backslashes * 2)).append('"').toString();
    }

    private static IllegalStateException unavailable() {
        return new IllegalStateException("Windows process containment is unavailable.");
    }

    private static void close(HANDLE handle) {
        if (handle != null) {
            Kernel32.INSTANCE.CloseHandle(handle);
        }
    }

    private interface JobApi extends StdCallLibrary {
        JobApi INSTANCE = Native.load("kernel32", JobApi.class);

        HANDLE CreateJobObjectW(Pointer attributes, String name);
        boolean SetInformationJobObject(HANDLE job, int informationClass, Structure information, int length);
        boolean AssignProcessToJobObject(HANDLE job, HANDLE process);
        int ResumeThread(HANDLE thread);
    }

    private static final class ContainedProcess extends Process implements AutoCloseable {
        private final HANDLE job;
        private final HANDLE process;
        private final InputStream output;
        private boolean closed;

        private ContainedProcess(HANDLE job, HANDLE process, HANDLE output) {
            this.job = job;
            this.process = process;
            this.output = new HandleInputStream(output);
        }

        @Override public OutputStream getOutputStream() { return OutputStream.nullOutputStream(); }
        @Override public InputStream getInputStream() { return output; }
        @Override public InputStream getErrorStream() { return InputStream.nullInputStream(); }
        @Override public int waitFor() throws InterruptedException {
            Kernel32.INSTANCE.WaitForSingleObject(process, WinBase.INFINITE);
            return exitValue();
        }
        @Override public boolean waitFor(long timeout, TimeUnit unit) {
            return Kernel32.INSTANCE.WaitForSingleObject(process, (int) Math.min(Integer.MAX_VALUE, unit.toMillis(timeout)))
                    == WinBase.WAIT_OBJECT_0;
        }
        @Override public int exitValue() {
            IntByReference exitCode = new IntByReference();
            if (!Kernel32.INSTANCE.GetExitCodeProcess(process, exitCode) || exitCode.getValue() == WinBase.STILL_ACTIVE) {
                throw new IllegalThreadStateException("Process is still running.");
            }
            return exitCode.getValue();
        }
        @Override public void destroy() { close(); }
        @Override public Process destroyForcibly() { close(); return this; }
        @Override public boolean isAlive() {
            return Kernel32.INSTANCE.WaitForSingleObject(process, 0) == WAIT_TIMEOUT;
        }
        @Override public long pid() {
            return Kernel32.INSTANCE.GetProcessId(process);
        }
        @Override public synchronized void close() {
            if (!closed) {
                try {
                    output.close();
                } catch (IOException ignored) {
                    // Closing the job remains mandatory when the output pipe is already unavailable.
                }
                WindowsProcessJob.close(job);
                WindowsProcessJob.close(process);
                closed = true;
            }
        }
    }

    private static final class HandleInputStream extends InputStream {
        private volatile HANDLE handle;

        private HandleInputStream(HANDLE handle) {
            this.handle = handle;
        }

        @Override public int read() throws IOException {
            byte[] oneByte = new byte[1];
            return read(oneByte, 0, 1) == -1 ? -1 : Byte.toUnsignedInt(oneByte[0]);
        }

        @Override public int read(byte[] buffer, int offset, int length) throws IOException {
            if (length == 0) {
                return 0;
            }
            if (handle == null) {
                return -1;
            }
            int available = awaitAvailable();
            if (available == -1) {
                return -1;
            }
            byte[] target = offset == 0 && length == buffer.length ? buffer : new byte[Math.min(length, available)];
            IntByReference read = new IntByReference();
            if (!Kernel32.INSTANCE.ReadFile(handle, target, target.length, read, null)) {
                if (read.getValue() == 0 && Kernel32.INSTANCE.GetLastError() == ERROR_BROKEN_PIPE) {
                    return -1;
                }
                throw new IOException("Windows process output could not be read.");
            }
            if (read.getValue() == 0) {
                return -1;
            }
            if (target != buffer) {
                System.arraycopy(target, 0, buffer, offset, read.getValue());
            }
            return read.getValue();
        }

        private int awaitAvailable() throws IOException {
            IntByReference available = new IntByReference();
            while (handle != null) {
                if (!Kernel32.INSTANCE.PeekNamedPipe(handle, new byte[0], 0, null, available, null)) {
                    if (Kernel32.INSTANCE.GetLastError() == ERROR_BROKEN_PIPE) {
                        return -1;
                    }
                    throw new IOException("Windows process output could not be read.");
                }
                if (available.getValue() > 0) {
                    return available.getValue();
                }
                try {
                    Thread.sleep(PIPE_POLL_MILLIS);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedIOException("Windows process output reader was interrupted.");
                }
            }
            return -1;
        }

        @Override public void close() {
            if (handle != null) {
                WindowsProcessJob.close(handle);
                handle = null;
            }
        }
    }

    @Structure.FieldOrder({"PerProcessUserTimeLimit", "PerJobUserTimeLimit", "LimitFlags", "MinimumWorkingSetSize", "MaximumWorkingSetSize", "ActiveProcessLimit", "Affinity", "PriorityClass", "SchedulingClass"})
    public static final class BasicLimitInformation extends Structure {
        public long PerProcessUserTimeLimit;
        public long PerJobUserTimeLimit;
        public int LimitFlags;
        public long MinimumWorkingSetSize;
        public long MaximumWorkingSetSize;
        public int ActiveProcessLimit;
        public long Affinity;
        public int PriorityClass;
        public int SchedulingClass;
    }

    @Structure.FieldOrder({"BasicLimitInformation", "IoInfo", "ProcessMemoryLimit", "JobMemoryLimit", "PeakProcessMemoryUsed", "PeakJobMemoryUsed"})
    public static final class ExtendedLimitInformation extends Structure {
        public BasicLimitInformation BasicLimitInformation = new BasicLimitInformation();
        public IoCounters IoInfo = new IoCounters();
        public long ProcessMemoryLimit;
        public long JobMemoryLimit;
        public long PeakProcessMemoryUsed;
        public long PeakJobMemoryUsed;
    }

    @Structure.FieldOrder({"ReadOperationCount", "WriteOperationCount", "OtherOperationCount", "ReadTransferCount", "WriteTransferCount", "OtherTransferCount"})
    public static final class IoCounters extends Structure {
        public long ReadOperationCount;
        public long WriteOperationCount;
        public long OtherOperationCount;
        public long ReadTransferCount;
        public long WriteTransferCount;
        public long OtherTransferCount;
    }
}
