package com.pgcraft.xuhc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import me.confuser.barapi.BarAPI;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
		for (Player target : plugin.getServer().getOnlinePlayers()) {
			for (Entry<Integer, ? extends ItemStack> entry : target.getInventory().all(Material.COMPASS).entrySet()) {
			    ItemMeta meta = entry.getValue().getItemMeta();
			    meta.setDisplayName(ChatColor.DARK_BLUE+"Tracking compass");
			    List<String> loreList = new ArrayList<String>();
			    loreList.add(ChatColor.DARK_GRAY + "This compass will point to enemies");		// Line 1
			    loreList.add(ChatColor.DARK_GRAY + "at random times throughout the night!");	// Line 2
			    meta.setLore(loreList);
			    entry.getValue().setItemMeta(meta);
			}
		}
		if((plugin.compassAllowed && plugin.getServer().getWorlds().get(0).getTime() > 12000) || plugin.compassForced || compassActive) {
			// Work out if compass will be active...
			if (new Random(System.currentTimeMillis()).nextInt(plugin.frequency*2)+1==plugin.frequency || plugin.compassForced) {
				plugin.compassForced = false;
				if (!compassActive) {
					compassActive = true;
					compassCount = plugin.length;
					for (Player target : plugin.getServer().getOnlinePlayers()) {
						target.playSound(target.getLocation(), Sound.WITHER_SPAWN, 2, 0);
					}
					plugin.getServer().broadcastMessage(plugin.chatEnable);
				}
			} else {
				if (compassActive) {
					compassCount--;
					if (compassCount <= 0) {
						compassActive = false;
						compassCount = 0;
						for (Player target : plugin.getServer().getOnlinePlayers()) {
							target.playSound(target.getLocation(), Sound.VILLAGER_IDLE, 2, 0);
						}
						plugin.getServer().broadcastMessage(plugin.chatDisable);
					}
				}
			}
			
			// Change compass target...
			if (compassActive) {
				// Find closest player to point to...
				for (Player player : plugin.getServer().getOnlinePlayers()) {
					Location myLocation = player.getLocation();
					Player closest = null;
					double closestDist = -1; // Impossible value, used to initialise
					
					for (Player target : plugin.getServer().getOnlinePlayers()) {
						if ((target.getLocation().distanceSquared(myLocation) < closestDist || closestDist < 0) && !target.getName().equals(player.getName())) {
							if (plugin.mainScoreboard.getPlayerTeam(target)==null || plugin.mainScoreboard.getPlayerTeam(player)==null || !plugin.mainScoreboard.getPlayerTeam(target).equals(plugin.mainScoreboard.getPlayerTeam(player))) {
								if (!plugin.sp.isSpectator(target)) { // Check if the player is a spectator (from SpectatorPlus)
									closest = target;
									closestDist = closest.getLocation().distanceSquared(myLocation);
								}
							}
							
						}
					}
					if (closest != null) {
						if (compassCount == plugin.length) {plugin.store.put(player, player.getCompassTarget());}
						player.setCompassTarget(closest.getLocation());
						String msg = plugin.bossBarEnable.replaceAll("%player%", closest.getDisplayName());
						BarAPI.setMessage(player, msg);
					} else {
						BarAPI.setMessage(player, ChatColor.DARK_RED + "Compass cannot find a target!");
					}
				}
			} else {
				// Point back to spawn
				for (Player player : plugin.getServer().getOnlinePlayers()) {
					player.setCompassTarget(plugin.store.get(player));
					BarAPI.setMessage(player, plugin.bossBarDisable);
					BarAPI.setHealth(player, 0); // clear the bar
				}
			}
		}
	}
}
