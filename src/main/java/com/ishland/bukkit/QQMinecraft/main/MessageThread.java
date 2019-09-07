package com.ishland.bukkit.QQMinecraft.main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageThread extends Thread {

    public static BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    private boolean isStopping = false;

    private MessageHandler handler;

    public MessageThread(MessageHandler handler) {
	super();
	this.handler = handler;
    }

    public void run() {
	while (true) {
	    synchronized (queue) {
		try {
		    queue.wait();
		} catch (InterruptedException e) {
		}
	    }
	    while (!handler.isReady())
		try {
		    Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	    while (!queue.isEmpty()) {
		handler.sendNow(queue.poll());
	    }
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
