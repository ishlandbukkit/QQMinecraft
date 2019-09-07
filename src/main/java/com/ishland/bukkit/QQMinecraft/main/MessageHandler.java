package com.ishland.bukkit.QQMinecraft.main;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class MessageHandler {
    private WSClient wsClient;
    private Launcher plugin;
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
