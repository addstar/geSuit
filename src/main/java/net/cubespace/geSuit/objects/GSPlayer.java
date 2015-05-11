package net.cubespace.geSuit.objects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import net.cubespace.geSuit.Utilities;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GSPlayer
{

    private String playername;
    private String uuid;
    private boolean acceptingTeleports;
    private String server = null;
    private String ip;
    private Timestamp lastOnline;
    private Timestamp firstOnline;

    private HashMap<String, ArrayList<Home>> homes = new HashMap<>();
    private Track previousName;
    private Location deathBackLocation;
    private Location teleportBackLocation;
    private boolean lastBack;
    private boolean firstConnect = true;
    private boolean joinAnnounced = false;
    private boolean isFirstJoin = false;
    private boolean newSpawn = false;
    private long loginTime;

    public GSPlayer(String name, String uuid, boolean tps)
    {
        this(name, uuid, tps, null);
    }

    public GSPlayer(String name, String uuid, boolean tps, String ip)
    {
        this(name, uuid, tps, false, ip, new Timestamp(new Date().getTime()), new Timestamp(new Date().getTime()));
    }
    
    public GSPlayer(String name, String uuid, boolean tps, boolean newspawn, String ip, Timestamp lastOnline, Timestamp firstOnline)
    {
        //ProxyServer.getInstance().getLogger().info("LOADED DATA: "+name+" "+uuid+" "+tps+" "+ip+" "+lastOnline);
        this.playername = name;
        this.uuid = uuid;
        this.acceptingTeleports = tps;
        this.ip = ip;
        this.lastOnline = lastOnline;
        this.firstOnline = firstOnline;
        this.newSpawn = newspawn;
        this.loginTime = new Date().getTime();
    }

    public String getName()
    {
        return playername;
    }

    public void setName(String newPlayerName)
    {
        playername = newPlayerName;
    }

    public ProxiedPlayer getProxiedPlayer()
    {
        return ProxyServer.getInstance().getPlayer(playername);
    }

    public void sendMessage(String message)
    {
    	// Allow messages to be "silenced" by providing an empty string
    	// (if you really must send a blank line for some reason, use a formatting code on its own, eg. "&f")
    	if (message == null || message.isEmpty())
    		return;

        for (String line : message.split("\n|\\{N\\}")) {
            getProxiedPlayer().sendMessage(TextComponent.fromLegacyText(Utilities.colorize(line)));
        }
    }

    public boolean acceptingTeleports()
    {
        return this.acceptingTeleports;
    }

    public void setAcceptingTeleports(boolean tp)
    {
        this.acceptingTeleports = tp;
    }

    public void setDeathBackLocation(Location loc)
    {
        deathBackLocation = loc;
        lastBack = true;
    }

    public boolean hasDeathBackLocation()
    {
        return deathBackLocation != null;
    }

    public void setTeleportBackLocation(Location loc)
    {
        teleportBackLocation = loc;
        lastBack = false;
    }

    public Location getLastBackLocation()
    {
        if (lastBack) {
            return deathBackLocation;
        }
        else {
            return teleportBackLocation;
        }
    }

    public boolean hasTeleportBackLocation()
    {
        return teleportBackLocation != null;
    }

    public Location getDeathBackLocation()
    {
        return deathBackLocation;
    }

    public Location getTeleportBackLocation()
    {
        return teleportBackLocation;
    }

    public String getServer()
    {
        if ((getProxiedPlayer() == null) || (getProxiedPlayer().getServer() == null)){
            return server;
        }

        return getProxiedPlayer().getServer().getInfo().getName();
    }

    public HashMap<String, ArrayList<Home>> getHomes()
    {
        return homes;
    }

    /**
     * Will the next server connect be the first server to be joined in this session
     */
    public boolean firstConnect()
    {
        return firstConnect;
    }

    /**
     * Called in ServerConnectedEvent to signify that it has connected to a server
     */
    public void connected()
    {
        firstConnect = false;
    }

    public void connectTo(ServerInfo s)
    {
        getProxiedPlayer().connect(s);
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ipAddress)
    {
        ip = ipAddress;
    }

    public Timestamp getLastOnline()
    {
        return lastOnline;
    }

    public void setLastOnline(Timestamp value)
    {
        lastOnline = value;
    }

    public Timestamp getFirstOnline()
    {
        return firstOnline;
    }

    public void setFirstOnline(Timestamp value)
    {
        firstOnline = value;
    }

    /**
     * Is this player a new player (as in the first time they have ever joined the proxy)
     */
    public boolean isFirstJoin()
    {
    	return isFirstJoin;
    }
    
    public void setFirstJoin(boolean value)
    {
    	isFirstJoin = value;
    }

	public boolean hasJoinAnnounced() {
		return joinAnnounced;
	}

	public void setJoinAnnounced(boolean joinAnnounced) {
		this.joinAnnounced = joinAnnounced;
	}

	/**
	 * Signifies that this player must be taken to the new player spawn upon first connect
	 */
	public boolean isNewSpawn() {
		return newSpawn;
	}

	public void setNewSpawn(boolean newSpawn) {
		this.newSpawn = newSpawn;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}
	
	public void setLastName(Track track) {
	    previousName = track;
	}
	
	public Track getLastName() {
	    return previousName;
	}
}
