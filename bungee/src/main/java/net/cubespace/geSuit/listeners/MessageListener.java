package net.cubespace.geSuit.listeners;


import net.cubespace.geSuit.geSuit;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 31/08/2018.
 */
public abstract class MessageListener implements Listener {
    private final geSuit.CHANNEL_NAMES channelName;

    public MessageListener(boolean legacy, geSuit.CHANNEL_NAMES channel) {
        channelName = channel;
    }

    /**
     * If true the handler should continue processing the event - but the event will now be cancelled.
     * @param event The event to check
     * @return boolean
     */
    public boolean eventMatched(PluginMessageEvent event) {
        if (event.isCancelled()) return false;
        if (!(event.getSender() instanceof Server)) return false;
        if (event.getTag().equalsIgnoreCase(channelName.toString())
                || (event.getTag().equalsIgnoreCase(channelName.getLegacy()))) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }
}
