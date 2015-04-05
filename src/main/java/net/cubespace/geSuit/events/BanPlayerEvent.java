package net.cubespace.geSuit.events;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

import com.google.common.base.Strings;

import net.cubespace.geSuit.Utilities;
import net.cubespace.geSuit.objects.Ban;
import net.md_5.bungee.api.plugin.Event;

public class BanPlayerEvent extends Event {
    private Ban ban;
    private boolean isAuto;
    
    public BanPlayerEvent(Ban ban, boolean isAuto) {
        this.ban = ban;
        this.isAuto = isAuto;
    }
    
    public String getPlayerName() {
        return ban.getPlayer();
    }
    
    public UUID getPlayerId() {
        if (Strings.isNullOrEmpty(ban.getUuid()))
            return null;
        return Utilities.makeUUID(ban.getUuid());
    }
    
    public InetAddress getPlayerIP() {
        if (ban.getIp() == null)
            return null;
        try {
            return InetAddress.getByName(ban.getIp());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getReason() {
        return ban.getReason();
    }
    
    public BanType getType() {
        switch (ban.getType())
        {
        case "tempban":
            return BanType.Temporary;
        case "ipban":
            return BanType.IP;
        default:
            return BanType.Name;
        }
    }
    
    public String getBannedBy() {
        return ban.getBannedBy();
    }
    
    public Date getUnbanDate() {
        if (getType() != BanType.Temporary) {
            return new Date(Long.MAX_VALUE);
        }
        return ban.getBannedUntil();
    }
    
    public boolean isAutomatic() {
        return isAuto;
    }
    
    public enum BanType {
        Name,
        IP,
        Temporary
    }
}
