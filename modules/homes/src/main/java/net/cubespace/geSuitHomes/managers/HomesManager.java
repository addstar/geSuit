package net.cubespace.geSuitHomes.managers;

import net.cubespace.geSuit.BukkitModule;
import net.cubespace.geSuit.managers.DataManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;

public class HomesManager extends DataManager {

    public HomesManager(BukkitModule instance) {
        super(instance);
    }

    public void deleteHome(CommandSender sender, String home) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "DeleteHome" );
            out.writeUTF( sender.getName() );
            out.writeUTF( home );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);
    }

    public void deleteOtherHime(CommandSender sender, String player, String home) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "DeleteOtherPlayerHome" );
            out.writeUTF( sender.getName() );
            out.writeUTF( player );
            out.writeUTF( home );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);

    }

    public void sendHome(CommandSender sender, String home) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("SendPlayerHome");
                out.writeUTF(sender.getName());
                out.writeUTF(home);
            } catch (IOException e) {
                e.printStackTrace();
            }
        instance.sendMessage(b);
    }

    public void sendOtherHome(CommandSender sender, String player, String home) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SendOtherPlayerHome" );
            out.writeUTF( sender.getName() );
            out.writeUTF( player );
            out.writeUTF( home );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);

    }

    public void getHomesList(CommandSender sender) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "GetHomesList" );
            out.writeUTF( sender.getName() );
            out.writeInt( getGlobalHomesLimit( sender ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);

    }

    public void getOtherHomesList(CommandSender sender, String player) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "GetOtherHomesList" );
            out.writeUTF( sender.getName() );
            out.writeUTF( player );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);

    }

    public void importHomes(CommandSender sender) {
        String path = "plugins/Essentials/userdata";
        File folder = new File( path );

        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));

        int userCount = 0;
        int userHomeCount = 0;
        int homeCount = 0;
        if(listOfFiles != null) {
            for (File data : listOfFiles) {
                userCount++;
                FileConfiguration user = YamlConfiguration.loadConfiguration(data);
                if (user.contains("homes")) {
                    userHomeCount++;
                    Set<String> homedata = user.getConfigurationSection("homes").getKeys(false);
                    if (homedata != null) {
                        for (String homes : homedata) {
                            Location loc = new Location(Bukkit.getWorld(user.getString("homes." + homes + ".world")), user.getDouble("homes." + homes + ".x"), user.getDouble("homes." + homes + ".y"), user.getDouble("homes." + homes + ".z"), (float) user.getDouble("homes." + homes + ".yaw"), (float) user.getDouble("homes." + homes + ".pitch"));
                            setHome(data.getName().substring(0, data.getName().length() - 4), homes, loc);
                            homeCount++;
                        }
                    }
                }
            }
        }
        sender.sendMessage( ChatColor.GOLD + "Out of " + userCount + "users, " + userHomeCount + " had homes" );
        sender.sendMessage( ChatColor.GOLD + "Homes imported: " + homeCount );

    }

    public void reloadHomes(CommandSender sender) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "ReloadHomes" );
            out.writeUTF( sender.getName() );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        instance.sendMessage(b);

    }

    public void setHome(CommandSender sender, String home) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        Location l = ( ( Player ) sender ).getLocation();
        try {
            out.writeUTF( "SetPlayersHome" );
            out.writeUTF( sender.getName() );
            out.writeInt( getServerHomesLimit( sender ) );
            out.writeInt( getGlobalHomesLimit( sender ) );
            out.writeUTF( home );
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

    public void setHome(String sender, String home, Location l) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SetPlayersHome" );
            out.writeUTF( sender );
            out.writeInt( 100 );
            out.writeInt( 300 );
            out.writeUTF( home );
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

    public static int getServerHomesLimit( CommandSender p ) {
        int max = 0;

        int maxlimit = 100;

        if ( p.hasPermission( "gesuit.homes.limits.server.*" ) || p.hasPermission( "gesuit.homes.limits.*" ) ) {
            return maxlimit;
        } else {
            for ( int ctr = 0; ctr < maxlimit; ctr++ ) {
                if ( p.hasPermission( "gesuit.homes.limits.server." + ctr ) ) {
                    max = ctr;
                }
            }

        }

        return max;
    }

    public static int getGlobalHomesLimit( CommandSender p ) {
        int max = 0;

        int maxlimit = 300;

        if ( p.hasPermission( "gesuit.homes.limits.global.*" ) || p.hasPermission( "gesuit.homes.limits.*" ) ) {
            return maxlimit;
        } else {
            for ( int ctr = 0; ctr < maxlimit; ctr++ ) {
                if ( p.hasPermission( "gesuit.homes.limits.global." + ctr ) ) {
                    max = ctr;
                }
            }

        }
        return max;
    }
}
