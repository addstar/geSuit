package net.cubespace.geSuitPortals.listeners;

import java.util.ArrayList;
import java.util.List;

import net.cubespace.geSuitPortals.geSuitPortals;
import net.cubespace.geSuitPortals.managers.PortalsManager;
import net.cubespace.geSuitPortals.objects.Portal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class AntiBurnListener implements Listener {

    private final geSuitPortals instance;

    public AntiBurnListener(geSuitPortals instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamageEvent(EntityDamageEvent event) {
        if (!PortalsManager.RECEIVED || !(event.getEntity() instanceof Player) ||
                (!event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) &&
                        !event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) &&
                        !event.getCause().equals(EntityDamageEvent.DamageCause.FIRE))) return;

        Location location = event.getEntity().getLocation();
        int FIRE_SPREAD_RADIUS = 2;
        event.setCancelled(check(event.getEntity(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), FIRE_SPREAD_RADIUS));
    }

    private boolean check(Entity entity, int x, int y, int z, int radius) {
        return checkIfInPortal(entity.getWorld().getBlockAt(x, y, z)) ||
                (radius > 0 && (check(entity, x + 1, y, z, radius - 1)
                        || check(entity, x - 1, y, z, radius - 1)
                        || check(entity, x, y + 1, z, radius - 1)
                        || check(entity, x, y - 1, z, radius - 1)
                        || check(entity, x, y, z + 1, radius - 1)
                        || check(entity, x, y, z - 1, radius - 1)));
    }

    private boolean checkIfInPortal(Block block) {
        if (block == null) return false;
        if (canIgnite(block)) {
            List<Portal> portals = PortalsManager.PORTALS.get(block.getWorld());
            if (portals != null) {
                for (Portal p : PortalsManager.PORTALS.get(block.getWorld())) {
                    if (p.isBlockInPortal(block)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canIgnite(Block block) {
        Material mat = block.getType();
        List<Material> ignitable = new ArrayList<>();
        ignitable.add(Material.getMaterial("LAVA"));
        return (ignitable.contains(mat));
    }
}
