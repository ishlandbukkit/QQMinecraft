/**
 * 
 */
package com.ishland.bukkit.QQMinecraft.main;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author ishland
 *
 */
public class Launcher extends JavaPlugin implements Listener {
    public MessageHandler msgHandler = null;

    @Override
    public Logger getLogger() {
	return super.getLogger();
    }

    @Override
    public void onEnable() {
	this.saveDefaultConfig();
	this.reloadConfig();
	Bukkit.getPluginManager().registerEvents(this, this);
	URI wsURI = null;
	Long number = this.getConfig().getLong("group");
	if (number.intValue() == 0) {
	    getLogger().log(Level.SEVERE, "Error while reading configuration");
	    Bukkit.getPluginManager().disablePlugin(this);
	}
	try {
	    wsURI = new URI(this.getConfig().getString("ws"));
	} catch (URISyntaxException e) {
	    getLogger().log(Level.SEVERE, "Error while reading configuration", e);
	    Bukkit.getPluginManager().disablePlugin(this);
	}
	msgHandler = new MessageHandler(wsURI, number, this);
	msgHandler.send("Plugin started!");
	getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
	if (msgHandler != null) {
	    msgHandler.send("Plugin stopped!");
	    msgHandler.stop();
	}
	getLogger().info("Disabled");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
	msgHandler.send(event.getPlayer().getName() + " joined the server");
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
	msgHandler.send(event.getPlayer().getName() + " said: " + event.getMessage());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
	msgHandler.send(event.getPlayer().getName() + " left the server");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
	// event.setKeepInventory(true);
	msgHandler.send(event.getDeathMessage());
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
	msgHandler
		.send(event.getPlayer().getName() + " got advancement: " + event.getAdvancement().getKey().toString());
    }

}
