package com.ishland.bukkit.QQMinecraft.main;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender.Spigot;

import net.md_5.bungee.api.chat.BaseComponent;

public class AConsoleCommandSenderSpigot extends Spigot {
    private AConsoleCommandSender parent = null;

    public AConsoleCommandSenderSpigot(AConsoleCommandSender parent) {
	this.parent = parent;
    }

    @Override
    public void sendMessage(BaseComponent component) {
	Bukkit.getConsoleSender().spigot().sendMessage(component);
	String result = component.toPlainText();
	for (String regex : parent.regexes)
	    result = result.replaceAll(regex, "");
	Launcher.msgHandler.send(result);
    }

    @Override
    public void sendMessage(BaseComponent... components) {
	Bukkit.getConsoleSender().spigot().sendMessage(components);
	String result = "";
	for (BaseComponent component : components)
	    result += component.toPlainText();
	for (String regex : parent.regexes)
	    result = result.replaceAll(regex, "");
	Launcher.msgHandler.send(result);
    }
}
