package io.github.MGDCl.TheBridge.database;

import io.github.MGDCl.TheBridge.TheBridge;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.TimeZone;

public class Cosmeticbase {

    public TheBridge plugin;
    private Connection connection;

    public Cosmeticbase(TheBridge plugin) {
        this.plugin = plugin;
        conectar();
    }

    public void conectar() {
        if (plugin.getConfig().getBoolean("MySQL.enabled")) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(
                        "jdbc:mysql://"
                                + (plugin.getConfig().getString("MySQL.host") + ":"
                                + plugin.getConfig().getString("MySQL.port"))
                                + "/" + plugin.getConfig().getString("MySQL.database") + "?serverTimezone="
                                + TimeZone.getDefault().getID()
                                + "&autoReconnect=true&wait_timeout=31536000&interactive_timeout=31536000",
                        plugin.getConfig().getString("MySQL.username"), plugin.getConfig().getString("MySQL.password"));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[TheBridge] MySQL connected.");
                newTables();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File DataFile = new File(plugin.getDataFolder(), "/TheBridge.db");
            if (!DataFile.exists()) {
                try {
                    DataFile.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Bukkit.getPluginManager().disablePlugin(plugin);
                }
            }
            try {
                Class.forName("org.sqlite.JDBC");
                try {
                    connection = DriverManager.getConnection("jdbc:sqlite:" + DataFile);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[TheBridge] SQLLite connected.");
                    newTables();
                } catch (SQLException ex2) {
                    ex2.printStackTrace();
                    Bukkit.getPluginManager().disablePlugin(plugin);
                }
            } catch (ClassNotFoundException ex3) {
                ex3.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public void checkConnection() {
        try {
            if (connection.isClosed() || connection == null) {
                conectar();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createNewPlayer(String fId, String name) {
        PreparedStatement preparedStatement = null;
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("INSERT INTO `BG_Cosmetic` ");
            queryBuilder.append("(`UUID`, `Name`, `Cage`, `Arrow_Trail`, `Feet_Trail`, `KillSound`) ");
            queryBuilder.append("VALUES ");
            queryBuilder.append("(?, ?, ?, ?, ?, ?);");
            preparedStatement = connection.prepareStatement(queryBuilder.toString());
            preparedStatement.setString(1, fId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, "default");
            preparedStatement.setString(4, "default");
            preparedStatement.setString(5, "default");
            preparedStatement.setString(6, "default");
            preparedStatement.executeUpdate();
        } catch (final SQLException sqlException) {
            sqlException.printStackTrace();

        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (final SQLException ignored) {
                }
            }
        }
    }

    public boolean hasPlayer(Player p, String uuid) {
        PreparedStatement statement = null;
        try {
            statement = this.connection.prepareStatement("SELECT UUID FROM BG_Cosmetic WHERE UUID ='" + uuid + "'");
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
                return false;
            }
            return false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public void loadData(PlayerStat stat) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Connection connection = getConnection();
                if (!hasPlayer(stat.getPlayer(), stat.getUUID())) {
                    createNewPlayer(stat.getUUID(), stat.getPlayer().getName());
                    loadData(stat);
                    return;
                }
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                try {
                    StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("SELECT `Cage`, `Arrow_Trail`, `Feet_Trail`, `KillSound` ");
                    queryBuilder.append("FROM `BG_Cosmetic` ");
                    queryBuilder.append("WHERE `UUID` = ? ");
                    queryBuilder.append("LIMIT 1;");
                    preparedStatement = connection.prepareStatement(queryBuilder.toString());
                    preparedStatement.setString(1, stat.getUUID().toString());
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet != null && resultSet.next()) {
                        stat.setCage(resultSet.getString("Cage"));
                        stat.setArrow_trail(resultSet.getString("Arrow_Trail"));
                        stat.setFeet_trail(resultSet.getString("Feet_Trail"));
                    }
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                } finally {
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (SQLException ignored) {
                        }
                    }
                    if (preparedStatement != null) {
                        try {
                            preparedStatement.close();
                        } catch (SQLException ignored) {
                        }
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void saveData(PlayerStat stat) {
        checkConnection();
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("UPDATE `BG_Cosmetic` SET ");
            queryBuilder.append("`Cage` = ?, `Arrow_Trail` = ?, `Feet_Trail` = ?, `KillSound` = ?");
            queryBuilder.append("WHERE `UUID` = ?;");
            preparedStatement = connection.prepareStatement(queryBuilder.toString());
            preparedStatement.setString(1, stat.getCage());
            preparedStatement.setString(2, stat.getArrow_trail());
            preparedStatement.setString(3, stat.getFeet_trail());
            preparedStatement.setString(4, "default");
            preparedStatement.setString(5, stat.getUUID());
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();

        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void newTables() {
        Connection connection = getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS BG_Cosmetic (UUID VARCHAR(60), Name VARCHAR(60), Cage TEXT, Arrow_Trail TEXT, Feet_Trail TEXT, KillSound TEXT)");
            } catch (SQLException ex) {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException ex2) {
                }
                return;
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException ex3) {
                }
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex4) {
            }
        } catch (SQLException ex5) {
        }
    }

}

