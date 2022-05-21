package me.efco.yadbtickets.data;

import me.efco.yadbtickets.entities.ServerInfo;

import java.sql.*;

public class DBConnection {
    private static final DBConnection INSTANCE = new DBConnection();
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

    public boolean serverInfoExists(long serverId) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT server_id FROM server_info WHERE server_id=?")){
            statement.setLong(1, serverId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
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
        if (serverInfoExists(serverInfo.getServerId())) {
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
        } else {
            insertServerInfo(serverInfo);
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

    public int insertButtonInteractions(String type, String information) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO button_interaction (event_type, event_information) VALUES (?,?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, type);
            statement.setString(2, information);

            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            return result.getInt("button_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public String[] getButtonInteractions(int buttonId) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM button_interaction WHERE button_id=?;")) {
            statement.setInt(1, buttonId);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new String[]{result.getString("event_type"),result.getString("event_information")};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int insertTicket(long author) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO tickets (author) VALUES (?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, author);

            statement.executeUpdate();
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            return result.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int getActiveTickets(long author) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM tickets WHERE author=? AND active=true")) {
            statement.setLong(1, author);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void updateTicketSupporter(long id) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE tickets SET supporter=?;")) {
            statement.setLong(1, id);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getTicketSupporter(long id) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT supporter FROM tickets WHERE id=?;")) {
            statement.setLong(1, id);

            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return result.getLong("supporter");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void setTicketSolved(long id, boolean state) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE tickets SET active=? WHERE id=?")) {
            statement.setBoolean(1, state);
            statement.setLong(2, id);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getSolvedTicketsByadmin(long id) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + CONNECTION_STRING,USERNAME,PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM tickets WHERE supporter=? AND active=false")) {
            statement.setLong(1, id);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
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
