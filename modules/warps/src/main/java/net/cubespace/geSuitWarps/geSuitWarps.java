package net.cubespace.geSuitWarps;

import net.cubespace.geSuit.BukkitModule;
import net.cubespace.geSuitWarps.commands.DeleteWarpCommand;
import net.cubespace.geSuitWarps.commands.ListWarpsCommand;
import net.cubespace.geSuitWarps.commands.SetWarpCommand;
import net.cubespace.geSuitWarps.commands.SetWarpDescCommand;
import net.cubespace.geSuitWarps.commands.SilentWarpCommand;
import net.cubespace.geSuitWarps.commands.WarpCommand;
import net.cubespace.geSuitWarps.managers.WarpsManager;

public class geSuitWarps extends BukkitModule {

    private final WarpsManager manager;
	public geSuitWarps() {
		super("warps", true);
        manager = new WarpsManager(this);
	}
	
	protected void registerCommands() {
        getCommand("warp").setExecutor(new WarpCommand(manager, this));
        getCommand("warps").setExecutor(new ListWarpsCommand(manager));
        getCommand("setwarp").setExecutor(new SetWarpCommand(manager));
        getCommand("setwarpdesc").setExecutor(new SetWarpDescCommand(manager));
        getCommand("silentwarp").setExecutor(new SilentWarpCommand(manager, this));
        getCommand("delwarp").setExecutor(new DeleteWarpCommand(manager));
	}
	
	protected void registerListeners() {
	}
}
