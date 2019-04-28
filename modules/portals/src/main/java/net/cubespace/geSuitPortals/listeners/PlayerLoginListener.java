package net.cubespace.geSuitPortals.listeners;

import net.cubespace.geSuitPortals.geSuitPortals;
import net.cubespace.geSuitPortals.managers.PortalsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class PlayerLoginListener implements Listener {

    private final geSuitPortals instance;
    private final PortalsManager manager;

    public PlayerLoginListener(geSuitPortals instance, PortalsManager manager) {
        this.instance = instance;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerConnect(PlayerJoinEvent e) {
        if (!PortalsManager.RECEIVED) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> {
                if (!PortalsManager.RECEIVED) {
                    PortalsManager.RECEIVED = true;
                    manager.requestPortals();
                }

            }, 10L);
        }
        if (PortalsManager.pendingTeleports.containsKey(e.getPlayer().getName())) {
            Location l = PortalsManager.pendingTeleports.get(e.getPlayer().getName());
            PortalsManager.pendingTeleports.remove(e.getPlayer().getName());
            e.getPlayer().teleport(l);
        }
    }

}
