package net.cubespace.geSuit.configs;

import net.cubespace.Yamler.Config.YamlConfig;
import net.cubespace.geSuit.geSuit;

import java.io.File;
import java.util.ArrayList;

public class TeleportConfig extends YamlConfig {
    public TeleportConfig() {
        CONFIG_FILE = new File(geSuit.getInstance().getDataFolder(), "teleport.yml");
    }

    public Integer TeleportRequestExpireTime = 10;
    
    public Boolean SenderNeedsEnabledTpToggle = true;

    public ArrayList<String> TPAWhitelist = new ArrayList<>();
}
