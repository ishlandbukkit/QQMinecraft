package com.ishland.bukkit.QQMinecraft.main;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class AConsoleCommandSender implements ConsoleCommandSender {

    public String[] regexes = { "§1{1}", "§2{1}", "§3{1}", "§4{1}", "§5{1}",
	    "§6{1}", "§7{1}", "§8{1}", "§9{1}", "§0{1}", "§a{1}", "§b{1}",
	    "§c{1}", "§d{1}", "§e{1}", "§f{1}", "§k{1}", "§l{1}", "§m{1}",
	    "§n{1}", "§o{1}", "§r{1}", };

    @Override
    public void sendMessage(String message) {
	Bukkit.getConsoleSender().sendMessage(message);
	String result = message;
	for (String regex : regexes)
	    result = result.replaceAll(regex, "");
	Launcher.msgHandler.send(result);
    }

    @Override
    public void sendMessage(String[] messages) {
	Bukkit.getConsoleSender().sendMessage(messages);
	String result = "";
	for (String str : messages)
	    result += str + "\n";
	for (String regex : regexes)
	    result = result.replaceAll(regex, "");
	Launcher.msgHandler.send(result);
    }

    @Override
    public Server getServer() {
	return Bukkit.getConsoleSender().getServer();
    }

    @Override
    public String getName() {
	return Bukkit.getConsoleSender().getName();
    }

    @Override
    public Spigot spigot() {
	return new AConsoleCommandSenderSpigot(this);
    }

    @Override
    public boolean isPermissionSet(String name) {
	return Bukkit.getConsoleSender().isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
	return Bukkit.getConsoleSender().isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
	return Bukkit.getConsoleSender().hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
	return Bukkit.getConsoleSender().hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name,
	    boolean value) {
	return Bukkit.getConsoleSender().addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
	return Bukkit.getConsoleSender().addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name,
	    boolean value, int ticks) {
	return Bukkit.getConsoleSender().addAttachment(plugin, name, value,
		ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
	return Bukkit.getConsoleSender().addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
	Bukkit.getConsoleSender().removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
	Bukkit.getConsoleSender().recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
	return Bukkit.getConsoleSender().getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
	return Bukkit.getConsoleSender().isOp();
    }

    @Override
    public void setOp(boolean value) {
	Bukkit.getConsoleSender().setOp(value);
    }

    @Override
    public boolean isConversing() {
	return Bukkit.getConsoleSender().isConversing();
    }

    @Override
    public void acceptConversationInput(String input) {
	Bukkit.getConsoleSender().acceptConversationInput(input);
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
	return Bukkit.getConsoleSender().beginConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation) {
	Bukkit.getConsoleSender().abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation,
	    ConversationAbandonedEvent details) {
	Bukkit.getConsoleSender().abandonConversation(conversation, details);
    }

    @Override
    public void sendRawMessage(String message) {
	Bukkit.getConsoleSender().sendRawMessage(message);
	String result = message;
	for (String regex : regexes)
	    result = result.replaceAll(regex, "");
	Launcher.msgHandler.send(result);
    }

}
