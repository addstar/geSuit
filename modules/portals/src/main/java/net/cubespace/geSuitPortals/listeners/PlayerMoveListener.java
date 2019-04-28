package net.cubespace.geSuitPortals.listeners;

import net.cubespace.geSuitPortals.geSuitPortals;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.util.Vector;

import net.cubespace.geSuitPortals.managers.PortalsManager;
import net.cubespace.geSuitPortals.objects.Portal;

public class PlayerMoveListener implements Listener {
    private PortalsManager manager;
    private geSuitPortals instance;

    public PlayerMoveListener(PortalsManager manager, geSuitPortals instance) {
        this.manager = manager;
        this.instance = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void PlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.hasMetadata("NPC")) return; // Ignore NPCs
        Block t = e.getTo().getBlock();
        Block f = e.getFrom().getBlock();
        if (f.equals(t)) {
            return;
        }
        if (!PortalsManager.PORTALS.containsKey(t.getWorld())) {
            return;
        }
        for (Portal p : PortalsManager.PORTALS.get(t.getWorld())) {
            if (p.isBlockInPortal(t)) {
                if (player.hasPermission("gesuit.portals.portal.*") || player.hasPermission("gesuit.portals.portal." + p.getName())) {
                    manager.teleportPlayer(player, p);
                    Vector unitVector = e.getFrom().toVector().subtract(e.getTo().toVector()).normalize();
                    Location l = player.getLocation();
                    l.setYaw(l.getYaw() + 180);
                    player.teleport(l);
                    player.setVelocity(unitVector.multiply(0.3));
                } else {
                    player.sendMessage(ChatColor.RED + "Sorry! " + ChatColor.GOLD + "You do not have permission to use this portal.");
                }
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void PlayerMove(PlayerPortalEvent e) {
        if (e.getPlayer().hasMetadata("NPC")) return; // Ignore NPCs
        Block b = null;
        Block f = e.getFrom().getBlock();
        if (!PortalsManager.PORTALS.containsKey(f.getWorld())) {
            return;
        }
        Material portal;
        Material enderPortal;
        portal = Material.getMaterial("NETHER_PORTAL");
        enderPortal = Material.getMaterial("END_PORTAL");

        if (f.getRelative(BlockFace.NORTH).getType() == portal || f.getRelative(BlockFace.NORTH).getType() == enderPortal) {
            b = f.getRelative(BlockFace.NORTH);
        } else if (f.getRelative(BlockFace.EAST).getType() == portal || f.getRelative(BlockFace.EAST).getType() == enderPortal) {
            b = f.getRelative(BlockFace.EAST);
        } else if (f.getRelative(BlockFace.SOUTH).getType() == portal || f.getRelative(BlockFace.SOUTH).getType() == enderPortal) {
            b = f.getRelative(BlockFace.SOUTH);
        } else if (f.getRelative(BlockFace.WEST).getType() == portal || f.getRelative(BlockFace.WEST).getType() == enderPortal) {
            b = f.getRelative(BlockFace.WEST);
        } else {
            return;
        }
        for (Portal p : PortalsManager.PORTALS.get(f.getWorld())) {
            if (p.isBlockInPortal(b)) {
                e.setCancelled(true);
            }
        }
    }

}
