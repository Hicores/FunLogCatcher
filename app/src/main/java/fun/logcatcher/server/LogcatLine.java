package fun.logcatcher.server;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;

public class LogcatLine  implements Parcelable {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final Creator<LogcatLine> CREATOR = new Creator<LogcatLine>() {
        @Override
        public LogcatLine createFromParcel(Parcel in) {
            return new LogcatLine(in);
        }

        @Override
        public LogcatLine[] newArray(int size) {
            return new LogcatLine[size];
        }
    };
    public int pid;
    public int tid;
    public int uid;
    public int sec;
    public int nsec;

    public int priority;
    public String tag;
    public String content;
    public LogcatLine(){

    }
    public LogcatLine(Parcel parcel){
        pid = parcel.readInt();
        tid = parcel.readInt();
        uid = parcel.readInt();
        sec = parcel.readInt();
        nsec = parcel.readInt();

        priority = parcel.readInt();
        tag = parcel.readString();
        content = parcel.readString();

    }

    @Override
    public String toString() {
        return "LogcatLine{" +
                "pid=" + pid +
                ", tid=" + tid +
                ", uid=" + uid +
                ", sec=" + sec +
                ", priority=" + priority +
                ", tag='" + tag + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
    public String buildStr(){
        return sdf.format(sec * 1000L) +"." + (nsec / 1000 / 1000)+ " "+pid + "-" + tid+"  " + tag + " "+ priorityToString(priority)+"  " + content;
    }
    private static String priorityToString(int p){
        switch (p){
            case 2:
                return "V";
            case 3:
                return "D";
            case 4:
                return "I";
            case 5:
                return "W";
            case 6:
                return "E";
            case 7:
                return "F";
            default:
                return "U";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(pid);
        dest.writeInt(tid);
        dest.writeInt(uid);
        dest.writeInt(sec);
        dest.writeInt(nsec);
        dest.writeInt(priority);
        dest.writeString(tag);
        dest.writeString(content);
    }
}
