package net.cubespace.geSuit.pluginmessages;

import net.cubespace.geSuit.geSuit;
import net.cubespace.geSuit.managers.LoggingManager;
import net.cubespace.geSuit.objects.GSPlayer;
import net.cubespace.geSuit.objects.Location;
import net.cubespace.geSuit.tasks.SendPluginMessage;
import net.md_5.bungee.api.ChatColor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class TeleportToLocation
{
    public static void execute(GSPlayer player, Location location)
    {
        if (location == null) {
            geSuit.instance.getLogger().severe("Location is null, this should never happen. Please check");
            new Exception("").printStackTrace();
            return;
        }
        
        if (location.getServer() == null) {
            geSuit.getInstance().getLogger().severe("Location has no Server, this should never happen. Please check");
            new Exception("").printStackTrace();
            return;
        }

        if (player == null) {
        	LoggingManager.log(ChatColor.RED + "Warning! Teleport called but player is null!");
            new Exception("").printStackTrace();
        	return;
        }
        
        if (player.getServer() == null || !player.getServer().equals(location.getServer().getName())) {
            player.getProxiedPlayer().connect(location.getServer());
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);

        try {
            out.writeUTF("TeleportToLocation");
            out.writeUTF(player.getName());
            out.writeUTF(location.getWorld());
            out.writeDouble(location.getX());
            out.writeDouble(location.getY());
            out.writeDouble(location.getZ());
            out.writeFloat(location.getYaw());
            out.writeFloat(location.getPitch());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    
        geSuit.proxy.getScheduler().runAsync(geSuit.getInstance(), new SendPluginMessage(geSuit
                .CHANNEL_NAMES.TELEPORT_CHANNEL.toString(), location.getServer(), bytes));
    }
}
