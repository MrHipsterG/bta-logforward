package milkfrog.logforward;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;


public class LogForward implements ModInitializer {
    public static final String MOD_ID = "logforward";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String FULL_LOG = "logs/full.log";
    private static RandomAccessFile fullLogFile;
    public static final String LOG_FILE = "logs/logs.txt";

    @Override
    public void onInitialize() {
        System.out.println("Initializing Log Forwarding Mod");

        // Start your log-reading logic here in a new thread
        new Thread(this::startReadingLogs).start();
    }

    public static RandomAccessFile getFullLogFile() {
        if (fullLogFile == null) {
            try {
                fullLogFile = new RandomAccessFile(FULL_LOG, "rw");
            } catch (Exception e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        return fullLogFile;
    }
    private void startReadingLogs() {
        // if the directory for LOG_FILE doesn't exist, create it
        File logDirectory = new File(LOG_FILE.substring(0, LOG_FILE.lastIndexOf("/")));
        if (!logDirectory.exists()) {
            try {
                logDirectory.mkdirs();
            } catch (Exception e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        // if the file for LOG_FILE doesn't exist, create it
        File logsFile = new File(LOG_FILE);
        if (!logsFile.exists()) {
            try {
                logsFile.createNewFile();
            } catch (Exception e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        // if the file for LOG_FILE is empty, write the default log files to it
        if (logsFile.length() == 0) {
            try {
                RandomAccessFile raf = new RandomAccessFile(logsFile, "rw");
                raf.writeBytes("logs/latest.log\n");
                raf.writeBytes("logs/debug.log\n");
                raf.writeBytes("fabricloader.log\n");
                raf.writeBytes("player-logs/FullBlockPlaceRecord.txt\n");
                raf.writeBytes("player-logs/FullBucketRecord.txt\n");
                raf.writeBytes("player-logs/FullChestRecord.txt\n");
                raf.writeBytes("player-logs/FullPositionRecord.txt\n");
                raf.close();
            } catch (Exception e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        // Read the config file and start a new thread for each log file
        if (logsFile.length() > 0) {
            try {
                RandomAccessFile raf = new RandomAccessFile(logsFile, "r");
                File fullLogFile = new File(FULL_LOG);
                if (!fullLogFile.exists()) {
                    fullLogFile.createNewFile();
                }
                String line;
                long filePosition = 0;
                long pointerPosition = 0;
                while ((line = raf.readLine()) != null) {
                    System.out.println("Starting a new thread for " + line);
                    LogForwardingThread thread = new LogForwardingThread(line);
                    thread.start();
                    filePosition = filePosition + 1;
                }
                raf.close();
            } catch (Exception e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

    }
}

