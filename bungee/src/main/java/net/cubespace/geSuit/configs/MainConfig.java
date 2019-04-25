package net.cubespace.geSuit.configs;

import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.ConfigMode;
import net.cubespace.Yamler.Config.YamlConfig;
import net.cubespace.geSuit.configs.SubConfig.Database;
import net.cubespace.geSuit.geSuit;

import java.io.File;

public class MainConfig extends YamlConfig {
    public MainConfig() {
        CONFIG_FILE = new File(geSuit.getInstance().getDataFolder(), "config.yml");
        CONFIG_MODE = ConfigMode.PATH_BY_UNDERSCORE;
    }

    public MainConfig(File file) {
        CONFIG_FILE = file;
        CONFIG_MODE = ConfigMode.PATH_BY_UNDERSCORE;
    }
    public Database Database = new Database();

    @Comment("This can be used if you have multiple Proxies to seperate the Homes in it")
    public String Table_Homes = "homes";
    @Comment("This can be used if you have multiple Proxies to seperate the Players in it")
    public String Table_Players = "players";
    @Comment("This can be used if you have multiple Proxies to seperate the Warps in it")
    public String Table_Warps = "warps";
    @Comment("This can be used if you have multiple Proxies to seperate the Bans in it")
    public String Table_Bans = "bans";
    @Comment("This can be used if you have multiple Proxies to seperate the Portals in it")
    public String Table_Portals = "portals";
    @Comment("This can be used if you have multiple Proxies to seperate the Spawns in it")
    public String Table_Spawns = "spawns";
    @Comment("This can be used if you have multiple Proxies to seperate the Tracking in it")
    public String Table_Tracking = "tracking";
    @Comment("This can be used if you have multiple Proxies to seperate the Ontime in it")
    public String Table_OnTime = "ontime";

    public Boolean ConvertFromBungeeSuite = false;
    public Database BungeeSuiteDatabase = new Database();

    @Comment("Turn this to false if you want to use your regular /motd comand (requires restart)")
    public Boolean MOTD_Enabled = true;
    @Comment("Turn this to false if you want to use your your regular /seen comand (requires restart)")
    public Boolean Seen_Enabled = false;
    
    @Comment()
    public Boolean NewPlayerBroadcast = true;
    public Boolean BroadcastProxyConnectionMessages = true;
    public Integer PlayerDisconnectDelay = 0;
    @Comment("This should be true on offline Mode Server since they can't use UUIDs provided by Mojang")
    public Boolean OverwriteUUID = false;
    @Comment("The minimum version allowed to join the server - players less than this version will be disallowed and recieve a message.")
    public String minClientVersion = "1.8";
    @Comment("The minimum protocol - use wiki.vg - allowed to join the server")
    public Integer minClientProtocol = 47;

    @Comment("Legacy channel handling...channels names prior to 1.13 were not namespaced...enabling this will support legacy and namespaced names.")
    public Boolean enableLegacy = false;

    @Comment("Enable this if you want to use BungeeChat with geSuit.")
    public Boolean BungeeChatIntegration = false;

    @Comment("Do not alter this. It will be used automatically.")
    public Boolean Inited = false;

    @Comment("Stored version informations. If you alter this you can damage your Database")
    public Integer Version_Database_Ban = 3;
    @Comment("Stored version informations. If you alter this you can damage your Database")
    public Integer Version_Database_Homes = 2;
    @Comment("Stored version informations. If you alter this you can damage your Database")
    public Integer Version_Database_Players = 3;
    @Comment("Stored version informations. If you alter this you can damage your Database")
    public Integer Version_Database_Portals = 1;
    @Comment("Stored version informations. If you alter this you can damage your Database")
    public Integer Version_Database_Spawns = 1;
    @Comment("Stored version informations. If you alter this you can damage your Database")
    public Integer Version_Database_Warps = 1;
    @Comment("Stored version informations. If you alter this you can damage your Database")
    public Integer Version_Database_Tracking = 1;
}
