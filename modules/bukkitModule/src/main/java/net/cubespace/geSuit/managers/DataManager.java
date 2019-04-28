package net.cubespace.geSuit.managers;

import net.cubespace.geSuit.BukkitModule;

import org.bukkit.ChatColor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 17/09/2018.
 */
public abstract class DataManager {

    protected final BukkitModule instance;

    public DataManager(BukkitModule instance) {
        this.instance = instance;
    }

    public void sendVersion() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("SendVersion");
            out.writeUTF(ChatColor.RED + instance.getName() + " - " + ChatColor.GOLD + instance.getDescription().getVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }
        instance.sendMessage(b);
    }
}
