package com.ishland.bukkit.QQMinecraft.multiVersion;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import protocolsupport.api.ProtocolType;
import protocolsupport.api.ProtocolVersion;

public class ProtocolSupportMain implements Listener {
    private Plugin plugin;
    private ProtocolVersion serverVersion = ProtocolVersion
	    .getLatest(ProtocolType.PC);

    public ProtocolSupportMain(Plugin plugin) {
	this.plugin = plugin;
    }

    public void hook() {
	plugin.getLogger().info("Hooking into ProtocolSupport");
	Bukkit.getPluginManager().registerEvents(this, plugin);
	// Get server version
	plugin.getLogger()
		.info("Detected server version " + serverVersion.getName());
	plugin.getLogger()
		.info("Supported protocol versions by ProtocolSupport: "
			+ ProtocolVersion.getAllSupported());
    }
}
