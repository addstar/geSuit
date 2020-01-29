package net.cubespace.geSuit.managers;

import net.cubespace.geSuit.Utilities;
import net.md_5.bungee.api.ProxyServer;

import java.util.logging.Logger;

public class LoggingManager {
    static final Logger log = ProxyServer.getInstance().getLogger();

    public static void log(String message) {
        log.info(Utilities.colorize(message));
    }

}

