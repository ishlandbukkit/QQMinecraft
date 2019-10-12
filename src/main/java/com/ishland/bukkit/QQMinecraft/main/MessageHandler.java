package com.ishland.bukkit.QQMinecraft.main;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.google.gson.Gson;

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

    public void processCommand(String message) {
	String[] splited = message.split(" ");
	String commandName = splited[0];
	String[] args = new String[splited.length - 1];
	for (int i = 1; i < splited.length; i++)
	    args[i] = splited[i];
	splited = null;
	Iterator<CommandHandler> it = commandHandlerList.iterator();
	boolean didExec = false;
	while (it.hasNext()) {
	    CommandHandler handler = it.next();
	    if (handler.commandName().equals(commandName)) {
		boolean doBlock = false;
		try {
		    doBlock = handler.onCommand(args);
		    didExec = true;
		} catch (Throwable t) {
		    plugin.getLogger().log(Level.SEVERE,
			    "Error while passing command event to "
				    + handler.getClass().getName(),
			    t);
		    Launcher.msgHandler.send(
			    "An internal error occurred while passing command event to "
				    + handler.getClass().getName());
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
	synchronized (MessageThread.queue) {
	    MessageThread.queue.notifyAll();
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
	return wsClient.isOpen();
    }
}
