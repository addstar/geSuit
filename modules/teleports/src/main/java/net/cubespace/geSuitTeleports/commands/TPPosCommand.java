package net.cubespace.geSuitTeleports.commands;


import net.cubespace.geSuit.managers.CommandManager;
import net.cubespace.geSuitTeleports.geSuitTeleports;
import net.cubespace.geSuitTeleports.managers.TeleportsManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPPosCommand extends CommandManager<TeleportsManager> {

    public TPPosCommand(TeleportsManager manager) {
        super(manager);
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {

        if ( !( sender instanceof Player ) ) {

            /* Console Commands */

            if (args.length < 4 || args.length > 8) {
                return false;
            }

            @SuppressWarnings("deprecation") Player p = Bukkit.getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage(geSuitTeleports.invalid_offline + args[0]);
                return true;
            }

            // tppos Player X Y Z
            if ( args.length == 4 ) {
                // Send player to specified coordinates
                // Supports coordinates relative to the player
                if (!validCoordinates(sender, args, 1, true)) {
                    return true;
                }
                sender.sendMessage(geSuitTeleports.sending + p.getName() + geSuitTeleports.to + args[1] + " " + args[2] + " " + args[3]);
                p.saveData();
                p.teleport( new Location(
                        p.getWorld(),
                        getCoordinate(p.getLocation().getX(), args[1]),
                        getCoordinate(p.getLocation().getY(), args[2]),
                        getCoordinate(p.getLocation().getZ(), args[3]),
                        p.getLocation().getYaw(),
                        p.getLocation().getPitch() ) );
                return true;
            }

            // tppos Player X Y Z World
            if ( args.length == 5 ) {
                // Send player to the given coordinates of the given world (on this server)
                // Cannot use relative coordinates since potentially switching worlds
                if (!validCoordinates(sender, args, 1, false)) {
                    return true;
                }
                sender.sendMessage(geSuitTeleports.sending + p.getName() + geSuitTeleports.to + args[1] + " " + args[2] + " " + args[3] + geSuitTeleports.in_world + args[4]);
                p.saveData();
                p.teleport(new Location(
                        Bukkit.getWorld(args[4]),
                        getCoordinate(p.getLocation().getX(), args[1]),
                        getCoordinate(p.getLocation().getY(), args[2]),
                        getCoordinate(p.getLocation().getZ(), args[3]),
                        p.getLocation().getYaw(),
                        p.getLocation().getPitch() ) );
                return true;
            }

            // tppos Player X Y Z Yaw Pitch
            if ( args.length == 6 ) {
                // Send player to specified coordinates
                // Supports coordinates relative to the player
                if (!validCoordinates(sender, args, 1, true)) {
                    return true;
                }
                if (!validYawPitch(sender, args, 4)) {
                    return true;
                }
                sender.sendMessage(geSuitTeleports.sending + p.getName() + geSuitTeleports.to + args[1] + " " + args[2] + " " + args[3]);
                p.saveData();
                p.teleport( new Location(
                        p.getWorld(),
                        getCoordinate(p.getLocation().getX(), args[1]),
                        getCoordinate(p.getLocation().getY(), args[2]),
                        getCoordinate(p.getLocation().getZ(), args[3]),
                        Float.valueOf(args[4]),
                        Float.valueOf(args[5]) ) );
                return true;
            }

            // tppos Player X Y Z Yaw Pitch World
            if ( args.length == 7 ) {
                // Send player to specified coordinates
                // Cannot use relative coordinates since potentially switching worlds
                if (!validCoordinates(sender, args, 1, false)) {
                    return true;
                }
                if (!validYawPitch(sender, args, 4)) {
                    return true;
                }
                sender.sendMessage(geSuitTeleports.sending + p.getName() + geSuitTeleports.to + args[1] + " " + args[2] + " " + args[3] + geSuitTeleports.in_world + args[6]);
                p.saveData();
                p.teleport(new Location(
                        Bukkit.getWorld(args[6]),
                        getCoordinate(p.getLocation().getX(), args[1]),
                        getCoordinate(p.getLocation().getY(), args[2]),
                        getCoordinate(p.getLocation().getZ(), args[3]),
                        Float.valueOf(args[4]),
                        Float.valueOf(args[5]) ) );
                return true;
            }

            // tppos Player X Y Z Yaw Pitch World Server
            if ( args.length == 8 ) {
                // Send player to specified coordinates
                // Cannot use relative coordinates since potentially switching worlds
                if (!validCoordinates(sender, args, 1, false)) {
                    return true;
                }
                if (!validYawPitch(sender, args, 4)) {
                    return true;
                }
                sender.sendMessage(geSuitTeleports.sending + p.getName() + geSuitTeleports.to + args[1] + " " + args[2] + " " + args[3] + geSuitTeleports.in_world + args[6] + geSuitTeleports.on_server + args[7]);
                p.saveData();
                manager.teleportToLocation(
                        p.getName(),                 // Player to teleport
                        args[7],                     // Server
                        args[6],                     // World
                        getCoordinate(p.getLocation().getX(), args[1]),
                        getCoordinate(p.getLocation().getY(), args[2]),
                        getCoordinate(p.getLocation().getZ(), args[3]),
                        Float.valueOf(args[4]),
                        Float.valueOf(args[5]) );
                return true;
            }

            return false;
        }

        /* Player Commands */

        if (args.length < 3 || args.length > 8) {
            return false;
        }

        @SuppressWarnings("deprecation") Player p = Bukkit.getPlayer(sender.getName());

        // tppos X Y Z
        if ( args.length == 3 ) {
            // Teleport yourself to the specified coordinates (of this world)
            // Supports relative coordinates
            if (!validCoordinates(sender, args, 0, true)) {
                return true;
            }
            p.saveData();
            manager.teleportToLocation(
                    p.getName(),                 // Player to teleport
                    "",                          // Server
                    p.getWorld().getName(),      // World
                    getCoordinate(p.getLocation().getX(), args[0]),
                    getCoordinate(p.getLocation().getY(), args[1]),
                    getCoordinate(p.getLocation().getZ(), args[2]),
                    p.getLocation().getYaw(),
                    p.getLocation().getPitch() );
            return true;
        }

        @SuppressWarnings("deprecation") Player p2 = Bukkit.getPlayer(args[0]);

        if ( p2 != null && !p.hasPermission("gesuit.teleports.tp.others")) {
            p.sendMessage(geSuitTeleports.no_perms_for_teleporting_others);
            return true;
        }

        // tppos Player X Y Z
        // tppos X Y Z World
        if ( args.length == 4) {
            if ( p2 != null ) {
                // Teleport another player to the given coordinates (of this world)
                // Supports coordinates relative to the sender
                if (!validCoordinates(sender, args, 1, true)) {
                    return true;
                }
                p2.saveData();
                manager.teleportToLocation(
                        p2.getName(),                // Player to teleport
                        "",                          // Server
                        p.getWorld().getName(),      // World
                        getCoordinate(p.getLocation().getX(), args[1]),
                        getCoordinate(p.getLocation().getY(), args[2]),
                        getCoordinate(p.getLocation().getZ(), args[3]),
                        p2.getLocation().getYaw(),
                        p2.getLocation().getPitch() );
            } else {
                // Teleport yourself to the specified coordinates of the given world (on this server)
                // Cannot use relative coordinates since potentially switching worlds
                if (!validCoordinates(sender, args, 0, false)) {
                    return true;
                }
                p.saveData();
                manager.teleportToLocation(
                        p.getName(),                 // Player to teleport
                        "",                          // Server
                        args[3],                     // World
                        Double.valueOf(args[0]),
                        Double.valueOf(args[1]),
                        Double.valueOf(args[2]),
                        p.getLocation().getYaw(),
                        p.getLocation().getPitch() );
            }
            return true;
        }

        // tppos Player X Y Z World
        // tppos X Y Z Yaw Pitch
        if ( args.length == 5 ) {
            if ( p2 != null ) {
                // Teleport another player to the given coordinates of the given world (on this server)
                // Cannot use relative coordinates since potentially switching worlds
                if (!validCoordinates(sender, args, 1, false)) {
                    return true;
                }
                p2.saveData();
                manager.teleportToLocation(
                        p2.getName(),                // Player to teleport
                        "",                          // Server
                        args[4],                     // World
                        Double.valueOf(args[1]),
                        Double.valueOf(args[2]),
                        Double.valueOf(args[3]),
                        p2.getLocation().getYaw(),
                        p2.getLocation().getPitch() );
            } else {
                // Teleport yourself to the given coordinates (with yaw and pitch)
                // Supports coordinates relative to the sender
                if (!validCoordinates(sender, args, 0, true)) {
                    return true;
                }
                if (!validYawPitch(sender, args, 3)) {
                    return true;
                }
                p.saveData();
                manager.teleportToLocation(
                        p.getName(),                 // Player to teleport
                        "",                          // Server
                        p.getWorld().getName(),      // World
                        getCoordinate(p.getLocation().getX(), args[0]),
                        getCoordinate(p.getLocation().getY(), args[1]),
                        getCoordinate(p.getLocation().getZ(), args[2]),
                        Float.valueOf(args[3]),
                        Float.valueOf(args[4]) );
            }
            return true;
        }

        // tppos Player X Y Z Yaw Pitch
        // tppos X Y Z Yaw Pitch World
        if ( args.length == 6 ) {
            if ( p2 != null ) {
                // Teleport another player to the given coordinates (with yaw and pitch)
                // Supports coordinates relative to the sender
                if (!validCoordinates(sender, args, 1, true)) {
                    return true;
                }
                if (!validYawPitch(sender, args, 4)) {
                    return true;
                }
                p2.saveData();
                manager.teleportToLocation(
                        p2.getName(),                // Player to teleport
                        "",                          // Server
                        p.getWorld().getName(),      // World
                        getCoordinate(p.getLocation().getX(), args[1]),
                        getCoordinate(p.getLocation().getY(), args[2]),
                        getCoordinate(p.getLocation().getZ(), args[3]),
                        Float.valueOf(args[4]),
                        Float.valueOf(args[5]) );

            } else {
                // Teleport yourself to the specified coordinates of the given world (on this server)
                // Cannot use relative coordinates since potentially switching worlds
                if (!validCoordinates(sender, args, 0, false)) {
                    return true;
                }
                if (!validYawPitch(sender, args, 3)) {
                    return true;
                }
                p.saveData();
                manager.teleportToLocation(
                        p.getName(),                 // Player to teleport
                        "",                          // Server
                        args[5],                     // World
                        getCoordinate(p.getLocation().getX(), args[0]),
                        getCoordinate(p.getLocation().getY(), args[1]),
                        getCoordinate(p.getLocation().getZ(), args[2]),
                        Float.valueOf(args[3]),
                        Float.valueOf(args[4]) );
            }
        }

        // tppos Player X Y Z Yaw Pitch World
        // tppos X Y Z Yaw Pitch World Server
        if (args.length == 7) {
            if (p2 != null) {
                // Teleport another player to the given coordinates of the given world (on this server)
                // Cannot use relative coordinates since potentially switching worlds
                if (!validCoordinates(sender, args, 1, false)) {
                    return true;
                }
                if (!validYawPitch(sender, args, 4)) {
                    return true;
                }
                p2.saveData();
                manager.teleportToLocation(
                        p2.getName(),                // Player to teleport
                        "",                          // Server
                        args[6],                     // World
                        Double.valueOf(args[1]),
                        Double.valueOf(args[2]),
                        Double.valueOf(args[3]),
                        Float.valueOf(args[4]),
                        Float.valueOf(args[5]));
            } else {
                // Teleport yourself to the specified coordinates of the given world and server
                // Cannot use relative coordinates since potentially switching worlds
                if (!validCoordinates(sender, args, 0, false)) {
                    return true;
                }
                if (!validYawPitch(sender, args, 3)) {
                    return true;
                }
                p.saveData();
                manager.teleportToLocation(
                        p.getName(),                 // Player to teleport
                        args[6],                     // Server
                        args[5],                     // World
                        getCoordinate(p.getLocation().getX(), args[0]),
                        getCoordinate(p.getLocation().getY(), args[1]),
                        getCoordinate(p.getLocation().getZ(), args[2]),
                        Float.valueOf(args[3]),
                        Float.valueOf(args[4]) );
            }
        }

        // tppos Player X Y Z Yaw Pitch World Server
        if (args.length == 8) {
            if (p2 == null) {
                sender.sendMessage(geSuitTeleports.invalid_offline + args[0]);
                return true;
            }

            // Teleport another player to the given coordinates of the given world (on this server)
            // Cannot use relative coordinates since potentially switching worlds
            if (!validCoordinates(sender, args, 1, false)) {
                return true;
            }
            if (!validYawPitch(sender, args, 4)) {
                return true;
            }
            p2.saveData();
            manager.teleportToLocation(
                    p2.getName(),                // Player to teleport
                    args[7],                     // Server
                    args[6],                     // World
                    Double.valueOf(args[1]),
                    Double.valueOf(args[2]),
                    Double.valueOf(args[3]),
                    Float.valueOf(args[4]),
                    Float.valueOf(args[5]));
        }
        return false;
    }

    private double getCoordinate(double startingCoordinate, String coordValue) {

        if (coordValue.startsWith("~")) {
            // Return a relative coordinate
            if (coordValue.length() > 1)
                return startingCoordinate + Double.valueOf(coordValue.substring(1));
            else
                return startingCoordinate;
        }

        // Return an absolute coordinate
        return Double.valueOf(coordValue);
    }

    private boolean validCoordinates(CommandSender sender, String[] args, int startIndex, boolean allowRelative) {

        return validCoordinate(sender, allowRelative, "X", args[startIndex]) &&
                validCoordinate(sender, allowRelative, "Y", args[startIndex + 1]) &&
                validCoordinate(sender, allowRelative, "Z", args[startIndex + 2]);
    }

    private boolean validYawPitch(CommandSender sender, String[] args, int startIndex) {
        if (!NumberUtils.isNumber(args[startIndex])) {
            sender.sendMessage(geSuitTeleports.invalid_yaw + args[startIndex]);
            return false;
        }

        if (!NumberUtils.isNumber(args[startIndex + 1])) {
            sender.sendMessage(geSuitTeleports.invalid_pitch + args[startIndex + 1]);
            return false;
        }

        return true;
    }

    private boolean validCoordinate(CommandSender sender, boolean allowRelative, String coordName, String coordValue) {

        if (coordValue.startsWith("~")) {
            if (!allowRelative) {
                sender.sendMessage(geSuitTeleports.relative_coords_not_valid);
                return false;
            }

            if (coordValue.length() == 1)
                return true;
            else
                coordValue = coordValue.substring(1);
        }

        return !TPCommand.checkValidCoord(sender, coordName, coordValue);
    }

}
