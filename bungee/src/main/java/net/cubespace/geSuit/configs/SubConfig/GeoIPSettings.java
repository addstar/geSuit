package net.cubespace.geSuit.configs.SubConfig;

import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.YamlConfig;

public class GeoIPSettings extends YamlConfig {

    public final boolean ShowOnLogin = true;
    @Comment("Customise the path to your GeoIP2 Files")
    public String pathToGeoIPFiles = "/usr/share/GeoIP/";

    @Comment("GeoIP City Database")
    public String geoIPCity = "GeoLite2-City.mmdb";

    @Comment("GeoIP City Database")
    public String geoIPCountry = "GeoLite2-Country.mmdb";

    @Comment("GeoIP City Database")
    public String geoIPASN = "GeoLite2-ASN.mmdb";

    public final boolean ShowCity = true;
    public final boolean ShowDetail = false;
}
