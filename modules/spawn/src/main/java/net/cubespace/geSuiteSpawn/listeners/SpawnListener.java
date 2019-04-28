package net.cubespace.geSuiteSpawn.listeners;

import net.cubespace.geSuiteSpawn.geSuitSpawn;
import net.cubespace.geSuiteSpawn.managers.SpawnManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnListener implements Listener {

    private final SpawnManager manager;
    private final geSuitSpawn instance;

    public SpawnListener(SpawnManager manager, geSuitSpawn instance) {
        this.manager = manager;
        this.instance = instance;
    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void playerLogin( PlayerJoinEvent e ) {
		if (e.getPlayer().hasMetadata("NPC")) return; // Ignore NPCs
        if ( !SpawnManager.HAS_SPAWNS ) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> {
                if (!SpawnManager.HAS_SPAWNS) {
                    manager.getSpawns();
                    SpawnManager.HAS_SPAWNS = true;
                }
            }, 10L );
        }

        Player p = e.getPlayer();
        if (!p.hasPlayedBefore() && !p.isOp()) {
            if ( SpawnManager.hasWorldSpawn( p.getWorld() ) && p.hasPermission( "gesuit.spawns.new.world" ) ) {
                manager.sendPlayerToWorldSpawn(p);
            } else if ( SpawnManager.hasServerSpawn() && p.hasPermission( "gesuit.spawns.new.server" ) ) {
                manager.sendPlayerToServerSpawn(p);
            } else if ( p.hasPermission( "gesuit.spawns.new.global" ) ) {
                manager.sendPlayerToProxySpawn(p, true);
            }
        }

    }

    @EventHandler( priority = EventPriority.NORMAL, ignoreCancelled=true )
    public void playerRespawn( PlayerRespawnEvent e ) {
		if (e.getPlayer().hasMetadata("NPC")) return; // Ignore NPCs
        Player p = e.getPlayer();
        if ( p.getBedSpawnLocation() != null && p.hasPermission( "gesuit.spawns.spawn.bed" ) ) {
            e.setRespawnLocation( p.getBedSpawnLocation() );
        } else if ( SpawnManager.hasWorldSpawn( p.getWorld() ) && p.hasPermission( "gesuit.spawns.spawn.world" ) ) {
            e.setRespawnLocation( SpawnManager.getWorldSpawn( p.getWorld() ) );
        } else if ( SpawnManager.hasServerSpawn() && p.hasPermission( "gesuit.spawns.spawn.server" ) ) {
            e.setRespawnLocation( SpawnManager.getServerSpawn() );
        } else if ( p.hasPermission( "gesuit.spawns.spawn.global" ) ) {
            if ( SpawnManager.hasWorldSpawn( p.getWorld() ) ) {
                e.setRespawnLocation( SpawnManager.getWorldSpawn( p.getWorld() ) );
            } else if ( SpawnManager.hasServerSpawn() ) {
                e.setRespawnLocation( SpawnManager.getServerSpawn() );
            }

            manager.sendPlayerToProxySpawn(p, true);
        }
    }

}
