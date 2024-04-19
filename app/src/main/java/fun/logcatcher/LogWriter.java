package fun.logcatcher;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class LogWriter {
    private static String cachedFilePath;
    private static BufferedOutputStream out;
    public static void writeLine(String file, String line){
        try {
            synchronized (LogWriter.class){
                if (cachedFilePath == null || !cachedFilePath.equals(file)){
                    if (out != null){
                        try {
                            out.close();
                        }catch (Exception ignored){ }
                    }
                    out = new BufferedOutputStream(new FileOutputStream(file, true), 128 * 1024);
                    cachedFilePath = file;
                }
                out.write(line.getBytes());
                out.write("\n".getBytes());
            }
        }catch (Exception e){
            out = null;
        }
    }
    static {
        new Thread(LogWriter::flushIO).start();
    }
    public static void flushIO(){
        while (true){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (LogWriter.class){
                if (out != null){
                    try {
                        out.flush();
                    }catch (Exception ignored){ }
                }
            }

        }
    }
}
