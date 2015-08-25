package net.cubespace.geSuit;

import net.cubespace.geSuit.commands.*;
import net.cubespace.geSuit.database.ConnectionHandler;
import net.cubespace.geSuit.database.convert.Converter;
import net.cubespace.geSuit.listeners.APIMessageListener;
import net.cubespace.geSuit.listeners.BansMessageListener;
import net.cubespace.geSuit.listeners.BungeeChatListener;
import net.cubespace.geSuit.listeners.HomesMessageListener;
import net.cubespace.geSuit.listeners.PlayerListener;
import net.cubespace.geSuit.listeners.PortalsMessageListener;
import net.cubespace.geSuit.listeners.SpawnListener;
import net.cubespace.geSuit.listeners.SpawnMessageListener;
import net.cubespace.geSuit.listeners.TeleportsListener;
import net.cubespace.geSuit.listeners.TeleportsMessageListener;
import net.cubespace.geSuit.listeners.WarpsMessageListener;
import net.cubespace.geSuit.managers.ConfigManager;
import net.cubespace.geSuit.managers.DatabaseManager;
import net.cubespace.geSuit.managers.GeoIPManager;
import net.cubespace.geSuit.managers.LoggingManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class geSuit extends Plugin
{

    public static geSuit instance;
    public static ProxyServer proxy;
    private boolean DebugEnabled = false;

    public void onEnable()
    {
        instance = this;
        LoggingManager.log(ChatColor.GREEN + "Starting geSuit");
        proxy = ProxyServer.getInstance();
        LoggingManager.log(ChatColor.GREEN + "Initialising Managers");

        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();
        connectionHandler.release();

        if (ConfigManager.main.ConvertFromBungeeSuite) {
            Converter converter = new Converter();
            converter.convert();
        }

        registerListeners();
        registerCommands();
        GeoIPManager.initialize();
    }

    private void registerCommands()
    {
        // A little hardcore. Prevent updating without a restart. But command squatting = bad!
        if (ConfigManager.main.MOTD_Enabled) {
            proxy.getPluginManager().registerCommand(this, new MOTDCommand());
        }
        if (ConfigManager.main.Seen_Enabled) {
            proxy.getPluginManager().registerCommand(this, new SeenCommand());
        }
        proxy.getPluginManager().registerCommand(this, new UnbanCommand());
        proxy.getPluginManager().registerCommand(this, new BanCommand());
        proxy.getPluginManager().registerCommand(this, new TempBanCommand());
        proxy.getPluginManager().registerCommand(this, new WarnCommand());
        proxy.getPluginManager().registerCommand(this, new WhereCommand());
        proxy.getPluginManager().registerCommand(this, new ReloadCommand());
        proxy.getPluginManager().registerCommand(this, new DebugCommand());
        proxy.getPluginManager().registerCommand(this, new WarnHistoryCommand());
        proxy.getPluginManager().registerCommand(this, new NamesCommand());
        if (ConfigManager.bans.TrackOnTime) {
        	proxy.getPluginManager().registerCommand(this, new OnTimeCommand());
            proxy.getPluginManager().registerCommand(this, new LastLoginsCommand());
        }
    }

    private void registerListeners()
    {
        getProxy().registerChannel("geSuitTeleport");       // Teleport out/in
        getProxy().registerChannel("geSuitSpawns");         // Spawns out/in
        getProxy().registerChannel("geSuitBans");           // Bans in
        getProxy().registerChannel("geSuitPortals");        // Portals out/in
        getProxy().registerChannel("geSuitWarps");          // Warps in
        getProxy().registerChannel("geSuitHomes");          // Homes in
        getProxy().registerChannel("geSuitAPI");            // API messages in

        proxy.getPluginManager().registerListener(this, new PlayerListener());
        proxy.getPluginManager().registerListener(this, new BansMessageListener());
        proxy.getPluginManager().registerListener(this, new TeleportsListener());
        proxy.getPluginManager().registerListener(this, new TeleportsMessageListener());
        proxy.getPluginManager().registerListener(this, new WarpsMessageListener());
        proxy.getPluginManager().registerListener(this, new HomesMessageListener());
        proxy.getPluginManager().registerListener(this, new PortalsMessageListener());
        proxy.getPluginManager().registerListener(this, new SpawnListener());
        proxy.getPluginManager().registerListener(this, new SpawnMessageListener());
        proxy.getPluginManager().registerListener(this, new APIMessageListener());
        if (ConfigManager.main.BungeeChatIntegration) {
            proxy.getPluginManager().registerListener(this, new BungeeChatListener());
        }
    }

    public void onDisable()
    {
        DatabaseManager.connectionPool.closeConnections();
    }

	public boolean isDebugEnabled() {
		return DebugEnabled;
	}

	public void setDebugEnabled(boolean debugEnabled) {
		DebugEnabled = debugEnabled;
	}
	
	public void DebugMsg(String msg) {
		if (isDebugEnabled()) {
			geSuit.instance.getLogger().info("DEBUG: " + msg);
		}
	}
}