package net.cubespace.geSuit.managers;

import net.cubespace.geSuit.BukkitModule;

import org.bukkit.command.CommandExecutor;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 17/09/2018.
 */
public abstract class CommandManager<T extends DataManager> implements CommandExecutor {

    protected final T manager;
    protected final BukkitModule instance;

    public CommandManager(T manager, BukkitModule mod) {
        this.manager = manager;
        instance = mod;
    }

    public CommandManager(T manager) {
        this(manager, null);
    }

    public T getManager() {
        return manager;
    }
}

