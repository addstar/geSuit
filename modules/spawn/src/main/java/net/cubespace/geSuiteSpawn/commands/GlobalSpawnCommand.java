package net.cubespace.geSuiteSpawn.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.cubespace.geSuit.managers.CommandManager;
import net.cubespace.geSuiteSpawn.geSuitSpawn;
import net.cubespace.geSuiteSpawn.managers.SpawnManager;

public class GlobalSpawnCommand extends CommandManager<SpawnManager> {


    public GlobalSpawnCommand(SpawnManager manager, geSuitSpawn p) {
        super(manager, p);
    }

    @Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

            /* Console Commands */
            if ( !( sender instanceof Player ) ) {
                if (args.length != 1) {
                    return false;
                }

                Player player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid username or player is offline.");
                    return true;
                }

                manager.sendPlayerToProxySpawn(player, false);
                return true;
            }

            /* Player Commands */
            final Player player = (Player) sender;
            
            if (args.length == 1) {
                if (!player.hasPermission("gesuit.warps.command.warp.other")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to do this.");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Invalid username or player is offline.");
                    return true;
                }

                manager.sendPlayerToProxySpawn(target, false);
                return true;
            }

        if (!player.hasPermission("gesuit.warps.bypass.delay")) {
            final Location lastLocation = player.getLocation();

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Teleportation will commence in &c3 seconds&6. Don't move."));
            Bukkit.getServer().getScheduler().runTaskLater(instance, () -> {
                if (player.isOnline()) {
                    if ((lastLocation == null) || (lastLocation.getBlock() == null))
                        return;

                    if (lastLocation.getBlock().equals(player.getLocation().getBlock())) {
                        player.sendMessage(ChatColor.GOLD + "Teleportation commencing...");
                        manager.sendPlayerToProxySpawn(player, false);
                    } else {
                        player.sendMessage(ChatColor.RED + "Teleportation aborted because you moved.");
                    }
                }
            }, 60L);
            return true;
        } else {
            manager.sendPlayerToProxySpawn(player, false);
            return true;
        }
	}

}
