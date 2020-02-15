package com.ishland.bukkit.QQMinecraft.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.java_websocket.exceptions.WebsocketNotConnectedException;

public class MessageThread extends Thread {
	public static final int LENGTH_HARD_LIMIT = 2560;

	public static BlockingDeque<String> queue = new LinkedBlockingDeque<>();

	private boolean isStopping = false;

	private MessageHandler handler;

	public MessageThread(MessageHandler handler) {
		super();
		this.handler = handler;
	}

	public void run() {
		handler.plugin.getLogger().info("start");
		for (int i = 0; i < 60 * 10 && !Launcher.isCompletelyStarted; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		if (!Launcher.isCompletelyStarted) {
			handler.plugin.getLogger().warning("MSGThread did not start");
		}
		try {
			while (!isStopping && handler.plugin.isEnabled()) {
				if (!handler.isReady())
					continue;
				String result = "";
				List<String> arr = new ArrayList<>();
				int currentLength = 0;
				try {
					for (String current = queue.poll(100, TimeUnit.MILLISECONDS); current != null; current = queue
							.poll(100, TimeUnit.MILLISECONDS)) {
						if (current.length() > MessageThread.LENGTH_HARD_LIMIT && arr.size() == 0) {
							arr.add(current.substring(0, MessageThread.LENGTH_HARD_LIMIT));
							queue.putFirst(current.substring(MessageThread.LENGTH_HARD_LIMIT));
							currentLength += MessageThread.LENGTH_HARD_LIMIT;
							break;
						}
						currentLength += current.length();
						if (currentLength > MessageThread.LENGTH_HARD_LIMIT) {
							queue.putFirst(current);
							break;
						}
						arr.add(current);
					}
				} catch (InterruptedException e) {
					reInsert(arr);
				}
				for (String str : arr)
					result += str + "\n";
				if (result.isEmpty())
					continue;
				result = result.substring(0, result.length() - 1);
				try {
					handler.sendNow(result);
				} catch (WebsocketNotConnectedException e) {
					handler.plugin.getLogger().warning("Error while sending message: not connected, retrying");
					reInsert(arr);
					continue;
				}

				APIResponse response = BlockingLock.waitForResult();
				if (response == null) {
					handler.plugin.getLogger().warning("Error while sending message: no response, retrying");
					reInsert(arr);
					continue;
				}
				if (response.retcode != 0 && response.retcode != 1) {
					handler.plugin.getLogger().warning(
							"Error while sending message: " + response.status + "(" + response.retcode + "), retrying");
					reInsert(arr);
					continue;
				}
			}
			handler.plugin.getLogger().info("Shutdown");
			handler = null;
			Launcher.msgHandler = null;
		} catch (Throwable t) {
			handler.plugin.getLogger().log(java.util.logging.Level.WARNING, "Error", t);
		}
	}

	private void reInsert(List<String> list) {
		Collections.reverse(list);
		for (String str : list)
			try {
				queue.putFirst(str);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
