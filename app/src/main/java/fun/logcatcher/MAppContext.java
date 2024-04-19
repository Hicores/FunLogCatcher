package fun.logcatcher;

import android.app.Application;

public class MAppContext extends Application {
    public static MAppContext instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
