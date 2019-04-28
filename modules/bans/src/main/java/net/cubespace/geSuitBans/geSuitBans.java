package net.cubespace.geSuitBans;

import net.cubespace.geSuitBans.commands.*;
import net.cubespace.geSuit.BukkitModule;
import net.cubespace.geSuitBans.managers.BansManager;

import java.util.Objects;

public class geSuitBans extends BukkitModule {

    public geSuitBans() {
        super("bans",true);
    }
    
    protected void registerCommands() {
        BansManager manager = new BansManager(this);
        Objects.requireNonNull(getCommand("ban")).setExecutor(new BanCommand(manager, this));
        Objects.requireNonNull(getCommand("warn")).setExecutor(new WarnCommand(manager));
        Objects.requireNonNull(getCommand("checkban")).setExecutor(new CheckBanCommand(manager));
        Objects.requireNonNull(getCommand("banhistory")).setExecutor(new BanHistoryCommand(manager));
        Objects.requireNonNull(getCommand("warnhistory")).setExecutor(new WarnHistoryCommand(manager));
        Objects.requireNonNull(getCommand("where")).setExecutor(new WhereCommand(manager));
        Objects.requireNonNull(getCommand("ipban")).setExecutor(new IPBanCommand(manager));
        Objects.requireNonNull(getCommand("kick")).setExecutor(new KickCommand(manager));
        Objects.requireNonNull(getCommand("kickall")).setExecutor(new KickAllCommand(manager));
        Objects.requireNonNull(getCommand("reloadbans")).setExecutor(new ReloadBansCommand(manager));
        Objects.requireNonNull(getCommand("tempban")).setExecutor(new TempBanCommand(manager));
        Objects.requireNonNull(getCommand("unban")).setExecutor(new UnbanCommand(manager));
        Objects.requireNonNull(getCommand("unipban")).setExecutor(new UnBanIPCommand(manager));
        Objects.requireNonNull(getCommand("ontime")).setExecutor(new OnTimeCommand(manager));
        Objects.requireNonNull(getCommand("lastlogins")).setExecutor(new LastLoginsCommand(manager));
        Objects.requireNonNull(getCommand("namehistory")).setExecutor(new NameHistoryCommand(manager));
        Objects.requireNonNull(getCommand("lockdown")).setExecutor(new LockDownCommand(manager));
        Objects.requireNonNull(getCommand("newSpawn")).setExecutor(new NewSpawnCommand(manager));
    
    }
}
