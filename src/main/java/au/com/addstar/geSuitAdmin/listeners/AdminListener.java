package au.com.addstar.geSuitAdmin.listeners;

import au.com.addstar.geSuitAdmin.Utilities;
import au.com.addstar.geSuitAdmin.geSuitAdmin;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 7/08/2017.
 */
public class AdminListener implements PluginMessageListener, Listener{

    private geSuitAdmin instance;

    public AdminListener(geSuitAdmin instance) {
        this.instance = instance;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(instance.isDebug()) {
            instance.getLogger().info("DEBUG: " + Utilities.dumpPacket(channel,"RECV",message));
            instance.getLogger().info("Debug: this Server Name = " + instance.getName());
        }
        DataInputStream in = new DataInputStream( new ByteArrayInputStream( message ) );
        String task;
        try {
            task = in.readUTF();
            switch ( task ) {
                case "ServerRestart":
                    // servername sender milliSecs
                    String server = in.readUTF();
                    String sender = in.readUTF();
                    Long time =  in.readLong();

                    if (instance.getName().equals(server)) {
                        instance.getLogger().info("Restart issued by " +sender + " via geSuitAdmin in " + Utilities.buildTimeDiffString(time, 4));
                        instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "countdown start " + Utilities.buildShortTimeDiffString(time, 4) + " restart");
                    }else{
                        if(instance.isDebug()) {
                            instance.getLogger().info("Debug: this Server Name = " + instance.getName());
                        }
                    }
                    break;
            }

        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
