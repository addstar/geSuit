package net.cubespace.geSuit.managers;

import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 24/09/2018.
 */
public class LoggingManager {
    private static final Logger logger = Bukkit.getLogger();
    private static Level level = Level.INFO;

    public static void setLevel(Level l) {
        level = l;
    }

    public static void info(String message) {
        if (level.intValue() >= Level.INFO.intValue()) logger.info(message);
    }

    public static void warn(String message) {
        if (level.intValue() >= Level.WARNING.intValue()) logger.warning(message);
    }

    public static void error(String message) {
        if (level.intValue() >= Level.SEVERE.intValue()) logger.severe(message);
    }

    public static void debug(String message) {
        if (level.intValue() >= Level.FINE.intValue()) logger.info("[DEBUG] " + message);
    }
}
