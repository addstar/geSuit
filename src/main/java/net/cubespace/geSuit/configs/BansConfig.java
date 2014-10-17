package net.cubespace.geSuit.configs;

import net.cubespace.geSuit.geSuit;
import net.cubespace.geSuit.configs.SubConfig.GeoIPSettings;
import net.cubespace.Yamler.Config.Config;

import java.io.File;
import java.util.HashMap;

public class BansConfig extends Config {
    public BansConfig() {
        CONFIG_FILE = new File(geSuit.instance.getDataFolder(), "bans.yml");
    }

    public Boolean Enabled = true;
    public Boolean BroadcastBans = true;
    public Boolean BroadcastUnbans = false;
    public Boolean BroadcastWarns = true;
    public Boolean BroadcastKicks = true;
    public Boolean DetectAltAccounts = true;
    public Boolean ShowAltAccountsOnlyIfBanned = true;
    
    public HashMap<Integer, String> Actions = new HashMap<>();
    
    public GeoIPSettings GeoIP = new GeoIPSettings();
}
