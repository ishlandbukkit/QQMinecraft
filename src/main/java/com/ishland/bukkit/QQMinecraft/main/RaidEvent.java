package com.ishland.bukkit.QQMinecraft.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidStopEvent.Reason;
import org.bukkit.event.raid.RaidTriggerEvent;

public class RaidEvent implements Listener {
    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent event) {
	Launcher.msgHandler.send(event.getPlayer() + "触发了袭击！ "
		+ event.getPlayer().getLocation().toString());
    }

    @EventHandler
    public void onRaidSpawnWave(RaidSpawnWaveEvent event) {
	Launcher.msgHandler
		.send("新一波袭击发生于 " + event.getRaid().getLocation().toString());
    }

    @EventHandler
    public void onRaidStop(RaidStopEvent event) {
	if (event.getReason() == Reason.FINISHED)
	    return;
	String reason = "";
	switch (event.getReason()) {
	case UNSPAWNABLE:
	    reason = "无法生成新掠夺者";
	    break;
	case NOT_IN_VILLAGE:
	    reason = "掠夺位置不再是村庄";
	    break;
	case PEACE:
	    reason = "难度调为和平";
	    break;
	case TIMEOUT:
	    reason = "掠夺时间过长，未获得结果";
	    break;
	default:
	    break;
	}
	Launcher.msgHandler.send("袭击位于 "
		+ event.getRaid().getLocation().toString() + " 停止： " + reason);
    }

    @EventHandler
    public void onRaidFinish(RaidFinishEvent event) {
	String str = "袭击位于 " + event.getRaid().getLocation().toString()
		+ " 完成：\n";
	str += "村庄英雄：";
	for (Player p : event.getWinners()) {
	    str += p.getName() + " ";
	}
	str += "\n";
	Launcher.msgHandler.send(str);

    }
}
