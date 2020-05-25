package net.cubespace.geSuit.managers;

import com.google.common.collect.Iterables;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.geSuit.TimeParser;
import net.cubespace.geSuit.Utilities;
import net.cubespace.geSuit.events.BanPlayerEvent;
import net.cubespace.geSuit.events.UnbanPlayerEvent;
import net.cubespace.geSuit.events.WarnPlayerEvent;
import net.cubespace.geSuit.events.WarnPlayerEvent.ActionType;
import net.cubespace.geSuit.geSuit;
import net.cubespace.geSuit.objects.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BansManager {

    private static final List<Kick> kicks = new ArrayList<>();

    public static void banPlayer(String bannedBy, String player, String reason) {
    	banPlayer(bannedBy, player, reason, false);
    }

    public static void banPlayer(String bannedBy, String player, String reason, Boolean auto) {
        GSPlayer s = PlayerManager.getPlayer(bannedBy);
        CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());
        banPlayer(sender, player, reason, auto);

    }

    public static void banPlayer(CommandSender sender, String player, String reason, Boolean auto) {
        BanTarget t = getBanTarget(player);
        if (t.gsp == null)
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.UNKNOWN_PLAYER_STILL_BANNING);
        Ban b = DatabaseManager.bans.getBanInfo(t.name, t.uuid, null);
        if (b != null) {
            if (b.getType().equals("tempban")) {
                // We don't want tempbans AND bans in place.. it could cause issues!
                DatabaseManager.bans.unbanPlayer(b.getId());
            } else {
                PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.PLAYER_ALREADY_BANNED);
                return;
            }
        }
        String bannedBy = sender.getName();
        if (reason == null || reason.equals("")) {
            // Do not allow a ban without a reason since people accidentally do /db instead of /dst or /dtb
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.BAN_REASON_REQUIRED);
            return;
        }

        DatabaseManager.bans.banPlayer(t.name, t.uuid, null, bannedBy, reason, "ban");

        callEvent(new BanPlayerEvent(new Ban(-1, t.name, t.uuid, null, bannedBy, reason, "ban", 1, null, null), auto));

        // Player is online so kick them
        if ((t.gsp != null) && (t.gsp.getProxiedPlayer() != null)) {
            disconnectPlayer(t.gsp.getProxiedPlayer(), Utilities.colorize(ConfigManager.messages.BAN_PLAYER_MESSAGE.replace("{message}", reason).replace("{sender}", bannedBy)));
        }

        if (ConfigManager.bans.BroadcastBans) {
            if (auto) {
                PlayerManager.sendBroadcast(Utilities.colorize(ConfigManager.messages.BAN_PLAYER_AUTO_BROADCAST.replace("{player}", t.dispname).replace("{sender}", sender.getName())), t.name);
            } else {
                PlayerManager.sendBroadcast(ConfigManager.messages.BAN_PLAYER_BROADCAST.replace("{player}", t.dispname).replace("{message}", reason).replace("{sender}", bannedBy), t.name);
            }
        } else {
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.BAN_PLAYER_BROADCAST.replace("{player}", t.dispname).replace("{message}", reason).replace("{sender}", bannedBy));
        }

    }


    public static void unbanPlayer(String sentBy, String player) {
        GSPlayer s = PlayerManager.getPlayer(sentBy);
        CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());
        unbanPlayer(sender, player);
    }

    public static void unbanPlayer(CommandSender sender, String player) {
        BanTarget t = getBanTarget(player);
        if (!DatabaseManager.bans.isPlayerBanned(t.name, t.uuid, player)) {
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.PLAYER_NOT_BANNED);
            return;
        }

        Ban b = DatabaseManager.bans.getBanInfo(t.name, t.uuid, player);

        DatabaseManager.bans.unbanPlayer(b.getId());
        callEvent(new UnbanPlayerEvent(b, sender.getName()));

        if (ConfigManager.bans.BroadcastUnbans) {
            PlayerManager.sendBroadcast(ConfigManager.messages.PLAYER_UNBANNED.replace("{player}", t.dispname).replace("{sender}", sender.getName()));
        } else {
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.PLAYER_UNBANNED.replace("{player}", t.dispname).replace("{sender}", sender.getName()));
        }
    }


    public static void banIP(String bannedBy, String target, String reason) {
        GSPlayer s = PlayerManager.getPlayer(bannedBy);
        CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());
        banIP(sender, target, reason);

    }

    public static void banIP(CommandSender sender, String target, String reason) {
        if (reason == null || reason.equals("")) {
            reason = Utilities.colorize(ConfigManager.messages.DEFAULT_BAN_REASON);
        }

        String ip = null;
        String uuid = null;
        String player = null;
        if (Utilities.isIPAddress(target)) {
            // Target is just an IP address.. we don't know which player/uuid so keep them null
            ip = target;
        } else {
            // Target is a player name or uuid.. grab the player details and record it all
            GSPlayer gs = DatabaseManager.players.loadPlayer(target);
            if (gs != null) {
                ip = gs.getIp();
                uuid = gs.getUuid();
                player = gs.getName();
            }
        }

        if ((ip == null) || (ip.isEmpty())) {
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.PLAYER_DOES_NOT_EXIST);
            return;
        }
        String bannedBy = sender.getName();
        if (!DatabaseManager.bans.isPlayerBanned(ip)) {
            if (DatabaseManager.bans.banPlayer(player, uuid, ip, bannedBy, reason, "ipban") > 0) {
                callEvent(new BanPlayerEvent(new Ban(-1, player, uuid, ip, bannedBy, reason, "ipban", 1, null, null), false));
            } else {
                PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.BAN_FAILED_UNKNOWN_REASON);
                return;
            }
        }

        for (GSPlayer p : PlayerManager.getPlayersByIP(ip)) {
            if (p.getProxiedPlayer() != null) {
                disconnectPlayer(p.getProxiedPlayer(), ConfigManager.messages.IPBAN_PLAYER.replace("{message}", reason).replace("{sender}", bannedBy));
            }
        }

        if (ConfigManager.bans.BroadcastBans) {
            // Notify players of the ban (use the regular Ban message)
            PlayerManager.sendBroadcast(ConfigManager.messages.BAN_PLAYER_BROADCAST.replace("{player}", player).replace("{message}", reason).replace("{sender}", bannedBy));
            if (ConfigManager.main.BungeeChatIntegration) {
                // Notify staff that this was an IP Ban
                Utilities.doBungeeChatMirror("StaffNotice", ConfigManager.messages.IPBAN_PLAYER_BROADCAST.replace("{player}", player).replace("{message}", reason).replace("{sender}", bannedBy));
            }
        } else {
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.IPBAN_PLAYER_BROADCAST.replace("{player}", player).replace("{message}", reason).replace("{sender}", bannedBy));
        }
    }

    public static void kickAll(String sender, String message) {
        if (message.equals("")) {
            message = ConfigManager.messages.DEFAULT_KICK_MESSAGE;
        }

        message = Utilities.colorize(ConfigManager.messages.KICK_PLAYER_MESSAGE.replace("{message}", message).replace("{sender}", sender));

        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            // Don't kick the player executing the command or anyone with bypass permission
            if ((!p.hasPermission("gesuit.bypass.kickall")) && (!p.getName().equals(sender))) {
                disconnectPlayer(p, message);
            }
        }
    }


    public static void checkPlayersBan(String sentBy, String player) {
        GSPlayer s = PlayerManager.getPlayer(sentBy);
        CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

        BanTarget t = getBanTarget(player);
        Ban b = DatabaseManager.bans.getBanInfo(t.name);

        if (b == null) {
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.PLAYER_NOT_BANNED);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("dd MMM yyyy HH:mm:ss z");
            PlayerManager.sendMessageToTarget(sender, ChatColor.DARK_AQUA + "-------- " + ChatColor.RED + "Ban Info" + ChatColor.DARK_AQUA + " --------");
            PlayerManager.sendMessageToTarget(sender, ChatColor.RED + "Player: " + ChatColor.AQUA + b.getPlayer());
            if (b.getUuid() != null) {
                PlayerManager.sendMessageToTarget(sender, ChatColor.RED + "UUID: " + ChatColor.AQUA + b.getUuid());
            }
            PlayerManager.sendMessageToTarget(sender, ChatColor.RED + "Type: " + ChatColor.AQUA + b.getType());
            PlayerManager.sendMessageToTarget(sender, ChatColor.RED + "By: " + ChatColor.AQUA + b.getBannedBy());
            PlayerManager.sendMessageToTarget(sender, ChatColor.RED + "Reason: " + ChatColor.AQUA + b.getReason());
            PlayerManager.sendMessageToTarget(sender, ChatColor.RED + "Date: " + ChatColor.AQUA + sdf.format(b.getBannedOn()));

            if (b.getBannedUntil() == null) {
                PlayerManager.sendMessageToTarget(sender, ChatColor.RED + "Until: " + ChatColor.AQUA + "-Forever-");
            } else {
                PlayerManager.sendMessageToTarget(sender, ChatColor.RED + "Until: " + ChatColor.AQUA + sdf.format(b.getBannedUntil()));
            }
        }
    }

    public static void displayPlayerBanHistory(String sentBy, String player) {
        GSPlayer s = PlayerManager.getPlayer(sentBy);
        CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

        BanTarget t = getBanTarget(player);
        List<Ban> bans = DatabaseManager.bans.getBanHistory(t.name);

        if (bans == null || bans.isEmpty()) {
            PlayerManager.sendMessageToTarget(sender, Utilities.colorize(ConfigManager.messages.PLAYER_NEVER_BANNED.replace("{player}", t.dispname)));
            return;
        }
        PlayerManager.sendMessageToTarget(sender, ChatColor.DARK_AQUA + "-------- " + ChatColor.YELLOW + player + "'s Ban History" + ChatColor.DARK_AQUA + " --------");
        boolean first = true;
        for (Ban b : bans) {
            if (first) {
                first = false;
            } else {
                PlayerManager.sendMessageToTarget(sender, "");
            }
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("dd MMM yyyy HH:mm");

            PlayerManager.sendMessageToTarget(sender,
            		(b.getBannedUntil() != null ? ChatColor.GOLD : ChatColor.RED) + "Date: " +
            		ChatColor.GREEN + sdf.format(b.getBannedOn()) +
            		(b.getBannedUntil() != null ?
            				ChatColor.GOLD + " > " + sdf.format(b.getBannedUntil()) :
            				ChatColor.RED + " > permban"));

            PlayerManager.sendMessageToTarget(sender,
            		(b.getBannedUntil() != null ? ChatColor.GOLD : ChatColor.RED) + "By: " +
            		ChatColor.AQUA + b.getBannedBy() +
            		ChatColor.DARK_AQUA + " (" + ChatColor.GRAY + b.getReason() + ChatColor.DARK_AQUA + ")");
        }
    }

    public static void kickPlayer(String kickedBy, String player, String reason) {
    	kickPlayer(kickedBy, player, reason, false);
    }

    public static void kickPlayer(String kickedBy, String player, String reason, Boolean auto) {
        GSPlayer s = PlayerManager.getPlayer(kickedBy);
        CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

        BanTarget t = getBanTarget(player);
        if ((t.gsp == null) || (t.gsp.getProxiedPlayer() == null)) {
        	PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.PLAYER_NOT_ONLINE);
        	return;
        }

        if (reason.isEmpty()) {
            reason = ConfigManager.messages.DEFAULT_KICK_MESSAGE;
        }

        disconnectPlayer(t.gsp.getProxiedPlayer(), Utilities.colorize(ConfigManager.messages.KICK_PLAYER_MESSAGE.replace("{message}", reason).replace("{sender}", sender.getName())));
        int kickLimit = ConfigManager.bans.KickLimit;
        if (kickLimit > 0) {
            checkKickTempBan(kickLimit, t, kickedBy, reason);
        }
        if (ConfigManager.bans.RecordKicks) {
            DatabaseManager.bans.kickPlayer(t.name, t.uuid, kickedBy, reason);
        }

        if (ConfigManager.bans.BroadcastKicks) {
            if (auto) {
            	PlayerManager.sendBroadcast(Utilities.colorize(ConfigManager.messages.KICK_PLAYER_AUTO_BROADCAST.replace("{player}", t.dispname).replace("{sender}", sender.getName())), t.name);
            } else {
            	PlayerManager.sendBroadcast(Utilities.colorize(ConfigManager.messages.KICK_PLAYER_BROADCAST.replace("{message}", reason).replace("{player}", t.dispname).replace("{sender}", sender.getName())), t.name);
            }
        } else {
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.KICK_PLAYER_BROADCAST.replace("{message}", reason).replace("{player}", t.dispname).replace("{sender}", sender.getName()));
        }
    }

    public static void disconnectPlayer(ProxiedPlayer player, String message) {
        player.disconnect(new TextComponent(Utilities.colorize(message)));
    }

    public static void reloadBans(String sender) {
        PlayerManager.sendMessageToTarget(sender, "Bans Reloaded");

        try {
            ConfigManager.bans.reload();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void tempBanPlayer(String bannedBy, String player, int seconds, String message) {
    	tempBanPlayer(bannedBy, player, seconds, message, false);
    }

    public static void tempBanPlayer(String bannedBy, String player, long seconds, String message, Boolean auto) {
        BanTarget t = getBanTarget(player);
        tempBanPlayer(bannedBy, t, seconds, message, auto);
    }

    public static void tempBanPlayer(String bannedBy, BanTarget t, long seconds, String message, Boolean auto) {
        GSPlayer s = PlayerManager.getPlayer(bannedBy);
        CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());
        if (t.gsp == null)
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.UNKNOWN_PLAYER_STILL_BANNING);

        Ban b = DatabaseManager.bans.getBanInfo(t.name, t.uuid, null);
        if (b != null) {
            if (b.getType().equals("tempban")) {
                // We don't want tempbans AND bans in place.. it could cause issues!
                DatabaseManager.bans.unbanPlayer(b.getId());
            } else {
                PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.PLAYER_ALREADY_BANNED);
                return;
            }
        }

        if (message == null || message.equals("")) {
            // Do not allow a temp ban without a reason since people accidentally do /dtb instead of /dst
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.TEMP_BAN_REASON_REQUIRED);
            return;
        }

        Date sqlToday = new Date(System.currentTimeMillis() + (seconds * 1000L));
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd MMM yyyy HH:mm:ss");
        String time = sdf.format(sqlToday);
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        String timeDiff = Utilities.buildTimeDiffString(seconds * 1000L, 2);
        String shortTimeDiff = Utilities.buildShortTimeDiffString(seconds * 1000L, 10);

        DatabaseManager.bans.tempBanPlayer(t.name, t.uuid, bannedBy, message, sdf.format(sqlToday));
        PlayerManager.sendtoNewSpawn(t.gsp);
        callEvent(new BanPlayerEvent(new Ban(-1, t.name, t.uuid, null, bannedBy, message, "tempban", 1, null, new Timestamp(System.currentTimeMillis() + (seconds * 1000L))), auto));

        if ((t.gsp != null) && (t.gsp.getProxiedPlayer() != null)) {
            disconnectPlayer(t.gsp.getProxiedPlayer(), ConfigManager.messages.TEMP_BAN_MESSAGE.replace("{sender}", t.dispname).replace("{time}", time).replace("{left}", timeDiff).replace("{shortleft}", shortTimeDiff).replace("{message}", message));
        }

        if (ConfigManager.bans.BroadcastBans) {
            if (auto) {
                PlayerManager.sendBroadcast(Utilities.colorize(ConfigManager.messages.TEMP_BAN_AUTO_BROADCAST.replace("{player}", t.dispname).replace("{sender}", sender.getName()).replace("{time}", time).replace("{left}", timeDiff).replace("{shortleft}", shortTimeDiff)));
            } else {
                PlayerManager.sendBroadcast(ConfigManager.messages.TEMP_BAN_BROADCAST.replace("{player}", t.dispname).replace("{sender}", sender.getName()).replace("{message}", message).replace("{time}", time).replace("{left}", timeDiff).replace("{shortleft}", shortTimeDiff));
            }
        } else {
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.TEMP_BAN_BROADCAST.replace("{player}", t.dispname).replace("{sender}", sender.getName()).replace("{message}", message).replace("{time}", time).replace("{left}", timeDiff).replace("{shortleft}", shortTimeDiff));
        }
    }

    public static boolean checkTempBan(Ban b) {
        java.util.Date today = new java.util.Date(Calendar.getInstance().getTimeInMillis());
        java.util.Date banned = b.getBannedUntil();

        if (today.compareTo(banned) >= 0) {
            DatabaseManager.bans.unbanPlayer(b.getId());
            return false;
        }

        return true;
    }

    public static void warnPlayer(String warnedBy, String player, String reason) {
        GSPlayer s = PlayerManager.getPlayer(warnedBy);
        CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

        BanTarget t = getBanTarget(player);
        if (t.gsp == null) {
        	PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.UNKNOWN_PLAYER_NOT_WARNING);
        	return;
    	}
        
        if (reason == null || reason.isEmpty()) {
            // Do not allow a warning without a reason since people accidentally do /dw instead of /dst
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.WARN_REASON_REQUIRED);
            return;
        }

        DatabaseManager.bans.warnPlayer(t.name, t.uuid, warnedBy, reason);

        if (ConfigManager.bans.BroadcastWarns) {
            PlayerManager.sendBroadcast(ConfigManager.messages.WARN_PLAYER_BROADCAST.replace("{player}", t.dispname).replace("{message}", reason).replace("{sender}", warnedBy));
        } else {
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.WARN_PLAYER_BROADCAST.replace("{player}", t.dispname).replace("{message}", reason).replace("{sender}", warnedBy));
        }

        int warncount = 0;
        ActionType actionType = ActionType.None;
        String actionExtra = "";
        // Check if we have warning actions defined
        if (ConfigManager.bans.Actions != null) {
        	List<Ban> warnings = DatabaseManager.bans.getWarnHistory(t.name, t.uuid);
        	warncount = 0;
            for (Ban w : warnings) {
            	// Only count warnings that have not expired
            	Date now = new Date(); 
	            int age = (int) ((now.getTime() - w.getBannedOn().getTime()) / 1000 / 86400);
	        	if (age < ConfigManager.bans.WarningExpiryDays) {
	        		warncount++;
	        	}
            }

        	if (ConfigManager.bans.Actions.containsKey(warncount)) {
        		String fullaction = ConfigManager.bans.Actions.get(warncount);
        		String[] parts = fullaction.split(" ");

        		String action = parts[0];
                switch (action) {
                    case "kick":
                        actionType = ActionType.Kick;
                        if ((t.gsp != null) && (t.gsp.getProxiedPlayer() != null)) {
                            kickPlayer(warnedBy, t.name, reason, true);
                        }
                        break;
                    case "tempban":
                        actionType = ActionType.TempBan;
                        int seconds = TimeParser.parseString(parts[1]);
                        tempBanPlayer(warnedBy, t.name, seconds, reason, true);
                        actionExtra = "for " + parts[1];
                        break;
                    case "ban":
                        actionType = ActionType.Ban;
                        banPlayer(warnedBy, t.name, reason, true);
                        break;
                    default:
                        PlayerManager.sendMessageToTarget(sender, ChatColor.RED + "Warning action of \"" + fullaction + "\" is invalid!");
                        LoggingManager.log(ChatColor.RED + "Warning action of \"" + fullaction + "\" is invalid!");
                        break;
                }
            }
        }
        
        callEvent(new WarnPlayerEvent(t.name, t.uuid, warnedBy, reason, actionType, actionExtra, warncount));
    }

    public static void displayPlayerWarnHistory(final String sentBy, final String player, final boolean showStaffNames) {
        ProxyServer.getInstance().getScheduler().runAsync(geSuit.getInstance(), () -> {
            GSPlayer s = PlayerManager.getPlayer(sentBy);

            CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

            // Resolve the target player
            GSPlayer target = PlayerManager.getPlayer(player);
            String targetId;
            if (target == null) {
                Map<String, UUID> ids = DatabaseManager.players.resolvePlayerNamesHistoric(Collections.singletonList(player));
                UUID id = Iterables.getFirst(ids.values(), null);
                if (id == null) {
                    PlayerManager.sendMessageToTarget(sender, Utilities.colorize(ConfigManager.messages.PLAYER_NEVER_WARNED.replace("{player}", player)));
                    return;
                }
                targetId = id.toString().replace("-", "");
            } else {
                targetId = target.getUuid();
            }

            List<Ban> warns = DatabaseManager.bans.getWarnHistory(player, targetId);
            if (warns == null || warns.isEmpty()) {
                PlayerManager.sendMessageToTarget(sender, Utilities.colorize(ConfigManager.messages.PLAYER_NEVER_WARNED.replace("{player}", player)));
                return;
            }
            PlayerManager.sendMessageToTarget(sender, ChatColor.DARK_AQUA + "-------- " + ChatColor.YELLOW + player + "'s Warning History" + ChatColor.DARK_AQUA + " --------");

            int count = 0;
            for (Ban b : warns) {
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("dd MMM yyyy HH:mm");

                Date now = new Date();
                int age = (int) ((now.getTime() - b.getBannedOn().getTime()) / 1000 / 86400);

                String warnedBy = " ";

                if (age >= ConfigManager.bans.WarningExpiryDays) {
                    if (showStaffNames)
                        warnedBy = ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + b.getBannedBy() + ChatColor.DARK_GRAY + ") ";

                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.GRAY + "- " +
                                    ChatColor.DARK_GRAY + sdf.format(b.getBannedOn()) +
                                    warnedBy +
                                    ChatColor.DARK_GRAY + b.getReason());
                } else {
                    count++;
                    if (showStaffNames)
                        warnedBy = ChatColor.YELLOW + " (" + ChatColor.GRAY + b.getBannedBy() + ChatColor.YELLOW + ") ";

                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.YELLOW + String.valueOf(count) + ": " +
                                    ChatColor.GREEN + sdf.format(b.getBannedOn()) +
                                    warnedBy +
                                    ChatColor.AQUA + b.getReason());
                }
            }
        });
    }

    public static void displayIPWarnBanHistory(final String sentBy, final String ip) {

        ProxyServer.getInstance().getScheduler().runAsync(geSuit.getInstance(), () -> {
            GSPlayer s = PlayerManager.getPlayer(sentBy);
            CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

            List<Track> tracking = DatabaseManager.tracking.getPlayerTracking(ip, "ip");
            if (tracking.isEmpty()) {
                PlayerManager.sendMessageToTarget(sender,
                        ChatColor.RED + "[Tracker] No known accounts match or contain \"" + ip + "\"");
                return;
            }

            // Construct a list of UUIDs and player names
            // (we only want to lookup warnings for the player's current name)
            HashMap<String, String> uuidNameMap = new HashMap<>();
            for (Track t : tracking) {
                uuidNameMap.put(t.getUuid(), t.getPlayer());
            }

            PlayerManager.sendMessageToTarget(sender,
                    ChatColor.GREEN + "[Tracker] IP address \"" + ip + "\" has " + uuidNameMap.size() + " accounts:");

            // Copy the names into a list so that we can sort
            List<String> sortedNames = new ArrayList<>(uuidNameMap.values());

            // Show warnings for each player, sorting alphabetically by name
            sortedNames.sort(String.CASE_INSENSITIVE_ORDER);
            for (String playerName : sortedNames) {
                displayPlayerWarnBanHistory(sender, playerName);
            }
        });

    }

    public static void displayPlayerWarnBanHistory(final String sentBy, final String player) {
        ProxyServer.getInstance().getScheduler().runAsync(geSuit.getInstance(), () -> {
            GSPlayer s = PlayerManager.getPlayer(sentBy);

            CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

            displayPlayerWarnBanHistory(sender, player);
        });
    }

    private static void displayPlayerWarnBanHistory(CommandSender sender, final String player) {

        // Resolve the target player
        GSPlayer target = PlayerManager.getPlayer(player);
        String targetId;
        if (target == null) {
            Map<String, UUID> ids = DatabaseManager.players.resolvePlayerNamesHistoric(Collections.singletonList(player));
            UUID id = Iterables.getFirst(ids.values(), null);
            if (id == null) {
                PlayerManager.sendMessageToTarget(sender, Utilities.colorize(ConfigManager.messages.PLAYER_NEVER_WARNED_OR_BANNED.replace("{player}", player)));
                return;
            }
            targetId = id.toString().replace("-", "");
        } else {
            targetId = target.getUuid();
        }

        // Retrieve warnings
        List<Ban> warns = DatabaseManager.bans.getWarnHistory(player, targetId);

        // Retrieve active bans
        BanTarget t = getBanTarget(player);
        Ban activeBan = DatabaseManager.bans.getBanInfo(t.name);

        if (activeBan == null && (warns == null || warns.isEmpty())) {
            PlayerManager.sendMessageToTarget(sender, Utilities.colorize(ConfigManager.messages.PLAYER_NEVER_WARNED_OR_BANNED.replace("{player}", player)));
            return;
        }

        if (warns == null || warns.isEmpty())
        {
            PlayerManager.sendMessageToTarget(sender, Utilities.colorize(ConfigManager.messages.PLAYER_NEVER_WARNED.replace("{player}", player)));
        } else {
            PlayerManager.sendMessageToTarget(sender, ChatColor.DARK_AQUA + "-------- " + ChatColor.YELLOW + player + "'s Warning History" + ChatColor.DARK_AQUA + " --------");

            int count = 0;
            for (Ban b : warns) {
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("dd MMM yyyy HH:mm");

                Date now = new Date();
                int age = (int) ((now.getTime() - b.getBannedOn().getTime()) / 1000 / 86400);

                String warnedBy = " ";

                if (age >= ConfigManager.bans.WarningExpiryDays) {
                    warnedBy = ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + b.getBannedBy() + ChatColor.DARK_GRAY + ") ";

                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.GRAY + "- " +
                                    ChatColor.DARK_GRAY + sdf.format(b.getBannedOn()) +
                                    warnedBy +
                                    ChatColor.DARK_GRAY + b.getReason());
                } else {
                    count++;
                    warnedBy = ChatColor.YELLOW + " (" + ChatColor.GRAY + b.getBannedBy() + ChatColor.YELLOW + ") ";

                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.YELLOW + String.valueOf(count) + ": " +
                                    ChatColor.GREEN + sdf.format(b.getBannedOn()) +
                                    warnedBy +
                                    ChatColor.AQUA + b.getReason());
                }
            }
        }

        if (activeBan != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("dd MMM yyyy HH:mm:ss z");
            String banType = activeBan.getType();

            PlayerManager.sendMessageToTarget(sender, "");
            PlayerManager.sendMessageToTarget(sender, ChatColor.DARK_AQUA + "-------- " + ChatColor.RED + "Ban Info" + ChatColor.DARK_AQUA + " --------");
            PlayerManager.sendMessageToTarget(sender,
                    ChatColor.AQUA + activeBan.getPlayer() +
                            ChatColor.WHITE + " was banned on " +
                            ChatColor.AQUA + sdf.format(activeBan.getBannedOn()) +
                            ChatColor.WHITE + " by " +
                            ChatColor.AQUA + activeBan.getBannedBy());

            if (activeBan.getBannedUntil() == null) {
                String banDescription = banType;

                if (banType.equalsIgnoreCase("ban"))
                    banDescription = "permanent ban";
                else if (banType.equalsIgnoreCase("ipban"))
                    banDescription = "IP ban";

                PlayerManager.sendMessageToTarget(sender, ChatColor.RED + "Type: " + ChatColor.AQUA + banDescription);

            } else {
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                if (currentTime.after(activeBan.getBannedUntil()))
                {
                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.RED + "Type: " +
                                    ChatColor.AQUA + banType +
                                    ChatColor.WHITE + ", expired " +
                                    ChatColor.GREEN + sdf.format(activeBan.getBannedUntil()));
                } else {
                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.RED + "Type: " +
                                    ChatColor.AQUA + banType +
                                    ChatColor.WHITE + ", until " +
                                    ChatColor.GREEN + sdf.format(activeBan.getBannedUntil()));
                }
            }
            PlayerManager.sendMessageToTarget(sender, ChatColor.RED + "Reason: " + ChatColor.AQUA + activeBan.getReason());
        }
    }

    public static void displayPlayerKickHistory(final String sentBy, final String player, final boolean showStaffNames) {
        ProxyServer.getInstance().getScheduler().runAsync(geSuit.getInstance(), () -> {
            GSPlayer s = PlayerManager.getPlayer(sentBy);

            CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

            // Resolve the target player
            GSPlayer target = PlayerManager.getPlayer(player);
            String targetId;
            if (target == null) {
                Map<String, UUID> ids = DatabaseManager.players.resolvePlayerNamesHistoric(Collections.singletonList(player));
                UUID id = Iterables.getFirst(ids.values(), null);
                if (id == null) {
                    PlayerManager.sendMessageToTarget(sender, Utilities.colorize(ConfigManager.messages.PLAYER_NEVER_KICKED.replace("{player}", player)));
                    return;
                }
                targetId = id.toString().replace("-", "");
            } else {
                targetId = target.getUuid();
            }

            List<Ban> warns = DatabaseManager.bans.getKickHistory(player, targetId);
            if (warns == null || warns.isEmpty()) {
                PlayerManager.sendMessageToTarget(sender, Utilities.colorize(ConfigManager.messages.PLAYER_NEVER_KICKED.replace("{player}", player)));
                return;
            }
            PlayerManager.sendMessageToTarget(sender, ChatColor.DARK_AQUA + "-------- " + ChatColor.YELLOW + player + "'s Kick History" + ChatColor.DARK_AQUA + " --------");

            int count = 0;
            for (Ban b : warns) {
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("dd MMM yyyy HH:mm");

                Date now = new Date();
                int age = (int) ((now.getTime() - b.getBannedOn().getTime()) / 1000 / 86400);

                String warnedBy = " ";

                if (age >= ConfigManager.bans.KickExpiryDays) {
                    if (showStaffNames)
                        warnedBy = ChatColor.DARK_GRAY + " (" + ChatColor.DARK_GRAY + b.getBannedBy() + ChatColor.DARK_GRAY + ") ";

                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.GRAY + "- " +
                                    ChatColor.DARK_GRAY + sdf.format(b.getBannedOn()) +
                                    warnedBy +
                                    ChatColor.DARK_GRAY + b.getReason());
                } else {
                    count++;
                    if (showStaffNames)
                        warnedBy = ChatColor.YELLOW + " (" + ChatColor.GRAY + b.getBannedBy() + ChatColor.YELLOW + ") ";

                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.YELLOW + String.valueOf(count) + ": " +
                                    ChatColor.GREEN + sdf.format(b.getBannedOn()) +
                                    warnedBy +
                                    ChatColor.AQUA + b.getReason());
                }
            }
        });
    }

    public static void displayWhereHistory(final String sentBy, final String options, final String search) {
        ProxyServer.getInstance().getScheduler().runAsync(geSuit.getInstance(), () -> {
            GSPlayer s = PlayerManager.getPlayer(sentBy);
            final CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

            List<Track> tracking;
            if (search.contains(".")) {
                tracking = DatabaseManager.tracking.getPlayerTracking(search, "ip");
                if (tracking.isEmpty()) {
                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.RED + "[Tracker] No known accounts match or contain \"" + search + "\"");
                    return;
                } else {
                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.GREEN + "[Tracker] IP address \"" + search + "\" matches " + tracking.size() + " accounts/IPs:");
                }
            } else {
                String type;
                String searchString = search;
                if (searchString.length() > 20) {
                    type = "uuid";
                    searchString = searchString.replace("-", "");
                } else {
                    type = "name";
                }

                if (!DatabaseManager.players.playerExists(searchString)) {
                    // No exact match... do a partial match
                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.AQUA + "[Tracker] No accounts matched exactly \"" + searchString + "\", trying wildcard search..");

                    List<String> matches = DatabaseManager.players.matchPlayers(searchString);
                    if (matches.isEmpty()) {
                        PlayerManager.sendMessageToTarget(sender,
                                ChatColor.RED + "[Tracker] No known accounts match or contain \"" + searchString + "\"");
                        return;
                    } else if (matches.size() == 1) {
                        if (searchString.length() < 20) {
                            // Searched for a player name
                            searchString = matches.get(0);
                        }
                    } else {
                        // Matched too many names, show list of names instead
                        // (showing the 20 most recent matches)
                        PlayerManager.sendMessageToTarget(sender,
                                ChatColor.RED + "[Tracker] More than one player matched \"" + searchString + "\":");
                        for (String m : matches) {
                            PlayerManager.sendMessageToTarget(sender,
                                    ChatColor.AQUA + " - " + m);
                        }
                        return;
                    }
                }

                tracking = DatabaseManager.tracking.getPlayerTracking(searchString, type);
                if (tracking.isEmpty()) {
                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.GREEN + "[Tracker] No known accounts match or contain \"" + searchString + "\"");
                    return;
                } else {
                    PlayerManager.sendMessageToTarget(sender,
                          ChatColor.GREEN + "[Tracker] Player \"" + searchString + "\" is associated with " + tracking.size() + " accounts/IPs:");
                    if (options.contains("d")) {
                        for (Track t : tracking) {
                            try {
                                InetAddress a = InetAddress.getByName(t.getIp());
                                String msg = ConfigManager.messages.GEOIP_REPORT
                                      .replace("{ip]", t.getIp())
                                      .replace("{location}", GeoIPManager.lookup(a));
                                PlayerManager.sendMessageToTarget(sender, msg);
                            } catch (UnknownHostException ignore) {
                                //suppress
                            }
                        }
                    }
                    if (geSuit.proxy.getPlayer(searchString) != null) {
                        final ProxiedPlayer player = geSuit.proxy.getPlayer(searchString);
                        geSuit.proxy.getScheduler().runAsync(geSuit.getInstance(), () -> {
                            List<String> location = GeoIPManager.detailLookup(player.getAddress().getAddress());
                            if (location.size() < 0) {
                                PlayerManager.sendMessageToTarget(sender, ChatColor.GREEN + "[Tracker] Player " + player.getName() + "'s IP resolves: ");
                                location.forEach(s1 -> PlayerManager.sendMessageToTarget(sender, ChatColor.GREEN + "[Tracker] " + s1));
                            }
                        });
                    }
                }
            }

            // Construct a mapping betweeen UUID and the player's most recent username
            // Also keep track of the number of names associated with each uuid
            HashMap<String, String> uuidNameMap = new HashMap<>();
            HashMap<String, Integer> uuidNameCount = new HashMap<>();

            for (Track t : tracking) {
                String currentUuid = t.getUuid();

                uuidNameMap.put(currentUuid, t.getPlayer());
                uuidNameCount.merge(currentUuid, 1, Integer::sum);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Track t : tracking) {
                StringBuilder builder = new StringBuilder();
                builder.append(ChatColor.DARK_GREEN);
                builder.append(" - ");

                String playerName = t.getPlayer();
                if (uuidNameCount.get(t.getUuid()) > 1) {
                    String latestName = uuidNameMap.get(t.getUuid());
                    if (!latestName.equalsIgnoreCase(playerName)) {
                        playerName = latestName + " (" + playerName + ")";
                    }
                }

                if (t.isNameBanned()) {
                    builder.append(ChatColor.DARK_AQUA);
                    builder.append(playerName);
                    builder.append(ChatColor.GREEN);
                    if (t.getBanType().equals("ban")) {
                        builder.append("[Ban]");
                    } else {
                        builder.append("[Tempban]");
                    }
                } else {
                    builder.append(playerName);
                }

                builder.append(' ');

                if (t.isIpBanned()) {
                    builder.append(ChatColor.DARK_AQUA);
                    builder.append(t.getIp());
                    builder.append(ChatColor.GREEN);
                    builder.append("[IPBan]");
                } else {
                    builder.append(ChatColor.YELLOW);
                    builder.append(t.getIp());
                }

                builder.append(ChatColor.GRAY);
                builder.append(" (");
                builder.append(sdf.format(t.getLastSeen()));
                builder.append(')');

                PlayerManager.sendMessageToTarget(sender, builder.toString());
            }
        });
    }

    public static void displayPlayerOnTime(final String sentBy, final String player) {
        final GSPlayer s = PlayerManager.getPlayer(sentBy);
        final CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

        ProxyServer.getInstance().getScheduler().runAsync(geSuit.getInstance(), () -> {
            BanTarget bt = getBanTarget(player);
            if ((bt == null) || (bt.gsp == null)) {
                PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.PLAYER_DOES_NOT_EXIST);
                return;
            }

            // Get time records and set online time (if player is online)
            TimeRecord tr = DatabaseManager.ontime.getPlayerOnTime(bt.uuid);
            boolean online = (bt.gsp.getProxiedPlayer() != null);
            if (online) {
                tr.setTimeSession(System.currentTimeMillis() - bt.gsp.getLoginTime());
            } else {
                tr.setTimeSession(-1);
            }

            PlayerManager.sendMessageToTarget(sender, ChatColor.DARK_AQUA + "-------- " + ChatColor.YELLOW + bt.dispname + "'s OnTime Statistics" + ChatColor.DARK_AQUA + " --------");

            // Player join date/time + number of days
            Timestamp firstTime = bt.gsp.getFirstOnline();
            String firstJoin;
            if (firstTime != null) {
                firstJoin = String.format("%s %s",
                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(firstTime),
                        DateFormat.getTimeInstance(DateFormat.SHORT).format(firstTime));
            } else {
                firstJoin = "Uknown - check with admins";
            }
            String days = Integer.toString((int) Math.floor((System.currentTimeMillis() - bt.gsp.getFirstOnline().getTime()) / TimeUnit.DAYS.toMillis(1)));
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.ONTIME_FIRST_JOINED
                    .replace("{date}", firstJoin)
                    .replace("{days}", days));

            // Current session length
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.ONTIME_TIME_SESSION
                    .replace("{diff}", (tr.getTimeSession() == -1) ? ChatColor.RED + "Offline" : Utilities.buildTimeDiffString(tr.getTimeSession(), 3)));

            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.ONTIME_TIME_TODAY.replace("{diff}", Utilities.buildTimeDiffString(tr.getTimeToday() + tr.getTimeSession(), 3)));
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.ONTIME_TIME_WEEK.replace("{diff}", Utilities.buildTimeDiffString(tr.getTimeWeek() + tr.getTimeSession(), 3)));
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.ONTIME_TIME_MONTH.replace("{diff}", Utilities.buildTimeDiffString(tr.getTimeMonth() + tr.getTimeSession(), 3)));
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.ONTIME_TIME_YEAR.replace("{diff}", Utilities.buildTimeDiffString(tr.getTimeYear() + tr.getTimeSession(), 3)));
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.ONTIME_TIME_TOTAL.replace("{diff}", Utilities.buildTimeDiffString(tr.getTimeTotal() + tr.getTimeSession(), 3)));
        });
    }

    public static void displayOnTimeTop(final String sentBy, final int page) {
        final GSPlayer s = PlayerManager.getPlayer(sentBy);
        final CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

        ProxyServer.getInstance().getScheduler().runAsync(geSuit.getInstance(), () -> {
            int pagenum;
            pagenum = page;
            if (pagenum > 20) {
                PlayerManager.sendMessageToTarget(sender, ChatColor.RED + "Sorry, maximum page number is 20.");
                return;
            }
            // Get time records and set online time (if player is online)
            Map<String, Long> results = DatabaseManager.ontime.getOnTimeTop(pagenum);
            PlayerManager.sendMessageToTarget(sender, ChatColor.DARK_AQUA + "-------- " + ChatColor.YELLOW + "OnTime Top Statistics" + ChatColor.DARK_AQUA + " (page " + page + ") --------");
            int offset = (pagenum < 1) ? 0 : (pagenum - 1) * 10;    // Offset = Page number x 10 (but starts at 0 and no less than 0
            for (String name : results.keySet()) {
                offset++;
                String line = ConfigManager.messages.ONTIME_TIME_TOP
                        .replace("{num}", String.format("%1$2s", offset))
                        .replace("{time}", Utilities.buildTimeDiffString(results.get(name) * 1000, 2))
                        .replace("{player}", name);
                PlayerManager.sendMessageToTarget(sender, line);
            }
        });
    }
    public static void displayLastLogins(final String sentBy, final String player, final int num){
        final GSPlayer s = PlayerManager.getPlayer(sentBy);
        final CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

        ProxyServer.getInstance().getScheduler().runAsync(geSuit.getInstance(), () -> {
            BanTarget bt = getBanTarget(player);
            if ((bt == null) || (bt.gsp == null)) {
                PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.PLAYER_DOES_NOT_EXIST);
                return;
            }
            Map<Timestamp, Long> results = DatabaseManager.ontime.getLastLogins(bt.uuid, num);
            PlayerManager.sendMessageToTarget(sender, ChatColor.DARK_AQUA + "-------- " + ChatColor.YELLOW + bt.dispname + "'s Last Login History" + ChatColor.DARK_AQUA + " --------");
            for (Map.Entry<Timestamp, Long> pair : results.entrySet()) {
                String line = ConfigManager.messages.LASTLOGINS_FORMAT
                        .replace("{date}", DateFormat.getDateInstance(DateFormat.MEDIUM).format(pair.getKey()))
                        .replace("{ontime}", Utilities.buildTimeDiffString(pair.getValue() * 1000, 2));
                PlayerManager.sendMessageToTarget(sender, line);
            }
        });
    }

    
    public static void displayNameHistory(final String sentBy, final String nameOrId) {
        ProxyServer.getInstance().getScheduler().runAsync(geSuit.getInstance(), () -> {
            GSPlayer s = PlayerManager.getPlayer(sentBy);
            final CommandSender sender = (s == null ? ProxyServer.getInstance().getConsole() : s.getProxiedPlayer());

            UUID id;
            try {
                id = Utilities.makeUUID(nameOrId);
            } catch (IllegalArgumentException e) {
                Map<String, UUID> result = DatabaseManager.players.resolvePlayerNamesHistoric(Collections.singletonList(nameOrId));
                if (result.isEmpty()) {
                    PlayerManager.sendMessageToTarget(sender,
                            ChatColor.RED + "Unknown player " + nameOrId);
                    return;
                } else {
                    id = Iterables.getFirst(result.values(), null);
                }
            }

            List<Track> names = DatabaseManager.tracking.getNameHistory(id);

            PlayerManager.sendMessageToTarget(sender,
                    ChatColor.GREEN + "Player " + nameOrId + " has had " + names.size() + " different names:");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Track t : names) {

                String builder = ChatColor.DARK_GREEN +
                        " - " +
                        t.getPlayer() +
                        ' ' +
                        ChatColor.GRAY +
                        sdf.format(t.getLastSeen());
                PlayerManager.sendMessageToTarget(sender, builder);
            }
        });
    }
    
    private static void callEvent(final Event event) {
        ProxyServer.getInstance().getScheduler().runAsync(geSuit.getInstance(), () -> ProxyServer.getInstance().getPluginManager().callEvent(event));
    }

    private static void checkKickTempBan(int kickLimit, BanTarget t, String kickedBy, String reason) {


        long kickBanTime = TimeUnit.MILLISECONDS.toSeconds(ConfigManager.bans.TempBanTime);
        int kickCount = 0;

        List<String> reasonIgnores = ConfigManager.bans.KickReasonIgnoreList;
        if (!reasonIgnores.isEmpty()) {
            for (String item : reasonIgnores) {
                if (reason.contains(item)) {
                    // Do not consider this ban when counting kicks
                    // For example, ignore Anti-AFK autokicks
                    return;
                }
            }
        }

        if (kicks.size() > 0) {
            clearKicks();
            if (kicks.size() > 0) {
                for (Kick kick : kicks) { //find active kicks.
                    if (t.gsp.getUuid().equals(kick.getUuid())) {
                        kickCount++;
                    }
                }
                kickCount++; //add 1 for the current kick
                if (kickCount >= kickLimit) {
                    tempBanPlayer(kickedBy, t, kickBanTime, reason, false);
                    //clear this players kicks
                    kicks.removeIf(kick -> kick.getUuid().equals(t.uuid));
                } else {
                    kicks.add(new Kick(t.gsp.getUuid(), t.dispname, kickedBy, reason, System.currentTimeMillis()));
                }
            } else {
                kicks.add(new Kick(t.gsp.getUuid(), t.dispname, kickedBy, reason, System.currentTimeMillis()));
            }
        } else {
            kicks.add(new Kick(t.gsp.getUuid(), t.dispname, kickedBy, reason, System.currentTimeMillis()));
        }

    }

    public static void clearKicks() {
        long kickTimeOut = ConfigManager.bans.KicksTimeOut;
        //remove kicks that would have expired first
        kicks.removeIf(kick -> kick.getBannedOn() + kickTimeOut < System.currentTimeMillis());

    }


    public static List<Kick> getKicks() {
        return kicks;
    }


    private static class BanTarget {
    	String name = null;
    	String dispname = null;
		String uuid = null;
    	GSPlayer gsp = null;
    }
    
    private static BanTarget getBanTarget(String player) {
    	BanTarget target = new BanTarget();

    	// Try to find the player online
    	GSPlayer t = PlayerManager.matchOnlinePlayer(player);

        // If they are not online, try to find them as an offline player
        if (t == null) {
        	t = DatabaseManager.players.loadPlayer(player);
        }

        // Set up the target + display name we should use
        if (t == null) {
        	// Can't find this player so just use whatever player string was given to us
    		target.name = player;
    		target.dispname = player;
        } else {
        	// Get their real name, UUID and display name (alias)
        	if ((t.getUuid() != null) && (!t.getUuid().isEmpty())) {
        		target.uuid = t.getUuid();
        	}
        	target.name = t.getName();

        	// Get their display name (used for broadcasts and messages)
        	if (t.getProxiedPlayer() != null) {
        		target.dispname = t.getProxiedPlayer().getDisplayName();
        	} else {
        		target.dispname = t.getName();
        	}
        	target.gsp = t;
        }
    	return target;
    }
}
