package fun.logcatcher.utils;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.system.Os;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fun.logcatcher.MAppContext;
import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuBinderWrapper;
import rikka.shizuku.SystemServiceHelper;

public class AppUtils {
    public static boolean isShizukuAvailable(){
        try {
            return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
        }catch (Exception e){
            return false;
        }

    }
    public static String getCacheDir(){
        return MAppContext.instance.getExternalMediaDirs()[0].getAbsolutePath();
    }
    public static void showToast(Object m){
        new Handler(Looper.getMainLooper()).post(()-> Toast.makeText(MAppContext.instance, String.valueOf(m), Toast.LENGTH_LONG).show());
    }
    public static List<PackageInfo> getPackageInfo(){
        if (isShizukuAvailable()){
            IPackageManager manager = IPackageManager.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package")));
            return manager.getInstalledPackages(PackageManager.GET_GIDS,getUserId()).getList();
        }else {
            return new ArrayList<>();
        }
    }
    public static ApplicationInfo getApplicationInfo(String packageName){
        if (isShizukuAvailable()){
            try {
                IPackageManager manager = IPackageManager.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package")));
                return manager.getApplicationInfo(packageName,0, getUserId());
            }catch (Exception e){
                return null;
            }
        }else {
            return null;
        }
    }
    public static String getPackageLocalIcon(String packageName) throws IOException {
        String local = MAppContext.instance.getCacheDir() + "/" + packageName + ".png";
        if (isShizukuAvailable()){
            IPackageManager manager = IPackageManager.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package")));
            ApplicationInfo info = manager.getApplicationInfo(packageName,0, getUserId());
            Drawable drawable = info.loadIcon(MAppContext.instance.getPackageManager());
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, Files.newOutputStream(Paths.get(local)));
            return local;
        }else {
            return "";
        }
    }

    private static int getUserId(){
        return Os.getuid() / 100000;
    }
}
