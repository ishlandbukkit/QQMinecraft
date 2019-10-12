package com.ishland.bukkit.QQMinecraft.main;

public interface CommandHandler {

    boolean onCommand(String[] args);

    String commandName();

    String description();

}
