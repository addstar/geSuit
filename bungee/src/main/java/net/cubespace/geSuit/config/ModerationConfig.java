package net.cubespace.geSuit.config;

import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Config;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

public class ModerationConfig extends Config {
    public ModerationConfig(File file) {
        CONFIG_FILE = file;
    }

    public Boolean Enabled = true;
    public Boolean BroadcastBans = true;
    public Boolean BroadcastUnbans = false;
    public Boolean BroadcastWarns = true;
    public Boolean BroadcastKicks = true;
    public Boolean ShowAltAccounts = true;
    public Boolean ShowBannedAltAccounts = true;
    public Boolean TrackOnTime = true;
    public int NameChangeNotifyTime = 20;

    public String LockdownTime = "5m";
    @Comment("if set to true the server will start lockedDown and will release in 5minutes")
    public boolean LockedDown = false;
    @Comment("if set and no message is set when the lockdown is started this will be used")
    public String LockDownStartupMsg = "";

    public int WarningExpiryDays = 180;
    
    public String DefaultBanReason = "Unknown";
    public String DefaultWarnReason = "Unknown";
    public String DefaultKickReason = "Unknown";
    
    public MuteSettings Mutes = new MuteSettings();

    public HashMap<Integer, String> Actions = new HashMap<Integer, String>();
    
    public GeoIPSettings GeoIP = new GeoIPSettings();
    
    public static class GeoIPSettings extends Config {
        public boolean ShowOnLogin = true;
        
        public boolean ShowCity = true;
        public boolean DownloadIfMissing = true;

        @Comment("URL for the database that provides country level lookups only")
        public String DownloadURL = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz";
        @Comment("URL for the database that provides city level lookups")
        public String CityDownloadURL = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz";
    }
    
    public static class MuteSettings extends Config {
        @Comment("When true, all player and ip mutes will be broadcast on mute")
        public boolean BroadcastMute = true;
        @Comment("When true, all player and ip mutes will be broadcast on manual unmute")
        public boolean BroadcastUnmute = false;
        @Comment("When true, all player and ip mutes will be broadcast on automatic unmute")
        public boolean BroadcastAutoUnmute = false;
        @Comment("When true, global mute will be broadcast on mute and unmute")
        public boolean BroadcastGlobal = true;
        
        @Comment("The maximum duration for mutes. eg. 5m, 1h10m. Can be 'none'")
        public String MaximumMuteDuration = "20m";
        @Comment("When true, mutes without a time limit can be used")
        public boolean AllowPermanentMutes = true;
        @Comment("When false, players that are already muted cannot be muted again without unmuting them first")
        public boolean AllowReMute = true;
        
        @Comment("The commands to block, or allow based on CommandListIsWhitelist. When in whitelist mode, these are the commands muted players can use")
        public List<String> CommandsList = Lists.newArrayList();
        public boolean CommandListIsWhitelist = true;
    }
}
