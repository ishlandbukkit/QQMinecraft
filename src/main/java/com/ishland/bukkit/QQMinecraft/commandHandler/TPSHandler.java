package com.ishland.bukkit.QQMinecraft.commandHandler;

import java.util.Arrays;

import org.bukkit.Bukkit;

import com.ishland.bukkit.QQMinecraft.api.CommandHandler;
import com.ishland.bukkit.QQMinecraft.main.Launcher;
import com.ishland.bukkit.QQMinecraft.main.QQSender;

public class TPSHandler implements CommandHandler {

	@Override
	public boolean onCommand(String[] args, QQSender sender) {
		Launcher.msgHandler.send("TPS from last 1m, 5m, 15m: " + Arrays.toString(Bukkit.getServer().getTPS()));
		return true;
	}

	@Override
	public String commandName() {
		return "tps";
	}

	@Override
	public String description() {
		return "查询服务器tps";
	}

}
