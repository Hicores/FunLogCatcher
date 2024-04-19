package fun.logcatcher;

import android.text.TextUtils;
import android.util.Log;

import fun.logcatcher.server.LogcatHandler;
import fun.logcatcher.server.LogcatLine;

public class MessageFilter {
    public static int filter_uid;
    public static String packageName;
    public static String regex_tag = "";
    public static String regex_content = "[FunBox]";
    public static int filter_priority;
    public static boolean catch_crash = true;


    private static int lock_filter_uid;
    private static String lock_regex_tag;
    private static String lock_regex_content;
    private static int lock_filter_priority;
    private static boolean lock_catch_crash;
    public static void lockFilter(){
        lock_filter_uid = filter_uid;
        lock_regex_tag = regex_tag;
        lock_regex_content = regex_content;
        lock_filter_priority = filter_priority;
        lock_catch_crash = catch_crash;
    }
    private static boolean isCrash(LogcatLine line){
        return (line.priority == 6 && line.tag.equals("AndroidRuntime") && line.content.contains("FATAL EXCEPTION")) || line.priority == 7;
    }
    public static LogcatLine filter(LogcatLine line){

//        if (line.content.contains("FunBox")){
//            Log.d("LogCatcherLog", "filter: " + line.content);
//        }

        if (line.content.endsWith("\n")){
            line.content = line.content.substring(0,line.content.length()-1);
        }

        if (lock_filter_uid != 0 && line.uid != lock_filter_uid){
            return null;
        }
        if (lock_catch_crash && isCrash(line)){
            return line;
        }
        if (lock_filter_priority != 0 && line.priority < lock_filter_priority){
            return null;
        }

        try {
            if (lock_regex_tag != null && !TextUtils.isEmpty(lock_regex_tag) && !line.tag.contains(lock_regex_tag)){
                return null;
            }

            if (lock_regex_content != null && !TextUtils.isEmpty(lock_regex_content) && !line.content.contains(lock_regex_content)){
                return null;
            }
        }catch (Exception ignored){ }


        return line;
    }
}
