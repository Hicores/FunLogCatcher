package fun.logcatcher.server.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class LogcatLineData implements Parcelable {
    public int pid;
    public int tid;
    public int uid;
    public int sec;

    public int priority;
    public String tag;
    public String content;

    protected LogcatLineData(Parcel in) {
        pid = in.readInt();
        tid = in.readInt();
        uid = in.readInt();
        sec = in.readInt();

        priority = in.readInt();
        tag = in.readString();
        content = in.readString();
    }

    public static final Creator<LogcatLineData> CREATOR = new Creator<LogcatLineData>() {
        @Override
        public LogcatLineData createFromParcel(Parcel in) {
            return new LogcatLineData(in);
        }

        @Override
        public LogcatLineData[] newArray(int size) {
            return new LogcatLineData[size];
        }
    };

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

        dest.writeInt(priority);
        dest.writeString(tag);
        dest.writeString(content);
    }

    public void readFromParcel(Parcel in) {
        pid = in.readInt();
        tid = in.readInt();
        uid = in.readInt();
        sec = in.readInt();

        priority = in.readInt();
        tag = in.readString();
        content = in.readString();
    }
}
