package net.cubespace.geSuitTeleports.managers;

import net.cubespace.geSuit.BukkitModule;
import net.cubespace.geSuit.managers.DataManager;
import net.cubespace.geSuitTeleports.geSuitTeleports;
import net.cubespace.geSuitTeleports.utils.LocationUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;


public class TeleportsManager extends DataManager {
    public static final HashMap<String, Player> pendingTeleports = new HashMap<>();
    public static final HashMap<String, Location> pendingTeleportLocations = new HashMap<>();
    public static final HashSet<Player> ignoreTeleport = new HashSet<>();
    public static final HashSet<Player> administrativeTeleport = new HashSet<>();

    static final HashMap<Player, Location> lastLocation = new HashMap<>();

    public LocationUtil getUtil() {
        return util;
    }

    private final LocationUtil util;

    public TeleportsManager(BukkitModule instance) {
        super(instance);
        util = new LocationUtil((geSuitTeleports) instance);
    }

    public static void RemovePlayer(Player player) {
    	pendingTeleports.remove(player.getName());
    	pendingTeleportLocations.remove(player.getName());
    	ignoreTeleport.remove(player);
    	lastLocation.remove(player);
        administrativeTeleport.remove(player);
    }

    public void tpAll(CommandSender sender, String targetPlayer) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "TpAll" );
            out.writeUTF( sender.getName() );
            out.writeUTF( targetPlayer );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);

    }

    public void tpaRequest(CommandSender sender, String targetPlayer) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "TpaRequest" );
            out.writeUTF( sender.getName() );
            out.writeUTF( targetPlayer );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.getInstance().sendMessage(b);
    }

    public void tpaHereRequest(CommandSender sender, String targetPlayer) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "TpaHereRequest" );
            out.writeUTF( sender.getName() );
            out.writeUTF( targetPlayer );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);

    }

    public void tpAccept(final CommandSender sender) {
        final Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            //noinspection deprecation
            player = Bukkit.getPlayer(sender.getName());
        }

        player.saveData();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("TpAccept");
            out.writeUTF(sender.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        instance.sendMessage(b);
    }

    public void tpDeny(String sender) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "TpDeny" );
            out.writeUTF( sender );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);

    }

    public void finishTPA(final Player player, final String target) {
        if (!player.hasPermission("gesuit.teleports.bypass.delay")) {
            lastLocation.put(player, player.getLocation());
            player.sendMessage(geSuitTeleports.teleportinitiated);

            instance.getServer().getScheduler().runTaskLater(instance, () -> {
                Location loc = lastLocation.get(player);
                lastLocation.remove(player);
                if (player.isOnline()) {
                    if ((loc != null) && (loc.getBlock().equals(player.getLocation().getBlock()))) {

                        player.sendMessage(geSuitTeleports.teleporting);
                        player.saveData();
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(b);
                        doTeleportToPlayer(out, player, target);
                        instance.sendMessage(b);
                    } else {
                        player.sendMessage(geSuitTeleports.aborted);
                    }
                }
            }, 60L);
        } else {
            player.saveData();
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            doTeleportToPlayer(out, player, target);
            instance.sendMessage(b);
        }
    }

    private void doTeleportToPlayer(DataOutputStream out, Player player, String target) {
        try {
            out.writeUTF("TeleportToPlayer");
            out.writeUTF(player.getName());
            out.writeUTF(player.getName());
            out.writeUTF(target);
            out.writeBoolean(false);
            out.writeBoolean(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doLeaveServer(Player p) {
        if (p == null) {
            return;
        }
        
        sendTeleportBackLocation(p, false);
    }

    public void sendDeathBackLocation(Player p) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "PlayersDeathBackLocation" );
            out.writeUTF( p.getName() );
            Location l = p.getLocation();
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

    public void sendTeleportBackLocation(Player p, boolean empty) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "PlayersTeleportBackLocation" );
            out.writeUTF( p.getName() );
            Location l = p.getLocation();
            out.writeUTF( l.getWorld().getName() );
            out.writeDouble( l.getX() );
            out.writeDouble( l.getY() );
            out.writeDouble( l.getZ() );
            out.writeFloat( l.getYaw() );
            out.writeFloat( l.getPitch() );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        //todo the boolean was being passed to the message sender which was ignoring it..need to
        // evaluate its importance
        instance.sendMessage(b);
    }

    @SuppressWarnings("deprecation")
    public void sendPlayerBack(final CommandSender sender) {
        final Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            player = Bukkit.getPlayer(sender.getName());
        }
        if (!player.hasPermission("gesuit.teleports.bypass.delay")) {
            lastLocation.put(player, player.getLocation());
            player.sendMessage(geSuitTeleports.teleportinitiated);

            instance.getServer().getScheduler().runTaskLater(instance, () -> {
                Location loc = lastLocation.get(player);
                lastLocation.remove(player);
                if (player.isOnline()) {
                    if ((loc != null) && (loc.getBlock().equals(player.getLocation().getBlock()))) {
                        player.sendMessage(geSuitTeleports.teleporting);
                        player.saveData();
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(b);
                        doSendBack(out, sender);
                        instance.sendMessage(b);
                    } else {
                        player.sendMessage(geSuitTeleports.aborted);
                    }
                }
            }, 60L);
        } else {
            player.saveData();
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            doSendBack(out, sender);
            instance.sendMessage(b);
        }
    }

    private static void doSendBack(DataOutputStream out, CommandSender sender) {
        try {
            out.writeUTF("SendPlayerBack");
            out.writeUTF(sender.getName());
            out.writeBoolean(sender.hasPermission("gesuit.teleports.back.death"));
            out.writeBoolean(sender.hasPermission("gesuit.teleports.back.teleport"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toggleTeleports(String name) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "ToggleTeleports" );
            out.writeUTF( name );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);
    }

    @SuppressWarnings("deprecation")
    public void teleportPlayerToPlayer(final String player, String target) {
        Player p = Bukkit.getPlayer( player );
        Player t = Bukkit.getPlayer( target );
        if(t.hasPermission("worldguard.teleports.allregions")) {
            administrativeTeleport.add(p);
        }
        if ( p != null ) {
            p.teleport( t );
        } else {
            pendingTeleports.put( player, t );
            //clear pending teleport if they dont connect
            Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> pendingTeleports.remove(player), 100L);
        }
    }

    public void teleportPlayerToLocation(final String player, String world, double x, double y, double z, float yaw, float pitch) {
        World w = Bukkit.getWorld( world );
        Location t;
        
        if (w != null) {
            t = new Location( w, x, y, z, yaw, pitch );
        } else {
            w = Bukkit.getWorlds().get(0);
            t = w.getSpawnLocation();
        }
        //noinspection deprecation
        Player p = Bukkit.getPlayer( player );
        if ( p != null ) {
            //Check if Block is safe
            if (util.isBlockUnsafe(t.getWorld(), t.getBlockX(), t.getBlockY(), t.getBlockZ())) {
                try {
                    Location l = util.getSafeDestination(p, t);
                    if (l != null) {
                    	p.teleport(l);
                    } else {
                        p.sendMessage(geSuitTeleports.unsafe_location);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                p.teleport(t);
            }
        } else {
            pendingTeleportLocations.put( player, t );
            //clear pending teleport if they dont connect
            Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> pendingTeleportLocations.remove(player), 100L);
        }
    }

    public void teleportToPlayer(final CommandSender sender, final String playerName, final String target) {
        //noinspection deprecation
        final Player player = Bukkit.getPlayer(sender.getName());

        if (!player.hasPermission("gesuit.teleports.bypass.delay")) {
            lastLocation.put(player, player.getLocation());
            player.sendMessage(geSuitTeleports.teleportinitiated);

            instance.getServer().getScheduler().runTaskLater(instance, () -> {
                Location loc = lastLocation.get(player);
                lastLocation.remove(player);
                if (player.isOnline()) {
                    if ((loc != null) && (loc.getBlock().equals(player.getLocation().getBlock()))) {
                        player.sendMessage(geSuitTeleports.teleporting);
                        player.saveData();
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(b);
                        doTeleportToPlayer(out, sender, playerName, target);
                        instance.sendMessage(b);
                    } else {
                        player.sendMessage(geSuitTeleports.aborted);
                    }
                }
            }, 60L);
        } else {
            player.saveData();
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            doTeleportToPlayer(out, sender, playerName, target);
            instance.sendMessage(b);
        }
    }

    private static void doTeleportToPlayer(DataOutputStream out, CommandSender sender, String playerName, String target) {
        try {
            out.writeUTF("TeleportToPlayer");
            out.writeUTF(sender.getName());
            out.writeUTF(playerName);
            out.writeUTF(target);
            out.writeBoolean(sender.hasPermission("gesuit.teleports.tp.silent"));
            out.writeBoolean(sender.hasPermission("gesuit.teleports.tp.bypass"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void teleportToLocation(String player, String server, String world, Double x, Double y, Double z) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "TeleportToLocation" );
            out.writeUTF( player );
            out.writeUTF( server );
            out.writeUTF( world );
            out.writeDouble( x );
            out.writeDouble( y );
            out.writeDouble( z );
            out.writeFloat( 0 );    // yaw
            out.writeFloat( 0 );    // pitch
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        instance.sendMessage(b);

    }

    public void teleportToLocation(String player, String server, String world, Double x, Double y, Double z, float yaw, float pitch) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "TeleportToLocation" );
            out.writeUTF( player );
            out.writeUTF( server );
            out.writeUTF( world );
            out.writeDouble( x );
            out.writeDouble( y );
            out.writeDouble( z );
            out.writeFloat( yaw );
            out.writeFloat( pitch );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        instance.sendMessage(b);

    }
}
