package net.cubespace.geSuit.listeners;

import net.cubespace.geSuit.Utilities;
import net.cubespace.geSuit.geSuit;
import net.cubespace.geSuit.managers.ConfigManager;
import net.cubespace.geSuit.managers.DatabaseManager;
import net.cubespace.geSuit.managers.GeoIPManager;
import net.cubespace.geSuit.managers.LoggingManager;
import net.cubespace.geSuit.managers.PlayerManager;
import net.cubespace.geSuit.managers.SpawnManager;
import net.cubespace.geSuit.objects.GSPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void playerLogin(LoginEvent event) {
        event.registerIntent(geSuit.instance);
        PlayerManager.initPlayer(event.getConnection(), event);
    }
    
	@EventHandler(priority = EventPriority.LOW)
	public void playerPostLogin(final PostLoginEvent e) {
		String ip = e.getPlayer().getAddress().getHostString();
		String name = e.getPlayer().getName();
		String uuid = e.getPlayer().getUniqueId().toString();
		LoggingManager.log("Player " + name + " (" + uuid + ") connected from " + ip);
	}

    @EventHandler(priority = EventPriority.LOW)
    public void playerServerConnected(final ServerConnectedEvent e) throws SQLException {
    	if (PlayerManager.getPlayer(e.getPlayer().getName()) == null) {
    		// NOTE: This event is called each time the player changes server
    		// This check ensures this is only handled when the player is first connecting to the proxy
    		if (ProxyServer.getInstance().getPlayer(e.getPlayer().getUniqueId()) == null) {
    			LoggingManager.log(ChatColor.RED + "Warning: ServerConnectedEvent called but player is not online any more.");
    			return;		// Do nothing if the player is offline
    		}

    		final GSPlayer p = PlayerManager.confirmJoin(e.getPlayer());
    		p.setServer(e.getServer().getInfo().getName());
    		
    		if (ConfigManager.main.BroadcastProxyConnectionMessages) {
    			PlayerManager.sendBroadcast(ConfigManager.messages.PLAYER_CONNECT_PROXY.replace("{player}", p.getName()));
    		}
    		final boolean newspawn = p.isNewSpawn();

			// Check if an existing player has a "newspawn" flag... send them to new player spawn
    		if ((!p.isFirstJoin()) && (newspawn)) {
    	    	SpawnManager.sendPlayerToNewPlayerSpawn(p);
    			p.setNewSpawn(false);
    		}

    		// Check for alt accounts and notify staff (used later)
    		String[] alt = null;
    		if (ConfigManager.bans.ShowAltAccounts) {
    			alt = DatabaseManager.players.getAltPlayer(p.getUuid(), p.getIp(), p.isFirstJoin());
    		}
    		
            DatabaseManager.players.updatePlayer(p);
    		
    		// Launch the MOTD message scheduler
    		if (ConfigManager.main.MOTD_Enabled && (p.firstConnect() || newspawn)) {
    	    	geSuit.proxy.getScheduler().schedule(geSuit.instance, new Runnable() {
    				@Override
    				public void run() {
    					if (ProxyServer.getInstance().getPlayer(e.getPlayer().getUniqueId()) != null) {	// Check if player is still online
	    					if (p.isFirstJoin() || newspawn) {
	    		            	PlayerManager.sendMessageToTarget(e.getPlayer().getName(), ConfigManager.motdNew.getMOTD().replace("{player}", p.getName()));
	    		            } else {
	    		            	PlayerManager.sendMessageToTarget(e.getPlayer().getName(), ConfigManager.motd.getMOTD().replace("{player}", p.getName()));
	    		            }
    	    			}
    				}
    	    	}, 500, TimeUnit.MILLISECONDS); 
	        }

    		p.connected();

    		final String[] fAlt = alt;
        	geSuit.proxy.getScheduler().schedule(geSuit.instance, new Runnable() {
        		@Override
    			public void run() {
        			// Show alt account logins for this player (if enabled)
        			if ((ConfigManager.bans.ShowAltAccounts) && (fAlt != null)) {
        				boolean bannedAlt = false;
        				if (ConfigManager.bans.ShowBannedAltAccounts) {
        					if (DatabaseManager.bans.isPlayerBanned(fAlt[0], fAlt[1], null))	// Check if alt is banned (by name or UUID)
        						bannedAlt = true;
        				}

        				if (bannedAlt) {
        					// Alt player is banned
        					String msg = ConfigManager.messages.PLAYER_BANNED_ALT_JOIN.
	    	    					replace("{player}", p.getName()).
	    	    					replace("{alt}", fAlt[0]).
	    	    					replace("{ip}", p.getIp());
        					Utilities.doBungeeChatMirror("StaffNotice", msg);
        				} else {
        					// Alt player is NOT banned
	        				String msg = ConfigManager.messages.PLAYER_ALT_JOIN.
	    	    					replace("{player}", p.getName()).
	    	    					replace("{alt}", fAlt[0]).
	    	    					replace("{ip}", p.getIp());
	    	    			Utilities.doBungeeChatMirror("StaffNotice", msg);
        				}
    	    		}

    	    		// Show Geo location notifications for player (if enabled)
    	    		if (ConfigManager.bans.GeoIP.ShowOnLogin) {
                        String location = GeoIPManager.lookup(e.getPlayer().getAddress().getAddress());
                        if (location != null) {
                            String msg = ConfigManager.messages.PLAYER_GEOIP.
                                    replace("{player}", p.getName()).
                                    replace("{location}", location);
                            Utilities.doBungeeChatMirror("StaffNotice", msg);
                        }
    	    		}

    				// Update player tracking information
    				PlayerManager.updateTracking(p);
    			}
        	}, 100, TimeUnit.MILLISECONDS); 
    	}
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerLogout(final PlayerDisconnectEvent e) {
        int dcTime = ConfigManager.main.PlayerDisconnectDelay;
        
        final GSPlayer p = PlayerManager.cachedPlayers.remove(e.getPlayer().getUniqueId());
        if (dcTime > 0) {
            geSuit.proxy.getScheduler().schedule(geSuit.instance, new Runnable() {
                @Override
                public void run() {
                    if (!PlayerManager.kickedPlayers.contains(e.getPlayer())) {
                        if (ConfigManager.main.BroadcastProxyConnectionMessages) {
                            PlayerManager.sendBroadcast(ConfigManager.messages.PLAYER_DISCONNECT_PROXY.replace("{player}", p.getName()));
                        }
                    } else {
                        PlayerManager.kickedPlayers.remove(e.getPlayer());
                    }

                    PlayerManager.unloadPlayer(e.getPlayer().getName());
                    DatabaseManager.players.updatePlayer(p);

                    // Update time tracking (if enabled)
                	if (ConfigManager.bans.TrackOnTime) {
                		DatabaseManager.ontime.updatePlayerOnTime(p.getName(), p.getUuid(), p.getLoginTime(), new Date().getTime());
                	}
                }

            }, dcTime, TimeUnit.SECONDS);
        } else {
            if (!PlayerManager.kickedPlayers.contains(e.getPlayer())) {
                if (ConfigManager.main.BroadcastProxyConnectionMessages) {
                    // Only show disconnect message if player was already successfully connected
                    if (PlayerManager.getPlayer(e.getPlayer().getName()) != null) {
                        PlayerManager.sendBroadcast(ConfigManager.messages.PLAYER_DISCONNECT_PROXY.replace("{player}", p.getName()));
                    }
                }
            } else {
                PlayerManager.kickedPlayers.remove(e.getPlayer());
            }
            PlayerManager.unloadPlayer(e.getPlayer().getName());

            geSuit.proxy.getScheduler().schedule(geSuit.instance, new Runnable() {
                @Override
                public void run() {
                	// Always update the player record when they disconnect
                	DatabaseManager.players.updatePlayer(p);

                	// Update time tracking (if enabled)
                	if (ConfigManager.bans.TrackOnTime) {
                		DatabaseManager.ontime.updatePlayerOnTime(p.getName(), p.getUuid(), p.getLoginTime(), new Date().getTime());
                	}
                }
            }, 1, TimeUnit.MILLISECONDS);
        }
    }
}
