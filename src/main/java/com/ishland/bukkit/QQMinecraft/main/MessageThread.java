package com.ishland.bukkit.QQMinecraft.main;

import java.util.NoSuchElementException;
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
		try {
		    handler.sendNow(queue.element());
		} catch (NoSuchElementException e) {
		    try {
			Thread.sleep(50);
		    } catch (InterruptedException e1) {
		    }
		    continue;
		}
		APIResponse response = BlockingLock.waitForResult();
		if (response == null) {
		    handler.plugin.getLogger().warning(
			    "Error while sending message: no response, retrying");
		    continue;
		}
		if (response.retcode != 0 && response.retcode != 1) {
		    handler.plugin.getLogger()
			    .warning("Error while sending message: "
				    + response.status + ", retrying");
		    continue;
		}
		queue.poll();
		try {
		    Thread.sleep(500);
		} catch (InterruptedException e) {
		}
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
