package fun.logcatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fun.logcatcher.server.INotifyMessage;
import fun.logcatcher.server.IShellService;
import fun.logcatcher.server.LogcatHandler;
import fun.logcatcher.server.LogcatLine;
import fun.logcatcher.server.ShellServer;
import fun.logcatcher.ui.XListDialog;
import fun.logcatcher.utils.AppUtils;
import fun.logcatcher.utils.DataUtils;
import fun.logcatcher.utils.FileUtils;
import rikka.shizuku.Shizuku;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LogCatcherLog";
    private static String err;
    IShellService service;
    FrameLayout frameLayout;
    int cur_tab = 0;
    volatile boolean isStarted = false;
    String localPath;
    Handler handler = new Handler(Looper.getMainLooper());
    INotifyMessage notifyMessage = new INotifyMessage.Stub() {
        @Override
        public void notifyMessage(LogcatLine message) throws RemoteException {
            try {
                if (isStarted){
                    LogcatLine mLine = MessageFilter.filter(message);
                    if (mLine != null){
                        LogWriter.writeLine(localPath,mLine.buildStr());
                        handler.post(()-> handleNotifyMessage(mLine));
                    }
                }
            }catch (Exception ignored){ }
        }
    };
    private void handleNotifyMessage(LogcatLine line){
        if (cur_tab == 1){

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HiddenApiBypass.addHiddenApiExemptions("L");


        setContentView(R.layout.activity_main);
        frameLayout = findViewById(R.id.main_content_container);
        shizukuConnect();


        Button btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(v->{
            if (service == null){
                AppUtils.showToast("FunLogCatcher Daemon服务异常,请检测Shizuku授权状态");
                return;
            }
            if (isStarted){
                isStarted = false;
                btn_start.setText("开始记录");
            }else {
                File f = new File(AppUtils.getCacheDir() + "/log");
                if (!f.exists())f.mkdir();
                if (TextUtils.isEmpty(MessageFilter.packageName)){
                    AppUtils.showToast("请先选择需要抓取的App");
                    return;
                }

                MessageFilter.lockFilter();
                localPath = AppUtils.getCacheDir() + "/log/logcat_" + System.currentTimeMillis() + ".log";
                isStarted = true;


                btn_start.setText("停止记录");
            }

        });
        switchToFilter();


        RadioButton tab_filter = findViewById(R.id.tab_filter);
        RadioButton tab_preview = findViewById(R.id.tab_log_preview);

        tab_filter.setOnClickListener( v->{
            if (tab_filter.isPressed() && tab_filter.isChecked()){
                switchToFilter();
            }
        });
        tab_preview.setOnClickListener(v->{
            if (tab_preview.isPressed() && tab_preview.isChecked()){
                switchToPreview();
            }
        });
    }
    private void switchToFilter(){
        cur_tab = 0;
        frameLayout.removeAllViews();
        ViewGroup frameRoot = (ViewGroup) getLayoutInflater().inflate(R.layout.filter_root,frameLayout,true);

        TextView package_title = frameRoot.findViewById(R.id.package_title);
        package_title.setText(MessageFilter.packageName);

        Button btn_select_apk = frameRoot.findViewById(R.id.btn_select_app);
        btn_select_apk.setOnClickListener(v->{
            if (!AppUtils.isShizukuAvailable()){
                AppUtils.showToast("Shizuku服务不可用");
                return;
            }
            List<PackageInfo> info = AppUtils.getPackageInfo();
            List<String> title = new ArrayList<>();
            List<XListDialog.XListItemData> dataList = new ArrayList<>();
            for (PackageInfo packageInfo : info){
                XListDialog.XListItemData data = new XListDialog.XListItemData();
                ApplicationInfo applicationInfo = AppUtils.getApplicationInfo(packageInfo.packageName);
                if (applicationInfo != null){
                    if (applicationInfo.uid == 1000)continue;
                    String label = applicationInfo.loadLabel(getPackageManager())+"";
                    if (label.equals(packageInfo.packageName)){
                        data.title = packageInfo.packageName + "[uid:" + applicationInfo.uid + "]";
                    }else {
                        data.title = applicationInfo.loadLabel(getPackageManager()) + "[uid:" + applicationInfo.uid + "]";
                    }

                    data.packageName = packageInfo.packageName;
                    data.tag = data.title + packageInfo.packageName;
                    data.extra = applicationInfo.uid;
                    dataList.add(data);
                }
            }

            XListDialog.build(this).data(dataList).title("选择需要抓取的App日志").event(tag -> {
                MessageFilter.filter_uid = (int)(tag.extra);
                MessageFilter.packageName = tag.packageName;
                package_title.setText(tag.packageName);
            }).show_search().show();;
        });

        EditText input_filter_tag = frameRoot.findViewById(R.id.input_filter_tag);
        EditText input_filter_content = frameRoot.findViewById(R.id.input_filter_content);

        input_filter_tag.setText(MessageFilter.regex_tag);
        input_filter_content.setText(MessageFilter.regex_content);

        input_filter_tag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MessageFilter.regex_tag = s.toString();
            }
        });

        input_filter_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MessageFilter.regex_content = s.toString();
            }
        });


        RadioButton cb_filter_all = frameRoot.findViewById(R.id.filter_all);
        RadioButton cb_filter_warning = frameRoot.findViewById(R.id.filter_warning);
        RadioButton cb_filter_error = frameRoot.findViewById(R.id.filter_error);
        RadioButton cb_filter_fatal = frameRoot.findViewById(R.id.filter_fetal);

        switch (MessageFilter.filter_priority){
            case 0:cb_filter_all.setChecked(true);break;
            case 4:cb_filter_warning.setChecked(true);break;
            case 6:cb_filter_error.setChecked(true);break;
            case 7:cb_filter_fatal.setChecked(true);break;
        }

        cb_filter_all.setOnClickListener(a ->{
            if (cb_filter_all.isChecked() && cb_filter_all.isPressed()){
                MessageFilter.filter_priority = 0;
            }
        });

        cb_filter_warning.setOnClickListener(a ->{
            if (cb_filter_warning.isChecked() && cb_filter_warning.isPressed()){
                MessageFilter.filter_priority = 4;
            }
        });

        cb_filter_error.setOnClickListener(a ->{
            if (cb_filter_error.isChecked() && cb_filter_error.isPressed()){
                MessageFilter.filter_priority = 6;
            }
        });

        cb_filter_fatal.setOnClickListener(a ->{
            if (cb_filter_fatal.isChecked() && cb_filter_fatal.isPressed()){
                MessageFilter.filter_priority = 7;
            }
        });


        TextView tip_view = frameRoot.findViewById(R.id.filter_tip);
        tip_view.setText("提示,文件存储在 " + AppUtils.getCacheDir() + "/log 目录中,请自行查看");


        CheckBox cb_catch_crash = frameRoot.findViewById(R.id.filter_always_catch_crash);
        cb_catch_crash.setChecked(MessageFilter.catch_crash);
        cb_catch_crash.setOnClickListener(v-> MessageFilter.catch_crash = cb_catch_crash.isChecked());
    }
    private void switchToPreview(){
        cur_tab = 0;
        frameLayout.removeAllViews();
        ViewGroup frameRoot = (ViewGroup) getLayoutInflater().inflate(R.layout.preview_root,frameLayout,true);

        TextView tv_size = frameRoot.findViewById(R.id.preview_size);
        Button btn_fresh = frameRoot.findViewById(R.id.preview_refresh);
        EditText et_content = frameRoot.findViewById(R.id.preview_content);
        et_content.setShowSoftInputOnFocus(false);

        btn_fresh.setOnClickListener(v->{
            if (!TextUtils.isEmpty(localPath)){
                tv_size.setText("日志文件大小:" + DataUtils.convertBytesToString(new File(localPath).length()));
                et_content.setText(FileUtils.readSize(localPath, 256 * 1024));
                et_content.setSelection(et_content.getText().length());
            }
        });

        btn_fresh.callOnClick();

        Button btn_send = frameRoot.findViewById(R.id.preview_send);
        btn_send.setOnClickListener(v->{
            if (TextUtils.isEmpty(localPath)){
                AppUtils.showToast("日志文件不存在");
                return;
            }
            String tmpPath = getCacheDir() + "/log/" + System.currentTimeMillis() + ".log";
            FileUtils.copy(localPath,tmpPath);
            File f = new File(tmpPath);


            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", f));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(shareIntent, "发送日志"));
        });
    }
    private void connectUserServer(){
        Shizuku.UserServiceArgs userServiceArgs =
                new Shizuku.UserServiceArgs(new ComponentName(BuildConfig.APPLICATION_ID, ShellServer.class.getName()))
                        .daemon(false)
                        .processNameSuffix("logcat-daemon")
                        .debuggable(BuildConfig.DEBUG)
                        .version(BuildConfig.VERSION_CODE);
        Shizuku.bindUserService(userServiceArgs, userServiceConnection);
    }
    List<PackageInfo> packageInfos;
    private void onConnected(){
        packageInfos = AppUtils.getPackageInfo();
        connectUserServer();
    }
    private void shizukuConnect(){
        try {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED){
                onConnected();
            }else if (Shizuku.shouldShowRequestPermissionRationale()){
                AppUtils.showToast("Shizuku未授权");
            }else {
                Shizuku.addRequestPermissionResultListener(new ShizukuListener());
                Shizuku.requestPermission(1);
            }
        }catch (Exception e){
            AppUtils.showToast("Shizuku 服务不可用");
        }
    }
    public class ShizukuListener implements Shizuku.OnRequestPermissionResultListener{

        @Override
        public void onRequestPermissionResult(int i, int i1) {
            if (i == 1 && i1 == PackageManager.PERMISSION_DENIED){
                AppUtils.showToast("Shizuku 未授权");
            }else {
                runOnUiThread(MainActivity.this::onConnected);
            }
        }
    }
    private final ServiceConnection userServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            if (binder != null && binder.pingBinder()) {
                service = IShellService.Stub.asInterface(binder);
                try {
                    if (service.getStatus() == 99){
                        service.addNotifyCallback(notifyMessage);
                        service.startServer();
                    }
                } catch (RemoteException e) {
                    AppUtils.showToast("FunLogCatcher Daemon服务异常");
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
            AppUtils.showToast("FunLogCatcher Daemon服务异常断开,请重启App");
        }
    };
}