package net.cubespace.geSuitPortals;


import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.cubespace.geSuit.BukkitModule;
import net.cubespace.geSuitPortals.commands.DeletePortalCommand;
import net.cubespace.geSuitPortals.commands.ListPortalsCommand;
import net.cubespace.geSuitPortals.commands.SetPortalCommand;
import net.cubespace.geSuitPortals.listeners.AntiBurnListener;
import net.cubespace.geSuitPortals.listeners.PhysicsListener;
import net.cubespace.geSuitPortals.listeners.PlayerLoginListener;
import net.cubespace.geSuitPortals.listeners.PlayerMoveListener;
import net.cubespace.geSuitPortals.listeners.PortalsMessageListener;
import net.cubespace.geSuitPortals.managers.PortalsManager;
import net.cubespace.geSuitPortals.objects.Portal;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class geSuitPortals extends BukkitModule {

    public static WorldEditPlugin WORLDEDIT = null;
    private final PortalsManager manager;

    public geSuitPortals() {
        super("portals",true);
        manager = new PortalsManager(this);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        loadWorldEdit();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (ArrayList<Portal> list : PortalsManager.PORTALS.values()) {
            for (Portal p : list) {
                p.clearPortal();
            }
        }
    }

    private void loadWorldEdit() {
        WORLDEDIT = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
    }

    protected void registerCommands() {
        getCommand("setportal").setExecutor(new SetPortalCommand(manager));
        getCommand("delportal").setExecutor(new DeletePortalCommand(manager));
        getCommand("portals").setExecutor(new ListPortalsCommand(manager));
    }
    
    protected void registerChannels() {
        registerOutgoingChannel();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }
    
    protected void registerListeners() {
        registerPluginMessageListener(this, new PortalsMessageListener(manager, this));

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(manager, this), this);
        getServer().getPluginManager().registerEvents(new PhysicsListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(this, manager), this);
        getServer().getPluginManager().registerEvents(new AntiBurnListener(this), this);
    }
}
