package au.com.addstar.geSuitAdmin;

import au.com.addstar.geSuitAdmin.commands.DebugCommand;
import au.com.addstar.geSuitAdmin.listeners.AdminListener;
import net.cubespace.geSuit.BukkitModule;
import net.cubespace.geSuitTeleports.geSuitTeleports;
import net.cubespace.geSuiteSpawn.geSuitSpawn;
import org.bukkit.Bukkit;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 7/08/2017.
 */
public class geSuitAdmin extends BukkitModule {

    public geSuitAdmin() {
        super("admin", false);
        setDebug(false);
    }

    @Override
    protected void registerListeners() {
        registerPluginMessageListener(this,new AdminListener(this));
    }

    @Override
    public void setDebug(boolean debug) {
        super.setDebug(debug);
    }

    protected void registerCommands() {
        getCommand("gesuitdebug").setExecutor(new DebugCommand(this));
    }
}
