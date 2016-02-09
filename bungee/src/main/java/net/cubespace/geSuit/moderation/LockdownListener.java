package net.cubespace.geSuit.moderation;


import net.cubespace.geSuit.core.util.Utilities;
import net.cubespace.geSuit.events.GlobalPlayerPreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by Narimm on 6/02/2016.
 */
public class LockdownListener implements Listener {
    private final LockdownManager manager;
    private final Logger logger;

    public LockdownListener(LockdownManager man, Logger logger) {
        manager = man;
        this.logger = logger;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void doLockDownCheck(GlobalPlayerPreLoginEvent event) {
        if (manager.checkExpiry()) {
            //dont do anything lockdown expired
        } else {
            if (event.getPlayer().isNewPlayer()) {
                event.setCancelled(true);
                event.denyLogin(manager.denyMessage());
                logger.log(Level.INFO, event.getPlayer().getName() + "(" + Utilities.toString(event.getPlayer().getUniqueId()) + ") login denied due to lockdown Expiry in" + manager.getExpiryIn());
            }
        }
    }

}
