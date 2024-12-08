package net.cubespace.geSuit.utils;

import net.cubespace.geSuit.managers.LoggingManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 7/08/2017.
 */
public class Utilities {

    private static final Pattern TIME_PATTERN = Pattern.compile("([0-9]+)([wdhms])");
    private static final int SECOND = 1;
    private static final int MINUTE = SECOND * 60;
    private static final int HOUR = MINUTE * 60;
    private static final int DAY = HOUR * 24;
    private static final int WEEK = DAY * 7;

    /**
     * Parse a string input into seconds, using w(eeks), d(ays), h(ours), m(inutes) and s(econds) For example: 4d8m2s -> 4 days, 8 minutes and 2 seconds
     *
     * @param string String to convert to Seconds
     * @return Seconds
     */
    public static int parseStringToSecs(String string) {
        Matcher m = TIME_PATTERN.matcher(string);
        int total = 0;
        while (m.find()) {
            int amount = Integer.valueOf(m.group(1));
            switch (m.group(2).charAt(0)) {
                case 's':
                    total += amount * SECOND;
                    break;
                case 'm':
                    total += amount * MINUTE;
                    break;
                case 'h':
                    total += amount * HOUR;
                    break;
                case 'd':
                    total += amount * DAY;
                    break;
                case 'w':
                    total += amount * WEEK;
                    break;
            }
        }
        return total;
    }

    public static long parseStringtoMillisecs(String string) {
        int total = parseStringToSecs(string);
        return total * 1000;
    }


    public static String buildTimeDiffString(long timeDiff, int precision) {
        StringBuilder builder = new StringBuilder();

        int count = 0;
        long amount = timeDiff / TimeUnit.DAYS.toMillis(1);
        if (amount >= 1) {
            builder.append(amount);
            if (amount > 1) {
                builder.append(" Days ");
            } else {
                builder.append(" Day ");
            }
            timeDiff -= amount * TimeUnit.DAYS.toMillis(1);
            ++count;
        }

        amount = timeDiff / TimeUnit.HOURS.toMillis(1);
        if (count < precision && amount >= 1) {
            builder.append(amount);
            if (amount > 1) {
                builder.append(" Hours ");
            } else {
                builder.append(" Hour ");
            }
            timeDiff -= amount * TimeUnit.HOURS.toMillis(1);
            ++count;
        }

        amount = timeDiff / TimeUnit.MINUTES.toMillis(1);
        if (count < precision && amount >= 1) {
            builder.append(amount);
            if (amount > 1) {
                builder.append(" Mins ");
            } else {
                builder.append(" Min ");
            }
            timeDiff -= amount * TimeUnit.MINUTES.toMillis(1);
            ++count;
        }

        amount = timeDiff / TimeUnit.SECONDS.toMillis(1);
        if (count < precision && amount >= 1) {
            builder.append(amount);
            if (amount > 1) {
                builder.append(" Secs ");
            } else {
                builder.append(" Sec ");
            }
            timeDiff -= amount * TimeUnit.SECONDS.toMillis(1);
            ++count;
        }

        if (timeDiff < 1000 && builder.length() == 0) {
            builder.append("0 Secs");
        }

        return builder.toString().trim();
    }

    public static String buildShortTimeDiffString(long timeDiff, int precision) {
        StringBuilder builder = new StringBuilder();

        int count = 0;
        long amount = timeDiff / TimeUnit.DAYS.toMillis(1);
        if (amount >= 1) {
            builder.append(amount);
            builder.append("d ");
            timeDiff -= amount * TimeUnit.DAYS.toMillis(1);
            ++count;
        }

        amount = timeDiff / TimeUnit.HOURS.toMillis(1);
        if (count < precision && amount >= 1) {
            builder.append(amount);
            builder.append("h ");
            timeDiff -= amount * TimeUnit.HOURS.toMillis(1);
            ++count;
        }

        amount = timeDiff / TimeUnit.MINUTES.toMillis(1);
        if (count < precision && amount >= 1) {
            builder.append(amount);
            builder.append("m ");
            timeDiff -= amount * TimeUnit.MINUTES.toMillis(1);
            ++count;
        }

        amount = timeDiff / TimeUnit.SECONDS.toMillis(1);
        if (count < precision && amount >= 1) {
            builder.append(amount);
            builder.append("s ");
            timeDiff -= amount * TimeUnit.SECONDS.toMillis(1);
            ++count;
        }

        if (timeDiff < 1000 && builder.length() == 0) {
            builder.append("0s");
        }

        return builder.toString().trim();
    }

    public static String createTimeStampString(long timeStamp) {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(timeStamp);
    }

    public static String dumpPacket(String channel, String direction, byte[] bytes) {
        StringBuilder data = new StringBuilder();
        data.append(channel);
        data.append(" - ");
        data.append(direction);
        data.append(" : ");
        //ByteArrayInputStream ds = new ByteArrayInputStream(bytes);
        //DataInputStream di = new DataInputStream(ds);
        // Read upto 20 parameters from the stream and load them into the string list
        for (byte c : bytes) {
            if (c >= 32 && c <= 126) {
                data.append((char) c);
            } else {
                data.append("\\x").append(Integer.toHexString(c));
            }
        }
        return data.toString();
    }

    // Check if player is falling
    public static boolean isPlayerTeleportAllowed(Player player, Location loc) {
        // Check if player has permission to bypass falling check or flying
        if (player.hasPermission("gesuit.teleport.bypass.falling") || player.isFlying()) {
            LoggingManager.debug("[isPlayerTeleportAllowed] Player " + player.getName() + " bypassed falling check (flying: " + player.isFlying() + ")");
            return true;
        }

        // Check if player is falling
        return hasGroundBelow(loc, 5);
    }

    // Check if there's ground below the player
    public static boolean hasGroundBelow(Location location, int maxDistance) {
        // Only check a certain distance below the player (either maxDistance or the bottom of the world)
        int yrange = Math.min(location.getBlockY() - location.getWorld().getMinHeight(), maxDistance);
        LoggingManager.debug("[hasGroundBelow] Checking for ground below "
                + location.getWorld().getName() + " " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()
                + " (: " + yrange + ")");

        for (int y = location.getBlockY(); y >= (location.getBlockY()-yrange); y--) {
            Block block = location.getWorld().getBlockAt(location.getBlockX(), y, location.getBlockZ());
            LoggingManager.debug("  [hasGroundBelow] Checking block " + block.getType() + " at " + block.getLocation().toString());
            if (!block.getType().isAir()) { // Found a solid block
                LoggingManager.debug("  [hasGroundBelow] Found " + block.getType() + " at " + block.getLocation().toString());
                return true; // Allow teleport
            }
        }
        LoggingManager.debug("  [hasGroundBelow] Found NO ground below " + location.toString());
        return false; // No solid block found
    }
}

