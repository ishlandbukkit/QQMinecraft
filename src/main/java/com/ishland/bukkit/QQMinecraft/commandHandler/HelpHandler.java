package com.ishland.bukkit.QQMinecraft.commandHandler;

import java.util.Iterator;

import com.ishland.bukkit.QQMinecraft.api.CommandHandler;
import com.ishland.bukkit.QQMinecraft.main.Launcher;
import com.ishland.bukkit.QQMinecraft.main.QQSender;

public class HelpHandler implements CommandHandler {

    @Override
    public boolean onCommand(String[] args, QQSender sender) {
	String result = "";
	result += "可用的命令：\n";
	Iterator<CommandHandler> it = Launcher.msgHandler.commandHandlerList
		.iterator();
	while (it.hasNext()) {
	    CommandHandler handler = it.next();
	    result += "/" + handler.commandName() + " : "
		    + handler.description() + "\n";
	}
	Launcher.msgHandler.send(result);
	return true;
    }

    @Override
    public String commandName() {
	return "help";
    }

    @Override
    public String description() {
	return "帮助信息";
    }

}
