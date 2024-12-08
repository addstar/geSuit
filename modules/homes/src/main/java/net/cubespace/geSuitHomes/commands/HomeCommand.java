package net.cubespace.geSuitHomes.commands;

import net.cubespace.geSuit.managers.CommandManager;
import net.cubespace.geSuit.managers.LoggingManager;
import net.cubespace.geSuit.utils.Utilities;
import net.cubespace.geSuitHomes.geSuitHomes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.cubespace.geSuitHomes.managers.HomesManager;

import org.bukkit.entity.Player;

public class HomeCommand extends CommandManager<HomesManager> {

	public HomeCommand(HomesManager manager, geSuitHomes plugin) {
		super(manager, plugin);
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command command,
			String label, final String[] args) {
		if (sender instanceof Player) {
			if (args.length == 0) {
				manager.getHomesList(sender);
			} else {
				final Player player = ((Player)sender).getPlayer();
				if (player == null) {
					sender.sendMessage(ChatColor.RED + "Player not available online.");
					return true;
				}
				final String pname;
				final String homename;
				if (args[0].contains(":")) {
					String[] parts = args[0].split(":");
					pname = parts[0];
					homename = (parts.length > 1) ? parts[1] : null;
				} else {
					homename = args[0];
					pname = null;
				}

				// Check perms if listing player homes or attempting to teleport to one
				if ((pname != null) && (!sender.hasPermission("gesuit.homes.commands.homes.other"))) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to do this.");
					return true;
				}

				if ((homename == null) && (pname != null)) {
					// Syntax: "/home player:home"
					manager.getOtherHomesList(sender, pname);
					return true;
				} else {
					if (!player.hasPermission("gesuit.homes.bypass.delay")) {
						if (!player.hasPermission("gesuit.homes.bypass.void") && (player.getVelocity().getY() < 0)) {
							LoggingManager.warn("Player " + player.getName() + " tried to teleport to a home while falling (velocity: " + player.getVelocity().getY() + ")");
							player.sendMessage(ChatColor.RED + "You cannot teleport while falling.");
							return true;
						}

						final Location lastLocation = player.getLocation();
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Teleportation will commence in &c3 seconds&6. Don't move."));

                        instance.getServer().getScheduler().runTaskLater(instance, () -> {
                            if (player.isOnline()) {
                                if (lastLocation.getBlock().equals(player.getLocation().getBlock())) {
                                    player.sendMessage(ChatColor.GOLD + "Teleportation commencing...");
                                    player.saveData();
                                    if (pname == null) {
                                        manager.sendHome(sender, homename);
                                    } else {
                                        manager.sendOtherHome(sender, pname, homename);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Teleportation aborted because you moved.");
								}
							}
						}, 60L);
					} else {
						player.saveData();
						if (pname == null) {
							// Teleport to own player home
							manager.sendHome(sender, homename);
						} else {
							// Teleport player to other player home
							manager.sendOtherHome(sender, pname, homename);
						}
					}
				}
			}

		} else {
			sender.sendMessage(ChatColor.RED + "This command cannot be run from console.");
			return false;
		}
		return true;
	}

}