package net.cubespace.geSuit.configs.SubConfig;

import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.YamlConfig;

public class GeoIPSettings extends YamlConfig {

    public final boolean ShowOnLogin = true;

    public final boolean ShowCity = true;
    public final boolean DownloadIfMissing = true;

    @Comment("URL for the database that provides country level lookups only")
    public final String DownloadURL = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz";
    @Comment("URL for the database that provides city level lookups")
    public final String CityDownloadURL = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz";
}
