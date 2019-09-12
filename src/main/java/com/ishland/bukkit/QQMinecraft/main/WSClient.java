package com.ishland.bukkit.QQMinecraft.main;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.logging.Level;

import org.apache.commons.lang.StringEscapeUtils;
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
    private boolean closed = false;
    private long lastDown = System.currentTimeMillis();

    public WSClient(URI serverUri, Long number, Launcher plugin) {
	super(serverUri);
	this.plugin = plugin;
	this.number = number;
	this.setConnectionLostTimeout(3);
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
	if (closed)
	    plugin.msgHandler.send("Connection restarted. It was down for "
		    + String.valueOf(System.currentTimeMillis() - lastDown) + "ms");
	closed = false;
    }

    @Override
    public void onMessage(String message) {
	JsonElement element = parser.parse(message);
	if (!element.isJsonObject()) {
	    plugin.getLogger().warning("Invaild message");
	    return;
	}

	JsonObject jsonObject = element.getAsJsonObject();
	if (jsonObject.get("retcode") != null)
	    return;
	if (!jsonObject.get("post_type").getAsString().equals("message")
		|| !jsonObject.get("message_type").getAsString().equals("group")
		|| jsonObject.get("group_id").getAsLong() != number.longValue()) {
	    // plugin.getLogger().info(String.valueOf(jsonObject.get("post_type").getAsString()
	    // != "message")
	    // + String.valueOf(jsonObject.get("message_type").getAsString() != "group"));
	    return;
	}

	if (jsonObject.get("sub_type").getAsString().equals("normal")) {
	    // plugin.getLogger().info("inside");
	    try {
		boardcastMessage(new ComponentBuilder("[QQ]").color(ChatColor.GREEN).append(" ")
			.append("["
				+ (jsonObject.get("sender").getAsJsonObject().get("title").getAsString().length() > 0
					? jsonObject.get("sender").getAsJsonObject().get("title").getAsString()
					: jsonObject.get("sender").getAsJsonObject().get("level").getAsString())
				+ "]")
			.color(ChatColor.BLUE).append(" ").append("<").color(ChatColor.WHITE)
			.append((jsonObject.get("sender").getAsJsonObject().get("card").getAsString().length() > 0
				? jsonObject.get("sender").getAsJsonObject().get("card").getAsString()
				: jsonObject.get("sender").getAsJsonObject().get("nickname").getAsString()))
			.color((!jsonObject.get("sender").getAsJsonObject().get("role").getAsString().equals("member")
				? ChatColor.RED
				: ChatColor.WHITE))
			.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("QQ: " + String
					.valueOf(jsonObject.get("sender").getAsJsonObject().get("user_id").getAsLong()))
						.append("\n")
						.append("Sent at " + format.format(jsonObject.get("time").getAsLong()))
						.create()))
			.append(">").color(ChatColor.WHITE).append(" ")
			.append(StringEscapeUtils
				.unescapeHtml(URLDecoder.decode(jsonObject.get("message").getAsString(), "UTF-8"))
				.replaceAll("\\[CQ:*\\]", "[Unsupported]"))
			.color(ChatColor.WHITE).create());
	    } catch (UnsupportedEncodingException e) {
	    }
	    return;
	}
	if (jsonObject.get("sub_type").getAsString().equals("notice")) {
	    boardcastMessage(new ComponentBuilder("[QQ]").color(ChatColor.GREEN).append(" ").append("<Notice>")
		    .color(ChatColor.WHITE).append(" ").append(jsonObject.get("message").getAsString()).create());
	    return;
	}
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
	if (reason.length() > 0 || remote) {
	    plugin.getLogger().info("Connection closed: " + reason + ", reconnecting...");
	    Bukkit.getScheduler().runTaskLater(plugin, () -> this.reconnect(), 2);
	    if (!closed) {
		closed = true;
		lastDown = System.currentTimeMillis();
	    }
	    return;
	}
	plugin.getLogger().info("Connection closed");
    }

    @Override
    public void onError(Exception ex) {
	plugin.getLogger().log(Level.SEVERE, "Exception in connection to coolq", ex);
    }

}
