/**
 *
 */
package com.ishland.bukkit.QQMinecraft.main;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.ishland.bukkit.QQMinecraft.api.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author ishland
 *
 */
public class Launcher extends JavaPlugin implements Listener {
    public static MessageHandler msgHandler = null;
    public static String serverVersion;
    public static boolean isCompletelyStarted = false;

    public Logger getPluginLogger() {
        return super.getLogger();
    }

    @Override
    public void onEnable() {
        isCompletelyStarted = false;
        serverVersion = super.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        getLogger().info("Detected server version: " + serverVersion);
        this.saveDefaultConfig();
        this.reloadConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTaskLater(this, () -> msgHandler.send("服务器启动完成！"), 20);
        try {
            Class.forName("org.bukkit.event.raid.RaidEvent");
            getLogger().info("Enabling raid event for " + serverVersion);
            Bukkit.getPluginManager().registerEvents(new RaidEvent(), this);
        } catch (Exception e) {

        }
        URI wsURI = null;
        Long number = this.getConfig().getLong("group");
        if (number.intValue() == 0) {
            getLogger().log(Level.SEVERE, "Error while reading configuration");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            wsURI = new URI(this.getConfig().getString("ws"));
        } catch (URISyntaxException e) {
            getLogger().log(Level.SEVERE, "Error while reading configuration", e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        msgHandler = new MessageHandler(wsURI, number, this);
        try {
            loadAddons();
            registerHandlers("com.ishland.bukkit.QQMinecraft.commandHandler", null);
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error while loading handlers", e);
            msgHandler.send("一个致命错误导致了命令无法加载，请查看后台。");
        }
        showAddonDetails();
        msgHandler.send("插件启动完成!");
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                isCompletelyStarted = true;
            }

        }, 100);
        getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        if (msgHandler != null) {
            for(CommandHandler handler: msgHandler.commandHandlerList){
                if(handler != null)
                    try {
                        handler.onDisable();
                    } catch (Throwable e){
                        e.printStackTrace();
                    }
            }
            msgHandler.send("插件已关闭");
        }
        getLogger().info("Disabled");
    }

    private void loadAddons() {
        File addonsPath = new File(this.getDataFolder() + "/addons");
        addonsPath.mkdirs();
        if (!addonsPath.isDirectory())
            return;
        String[] listAddonsJar = addonsPath.list();
        for (String addonsJarName : listAddonsJar) {
            String addonsJar = this.getDataFolder() + "/addons/" + addonsJarName;
            String addonsJarPath = "";
            // Load addon.yml
            try {
                ZipFile zipFile = new ZipFile(addonsJar);
                ZipEntry entry = zipFile.getEntry("commandHandlerPath");
                if (entry == null || entry.isDirectory()) {
                    getLogger().warning("Found a not addon file in addons path: " + addonsJar);
                    zipFile.close();
                    continue;
                }
                InputStream in = zipFile.getInputStream(entry);
                addonsJarPath = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
                in.close();
                zipFile.close();
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "Error while loading " + addonsJar, e);
                continue;
            }

            // Load it
            try {
                URLClassLoader child = new URLClassLoader(new URL[] { new File(addonsJar).toURI().toURL() },
                        this.getClass().getClassLoader());
                registerHandlers(addonsJarPath, child);
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Error while loading " + addonsJarPath, e);
                continue;
            }
            getLogger().info("Loaded " + addonsJar);
        }
    }

    private void registerHandlers(String packageName, ClassLoader loader) {
        getLogger().info("Looking for CommandHandlers in " + packageName);
        Reflections reflections;
        if (loader != null)
            reflections = new Reflections(packageName, loader);
        else
            reflections = new Reflections(packageName);
        Set<Class<? extends CommandHandler>> allClasses = reflections.getSubTypesOf(CommandHandler.class);
        if (allClasses == null)
            return;
        for (Class<? extends CommandHandler> theClass : allClasses) {
            getLogger().info("Loading handler " + theClass.getName());
            try {
                CommandHandler current = theClass.getDeclaredConstructor().newInstance();
                if (current.commandName() == null || current.description() == null)
                    throw new RuntimeException("Not a handler");
                msgHandler.commandHandlerList.add((current));
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Cannot load handler " + theClass.getName(), e);
            }
        }
    }

    private void showAddonDetails() {
        String str = "";
        str += "成功加载 " + msgHandler.commandHandlerList.size() + " 个命令处理器: \n";
        Iterator<CommandHandler> it = msgHandler.commandHandlerList.iterator();
        while (it.hasNext())
            str += it.next().getClass().getName() + "\n";
        getLogger().info(str);
        msgHandler.send(str);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != Result.ALLOWED)
            msgHandler.send(event.getName() + " 尝试进入服务器但是被移出: " + event.getKickMessage());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        msgHandler.send(event.getPlayer().getName() + " 进入了服务器 ");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            // msgHandler.send("Ignoring cancelled chat from " +
            // event.getPlayer().getName());
            return;
        }
        msgHandler.send(event.getPlayer().getName() + ": " + event.getMessage());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        msgHandler.send(event.getPlayer().getName() + " 被移出服务器: " + event.getReason());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        msgHandler.send(event.getPlayer().getName() + " 离开了服务器");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // event.setKeepInventory(true);
        msgHandler.send(event.getDeathMessage());
        getLogger().info(event.getDeathMessage() + " at " + event.getEntity().getLocation());
    }

}
