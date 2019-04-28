package net.cubespace.geSuit.objects;

import net.cubespace.geSuit.geSuit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class Location {
    private ServerInfo server;
    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Location(String server, String world, double x, double y, double z, float yaw, float pitch) {
        this.server = ProxyServer.getInstance().getServerInfo(server);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location(String serialised) {
        String[] loc = serialised.split("~!~");
        server = ProxyServer.getInstance().getServerInfo(loc[0]);
        world = loc[1];
        x = Double.parseDouble(loc[2]);
        y = Double.parseDouble(loc[3]);
        z = Double.parseDouble(loc[4]);
        yaw = Float.parseFloat(loc[5]);
        pitch = Float.parseFloat(loc[6]);
    }

    public Location(ServerInfo serverInfo, String world, double x, double y, double z) {
        this.server = serverInfo;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        yaw = 0;
        pitch = 0;
    }
    
    public Location(ServerInfo serverInfo, String world, double x, double y, double z, float yaw, float pitch) {
        this.server = serverInfo;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location(String serverInfo, String world, double x, double y, double z) {
        this.server = geSuit.proxy.getServerInfo(serverInfo);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        yaw = 0;
        pitch = 0;
    }

    public ServerInfo getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = ProxyServer.getInstance().getServerInfo(server);
    }

    public void setServer(ServerInfo server) {
        this.server = server;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public String toString() {
        return server.getName() + "~!~" + world + "~!~" + x + "~!~" + y + "~!~" + z + "~!~" + yaw + "~!~" + pitch;
    }
}
