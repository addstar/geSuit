package net.cubespace.geSuitTeleports.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.command.util.Logging;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import net.cubespace.geSuit.managers.LoggingManager;
import net.cubespace.geSuitTeleports.geSuitTeleports;
import net.cubespace.geSuitTeleports.managers.TeleportsManager;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The type Location util.
 */
public class LocationUtil {

    private final geSuitTeleports instance;

    public LocationUtil(geSuitTeleports instance) {
        this.instance = instance;
    }

    /**
     * The type Vector 3 d.
     */
    public static class Vector3D
    {
        /**
         * The X.
         */
        private final int x;
        /**
         * The Y.
         */
        private final int y;
        /**
         * The Z.
         */
        private final int z;

        /**
         * Instantiates a new Vector 3 d.
         *
         * @param x the x
         * @param y the y
         * @param z the z
         */
        public Vector3D(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * Get x int.
         *
         * @return the int
         */
        protected int getX() {
            return x;
        }

        /**
         * Get y int.
         *
         * @return the int
         */
        protected int getY() {
            return y;
        }

        /**
         * Gets z.
         *
         * @return the z
         */
        protected int getZ() {
            return z;
        }
    }

    /**
     * The constant RADIUS.
     */
    public static final int RADIUS = 16;
    /**
     * The constant VOLUME.
     */
    public static final Vector3D[] VOLUME;

    static
    {
        List<Vector3D> pos = new ArrayList<>();
        for (int x = -RADIUS; x <= RADIUS; x++)
        {
            for (int y = -RADIUS; y <= RADIUS; y++)
            {
                for (int z = -RADIUS; z <= RADIUS; z++)
                {
                    pos.add(new Vector3D(x, y, z));
                }
            }
        }
        pos.sort(Comparator.comparingInt(a -> (a.getX() * a.getX() + a.getY() * a.getY() + a.getZ() * a
                .getZ())));
        VOLUME = pos.toArray(new Vector3D[0]);
    }

    /**
     * Is block above air boolean.
     *
     * @param world the world
     * @param x     the x
     * @param y     the y
     * @param z     the z
     * @return the boolean
     */
    static boolean isBlockAboveAir(final World world, final int x, final int y, final int z) {
        return y > world.getMaxHeight() || !world.getBlockAt(x, y - 1,
                z).getType().isSolid();
    }

    /**
     * Is block unsafe boolean.
     *
     * @param world the world
     * @param x     the x
     * @param y     the y
     * @param z     the z
     * @return the boolean
     */
    public boolean isBlockUnsafe(final World world, final int x, final int y, final int z) {
        return isBlockDamaging(world, x, y, z) || isBlockAboveAir(world, x, y, z);

    }
    
    /**
     * Is block damaging boolean.
     *
     * @param world the world
     * @param x     the x
     * @param y     the y
     * @param z     the z
     *
     * @return the boolean
     */
    public boolean isBlockDamaging(final World world, final int x, final int y, final int z) {
        final Block below = world.getBlockAt(x, y - 1, z);
        Material magma = null;
        try {
            if (instance.getServer().getVersion().equals("1.13"))
                magma = Material.getMaterial("MAGMA_BLOCK");
        } catch (Exception e) {
            magma = Material.getMaterial("MAGMA");
        }
        if (below.getType() == Material.LAVA || below.getType() == magma) {
            return true;
        }
        if (below.getType() == Material.FIRE) {
            return true;
        }
        return (world.getBlockAt(x, y, z).getType()).isSolid() || (world
                .getBlockAt(x, y + 1, z).getType().isSolid());
    }

    /**
     * Gets rounded destination.
     *
     * @param loc the loc
     *
     * @return the rounded destination
     */
// Not needed if using getSafeDestination(loc)
    public static Location getRoundedDestination(final Location loc) {
        final World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = (int) Math.round(loc.getY());
        int z = loc.getBlockZ();
        return new Location(world, x + 0.5, y, z + 0.5, loc.getYaw(), loc.getPitch());
    }
    
    /**
     * Gets safe destination.
     *
     * @param player the player
     * @param loc    the loc
     *
     * @return the safe destination
     */
    public Location getSafeDestination(final Player player, final Location loc) {
        if (loc.getWorld().equals(player.getWorld())
                && ((player.getGameMode() == GameMode.CREATIVE) || (player.isFlying()))) {
            return getRoundedDestination(loc);
        }

        return getSafeDestination(loc);
    }
    
    /**
     * Gets safe destination.
     *
     * @param loc the loc
     *
     * @return the safe destination
     */
    public Location getSafeDestination(final Location loc) {
        if (loc == null || loc.getWorld() == null) {
            return null;
        }

        final World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = (int) Math.round(loc.getY());
        int z = loc.getBlockZ();
        final int origX = x;
        final int origY = y;
        final int origZ = z;
        while (isBlockAboveAir(world, x, y, z)) {
            y -= 1;
            if (y < -64) {
                y = origY;
                break;
            }
        }
        if (isBlockUnsafe(world, x, y, z)) {
            x = Math.round(loc.getX()) == origX ? x - 1 : x + 1;
            z = Math.round(loc.getZ()) == origZ ? z - 1 : z + 1;
        }
        int i = 0;
        while (isBlockUnsafe(world, x, y, z)) {
            i++;
            if (i >= VOLUME.length) {
                x = origX;
                y = origY + RADIUS;
                z = origZ;
                break;
            }
            x = origX + VOLUME[i].getX();
            y = origY + VOLUME[i].getY();
            z = origZ + VOLUME[i].getZ();
        }
        while (isBlockUnsafe(world, x, y, z)) {
            y += 1;
            if (y >= world.getMaxHeight()) {
                x += 1;
                break;
            }
        }
        while (isBlockUnsafe(world, x, y, z)) {
            y -= 1;
            if (y <= 1) {
                x += 1;
                y = world.getHighestBlockYAt(x, z);
                if (x - 48 > loc.getBlockX()) {
                    return null;
                }
            }
        }
        return new Location(world, x + 0.5, y, z + 0.5, loc.getYaw(), loc.getPitch());
    }
    
    /**
     * Should fly boolean.
     *
     * @param loc the loc
     *
     * @return the boolean
     */
    public boolean shouldFly(Location loc) {
        final World world = loc.getWorld();
        final int x = loc.getBlockX();
        int y = (int) Math.round(loc.getY());
        final int z = loc.getBlockZ();
        int count = 0;
        while (isBlockUnsafe(world, x, y, z) && y >= -65) {
            y--;
            count++;
            if (count > 2) {
                return true;
            }
        }

        return y < -65;
    }
    
    /**
     * World guard tp allowed boolean.
     *
     * @param l the l
     * @param p the p
     *
     * @return the boolean
     */
    public boolean worldGuardTpAllowed(Location l, Player p) {
        boolean result = true;
        Logger log = instance.getLogger();
        LoggingManager.debug("Checking if WG allows TP. Status of Plugin:" + geSuitTeleports.worldGuarded);//Todo remove after debug
        if (geSuitTeleports.worldGuarded) {
            try {
                WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionQuery query = container.createQuery();
                ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(l));
                if (!set.isVirtual()) {//VirtualSet indicates that there is no region protection to check
                    for (ProtectedRegion region : set) {
                        Set<String> flags = region.getFlag(Flags.BLOCKED_CMDS);
                        if (flags != null) {
                            LoggingManager.debug("Blocked Commands Found:" + flags.toString());
                            for (String cmd : flags) {
                                if (geSuitTeleports.deny_Teleport.contains(cmd)) {
                                    LoggingManager.debug("Test for " + cmd + " was true.");
                                    if (p.hasPermission("worldguard.teleports.allregions") || p.hasPermission("worldgaurd.teleports.allregions") || TeleportsManager.administrativeTeleport.contains(p)) {
                                        p.sendMessage(geSuitTeleports.tp_admin_bypass);
                                        LoggingManager.debug("Player:" + p.getDisplayName() + ":" + geSuitTeleports.tp_admin_bypass + "Location: Region=" + region.getId());
                                        TeleportsManager.administrativeTeleport.remove(p);
                                        result = true;
                                    } else {
                                        p.sendMessage(geSuitTeleports.location_blocked);
                                        result = false;
                                    }
                                }
                            }
                            LoggingManager.debug("Tests on List:" + geSuitTeleports.deny_Teleport.toString() + " completed");
                        } else {
                            LoggingManager.debug("FLAGS was null");
                        }
                    }
                } else {
                    LoggingManager.debug("Region set was virtual");
                }
            } catch (NoClassDefFoundError e) {
                e.printStackTrace();
                result = true;
            }
        }
        LoggingManager.debug("World guard check for TP completed: Player=" + p.getDisplayName() + " Location=(" + l.toString() + ") Region TP Allowed=" + result);
        return result;
    }

    // Check if there's ground below the player
    public static boolean hasGroundBelow(Location location, int maxDistance) {
        // Only check a certain distance below the player (either maxDistance or the bottom of the world)
        int yrange = Math.min(location.getBlockY() - location.getWorld().getMinHeight(), maxDistance);
        for (int y = location.getBlockY() - 1; y >= yrange; y--) {
            Block block = location.getWorld().getBlockAt(location.getBlockX(), y, location.getBlockZ());
            if (!block.getType().isAir()) { // Found a solid block
                LoggingManager.debug("  [hasGroundBelow] Found " + block.getType() + " at " + block.getLocation().toString());
                return true; // Allow teleport
            }
        }
        LoggingManager.debug("  [hasGroundBelow] Found NO ground below " + location.toString());
        return false; // No solid block found
    }
}
