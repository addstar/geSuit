package net.cubespace.geSuitTeleports.commands;

import net.cubespace.geSuit.managers.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.cubespace.geSuitTeleports.managers.TeleportsManager;
import org.bukkit.entity.Player;


public class TPAcceptCommand extends CommandManager<TeleportsManager> {

	public TPAcceptCommand(TeleportsManager manager) {
		super(manager);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Must be in game to use this command");
			return false;
		}
        @SuppressWarnings("deprecation") Player p = Bukkit.getPlayer(sender.getName());
		p.saveData();
		if (manager.getUtil().worldGuardTpAllowed(p.getLocation(), p) || sender.hasPermission("worldguard.teleports.allregions") || sender.hasPermission("worldgaurd.teleports.allregions"))
			manager.tpAccept(sender);
		else sender.sendMessage(ChatColor.RED + "This region will not allow teleporting");
		return true;
	}

}
