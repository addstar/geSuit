package net.cubespace.geSuit.tasks;

import java.util.ArrayList;
import java.util.Collection;

import net.cubespace.geSuit.Utilities;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GlobalAnnouncements implements Runnable
{

    private final ArrayList<String> list = new ArrayList<>();
    private int count = 0;

    public void addAnnouncement(String message)
    {
        list.add(Utilities.colorize(message));
    }

    public void run()
    {
        if (list.isEmpty()) {
            return;
        }

        Collection<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers();
        if (players.isEmpty()) {
            return;
        }
        
        for (ProxiedPlayer player : players) {
            for (String line : list.get(count).split("\n")) {
                // not sure if everything is thread safe. In doubt, leaving that one. It's colorized anyway.
                player.sendMessage(TextComponent.fromLegacyText(Utilities.colorize(line)));
            }
        }

        count++;

        if ((count + 1) > list.size()) {
            count = 0;
        }
    }
}
