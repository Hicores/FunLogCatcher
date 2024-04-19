// INotifyMessage.aidl
package fun.logcatcher.server;

import fun.logcatcher.server.LogcatLine;

// Declare any non-default types here with import statements

interface INotifyMessage {
    void notifyMessage(in LogcatLine message);
}