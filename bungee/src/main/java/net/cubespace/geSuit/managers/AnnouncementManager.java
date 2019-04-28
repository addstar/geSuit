package net.cubespace.geSuit.managers;

import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.geSuit.configs.SubConfig.AnnouncementEntry;
import net.cubespace.geSuit.geSuit;
import net.cubespace.geSuit.tasks.GlobalAnnouncements;
import net.cubespace.geSuit.tasks.ServerAnnouncements;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AnnouncementManager {
    public static final ArrayList<ScheduledTask> announcementTasks = new ArrayList<>();
    static final ProxyServer proxy = ProxyServer.getInstance();

    public static void loadAnnouncements() {
        // create defaults
        setDefaults();

        // load global announcements
        if (ConfigManager.announcements.Enabled) {
            List<String> global = ConfigManager.announcements.Announcements.get("global").Messages;
            if (!global.isEmpty()) {
                int interval = ConfigManager.announcements.Announcements.get("global").Interval;
                if (interval > 0) {
                    GlobalAnnouncements g = new GlobalAnnouncements();
                    for (String messages : global) {
                        g.addAnnouncement(messages);
                    }
                    ScheduledTask t = proxy.getScheduler().schedule(geSuit.getInstance(), g, interval, interval, TimeUnit.SECONDS);
                    announcementTasks.add(t);
                }
            }

            //load server announcements
            for (String server : proxy.getServers().keySet()) {
                List<String> servermes = ConfigManager.announcements.Announcements.get(server).Messages;
                if (!servermes.isEmpty()) {
                    int interval = ConfigManager.announcements.Announcements.get(server).Interval;
                    if (interval > 0) {
                        ServerAnnouncements s = new ServerAnnouncements(proxy.getServerInfo(server));
                        for (String messages : servermes) {
                            s.addAnnouncement(messages);
                        }
                        ScheduledTask t = proxy.getScheduler().schedule(geSuit.getInstance(), s, interval, interval, TimeUnit.SECONDS);
                        announcementTasks.add(t);
                    }
                }
            }
        }
    }

    private static void setDefaults() {
        Map<String, AnnouncementEntry> check = ConfigManager.announcements.Announcements;
        if (!check.containsKey("global")) {
            AnnouncementEntry announcementEntry = new AnnouncementEntry();
            announcementEntry.Interval = 300;
            announcementEntry.Messages.add("&4Welcome to the server!");
            announcementEntry.Messages.add("&aDon't forget to check out our website");

            check.put("global", announcementEntry);
        }

        for (String server : proxy.getServers().keySet()) {
            if (!check.containsKey(server)) {
                AnnouncementEntry announcementEntry = new AnnouncementEntry();
                announcementEntry.Interval = 150;
                announcementEntry.Messages.add("&4Welcome to the " + server + " server!");
                announcementEntry.Messages.add("&aDon't forget to check out our website");

                check.put(server, announcementEntry);
            }
        }

        try {
            ConfigManager.announcements.save();
        } catch (InvalidConfigurationException ignored) {

        }
    }

    public static void reloadAnnouncements() {
        for (ScheduledTask task : announcementTasks) {
            task.cancel();
        }

        announcementTasks.clear();
        loadAnnouncements();
    }
}
