package net.cubespace.geSuit.listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;

import net.cubespace.geSuit.Utilities;
import net.cubespace.geSuit.geSuit;
import net.cubespace.geSuit.managers.ConfigManager;
import net.cubespace.geSuit.managers.LoggingManager;
import net.cubespace.geSuit.managers.PlayerManager;
import net.cubespace.geSuit.managers.PortalManager;
import net.cubespace.geSuit.objects.GSPlayer;
import net.cubespace.geSuit.objects.Location;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PortalsMessageListener implements Listener {

    @EventHandler
    public void receivePluginMessage(PluginMessageEvent event) throws IOException, SQLException {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getSender() instanceof Server))
            return;
        if (!event.getTag().equalsIgnoreCase("geSuitPortals")) {
            return;
        }

        // Message debugging (can be toggled live)
		if (geSuit.instance.isDebugEnabled()) {
			Utilities.dumpPacket(event.getTag(), "RECV", event.getData(), true);
		}

        event.setCancelled(true);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

        String task = in.readUTF();
        Server s = (Server) event.getSender();
        if (task.equals("TeleportPlayer")) {
            PortalManager.teleportPlayer(PlayerManager.getPlayer(in.readUTF(), true), in.readUTF(), in.readUTF(), in.readBoolean());
        } else if (task.equals("ListPortals")) {
            PortalManager.listPortals(PlayerManager.getPlayer(in.readUTF(), true));
        } else if (task.equals("DeletePortal")) {
            PortalManager.deletePortal(PlayerManager.getPlayer(in.readUTF(), true), in.readUTF());
        } else if (task.equals("SetPortal")) {
            GSPlayer sender = PlayerManager.getPlayer(in.readUTF(), true);
            boolean selection = in.readBoolean();
            if (!selection) {
                PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.NO_SELECTION_MADE);
            } else {
                PortalManager.setPortal(sender, in.readUTF(), in.readUTF(), in.readUTF(), in.readUTF(), new Location(s.getInfo(), in.readUTF(), in.readDouble(), in.readDouble(), in.readDouble()), new Location(s.getInfo(), in.readUTF(), in.readDouble(), in.readDouble(), in.readDouble()));
            }
        } else if (task.equals("RequestPortals")) {
            PortalManager.getPortals(s.getInfo());
        } else if (task.equals("SendVersion")) {
            LoggingManager.log(in.readUTF());
        }

        in.close();

    }

}
