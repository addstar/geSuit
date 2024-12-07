package net.cubespace.geSuit.managers;

import net.cubespace.geSuit.geSuit;
import net.cubespace.geSuit.objects.GSPlayer;
import net.cubespace.geSuit.objects.Location;
import net.cubespace.geSuit.objects.Portal;
import net.cubespace.geSuit.objects.Warp;
import net.cubespace.geSuit.pluginmessages.DeletePortal;
import net.cubespace.geSuit.pluginmessages.SendPortal;
import net.cubespace.geSuit.pluginmessages.TeleportToLocation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortalManager {
    private static Map<ServerInfo, List<Portal>> portals = new HashMap<>();

    public static void loadPortals() {
        portals = DatabaseManager.portals.getPortals();
    }

    public static void getPortals(ServerInfo s) {
        List<Portal> list = portals.get(s);

        if (list == null) {
            return;
        }

        for (Portal p : portals.get(s)) {
            SendPortal.execute(p);
        }
    }

    public static void setPortal(GSPlayer sender, String name, String type, String dest, String fillType, Location max, Location min) {
        if (!(type.equalsIgnoreCase("warp") || type.equalsIgnoreCase("server"))) {
            sender.sendMessage(ConfigManager.messages.INVALID_PORTAL_TYPE);
            return;
        }

        fillType = fillType.toUpperCase();
        if (!(fillType.equals("AIR") || fillType.equals("LAVA") || fillType.equals("WATER") || fillType.equals("WEB") || fillType.equals("SUGAR_CANE") || fillType.equals("PORTAL") || fillType.equals("END_PORTAL"))) {
            sender.sendMessage(ConfigManager.messages.PORTAL_FILLTYPE);
            return;
        }

        if (type.equalsIgnoreCase("warp")) {
            Warp w = WarpsManager.getWarp(dest.toLowerCase());
            if (w == null) {
                sender.sendMessage(ConfigManager.messages.PORTAL_DESTINATION_NOT_EXIST);
                return;
            }
        } else {
            if (geSuit.proxy.getServerInfo(dest) == null) {
                sender.sendMessage(ConfigManager.messages.PORTAL_DESTINATION_NOT_EXIST);
                return;
            }
        }

        List<Portal> list = portals.computeIfAbsent(max.getServer(), k -> new ArrayList<>());

        Portal p = new Portal(name, max.getServer().getName(), fillType, type, dest, max, min);
        if (doesPortalExist(name)) {
            Portal old = getPortal(name);
            removePortal(old);

            DatabaseManager.portals.updatePortal(p);

            sender.sendMessage(ConfigManager.messages.PORTAL_UPDATED);
        } else {
            DatabaseManager.portals.insertPortal(p);
            sender.sendMessage(ConfigManager.messages.PORTAL_CREATED);
        }


        SendPortal.execute(p);
        list.add(p);
    }

    public static void removePortal(Portal p) {
        portals.get(p.getServer()).remove(p);

        DatabaseManager.portals.deletePortal(p.getName());

        DeletePortal.execute(p);
    }

    public static void deletePortal(GSPlayer sender, String portal) {
        if (!doesPortalExist(portal)) {
            sender.sendMessage(ConfigManager.messages.PORTAL_DOES_NOT_EXIST);
            return;
        }

        Portal p = getPortal(portal);
        removePortal(p);

        sender.sendMessage(ConfigManager.messages.PORTAL_DELETED);
    }

    public static Portal getPortal(String name) {
        for (List<Portal> list : portals.values()) {
            for (Portal p : list) {
                if (p.getName().equalsIgnoreCase(name)) {
                    return p;
                }
            }
        }

        return null;
    }

    public static boolean doesPortalExist(String name) {
        for (List<Portal> list : portals.values()) {
            for (Portal p : list) {
                if (p.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void teleportPlayer(GSPlayer p, String type, String dest, boolean perm) {
        if (!perm) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',ConfigManager.messages.PORTAL_NO_PERMISSION));
            return;
        }

        if (type.equalsIgnoreCase("warp")) {
            Warp w = WarpsManager.getWarp(dest.toLowerCase());

            if (w == null) {
                p.sendMessage(ConfigManager.messages.PORTAL_DESTINATION_NOT_EXIST);
            } else {
                TeleportToLocation.execute(p, w.getLocation());
            }
        } else {
            if (geSuit.proxy.getServerInfo(dest) == null) {
                p.sendMessage(ConfigManager.messages.PORTAL_DESTINATION_NOT_EXIST);
                return;
            }

            ServerInfo s = geSuit.proxy.getServerInfo(dest);
            if (!s.getName().equals(p.getServer())) {
                p.connectTo(s);
            }
        }
    }

    public static void listPortals(GSPlayer p) {
        for (ServerInfo s : portals.keySet()) {
            StringBuilder message = new StringBuilder();
            String serverName = s == null ? "unknown": s.getName();
            message.append(ChatColor.GOLD)
                    .append(serverName)
                    .append(": ")
                    .append(ChatColor.RESET);
            List<Portal> list = portals.get(s);
            for (Portal portal : list) {
                message.append(portal.getName()).append(", ");
            }

            p.sendMessage(message.toString());
        }
    }
}
