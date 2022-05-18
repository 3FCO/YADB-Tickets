package me.efco.yadbtickets.data;

import me.efco.yadbtickets.entities.ServerInfo;

import java.sql.*;

public class DBConnection {
    private static DBConnection INSTANCE = new DBConnection();
    private final String CONNECTION_STRING;
    private final String USERNAME;
    private final String PASSWORD;

    private DBConnection(){
        CONNECTION_STRING = ConfigLoader.getInstance().loadConfigByName("db_database");
        USERNAME = ConfigLoader.getInstance().loadConfigByName("db_user");
        PASSWORD = ConfigLoader.getInstance().loadConfigByName("db_pass");
    }

    public ServerInfo getServerInfo(long serverId) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM server_info WHERE server_id=?")){
            statement.setLong(1, serverId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new ServerInfo(serverId, result.getLong("channel_id"), result.getLong("support_id"), result.getLong("category_id"), result.getLong("moderator_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return new ServerInfo(serverId,0,0,0,0);
    }

    public void insertServerInfo(ServerInfo serverInfo) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO server_info (server_id,channel_id,support_id,category_id,moderator_id) VALUES (?,?,?,?,?)")) {
            statement.setLong(1, serverInfo.getServerId());
            statement.setLong(2, serverInfo.getChannelId());
            statement.setLong(3, serverInfo.getSupportId());
            statement.setLong(4, serverInfo.getCategoryId());
            statement.setLong(5, serverInfo.getModeratorId());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateServerInfo(ServerInfo serverInfo) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE server_info SET server_id=?,channel_id=?,category_id=?,support_id=?,moderator_id=?;")) {
            statement.setLong(1, serverInfo.getServerId());
            statement.setLong(2, serverInfo.getChannelId());
            statement.setLong(3, serverInfo.getCategoryId());
            statement.setLong(4, serverInfo.getSupportId());
            statement.setLong(5, serverInfo.getModeratorId());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getCategoryByServerId(long serverId) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT category_id FROM server_info WHERE server_id=?;")) {
            statement.setLong(1, serverId);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getLong("category_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static DBConnection getInstance() {
        return INSTANCE;
    }
}
