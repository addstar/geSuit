package net.cubespace.geSuit.listeners;

import net.cubespace.geSuit.geSuit;
import net.cubespace.geSuit.managers.LoggingManager;
import net.cubespace.geSuit.managers.PlayerManager;
import net.cubespace.geSuit.managers.TeleportManager;
import net.cubespace.geSuit.objects.GSPlayer;
import net.cubespace.geSuit.objects.Location;
import net.cubespace.geSuit.pluginmessages.TeleportToLocation;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class TeleportsMessageListener extends MessageListener {

    public TeleportsMessageListener(boolean legacy) {
        super(legacy, geSuit.CHANNEL_NAMES.TELEPORT_CHANNEL);
    }

    @EventHandler
    public void receivePluginMessage(PluginMessageEvent event) throws IOException {
        if (!eventMatched(event)) return;

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String task = in.readUTF();

        if (task.equals("TpAccept")) {
            TeleportManager.acceptTeleportRequest(PlayerManager.getPlayer(in.readUTF(), true));
            return;
        }

        if (task.equals("TeleportToLocation")) {
            GSPlayer player = PlayerManager.getPlayer(in.readUTF(), true);
            String server = in.readUTF();
            String serverName = (!server.equals("")) ? server : ((Server) event.getSender()).getInfo().getName();
            String world = in.readUTF();
    
            double X = in.readDouble();
            double Y = in.readDouble();
            double Z = in.readDouble();

            float yaw = 0;
            float pitch = 0;

            try {
                yaw = in.readFloat();

                try {
                    pitch = in.readFloat();
                } catch (IOException e){
                    // Leave pitch unchanged
                }
            } catch (IOException e){
                // Leave yaw unchanged
            }

            TeleportToLocation.execute(player, new Location(serverName, world, X, Y, Z, yaw, pitch));
            return;
        }

        if (task.equals("PlayersTeleportBackLocation")) {
            GSPlayer player = PlayerManager.getPlayer(in.readUTF());
            if (player != null) {
            	TeleportManager.setPlayersTeleportBackLocation(player, new Location(((Server) event.getSender()).getInfo(), in.readUTF(), in.readDouble(), in.readDouble(), in.readDouble(), in.readFloat(), in.readFloat()));
            }
            return;
        }

        if (task.equals("PlayersDeathBackLocation")) {
            TeleportManager.setPlayersDeathBackLocation(PlayerManager.getPlayer(in.readUTF(), true), new Location(((Server) event.getSender()).getInfo(), in.readUTF(), in.readDouble(), in.readDouble(), in.readDouble(), in.readFloat(), in.readFloat()));
            return;
        }

        if (task.equals("TeleportToPlayer")) {
            TeleportManager.teleportPlayerToPlayer(in.readUTF(), in.readUTF(), in.readUTF(), in.readBoolean(), in.readBoolean());
            return;
        }

        if (task.equals("TpaHereRequest")) {
            TeleportManager.requestPlayerTeleportToYou(in.readUTF(), in.readUTF());
            return;
        }

        if (task.equals("TpaRequest")) {
            TeleportManager.requestToTeleportToPlayer(in.readUTF(), in.readUTF());
            return;
        }

        if (task.equals("TpDeny")) {
            TeleportManager.denyTeleportRequest(PlayerManager.getPlayer(in.readUTF(), true));
            return;
        }

        if (task.equals("TpAll")) {
            TeleportManager.tpAll(in.readUTF(), in.readUTF());
            return;
        }

        if (task.equals("SendPlayerBack")) {
            TeleportManager.sendPlayerToLastBack(PlayerManager.getPlayer(in.readUTF(), true), in.readBoolean(), in.readBoolean());
            return;
        }

        if (task.equals("ToggleTeleports")) {
            TeleportManager.togglePlayersTeleports(PlayerManager.getPlayer(in.readUTF(), true));
            return;
        }

        if (task.equals("SendToServer")) {
            TeleportManager.sendPlayerToServer(in.readUTF(), in.readUTF());
            return;
        }

        if (task.equals("SendVersion")) {
            LoggingManager.log(in.readUTF());
        }
    }

}
