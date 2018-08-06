package net.cubespace.geSuit.database;

import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.cubespace.geSuit.Utilities;
import net.cubespace.geSuit.managers.ConfigManager;
import net.cubespace.geSuit.managers.DatabaseManager;
import net.cubespace.geSuit.objects.Ban;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Bans implements IRepository {

    public boolean isPlayerBanned(String player) {
        return isPlayerBanned(player, null, null);
    }

    public boolean isPlayerBanned(String player, String uuid, String ip) {
        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();

        try {
            PreparedStatement isPlayerBanned = connectionHandler.getPreparedStatement("isPlayerBanned");
            isPlayerBanned.setString(1, player);
            isPlayerBanned.setString(2, uuid);
            isPlayerBanned.setString(3, ip);

            return isPlayerBanned.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }

        return false;
    }

    public int banPlayer(String banned_playername, String banned_uuid, String banned_ip, String bannedBy, String reason, String type) {
        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();

        try {
            PreparedStatement banPlayer = connectionHandler.getPreparedStatement("banPlayer");
            banPlayer.setString(1, banned_playername);
            banPlayer.setString(2, banned_uuid);
            banPlayer.setString(3, banned_ip);
            banPlayer.setString(4, bannedBy);
            banPlayer.setString(5, reason);
            banPlayer.setString(6, type);

            banPlayer.executeUpdate();
            ResultSet rs = banPlayer.getGeneratedKeys();
            if ( rs != null && rs.next() ) {
                return rs.getInt( 1 );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }

        return -1;
    }

    public int warnPlayer(String banned_playername, String banned_uuid, String bannedBy, String reason) {
        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();

        try {
            PreparedStatement banPlayer = connectionHandler.getPreparedStatement("warnPlayer");
            banPlayer.setString(1, banned_playername);
            banPlayer.setString(2, banned_uuid);
            banPlayer.setString(3, bannedBy);
            banPlayer.setString(4, reason);

            banPlayer.executeUpdate();
            ResultSet rs = banPlayer.getGeneratedKeys();
            if ( rs != null && rs.next() ) {
                return rs.getInt( 1 );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }

        return -1;
    }

    public int kickPlayer(String banned_playername, String banned_uuid, String bannedBy, String reason) {
        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();

        try {
            PreparedStatement banPlayer = connectionHandler.getPreparedStatement("kickPlayer");
            banPlayer.setString(1, banned_playername);
            banPlayer.setString(2, banned_uuid);
            banPlayer.setString(3, bannedBy);
            banPlayer.setString(4, reason);

            banPlayer.executeUpdate();
            ResultSet rs = banPlayer.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }

        return -1;
    }

    public void tempBanPlayer(String banned_playername, String banned_uuid, String banned_by, String reason, String till) {
        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();

        try {
            PreparedStatement tempBanPlayer = connectionHandler.getPreparedStatement("tempBanPlayer");
            tempBanPlayer.setString(1, banned_playername);
            tempBanPlayer.setString(2, banned_uuid);
            tempBanPlayer.setString(3, banned_by);
            tempBanPlayer.setString(4, reason);
            tempBanPlayer.setString(5, till);

            tempBanPlayer.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }
    }

    public List<Ban> getBanHistory(String lookup, String uuid) {
        List<Ban> bans = new ArrayList<>();

        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();
        try {
            PreparedStatement banInfo = connectionHandler.getPreparedStatement("banHistory");
            banInfo.setString(1, lookup);
            banInfo.setString(2, uuid);
            banInfo.setString(3, lookup);

            ResultSet res = banInfo.executeQuery();
            while (res.next()) {
                bans.add(new Ban(res.getInt("id"), res.getString("banned_playername"), res.getString("banned_uuid"), res.getString("banned_ip"), res.getString("banned_by"), res.getString("reason"), res.getString("type"), res.getInt("active"), res.getTimestamp("banned_on"), res.getTimestamp("banned_until")));
            }

            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }

        return bans;
    }

    public List<Ban> getWarnHistory(String player, String uuid) {
        List<Ban> bans = new ArrayList<>();

        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();
        try {
            PreparedStatement banInfo = connectionHandler.getPreparedStatement("warnHistory");
            banInfo.setString(1, player);
            banInfo.setString(2, uuid);

            ResultSet res = banInfo.executeQuery();
            while (res.next()) {
                bans.add(new Ban(res.getInt("id"), res.getString("banned_playername"), res.getString("banned_uuid"), res.getString("banned_ip"), res.getString("banned_by"), res.getString("reason"), res.getString("type"), res.getInt("active"), res.getTimestamp("banned_on"), res.getTimestamp("banned_until")));
            }

            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }

        return bans;
    }

    public List<Ban> getKickHistory(String player, String uuid) {
        List<Ban> bans = new ArrayList<>();

        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();
        try {
            PreparedStatement banInfo = connectionHandler.getPreparedStatement("kickHistory");
            banInfo.setString(1, player);
            banInfo.setString(2, uuid);

            ResultSet res = banInfo.executeQuery();
            while (res.next()) {
                bans.add(new Ban(res.getInt("id"), res.getString("banned_playername"), res.getString("banned_uuid"), res.getString("banned_ip"), res.getString("banned_by"), res.getString("reason"), res.getString("type"), res.getInt("active"), res.getTimestamp("banned_on"), res.getTimestamp("banned_until")));
            }

            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }

        return bans;
    }

    public List<Ban> getKickWarnHistory(String player, String uuid) {
        List<Ban> bans = new ArrayList<>();

        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();
        try {
            PreparedStatement banInfo = connectionHandler.getPreparedStatement("kickwarnHistory");
            banInfo.setString(1, player);
            banInfo.setString(2, uuid);

            ResultSet res = banInfo.executeQuery();
            while (res.next()) {
                bans.add(new Ban(res.getInt("id"), res.getString("banned_playername"), res.getString("banned_uuid"), res.getString("banned_ip"), res.getString("banned_by"), res.getString("reason"), res.getString("type"), res.getInt("active"), res.getTimestamp("banned_on"), res.getTimestamp("banned_until")));
            }

            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }

        return bans;
    }

    public Ban getBanInfo(String player) {
        return getBanInfo(player, player, player);
    }

    public Ban getBanInfo(String player, String uuid, String ip) {
        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();

        Ban b = null;

        try {
            PreparedStatement banInfo = connectionHandler.getPreparedStatement("banInfo");
            banInfo.setString(1, player);
            banInfo.setString(2, uuid);
            banInfo.setString(3, ip);

            ResultSet res = banInfo.executeQuery();
            if (res.next()) {
                b = new Ban(res.getInt("id"), res.getString("banned_playername"), res.getString("banned_uuid"), res.getString("banned_ip"), res.getString("banned_by"), res.getString("reason"), res.getString("type"), res.getInt("active"), res.getTimestamp("banned_on"), res.getTimestamp("banned_until"));
            }

            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }

        return b;
    }

    public void unbanPlayer(int id) {
        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();

        try {
            PreparedStatement unbanPlayer = connectionHandler.getPreparedStatement("unbanPlayer");
            unbanPlayer.setInt(1, id);
            unbanPlayer.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }
    }

    public void insertBanConvert(String bannedBy, String player, String uuid, String ip, String reason, String type, int active, Date bannedOn, Date bannedUntil) {
        ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();

        try {
            PreparedStatement insertBanConvert = connectionHandler.getPreparedStatement("insertBanConvert");
            insertBanConvert.setString(1, player); //playerName
            insertBanConvert.setString(2, uuid); //UUID
            insertBanConvert.setString(3, ip); //IP
            insertBanConvert.setString(4, bannedBy);
            insertBanConvert.setString(5, reason);
            insertBanConvert.setString(6, type);
            insertBanConvert.setInt(6, active);
            insertBanConvert.setDate(7, bannedOn);
            insertBanConvert.setDate(8, bannedUntil);

            insertBanConvert.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionHandler.release();
        }
    }

    @Override
    public String[] getTable() {
        return new String[]{ConfigManager.main.Table_Bans, "id INT(11) NOT NULL AUTO_INCREMENT,"
                + "banned_playername VARCHAR(100), "
                + "banned_uuid VARCHAR(100), "
                + "banned_ip VARCHAR(15), "
                + "banned_by VARCHAR(100), "
                + "reason VARCHAR(255), "
                + "type VARCHAR(100), "
                + "active TINYINT(1), "
                + "banned_on DATETIME NOT NULL,"
                + "banned_until DATETIME, "
                + "CONSTRAINT pk_banid PRIMARY KEY (id)"};
    }

    @Override
    public void registerPreparedStatements(ConnectionHandler connection) {
        connection.addPreparedStatement("isPlayerBanned", "SELECT id FROM "+ ConfigManager.main.Table_Bans +" WHERE (banned_playername = ? OR banned_uuid = ? OR banned_ip = ?) AND type in ('ban', 'ipban', 'tempban') AND active = 1");
        connection.addPreparedStatement("banPlayer", "INSERT INTO "+ ConfigManager.main.Table_Bans +" (banned_playername,banned_uuid,banned_ip,banned_by,reason,type,active,banned_on) VALUES (?,?,?,?,?,?,1,NOW());", PreparedStatement.RETURN_GENERATED_KEYS);
        connection.addPreparedStatement("warnPlayer", "INSERT INTO "+ ConfigManager.main.Table_Bans +" (banned_playername,banned_uuid,banned_by,reason,type,active,banned_on) VALUES (?,?,?,?,'warn',0,NOW());", PreparedStatement.RETURN_GENERATED_KEYS);
        connection.addPreparedStatement("kickPlayer", "INSERT INTO " + ConfigManager.main.Table_Bans + " (banned_playername,banned_uuid,banned_by,reason,type,active,banned_on) VALUES (?,?,?,?,'kick',0,NOW());", PreparedStatement.RETURN_GENERATED_KEYS);
        connection.addPreparedStatement("unbanPlayer", "UPDATE "+ ConfigManager.main.Table_Bans +" SET active = 0 WHERE id = ?");
        connection.addPreparedStatement("banInfo", "SELECT * FROM "+ ConfigManager.main.Table_Bans +" WHERE (banned_playername = ? OR banned_uuid = ? OR banned_ip = ?) AND type in ('ban', 'ipban', 'tempban') AND active = 1 ORDER BY type");
        connection.addPreparedStatement("banHistory", "SELECT * FROM "+ ConfigManager.main.Table_Bans +" WHERE (banned_playername = ? OR banned_uuid = ? OR banned_ip = ?) AND type in ('ban', 'ipban', 'tempban') ORDER BY id ASC");
        connection.addPreparedStatement("warnHistory", "SELECT * FROM "+ ConfigManager.main.Table_Bans +" WHERE (banned_playername = ? OR banned_uuid = ? ) AND type = 'warn' ORDER BY id ASC");
        connection.addPreparedStatement("kickHistory", "SELECT * FROM " + ConfigManager.main.Table_Bans + " WHERE (banned_playername = ? OR banned_uuid = ? ) AND type = 'kick' ORDER BY id ASC");
        connection.addPreparedStatement("kickwarnHistory", "SELECT * FROM " + ConfigManager.main.Table_Bans + " WHERE (banned_playername = ? OR banned_uuid = ? ) AND type in ('kick','warn') ORDER BY id ASC");

        connection.addPreparedStatement("tempBanPlayer", "INSERT INTO "+ ConfigManager.main.Table_Bans +" (banned_playername,banned_uuid,banned_by,reason,type,active,banned_on,banned_until) VALUES(?,?,?,?,'tempban',1,NOW(),?)", PreparedStatement.RETURN_GENERATED_KEYS);
        connection.addPreparedStatement("insertBanConvert", "INSERT INTO "+ ConfigManager.main.Table_Bans +" (banned_playername,banned_uuid,banned_ip,banned_by,reason,type,active,banned_on,banned_until) VALUES(?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        connection.addPreparedStatement("getBans", "SELECT * FROM "+ ConfigManager.main.Table_Bans);
        connection.addPreparedStatement("updateRowUUID", "UPDATE "+ ConfigManager.main.Table_Bans +" SET banned_uuid = ? WHERE id = ?");
        connection.addPreparedStatement("updateToUUID", "UPDATE "+ ConfigManager.main.Table_Bans +" SET banned_uuid = ? WHERE id = ?");
        connection.addPreparedStatement("updateToVersion3-part1", "ALTER TABLE `"+ ConfigManager.main.Table_Bans +"` CHANGE `display` `banned_playername` VARCHAR( 100 );  ");
        connection.addPreparedStatement("updateToVersion3-part2", "ALTER TABLE `"+ ConfigManager.main.Table_Bans +"` CHANGE `banned_entity` `banned_uuid` VARCHAR( 100 );  ");
        connection.addPreparedStatement("updateToVersion3-part3", "ALTER TABLE `"+ ConfigManager.main.Table_Bans +"` ADD `banned_ip` VARCHAR( 15 ) NULL AFTER `banned_uuid`  ");
    }

    @Override
    public void checkUpdate() {
        //What current Version of the Database is this ?
        int installedVersion = ConfigManager.main.Version_Database_Ban;

        System.out.println("Current Version of the Ban Database: " + installedVersion);

        if (installedVersion < 2) {
            // Version 2 adds UUIDs as Field
            ConnectionHandler connectionHandler = DatabaseManager.connectionPool.getConnection();

            // Convert all Names to UUIDs
            PreparedStatement getBans = connectionHandler.getPreparedStatement("getBans");
            try {
                ResultSet res = getBans.executeQuery();
                while (res.next()) {
                    String bannedEntity = res.getString("banned_uuid");

                    if (!Utilities.isIPAddress(bannedEntity)) {
                        String uuid = Utilities.getUUID(bannedEntity);

                        if (uuid != null) {
                            ConnectionHandler connectionHandler1 = DatabaseManager.connectionPool.getConnection();

                            try {
                                PreparedStatement updateToUUID = connectionHandler1.getPreparedStatement("updateToUUID");
                                updateToUUID.setString(1, uuid);
                                updateToUUID.setInt(2, res.getInt("id"));
                                updateToUUID.executeUpdate();
                            } catch (SQLException e) {
                                System.out.println("Could not update Ban for update to version 2");
                                e.printStackTrace();
                            } finally {
                                connectionHandler1.release();
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("Could not get Bans for update to version 2");
                e.printStackTrace();
                return;
            } finally {
                connectionHandler.release();
            }
        }

        if (installedVersion < 3) { //dimensionZ aggressive+freedom-of-ban update
            ConnectionHandler connectionHandler = null;
            boolean updateCompleted = false;
            try {
                connectionHandler = DatabaseManager.connectionPool.getConnection();
                PreparedStatement updateToVersion3 = connectionHandler.getPreparedStatement("updateToVersion3-part1");
                updateToVersion3.executeUpdate();
                updateToVersion3 = connectionHandler.getPreparedStatement("updateToVersion3-part2");
                updateToVersion3.executeUpdate();
                updateToVersion3 = connectionHandler.getPreparedStatement("updateToVersion3-part3");
                updateToVersion3.executeUpdate();
                System.out.println("Updated Bans to version 3!");
                updateCompleted = true;
            } catch (SQLException ex) {
                System.out.println("Could not get Bans for update to version 3");
                Logger.getLogger(Bans.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (connectionHandler != null) {
                    connectionHandler.release();
                }
            }
            if (!updateCompleted) {
                return;
            }
            ConfigManager.main.Version_Database_Ban = 3;
            try {
                ConfigManager.main.save();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }

    }
}
