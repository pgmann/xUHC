package com.pgcraft.xuhc;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class UHCListener implements Listener {
	UHC plugin;
	public UHCListener(UHC plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.store.put(event.getPlayer(), event.getPlayer().getCompassTarget());
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		for (Player target : plugin.getServer().getOnlinePlayers()) {
			target.playSound(target.getLocation(), Sound.WITHER_DEATH, 1F, 1F);
		}
	}
}
