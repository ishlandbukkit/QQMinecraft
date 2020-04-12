package com.ishland.bukkit.QQMinecraft.commandHandler;

import com.ishland.bukkit.QQMinecraft.api.CommandHandler;
import com.ishland.bukkit.QQMinecraft.main.Launcher;
import com.ishland.bukkit.QQMinecraft.main.MessageThread;
import com.ishland.bukkit.QQMinecraft.main.QQSender;

public class FlushHandler implements CommandHandler {
    @Override
    public boolean onCommand(String[] args, QQSender sender) {
        if (sender.role.equals("member")) {
            Launcher.msgHandler.send("bash: flush: Permission denied");
            return true;
        }
        synchronized (MessageThread.queue) {
            int entries = MessageThread.queue.size();
            MessageThread.queue.clear();
            Launcher.msgHandler.send("Flush complete, removed " + entries + " entries.");
            return true;
        }
    }

    @Override
    public String commandName() {
        return "flush";
    }

    @Override
    public String description() {
        return "[管理员&群主] Flush message queue";
    }
}
