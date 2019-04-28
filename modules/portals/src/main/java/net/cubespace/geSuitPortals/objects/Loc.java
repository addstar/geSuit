package net.cubespace.geSuitPortals.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class Loc {

    final String world;
    final double x;
    final double y;
    final double z;


    public Loc(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public Block getBlock() {
        return getLocation().getBlock();
    }

    public boolean equals(Location loc) {
        if (!loc.getWorld().getName().equals(world)) {
            return false;
        }
        if (loc.getBlockX() != x) {
            return false;
        }
        if (loc.getBlockY() != y) {
            return false;
        }
        return !(loc.getBlockZ() != z);
    }

    public boolean equals(Block block) {
        Location loc = block.getLocation();
        if (!loc.getWorld().getName().equals(world)) {
            return false;
        }
        if (loc.getBlockX() != x) {
            return false;
        }
        if (loc.getBlockY() != y) {
            return false;
        }
        return !(loc.getBlockZ() != z);
    }
}
