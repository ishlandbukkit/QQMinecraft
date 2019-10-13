package com.ishland.bukkit.QQMinecraft.api;

import com.ishland.bukkit.QQMinecraft.main.QQSender;

public interface CommandHandler {

    boolean onCommand(String[] args, QQSender sender);

    String commandName();

    String description();

}
