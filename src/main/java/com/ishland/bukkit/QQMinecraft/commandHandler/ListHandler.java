package com.ishland.bukkit.QQMinecraft.commandHandler;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ishland.bukkit.QQMinecraft.api.CommandHandler;
import com.ishland.bukkit.QQMinecraft.main.Launcher;
import com.ishland.bukkit.QQMinecraft.main.QQSender;

public class ListHandler implements CommandHandler {

    @Override
    public boolean onCommand(String[] args, QQSender sender) {
	String result = "";
	result += "当前有 " + Bukkit.getOnlinePlayers().size() + " 个玩家在线，最大在线人数： "
		+ Bukkit.getMaxPlayers() + "\n";
	result += "当前在线玩家： ";
	Iterator<? extends Player> it = Bukkit.getOnlinePlayers().iterator();
	while (it.hasNext()) {
	    Player p = it.next();
	    result += p.getName() + " ";
	}

	Launcher.msgHandler.send(result);
	return true;
    }

    @Override
    public String commandName() {
	return "list";
    }

    @Override
    public String description() {
	return "获取服务器在线玩家";
    }

}
