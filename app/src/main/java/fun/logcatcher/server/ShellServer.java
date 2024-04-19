package fun.logcatcher.server;

import android.os.RemoteException;
import android.util.Log;

public class ShellServer extends IShellService.Stub {
    private static final String TAG = "LogCatcherLog";
    public static final String Shell_Tmp_Path = "/data/local/tmp/";
    static INotifyMessage msg;
    static {
        System.loadLibrary("logcatcher");
    }
    public ShellServer(){

    }
    @Override
    public void destroy() throws RemoteException {
        System.exit(0);
    }

    @Override
    public void exit() throws RemoteException {
        destroy();
    }

    @Override
    public void addNotifyCallback(INotifyMessage msg) throws RemoteException {
        ShellServer.msg = msg;
        Log.d(TAG,"addNotifyCallback:" + msg);
    }

    @Override
    public void startServer() throws RemoteException {
        new Thread(()->{
            if (msg == null){
                System.exit(0);
                return;
            }
            try {
                LogcatHandler.runLogcatService(System.currentTimeMillis() / 1000);
            }catch (Exception e){
                System.exit(0);
            }
        },"FunLogcatServer").start();
    }
    @Override
    public int getStatus() throws RemoteException {

        return 99;
    }
}
