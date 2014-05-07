package com.pgcraft.xuhc;

import java.util.Random;

import me.confuser.barapi.BarAPI;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UHCTask extends BukkitRunnable {
	UHC plugin;
	boolean compassActive = false;
	int compassCount = 0;
	public UHCTask(UHC plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		if(plugin.getServer().getWorlds().get(0).getTime() > 12000 || plugin.compassForced) {
			// Work out if compass will be active...
			if (new Random(System.currentTimeMillis()).nextInt(plugin.getConfig().getInt("frequency", 180))+1==plugin.getConfig().getInt("frequency", 180) || plugin.compassForced) {
				plugin.compassForced = false;
				if (!compassActive) {
					compassActive = true;
					compassCount = plugin.getConfig().getInt("length", 10);
					plugin.getServer().broadcastMessage(plugin.getConfig().getString("enable.chatmsg", ChatColor.GREEN + "Compass enabled!"));
				}
			} else {
				if (compassActive) {
					compassCount--;
					if (compassCount <= 0) {
						compassActive = false;
						compassCount = 0;
						plugin.getServer().broadcastMessage(plugin.getConfig().getString("disable.chatmsg", ChatColor.DARK_RED + "Compass disabled!"));
					}
				}
			}
			
			// Change compass target...
			if (compassActive) {
				// Find closest player to point to...
				for (Player player : plugin.getServer().getOnlinePlayers()) {
					Player[] players = plugin.getServer().getOnlinePlayers();
					Location myLocation = player.getLocation();
					Location closest = null;
					Player closestPlayer = null;
					double closestDist = 0;
					
					for (Player target : players) {
						if ((target.getLocation().distanceSquared(myLocation) < closestDist || closestDist == 0) && !target.getName().equals(player.getName())) {
							closest = target.getLocation();
							closestDist = closest.distanceSquared(myLocation);
							closestPlayer = target;
						}
					}
					if (closestPlayer != null) {
						if (compassCount == plugin.getConfig().getInt("length", 10)) {plugin.store.put(player, player.getCompassTarget());}
						player.setCompassTarget(closest);
						BarAPI.setMessage(player, plugin.getConfig().getString("enable.bossbarmsg", ChatColor.GREEN + "Compass tracking " + closestPlayer.getDisplayName() + ChatColor.GREEN + "!"));
					} else {
						BarAPI.setMessage(player, ChatColor.DARK_RED + "Compass cannot find a target!");
					}
				}
			} else {
				// Point back to spawn
				for (Player player : plugin.getServer().getOnlinePlayers()) {
					player.setCompassTarget(plugin.store.get(player));
					BarAPI.setMessage(player, plugin.getConfig().getString("disable.bossbarmsg", ChatColor.DARK_RED + "Compass disabled!"));
					BarAPI.setHealth(player, 0); // clear the bar
				}
			}
		}
	}
}
