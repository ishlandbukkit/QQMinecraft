package com.ishland.bukkit.QQMinecraft.main;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ishland.bukkit.QQMinecraft.api.CommandHandler;

public class MessageHandler {
    public List<CommandHandler> commandHandlerList = new ArrayList<>();

    private WSClient wsClient;
    public Launcher plugin;
    private Long number;
    private Gson gson = new Gson();
    private MessageThread thr;

    public MessageHandler(URI wsAddr, Long number, Launcher plugin) {
	this.plugin = plugin;
	this.number = number;
	this.thr = new MessageThread(this);
	thr.start();
	wsClient = new WSClient(wsAddr, number, this.plugin);
	try {
	    wsClient.connectBlocking();
	} catch (InterruptedException e) {
	}
    }

    public WSClient getClient() {
	return this.wsClient;
    }

    public void processCommand(String message, JsonObject sender) {
	String[] splited = message.split(" ");
	String commandName = splited[0];
	String[] args = Arrays.copyOfRange(splited, 1, splited.length);
	splited = null;

	// Parse sender
	QQSender senderObj = new QQSender();
	senderObj.age = sender.get("age") != null ? sender.get("age").getAsInt()
		: null;
	senderObj.area = sender.get("area") != null
		? sender.get("area").getAsString()
		: null;
	senderObj.card = sender.get("card") != null
		? sender.get("card").getAsString()
		: null;
	senderObj.level = sender.get("level") != null
		? sender.get("level").getAsString()
		: null;
	senderObj.nickname = sender.get("nickname") != null
		? sender.get("nickname").getAsString()
		: null;
	senderObj.role = sender.get("role") != null
		? sender.get("role").getAsString()
		: null;
	senderObj.sex = sender.get("sex") != null
		? sender.get("sex").getAsString()
		: null;
	senderObj.title = sender.get("title") != null
		? sender.get("title").getAsString()
		: null;
	senderObj.user_id = sender.get("user_id") != null
		? sender.get("user_id").getAsLong()
		: null;

	// Execute
	Iterator<CommandHandler> it = commandHandlerList.iterator();
	boolean didExec = false;
	while (it.hasNext()) {
	    CommandHandler handler = it.next();
	    if (handler.commandName().equals(commandName)) {
		boolean doBlock = false;
		try {
		    doBlock = handler.onCommand(args, senderObj);
		} catch (Throwable t) {
		    plugin.getLogger().log(Level.SEVERE,
			    "Error while passing command event to "
				    + handler.getClass().getName(),
			    t);
		    Launcher.msgHandler.send(
			    "An internal error occurred while passing command event to "
				    + handler.getClass().getName());
		} finally {
		    didExec = true;
		}
		if (doBlock)
		    return;
	    }
	}
	if (didExec)
	    return;
	Launcher.msgHandler.send("Unknown command. Type \"/help\" for help.");
    }

    public void send(String str) {
	try {
	    MessageThread.queue.put(str);
	} catch (InterruptedException e) {
	}
    }

    public void sendNow(String str) {
	Map<String, Object> params = new HashMap<>();
	params.put("group_id", number);
	params.put("message", str);
	Map<String, Object> map = new HashMap<>();
	map.put("action", "send_group_msg");
	map.put("params", params);
	wsClient.send(gson.toJson(map));
    }

    public void stop() {
	wsClient.close();
	thr.setStopping(true);
    }

    public boolean isReady() {
	return wsClient != null && wsClient.isOpen();
    }
}
