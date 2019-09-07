package com.ishland.bukkit.QQMinecraft.main;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class WSClient extends WebSocketClient {
    private Launcher plugin;
    private static final JsonParser parser = new JsonParser();
    private Long number;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public WSClient(URI serverUri, Long number, Launcher plugin) {
	super(serverUri);
	this.plugin = plugin;
	this.number = number;
    }

    private void boardcastMessage(BaseComponent[] msg) {
	Iterator<? extends Player> it = Bukkit.getOnlinePlayers().iterator();
	while (it.hasNext()) {
	    Player p = it.next();
	    p.spigot().sendMessage(msg);
	}
	Bukkit.getConsoleSender().spigot().sendMessage(msg);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
	plugin.getLogger().info("Connection started");
	;
    }

    @Override
    public void onMessage(String message) {
	JsonElement element = parser.parse(message);
	if (!element.isJsonObject()) {
	    plugin.getLogger().warning("Invaild message");
	    return;
	}

	JsonObject jsonObject = element.getAsJsonObject();
	if (jsonObject.get("post_type").getAsString() != "message"
		|| jsonObject.get("message_type").getAsString() != "group"
		|| jsonObject.get("group_id").getAsLong() != number.longValue() || jsonObject.get("retcode") != null) {
	    plugin.getLogger().finer("Ignoring event");
	    return;
	}

	if (jsonObject.get("sub_type").getAsString() == "normal") {
	    boardcastMessage(new ComponentBuilder("[QQ]").color(ChatColor.GREEN).append(" ")
		    .append("[" + jsonObject.get("sender").getAsJsonObject().get("title").getAsString() + "]")
		    .color(ChatColor.BLUE).append(" ")
		    .append("<" + jsonObject.get("sender").getAsJsonObject().get("nickname").getAsString() + ">")
		    .color((jsonObject.get("sender").getAsJsonObject().get("role").getAsString() != "member"
			    ? ChatColor.RED
			    : ChatColor.WHITE))
		    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
			    new ComponentBuilder("QQ: " + String
				    .valueOf(jsonObject.get("sender").getAsJsonObject().get("user_id").getAsLong()))
					    .append("\n")
					    .append("Sent at " + format.format(jsonObject.get("time").getAsLong()))
					    .create()))
		    .append(" ").append(jsonObject.get("message").getAsString()).color(ChatColor.WHITE).create());
	    return;
	}
	if (jsonObject.get("sub_type").getAsString() == "notice") {
	    boardcastMessage(new ComponentBuilder("[QQ]").append(" ").append("<Notice>").color(ChatColor.BLUE)
		    .append(" ").append(jsonObject.get("message").getAsString()).create());
	}
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
	if (remote) {
	    plugin.getLogger().info("Connection closed: " + reason + ", reconnecting...");
	    this.reconnect();
	    return;
	}
	plugin.getLogger().info("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
	plugin.getLogger().log(Level.SEVERE, "Exception in connection to coolq", ex);
    }

}
