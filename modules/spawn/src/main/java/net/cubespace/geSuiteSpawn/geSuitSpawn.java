package net.cubespace.geSuiteSpawn;

import net.cubespace.geSuit.BukkitModule;
import net.cubespace.geSuiteSpawn.commands.GlobalSpawnCommand;
import net.cubespace.geSuiteSpawn.commands.ServerSpawnCommand;
import net.cubespace.geSuiteSpawn.commands.SetGlobalSpawnCommand;
import net.cubespace.geSuiteSpawn.commands.SetNewSpawnCommand;
import net.cubespace.geSuiteSpawn.commands.SetServerSpawnCommand;
import net.cubespace.geSuiteSpawn.commands.SetWorldSpawnCommand;
import net.cubespace.geSuiteSpawn.commands.DelWorldSpawnCommand;
import net.cubespace.geSuiteSpawn.commands.SpawnCommand;
import net.cubespace.geSuiteSpawn.commands.WarpSpawnCommand;
import net.cubespace.geSuiteSpawn.commands.WorldSpawnCommand;
import net.cubespace.geSuiteSpawn.listeners.SpawnListener;
import net.cubespace.geSuiteSpawn.listeners.SpawnMessageListener;
import net.cubespace.geSuiteSpawn.managers.SpawnManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class geSuitSpawn extends BukkitModule {

    private final SpawnManager manager;
	public geSuitSpawn() {
        super("spawns", true);
		manager = new SpawnManager(this);
    }

    private boolean checkforGTeleport() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("geSuitTeleports");
        if (plugin == null) {
            this.getLogger().info("NOTICE: geSuitTeleports is not loaded and cross server " +
                    "teleportation will fail to this server");
            return false;
        }
        return true;

    }

    @Override
    public void onEnable() {
        super.onEnable();
        checkforGTeleport();
    }

    public SpawnManager getManager() {
		return manager;
	}
    protected void registerCommands() {
        Objects.requireNonNull(getCommand("setnewspawn")).setExecutor(new SetNewSpawnCommand(manager));
        Objects.requireNonNull(getCommand("setworldspawn")).setExecutor(new SetWorldSpawnCommand(manager));
        Objects.requireNonNull(getCommand("delworldspawn")).setExecutor(new DelWorldSpawnCommand(manager));
        Objects.requireNonNull(getCommand("setserverspawn")).setExecutor(new SetServerSpawnCommand(manager));
        Objects.requireNonNull(getCommand("setglobalspawn")).setExecutor(new SetGlobalSpawnCommand(manager));
        Objects.requireNonNull(getCommand("spawn")).setExecutor(new SpawnCommand(manager, this));
        Objects.requireNonNull(getCommand("worldspawn")).setExecutor(new WorldSpawnCommand(manager, this));
        Objects.requireNonNull(getCommand("serverspawn")).setExecutor(new ServerSpawnCommand(manager, this));
        Objects.requireNonNull(getCommand("globalspawn")).setExecutor(new GlobalSpawnCommand(manager, this));
        Objects.requireNonNull(getCommand("warpspawn")).setExecutor(new WarpSpawnCommand(manager, this));
	}
	   
    protected void registerListeners() {
		registerPluginMessageListener(this, new SpawnMessageListener(this, manager));
		getServer().getPluginManager().registerEvents(new SpawnListener(manager, this), this);
	}

}
