package net.cubespace.geSuiteSpawn.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.cubespace.geSuit.BukkitModule;
import net.cubespace.geSuit.managers.CommandManager;
import net.cubespace.geSuiteSpawn.managers.SpawnManager;

public class WarpSpawnCommand extends CommandManager<SpawnManager> {

    public WarpSpawnCommand(SpawnManager manager, BukkitModule mod) {
        super(manager, mod);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        /* Console Commands */
        if ( !( sender instanceof Player ) ) {
            if (args.length == 0 || args.length == 1) {
                return false;
            }

            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Invalid username or player is offline.");
                return true;
            }

            // warpspawn SpawnName
            if (args.length == 2) {
                manager.sendPlayerToArgSpawn(player, args[1], "");
                return true;
            }

            // warpspawn SpawnName Server
            if (args.length == 3) {
                manager.sendPlayerToArgSpawn(player, args[1], args[2]);
                return true;
            }

            return false;
        }

        /* Player Commands */
        final Player player = (Player) sender;

        if (args.length == 0) {
            return false;
        }

        // warpspawn SpawnName
        if (args.length == 1) {
            sendToSpawn(player, args[0], "");
            return true;
        }

        // warpspawn (Player) SpawnName Server
        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                if (!player.hasPermission("gesuit.warps.command.warp.other")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to do this.");
                    return true;
                }
                manager.sendPlayerToArgSpawn(target, args[1], "");
                return true;
            }
            sendToSpawn(player, args[0], args[1]);
            return true;
        }

        // warpspawn Player SpawnName Server
        if (args.length == 3) {
            if (!player.hasPermission("gesuit.warps.command.warp.other")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to do this.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Invalid username or player is offline.");
                return true;
            }

            manager.sendPlayerToArgSpawn(target, args[1], args[2]);
            return true;
        }

        return false;
    }

    private void sendToSpawn(final Player player, final String spawn, final String server) {
        if (!player.hasPermission("gesuit.warps.bypass.delay")) {
            final Location lastLocation = player.getLocation();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Teleportation will commence in &c3 seconds&6. Don't move."));
            Bukkit.getServer().getScheduler().runTaskLater(instance, () -> {
                if (player.isOnline()) {
                    if ((lastLocation == null) || (lastLocation.getBlock() == null))
                        return;

                    if (lastLocation.getBlock().equals(player.getLocation().getBlock())) {
                        player.sendMessage(ChatColor.GOLD + "Teleportation commencing...");
                        manager.sendPlayerToArgSpawn(player, spawn, server);
                    } else {
                        player.sendMessage(ChatColor.RED + "Teleportation aborted because you moved.");
                    }
                }
            }, 60L);
        } else {
            manager.sendPlayerToArgSpawn(player, spawn, server);
        }
    }

}
