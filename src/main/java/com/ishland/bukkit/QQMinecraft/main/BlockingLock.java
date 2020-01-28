package com.ishland.bukkit.QQMinecraft.main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingLock {

    public static BlockingQueue<APIResponse> queue = new LinkedBlockingQueue<>();

    public static void submitResult(APIResponse response) {
	while (true) {
	    try {
		queue.put(response);
	    } catch (InterruptedException e) {
		continue;
	    }
	    break;
	}
    }

    public static APIResponse waitForResult() {
	APIResponse result = null;
	try {
	    result = queue.poll(5, TimeUnit.SECONDS);
	} catch (InterruptedException e) {
	}
	return result;
    }
}
