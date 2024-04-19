package fun.logcatcher.server;

import fun.logcatcher.server.INotifyMessage;

interface IShellService{

    void destroy() = 16777114; // Destroy method defined by Shizuku server

    void exit() = 1; // Exit method defined by user

    void addNotifyCallback(INotifyMessage msg) = 11;

    void startServer() = 12;

    int getStatus() = 14;
}
