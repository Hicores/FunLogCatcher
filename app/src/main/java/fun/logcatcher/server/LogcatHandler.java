package fun.logcatcher.server;


public class LogcatHandler {
    private static final String TAG = "LogCatcherLog";

    public static native void runLogcatService(long time);
    public static void onLogcatLine(LogcatLine line){
        try {
            if (ShellServer.msg != null){
                ShellServer.msg.notifyMessage(line);
            }
        }catch (Exception e){
            ShellServer.msg = null;
        }
    }

}
