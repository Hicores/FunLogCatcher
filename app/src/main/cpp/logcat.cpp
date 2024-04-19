#include "logcat.h"

#include <jni.h>
#include "stdio.h"
#include <memory>
#include <unistd.h>

constexpr size_t kMaxLogSize = 4 * 1024 * 1024;
constexpr size_t kLogBufferSize = 64 * 1024;

class LogcatReader {
public:
    explicit LogcatReader(JNIEnv *env, jclass clz, jlong time) :_env(env), _line_clz(clz), cur_time(time){
        _pid = env->GetFieldID(clz,"pid","I");
        _tid = env->GetFieldID(clz,"tid","I");
        _uid = env->GetFieldID(clz,"uid","I");
        _sec = env->GetFieldID(clz,"sec","I");
        _nsec = env->GetFieldID(clz,"nsec","I");

        _priority = env->GetFieldID(clz,"priority","I");
        _tag = env->GetFieldID(clz,"tag","Ljava/lang/String;");
        _content = env->GetFieldID(clz,"content","Ljava/lang/String;");
        _handle_clz = env->FindClass("fun/logcatcher/server/LogcatHandler");
        _handle = env->GetStaticMethodID(_handle_clz,"onLogcatLine","(Lfun/logcatcher/server/LogcatLine;)V");
    }
    JNIEnv *_env;

    jclass _line_clz;
    jlong cur_time;

    jfieldID _pid;
    jfieldID _tid;
    jfieldID _uid;
    jfieldID _sec;
    jfieldID _nsec;

    jfieldID _priority;
    jfieldID _tag;
    jfieldID _content;

    jmethodID _handle;
    jclass _handle_clz;

    void runLoop();
};

void LogcatReader::runLoop() {
    size_t tail = 0;
    std::unique_ptr<logger_list, decltype(&android_logger_list_free)> logger_list{
            android_logger_list_alloc(0, tail, 0), &android_logger_list_free};

    for (log_id id:{LOG_ID_CRASH,LOG_ID_MAIN}) {
        auto *logger = android_logger_open(logger_list.get(), id);
        if (logger == nullptr) continue;
        if (auto size = android_logger_get_log_size(logger);
                size >= 0 && static_cast<size_t>(size) < kLogBufferSize) {
            android_logger_set_log_size(logger, kLogBufferSize);
        }
    }

    struct log_msg msg{};




    while (true) {
        if (android_logger_list_read(logger_list.get(), &msg) <= 0) [[unlikely]] break;
        AndroidLogEntry entry;
        if (android_log_processLogBuffer(&msg.entry, &entry) < 0) continue;
        if (msg.entry.sec < cur_time) continue;

        jobject line = _env->AllocObject(_line_clz);
        _env->SetIntField(line,_pid,msg.entry.pid);
        _env->SetIntField(line,_tid,msg.entry.tid);
        _env->SetIntField(line,_uid,msg.entry.uid);
        _env->SetIntField(line,_sec,msg.entry.sec);
        _env->SetIntField(line,_nsec,msg.entry.nsec);
        _env->SetIntField(line,_priority,entry.priority);
        _env->SetObjectField(line,_tag,_env->NewStringUTF(entry.tag));
        _env->SetObjectField(line,_content,_env->NewStringUTF(entry.message));

        _env->CallStaticVoidMethod(_handle_clz,_handle,line);

    }
}

extern "C"
JNIEXPORT void JNICALL
Java_fun_logcatcher_server_LogcatHandler_runLogcatService(JNIEnv *env, jclass clazz, jlong time) {
    jclass clz = env->FindClass("fun/logcatcher/server/LogcatLine");
    LogcatReader newReader(env,clz,time);
    newReader.runLoop();
}