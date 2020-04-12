package com.ishland.bukkit.QQMinecraft.commandHandler;

import java.util.Arrays;

import org.bukkit.Bukkit;

import com.ishland.bukkit.QQMinecraft.api.CommandHandler;
import com.ishland.bukkit.QQMinecraft.main.Launcher;
import com.ishland.bukkit.QQMinecraft.main.QQSender;

public class TPSHandler implements CommandHandler {

	@Override
	public boolean onCommand(String[] args, QQSender sender) {
		try {
			Launcher.msgHandler.send("TPS from last 1m, 5m, 15m: " + Arrays.toString(Bukkit.getServer().getTPS()));
		} catch (NoSuchMethodError e) {
			Launcher.msgHandler
					.send("Cannot find method: " + e.getMessage() + ", redirecting to console command \"tps\"");
			sender.role = "redirect";
			new ExecHandler().onCommand(new String[] { "tps" }, sender);
			return true;
		}
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
