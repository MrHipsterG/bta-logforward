package milkfrog.logforward;

import java.io.File;
import java.io.RandomAccessFile;

public class LogForwardingThread implements Runnable {
    private final String filePath;
    private long pointerPosition;

    public LogForwardingThread(String filePath) {
        this.filePath = filePath;
        // set pointerPosition to end of the file
        this.pointerPosition = new File(filePath).length();
        // unless this is latest.log
        if (filePath.contains("latest.log")) {
            this.pointerPosition = 0;
        }
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        File logFile = new File(filePath);
        if (!logFile.exists()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.run();
        }

        try (RandomAccessFile file = new RandomAccessFile(logFile, "r")) {
            file.seek(pointerPosition);
            while (true) {
                long fileLength = file.length();
                if (fileLength > pointerPosition) {
                    file.seek(pointerPosition);
                    String line;
                    while ((line = file.readLine()) != null) {
                        String[] parts = filePath.split("/");
                        String filename = parts[parts.length - 1];
                        // write out the line to the console
                        System.out.println(LogTruncator.truncateCoordinates(line));
                        // write out the line to the full log file
                        RandomAccessFile rafFull = LogForward.getFullLogFile();
                        // write the filename and the line to the full log file
                        rafFull.writeBytes(filename + " || " + line + "\n");
                    }
                    pointerPosition = file.getFilePointer();
                }
                // Sleep for a while before the next read; this reduces CPU usage
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

