package com.pgcraft.xuhc;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import com.pgcraft.spectatorplus.SpectateAPI;
import com.pgcraft.spectatorplus.SpectatorPlus;

public class UHC extends JavaPlugin {
	UHCListener listener = new UHCListener(this);
	public HashMap<Player, Location> store = new HashMap<Player, Location>();
	boolean compassForced = false;
	boolean compassAllowed = true;
	String prefix = ChatColor.DARK_BLUE + "[" + ChatColor.BLUE + "xUHC" + ChatColor.DARK_BLUE + "] ";
	SpectateAPI sp = null;
	Scoreboard mainScoreboard;
	
	// Config values
	String bossBarEnable, bossBarDisable, chatEnable, chatDisable, deathSound;
	int frequency, length, deathPitch, deathVolume;
	
	public void onEnable() {
		saveDefaultConfig();
		for (Player player : getServer().getOnlinePlayers()) {
			store.put(player, player.getCompassTarget());
		}
		// SpectatorPlus - Ensure the plugin is loaded
		Plugin spTest = Bukkit.getServer().getPluginManager().getPlugin("SpectatorPlus");
		if(spTest == null || !spTest.isEnabled()) return;
		sp = ((SpectatorPlus) spTest).getAPI();
		// ---
		getServer().getPluginManager().registerEvents(listener, this);
		getCommand("ct").setExecutor(forceCompass);
		new UHCTask(this).runTaskTimer(this, 10, 10);
		mainScoreboard = getServer().getScoreboardManager().getMainScoreboard();
		
		
		// Config initialise
		chatEnable = getConfig().getString("enable.chatmsg", ChatColor.GREEN + "Compass enabled!");
		chatEnable = ChatColor.translateAlternateColorCodes("&".charAt(0), chatEnable);
		chatDisable = getConfig().getString("disable.chatmsg", ChatColor.DARK_RED + "Compass disabled!");
		chatDisable = ChatColor.translateAlternateColorCodes("&".charAt(0), chatDisable);
		
		bossBarEnable = getConfig().getString("enable.bossbarmsg", ChatColor.GREEN + "Compass tracking %player%" + ChatColor.GREEN + "!");
		bossBarEnable = ChatColor.translateAlternateColorCodes("&".charAt(0), bossBarEnable);
		bossBarDisable = getConfig().getString("disable.bossbarmsg", ChatColor.DARK_RED + "Compass disabled!");
		bossBarDisable = ChatColor.translateAlternateColorCodes("&".charAt(0), bossBarDisable);
		
		frequency = getConfig().getInt("frequency", 180);
		length = getConfig().getInt("length", 10);
	}
	
	CommandExecutor forceCompass = new CommandExecutor() {
		@Override
		public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
			if (args.length == 0) {
				sender.sendMessage(prefix + ChatColor.GOLD + "Choose from /ct <force/enable/disable>");
			}
			if (args.length >= 1 && args[0].equals("force")) {
				compassForced = true;
				sender.sendMessage(prefix + ChatColor.GOLD + "Compass tracking was enabled by force!");
			}
			if (args.length >= 1 && args[0].equals("enable")) {
				compassAllowed = true;
				sender.sendMessage(prefix + ChatColor.GOLD + "Compass tracking will randomly enable when the time is after 12000ticks (sunset)");
			}
			if (args.length >= 1 && args[0].equals("disable")) {
				compassAllowed = false;
				sender.sendMessage(prefix + ChatColor.GOLD + "Compass tracking will not randomly enable!");
			}
			if (args.length >= 1 && args[0].equals("viewteam")) {
				sender.sendMessage(prefix + ChatColor.GOLD + "Your team is "+mainScoreboard.getPlayerTeam((Player) sender).getName());
				sender.sendMessage(ChatColor.GOLD+"Other players:");
				for (OfflinePlayer target : mainScoreboard.getPlayerTeam((Player) sender).getPlayers()) {
					sender.sendMessage(target.getName());
				}
			}
			return true;
		}
	};
}
