package com.ishland.bukkit.QQMinecraft.main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageThread extends Thread {

    public static BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    private boolean isStopping = false;

    private MessageHandler handler;

    public MessageThread(MessageHandler handler) {
	super();
	this.handler = handler;
    }

    public void run() {
	try {
	    Thread.sleep(1000);
	} catch (InterruptedException e2) {
	}
	while (!isStopping) {
	    if (!handler.isReady())
		continue;
	    String result = null;
	    try {
		result = queue.poll(1, TimeUnit.SECONDS);
	    } catch (InterruptedException e1) {
	    }
	    if (result == null)
		continue;
	    for (int i = 0; !queue.isEmpty() && i < 20; i++)
		result += "\n" + queue.poll();
	    handler.sendNow(result);
	}
    }

    /**
     * @return the isStopping
     */
    public boolean isStopping() {
	return isStopping;
    }

    /**
     * @param isStopping the isStopping to set
     */
    public void setStopping(boolean isStopping) {
	this.isStopping = isStopping;
    }

}
