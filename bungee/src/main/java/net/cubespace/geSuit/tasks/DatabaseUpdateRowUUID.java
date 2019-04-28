package net.cubespace.geSuit.tasks;

import net.cubespace.geSuit.managers.DatabaseManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JR
 */
public class DatabaseUpdateRowUUID implements Runnable
{

    final int rowID;
    final String playerName;

    public DatabaseUpdateRowUUID(int id, String pname)
    {
        rowID = id;
        playerName = pname;
    }

    @Override
    public void run()
    {
        if (rowID == -1) {
            ProxyServer.getInstance().getLogger().warning("Incorrect row " + rowID + " for player " + playerName);
            return;
        }

        String uuid = null;
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        if (player != null) {
            uuid = player.getUniqueId().toString().replaceAll("-", "");
        }

        if (uuid == null || uuid.isEmpty()) {
            ProxyServer.getInstance().getLogger().warning("Could not fetch UUID for player " + playerName);
        } else {
            try (Connection con = DatabaseManager.connectionPool.getConnection()) {
                PreparedStatement updateUUID = DatabaseManager.connectionPool.getPreparedStatement("updateRowUUID", con);
                updateUUID.setString(1, uuid);
                updateUUID.setInt(2, rowID);
                updateUUID.executeUpdate();
            }
            catch (SQLException ex) {
                ProxyServer.getInstance().getLogger().warning("Error while updating db for player " + playerName + " with UUID " + uuid);
                Logger.getLogger(DatabaseUpdateRowUUID.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
