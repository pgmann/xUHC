package com.pgcraft.xuhc;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class UHC extends JavaPlugin {
	UHCListener listener = new UHCListener(this);
	public HashMap<Player, Location> store = new HashMap<Player, Location>();
	boolean compassForced = false;
	String prefix = ChatColor.DARK_BLUE + "[" + ChatColor.BLUE + "xUHC" + ChatColor.DARK_BLUE + "] ";
	
	public void onEnable() {
		saveDefaultConfig();
		for (Player player : getServer().getOnlinePlayers()) {
			store.put(player, player.getCompassTarget());
		}
		getServer().getPluginManager().registerEvents(listener, this);
		getCommand("ct").setExecutor(forceCompass);
		new UHCTask(this).runTaskTimer(this, 10, 10);
	}
	
	CommandExecutor forceCompass = new CommandExecutor() {
		@Override
		public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
			if (args.length == 0) {
				sender.sendMessage(prefix + ChatColor.GOLD + "Compass force: /ct force");
			}
			if (args.length >= 1 && args[0].equals("force")) {
				compassForced = true;
				sender.sendMessage(prefix + ChatColor.GOLD + "Compass tracking was enabled by force!");
			}
			return true;
		}
	};
}
