package net.cubespace.geSuiteSpawn.managers;

import net.cubespace.geSuit.BukkitModule;
import net.cubespace.geSuit.managers.DataManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;


public class SpawnManager extends DataManager {
    public static boolean HAS_SPAWNS = false;
    public static HashMap<String, Location> SPAWNS = new HashMap<>();

    public SpawnManager(BukkitModule instance) {
        super(instance);
    }

    public void sendPlayerToProxySpawn(CommandSender sender, boolean silent) {

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SendToProxySpawn" );
            out.writeUTF( sender.getName() );
            out.writeBoolean( silent );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);
    }

    public void setNewPlayerSpawn(CommandSender sender) {
        Player p = ( Player ) sender;
        Location l = p.getLocation();

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SetNewPlayerSpawn" );
            out.writeUTF( sender.getName() );
            out.writeUTF( l.getWorld().getName() );
            out.writeDouble( l.getX() );
            out.writeDouble( l.getY() );
            out.writeDouble( l.getZ() );
            out.writeFloat( l.getYaw() );
            out.writeFloat( l.getPitch() );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);

    }

    public void setProxySpawn(CommandSender sender) {
        Player p = ( Player ) sender;
        Location l = p.getLocation();

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SetProxySpawn" );
            out.writeUTF( sender.getName() );
            out.writeUTF( l.getWorld().getName() );
            out.writeDouble( l.getX() );
            out.writeDouble( l.getY() );
            out.writeDouble( l.getZ() );
            out.writeFloat( l.getYaw() );
            out.writeFloat( l.getPitch() );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);
    }

    public void setServerSpawn(CommandSender sender) {
        Player p = ( Player ) sender;
        Location l = p.getLocation();
        p.getWorld().setSpawnLocation( l.getBlockX(), l.getBlockY(), l.getBlockZ() );
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SetServerSpawn" );
            out.writeUTF( sender.getName() );
            out.writeUTF( l.getWorld().getName() );
            out.writeDouble( l.getX() );
            out.writeDouble( l.getY() );
            out.writeDouble( l.getZ() );
            out.writeFloat( l.getYaw() );
            out.writeFloat( l.getPitch() );
            out.writeBoolean( hasServerSpawn() );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);

    }

    public void setWorldSpawn(CommandSender sender) {
        Player p = ( Player ) sender;
        Location l = p.getLocation();
        p.getWorld().setSpawnLocation( l.getBlockX(), l.getBlockY(), l.getBlockZ() );
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SetWorldSpawn" );
            out.writeUTF( sender.getName() );
            out.writeUTF( l.getWorld().getName() );
            out.writeDouble( l.getX() );
            out.writeDouble( l.getY() );
            out.writeDouble( l.getZ() );
            out.writeFloat( l.getYaw() );
            out.writeFloat( l.getPitch() );
            out.writeBoolean( hasWorldSpawn( p.getWorld() ) );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);

    }

    public void sendPlayerToServerSpawn(CommandSender sender) {
        Player p = ( Player ) sender;
        p.teleport( getServerSpawn() );
    }

    public void sendPlayerToWorldSpawn(CommandSender sender) {
        Player p = ( Player ) sender;
        Location l = getWorldSpawn( p.getWorld() );
        if ( l == null ) {
            p.teleport( p.getWorld().getSpawnLocation() );
        } else {
            p.teleport( getWorldSpawn( p.getWorld() ) );
        }
    }

    public void delWorldSpawn(CommandSender sender) {
        Player p = ( Player ) sender;
        Location l = p.getLocation();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "DelWorldSpawn" );
            out.writeUTF( sender.getName() );
            out.writeUTF( l.getWorld().getName() );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);
    }

    public void getSpawns() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "GetSpawns" );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        instance.sendMessage(b);
    }

    public static boolean hasWorldSpawn( World w ) {
        return SPAWNS.containsKey( w.getName() );
    }

    public static Location getWorldSpawn( World w ) {
        return SPAWNS.get( w.getName() );
    }

    public static boolean hasServerSpawn() {
        return SPAWNS.containsKey( "server" );
    }

    public static Location getServerSpawn() {
        return SPAWNS.get( "server" );
    }

    public void sendPlayerToSpawn(CommandSender sender) {
        Player p = ( Player ) sender;
        if (p.getBedSpawnLocation() != null && p.hasPermission("gesuit.spawns.spawn.bed")){
            p.teleport(p.getBedSpawnLocation());
        } else if (hasWorldSpawn(p.getWorld()) && p.hasPermission("gesuit.spawns.spawn.world")) {
            p.teleport( getWorldSpawn( p.getWorld() ) );
        } else if (hasServerSpawn() && p.hasPermission("gesuit.spawns.spawn.server")) {
            p.teleport( getServerSpawn() );
        } else if ( p.hasPermission( "gesuit.spawns.spawn.global" ) ) {
            sendPlayerToProxySpawn(p, false);
        }
    }

    public static void addSpawn( String name, String world, double x, double y, double z, float yaw, float pitch ) {
        SPAWNS.put( name, new Location( Bukkit.getWorld( world ), x, y, z, yaw, pitch ) );

    }

    public static void delWorldSpawn( String worldName) {
        SPAWNS.remove(worldName);

    }

    public void sendPlayerToArgSpawn(CommandSender sender, String spawn, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SendToArgSpawn" );
            out.writeUTF( sender.getName() );
            out.writeUTF( spawn );
            out.writeUTF( server );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);
    }

    public void sendVersion() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SendVersion" );
            out.writeUTF(ChatColor.RED + "Spawns - " + ChatColor.GOLD + instance.getDescription().getVersion());

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);
    }
}
