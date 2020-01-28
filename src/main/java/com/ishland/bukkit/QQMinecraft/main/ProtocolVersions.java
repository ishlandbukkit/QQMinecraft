package com.ishland.bukkit.QQMinecraft.main;

import java.util.HashMap;
import java.util.Map;

public class ProtocolVersions {
    public static Map<Integer, String> map = new HashMap<>();
    static {
	map.put(4, "1.7.2/1.7.4/1.7.5");
	map.put(5, "1.7.6/1.7.7/1.7.8/1.7.9/1.7.10");
	map.put(47, "1.8.x");
	map.put(107, "1.9");
	map.put(108, "1.9.1");
	map.put(109, "1.9.2");
	map.put(110, "1.9.3/1.9.4");
	map.put(210, "1.10.x");
	map.put(315, "1.11");
	map.put(316, "1.11.1/1.11.2");
	map.put(335, "1.12");
	map.put(338, "1.12.1");
	map.put(340, "1.12.2");
	map.put(393, "1.13");
	map.put(401, "1.13.1");
	map.put(404, "1.13.2");
	map.put(477, "1.14");
	map.put(480, "1.14.1");
	map.put(485, "1.14.2");
	map.put(490, "1.14.3");
	map.put(498, "1.14.4");
	map.put(565, "1.15-pre1");
	map.put(566, "1.15-pre2");
	map.put(567, "1.15-pre3");
    }
}
