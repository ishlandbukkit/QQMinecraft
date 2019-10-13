package com.ishland.bukkit.QQMinecraft.commandHandler;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.ishland.bukkit.QQMinecraft.api.CommandHandler;
import com.ishland.bukkit.QQMinecraft.main.AConsoleCommandSender;
import com.ishland.bukkit.QQMinecraft.main.Launcher;
import com.ishland.bukkit.QQMinecraft.main.QQSender;

public class ExecHandler implements CommandHandler {
    String command;

    @Override
    public boolean onCommand(String[] args, QQSender sender) {
	if (sender.role.equals("member")) {
	    Launcher.msgHandler.send("bash: exec: Permission denied");
	    return true;
	}
	command = "";
	for (String cmdpart : args)
	    command += cmdpart + " ";
	CommandSender s = new AConsoleCommandSender();
	Bukkit.getScheduler().runTaskLater(Launcher.msgHandler.plugin,
		() -> Bukkit.getServer().dispatchCommand(s, command), 1);
	return true;
    }

    @Override
    public String commandName() {
	return "console";
    }

    @Override
    public String description() {
	return "Execute command";
    }

}
