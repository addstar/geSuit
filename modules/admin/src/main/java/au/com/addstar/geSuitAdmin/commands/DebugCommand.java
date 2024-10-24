package au.com.addstar.geSuitAdmin.commands;

import au.com.addstar.geSuitAdmin.geSuitAdmin;

import net.cubespace.geSuit.BukkitModule;

import net.cubespace.geSuit.managers.LoggingManager;
import net.cubespace.geSuitBans.geSuitBans;
import net.cubespace.geSuitHomes.geSuitHomes;
import net.cubespace.geSuitPortals.geSuitPortals;
import net.cubespace.geSuitTeleports.geSuitTeleports;
import net.cubespace.geSuitWarps.geSuitWarps;
import net.cubespace.geSuiteSpawn.geSuitSpawn;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 8/08/2017.
 */
public class DebugCommand implements CommandExecutor{

    private final geSuitAdmin instance;
    public DebugCommand(geSuitAdmin plugin) {
        instance = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(commandSender.hasPermission("geSuit.admin")) {
            if ((args.length > 0) && (args[0].equalsIgnoreCase("info"))) {
                commandSender.sendMessage("geSuit Debug mode is: " + BukkitModule.isDebug());
                commandSender.sendMessage("LoggingManager level: " + LoggingManager.getLevel() + " (" + LoggingManager.getLevel().intValue() + ")");
            } else {
                boolean debug = !BukkitModule.isDebug();
                instance.setDebug(debug);
                commandSender.sendMessage(instance.getName() + " Debugging is " + debug);

                geSuitBans bans = (geSuitBans) Bukkit.getPluginManager().getPlugin("geSuitBans");
                if (bans != null) {
                    bans.setDebug(debug);
                    commandSender.sendMessage(bans.getName() + " Debugging is " + debug);
                }
                geSuitHomes homes = (geSuitHomes) Bukkit.getPluginManager().getPlugin("geSuitHomes");
                if (homes != null) {
                    homes.setDebug(debug);
                    commandSender.sendMessage(homes.getName() + " Debugging is " + debug);
                }
                geSuitPortals portals = (geSuitPortals) Bukkit.getPluginManager().getPlugin("geSuitPortals");
                if (portals != null) {
                    portals.setDebug(debug);
                    commandSender.sendMessage(portals.getName() + " Debugging is " + debug);
                }
                geSuitSpawn spawns = (geSuitSpawn) Bukkit.getPluginManager().getPlugin("geSuitSpawn");
                if (spawns != null) {
                    spawns.setDebug(debug);
                    commandSender.sendMessage(spawns.getName() + " Debugging is " + debug);
                }
                geSuitTeleports teleports = (geSuitTeleports) Bukkit.getPluginManager().getPlugin("geSuitTeleports");
                if (teleports != null) {
                    teleports.setDebug(debug);
                    commandSender.sendMessage(teleports.getName() + " Debugging is " + debug);
                }
                geSuitWarps warps = (geSuitWarps) Bukkit.getPluginManager().getPlugin("geSuitWarps");
                if (warps != null) {
                    warps.setDebug(debug);
                    commandSender.sendMessage(warps.getName() + " Debugging is " + debug);
                }
            }
            return true;
        }
        return false;
    }
}
