package net.cubespace.geSuitHomes;

import net.cubespace.geSuit.BukkitModule;
import net.cubespace.geSuitHomes.commands.DelHomeCommand;
import net.cubespace.geSuitHomes.commands.HomeCommand;
import net.cubespace.geSuitHomes.commands.HomesCommand;
import net.cubespace.geSuitHomes.commands.ImportHomesCommand;
import net.cubespace.geSuitHomes.commands.SetHomeCommand;
import net.cubespace.geSuitHomes.managers.HomesManager;

public class geSuitHomes extends BukkitModule {

    private final HomesManager manager;

	public geSuitHomes() {
		super("homes",true);
		manager = new HomesManager(this);
	}
	
	protected void registerCommands() {
		getCommand("sethome").setExecutor(new SetHomeCommand(manager));
		getCommand("home").setExecutor(new HomeCommand(manager, this));
		getCommand("delhome").setExecutor(new DelHomeCommand(manager));
		getCommand("homes").setExecutor(new HomesCommand(manager));
		getCommand("importhomes").setExecutor(new ImportHomesCommand(manager));
	}
	
	protected void registerListeners() {
	}



}
