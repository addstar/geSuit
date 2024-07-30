package net.cubespace.geSuit.listeners;

import net.cubespace.geSuit.Utilities;
import net.cubespace.geSuit.geSuit;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 7/08/2017.
 */
public class AdminMessageListener extends MessageListener {

    public AdminMessageListener(boolean legacy) {
        super(legacy, geSuit.CHANNEL_NAMES.ADMIN_CHANNEL);
    }

    @EventHandler
    public void receivePluginMessage(PluginMessageEvent event) {
        if (geSuit.getInstance().isDebugEnabled()) {
            if (geSuit.getInstance().getDebugLevel() == 2) {
                // Dump all packets
                Utilities.dumpPacket(event.getTag(), "RECV", event.getData(), true);
            } else {
                // Only dump the packet if it is a geSuit packet
                if (event.getTag().startsWith("gesuit:")) {
                    Utilities.dumpPacket(event.getTag(), "RECV", event.getData(), false);
                }
            }
        }
        if (!eventMatched(event)) {
        }
        //todo any message processing here
    }
}