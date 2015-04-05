package net.cubespace.geSuit.events;

import java.util.UUID;

import net.cubespace.geSuit.Utilities;
import net.md_5.bungee.api.plugin.Event;

public class WarnPlayerEvent extends Event {
    
    private String player;
    private UUID id;
    private String by;
    private String reason;
    private ActionType action;
    private String actionExtra;
    private int warnCount;
    
    public WarnPlayerEvent(String player, String uuid, String by, String reason, ActionType type, String actionExtra, int count) {
        this.player = player;
        this.id = Utilities.makeUUID(uuid);
        this.by = by;
        this.reason = reason;
        this.action = type;
        this.actionExtra = actionExtra;
        this.warnCount = count;
    }
    
    public String getPlayerName() {
        return player;
    }
    
    public UUID getPlayerId() {
        return id;
    }
    
    public String getReason() {
        return reason;
    }
    
    public String getBy() {
        return by;
    }
    
    public ActionType getAction() {
        return action;
    }
    
    public String getActionExtra() {
        return actionExtra;
    }
    
    public int getWarnCount() {
        return warnCount;
    }
    
    public enum ActionType {
        None,
        Kick,
        TempBan,
        Ban
    }
}
