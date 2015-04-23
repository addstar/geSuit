package net.cubespace.geSuit.listeners;

import net.cubespace.geSuit.managers.PlayerManager;
import net.cubespace.geSuit.pluginmessages.LeavingServer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TeleportsListener implements Listener {
    @EventHandler(priority=(byte)255)
    public void serverChange(ServerConnectEvent e) {
        if (!e.isCancelled() && e.getPlayer().getServer() != null)
            LeavingServer.execute(PlayerManager.getPlayer(e.getPlayer().getName(), true));
    }
}
