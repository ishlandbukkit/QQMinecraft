/**
 * 
 */
package com.ishland.bukkit.QQMinecraft.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.ishland.bukkit.QQMinecraft.api.CommandHandler;

/**
 * @author ishland
 *
 */
public class Launcher extends JavaPlugin implements Listener {
    public static MessageHandler msgHandler = null;

    public Logger getPluginLogger() {
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
	    getLogger().log(Level.SEVERE, "Error while reading configuration",
		    e);
	    Bukkit.getPluginManager().disablePlugin(this);
	}
	msgHandler = new MessageHandler(wsURI, number, this);
	loadAddons();
	registerHandlers("com.ishland.bukkit.QQMinecraft.commandHandler", null);
	showAddonDetails();
	msgHandler.send("Plugin started!");
	getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
	if (msgHandler != null) {
	    msgHandler.send("Plugin stopped!");
	    msgHandler.stop();
	}
	msgHandler = null;
	getLogger().info("Disabled");
    }

    private void loadAddons() {
	File addonsPath = new File(this.getDataFolder() + "/addons");
	addonsPath.mkdirs();
	if (!addonsPath.isDirectory())
	    return;
	String[] listAddonsJar = addonsPath.list();
	for (String addonsJarName : listAddonsJar) {
	    String addonsJar = this.getDataFolder() + "/addons/"
		    + addonsJarName;
	    String addonsJarPath = "";
	    // Load addon.yml
	    try {
		ZipFile zipFile = new ZipFile(addonsJar);
		ZipEntry entry = zipFile.getEntry("commandHandlerPath");
		if (entry == null || entry.isDirectory()) {
		    getLogger()
			    .warning("Found a not addon file in addons path: "
				    + addonsJar);
		    zipFile.close();
		    continue;
		}
		InputStream in = zipFile.getInputStream(entry);
		addonsJarPath = CharStreams
			.toString(new InputStreamReader(in, Charsets.UTF_8));
		in.close();
		zipFile.close();
	    } catch (IOException e) {
		getLogger().log(Level.WARNING,
			"Error while loading " + addonsJar, e);
		continue;
	    }

	    // Load it
	    try {
		URLClassLoader child = new URLClassLoader(
			new URL[] { new File(addonsJar).toURI().toURL() },
			this.getClass().getClassLoader());
		registerHandlers(addonsJarPath, child);
	    } catch (Exception e) {
		getLogger().log(Level.WARNING,
			"Error while loading " + addonsJarPath, e);
		continue;
	    }
	    getLogger().info("Loaded " + addonsJar);
	}
    }

    private void registerHandlers(String packageName, ClassLoader loader) {
	getLogger().info("Looking for CommandHandlers in " + packageName);
	Reflections reflections = null;
	if (loader == null)
	    reflections = new Reflections(packageName);
	else
	    reflections = new Reflections(packageName, loader);
	Set<Class<? extends CommandHandler>> allClasses = reflections
		.getSubTypesOf(CommandHandler.class);
	if (allClasses == null)
	    return;
	Iterator<Class<? extends CommandHandler>> itNames = allClasses
		.iterator();
	while (itNames.hasNext()) {
	    Class<? extends CommandHandler> theClass = itNames.next();
	    getLogger().info("Loading handler " + theClass.getName());
	    try {
		CommandHandler current = theClass.newInstance();
		if (current.commandName() == null
			|| current.description() == null)
		    throw new RuntimeException("Not a handler");
		msgHandler.commandHandlerList.add((current));
	    } catch (Exception e) {
		getLogger().log(Level.WARNING,
			"Cannot load handler " + theClass.getName(), e);
	    }
	}
    }

    private void showAddonDetails() {
	String str = "";
	str += "Loaded " + msgHandler.commandHandlerList.size()
		+ " handlers: \n";
	Iterator<CommandHandler> it = msgHandler.commandHandlerList.iterator();
	while (it.hasNext())
	    str += it.next().getClass().getName() + "\n";
	getLogger().info(str);
	msgHandler.send(str);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
	if (event.getLoginResult() != Result.ALLOWED)
	    msgHandler.send(event.getName() + " [" + event.getAddress()
		    + "] was about to join the server but failed: "
		    + event.getKickMessage());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
	msgHandler.send(event.getPlayer().getName() + " joined the server");
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
	msgHandler.send(
		event.getPlayer().getName() + " said: " + event.getMessage());
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

}
