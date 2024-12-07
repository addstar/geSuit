package net.cubespace.geSuitTeleports.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.cubespace.geSuit.managers.CommandManager;
import net.cubespace.geSuitTeleports.managers.TeleportsManager;

public class BackCommand extends CommandManager<TeleportsManager> {


	public BackCommand(TeleportsManager manager) {
		super(manager);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		manager.sendPlayerBack(sender);
		return true;
	}

}
