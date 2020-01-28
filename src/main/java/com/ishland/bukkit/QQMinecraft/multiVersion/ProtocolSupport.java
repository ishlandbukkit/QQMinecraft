package com.ishland.bukkit.QQMinecraft.multiVersion;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ProtocolSupport {
    private ProtocolSupportMain instance;

    public ProtocolSupport(Plugin plugin) {
	if (Bukkit.getPluginManager().getPlugin("ProtocolSupport") != null) {
	    this.instance = new ProtocolSupportMain(plugin);
	    this.instance.hook();
	} else {
	    throw new IllegalStateException("No ProtocolSupport detected.");
	}
    }

    /**
     * @return the instance
     */
    public ProtocolSupportMain getInstance() {
	return instance;
    }

}
