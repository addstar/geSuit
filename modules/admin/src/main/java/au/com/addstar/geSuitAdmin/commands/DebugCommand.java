package au.com.addstar.geSuitAdmin.commands;

import au.com.addstar.geSuitAdmin.geSuitAdmin;

import net.cubespace.geSuit.BukkitModule;

import net.cubespace.geSuit.managers.LoggingManager;
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
            if (args[0].equalsIgnoreCase("info")) {
                commandSender.sendMessage("geSuit Debug mode is: " + BukkitModule.isDebug());
                commandSender.sendMessage("LoggingManager level: " + LoggingManager.getLevel() + " (" + LoggingManager.getLevel().intValue() + ")");
            } else {
                instance.setDebug(!BukkitModule.isDebug());
                commandSender.sendMessage("geSuit Debugging is " + BukkitModule.isDebug() + " for " + instance.getServer().getName());
            }
            return true;
        }
        return false;
    }
}
