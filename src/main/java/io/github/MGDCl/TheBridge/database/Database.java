package io.github.MGDCl.TheBridge.database;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.tops.BoardType;
import io.github.MGDCl.TheBridge.tops.Top;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.TimeZone;

    public class Database {

    public TheBridge plugin;
    private Connection connection;

    public Database(TheBridge plugin) {
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

    public void loadNormalKills() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Connection connection = getConnection();
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                try {
                    StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("SELECT `UUID`, `Name`, `NKills` ");
                    queryBuilder.append("FROM `BG_Data` ");
                    queryBuilder.append("GROUP BY `UUID` ");
                    queryBuilder.append("ORDER BY `NKills` DESC LIMIT 10;");
                    preparedStatement = connection.prepareStatement(queryBuilder.toString());
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        new Top(resultSet.getString(2), resultSet.getInt(3), BoardType.NORMAL_KILLS);
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (plugin.getLm().getNormalKills() != null) {
                            plugin.getTop().createTop(plugin.getLm().getNormalKills(), BoardType.NORMAL_KILLS);
                        }
                    }
                }, 0);

            }
        }.runTaskAsynchronously(plugin);
    }

    public void loadNormalWins() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Connection connection = getConnection();
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                try {
                    StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("SELECT `UUID`, `Name`, `NWins` ");
                    queryBuilder.append("FROM `BG_Data` ");
                    queryBuilder.append("GROUP BY `UUID` ");
                    queryBuilder.append("ORDER BY `NWins` DESC LIMIT 10;");
                    preparedStatement = connection.prepareStatement(queryBuilder.toString());
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        new Top(resultSet.getString(2), resultSet.getInt(3), BoardType.NORMAL_WINS);
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (plugin.getLm().getNormalWins() != null) {
                            plugin.getTop().createTop(plugin.getLm().getNormalWins(), BoardType.NORMAL_WINS);
                        }
                    }
                }, 0);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void loadNormalGoals() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Connection connection = getConnection();
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                try {
                    StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("SELECT `UUID`, `Name`, `NGoals` ");
                    queryBuilder.append("FROM `BG_Data` ");
                    queryBuilder.append("GROUP BY `UUID` ");
                    queryBuilder.append("ORDER BY `NGoals` DESC LIMIT 10;");
                    preparedStatement = connection.prepareStatement(queryBuilder.toString());
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        new Top(resultSet.getString(2), resultSet.getInt(3), BoardType.NORMAL_GOALS);
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (plugin.getLm().getNormalGoals() != null) {
                            plugin.getTop().createTop(plugin.getLm().getNormalGoals(), BoardType.NORMAL_GOALS);
                        }
                    }
                }, 0);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void loadFourKills() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Connection connection = getConnection();
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                try {
                    StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("SELECT `UUID`, `Name`, `FKills` ");
                    queryBuilder.append("FROM `BG_Data` ");
                    queryBuilder.append("GROUP BY `UUID` ");
                    queryBuilder.append("ORDER BY `FKills` DESC LIMIT 10;");
                    preparedStatement = connection.prepareStatement(queryBuilder.toString());
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        new Top(resultSet.getString(2), resultSet.getInt(3), BoardType.FOUR_KILLS);
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (plugin.getLm().getFourKills() != null) {
                            plugin.getTop().createTop(plugin.getLm().getFourKills(), BoardType.FOUR_KILLS);
                        }
                    }
                }, 0);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void loadFourWins() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Connection connection = getConnection();
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                try {
                    StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("SELECT `UUID`, `Name`, `FWins` ");
                    queryBuilder.append("FROM `BG_Data` ");
                    queryBuilder.append("GROUP BY `UUID` ");
                    queryBuilder.append("ORDER BY `FWins` DESC LIMIT 10;");
                    preparedStatement = connection.prepareStatement(queryBuilder.toString());
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        new Top(resultSet.getString(2), resultSet.getInt(3), BoardType.FOUR_WINS);
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (plugin.getLm().getFourWins() != null) {
                            plugin.getTop().createTop(plugin.getLm().getFourWins(), BoardType.FOUR_WINS);
                        }
                    }
                }, 0);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void loadFourGoals() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Connection connection = getConnection();
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                try {
                    StringBuilder queryBuilder = new StringBuilder();
                    queryBuilder.append("SELECT `UUID`, `Name`, `FGoals` ");
                    queryBuilder.append("FROM `BG_Data` ");
                    queryBuilder.append("GROUP BY `UUID` ");
                    queryBuilder.append("ORDER BY `FGoals` DESC LIMIT 10;");
                    preparedStatement = connection.prepareStatement(queryBuilder.toString());
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        new Top(resultSet.getString(2), resultSet.getInt(3), BoardType.FOUR_GOALS);
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (plugin.getLm().getFourGoals() != null) {
                            plugin.getTop().createTop(plugin.getLm().getFourGoals(), BoardType.FOUR_GOALS);
                        }
                    }
                }, 0);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void createNewPlayer(String fId, String name) {
        PreparedStatement preparedStatement = null;
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("INSERT INTO `BG_Data` ");
            queryBuilder.append(
                    "(`UUID`, `Name`, `Coins`, `NKills`, `NWins`, `NGoals`, `FKills`, `FWins`, `FGoals`, `Inventory`, `XP`, `Placed`, `Broken`) ");
            queryBuilder.append("VALUES ");
            queryBuilder.append("(?, ?, 0, 0, 0, 0, 0, 0, 0, ?, 0, 0, 0);");
            preparedStatement = connection.prepareStatement(queryBuilder.toString());
            preparedStatement.setString(1, fId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, "none");
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
            statement = this.connection.prepareStatement("SELECT UUID FROM BG_Data WHERE UUID ='" + uuid + "'");
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
                    queryBuilder.append(
                            "SELECT `NKills`, `NWins`, `NGoals`, `FKills`, `FWins`, `FGoals`, `Coins`, `Inventory`, `XP`, `Placed`, `Broken` ");
                    queryBuilder.append("FROM `BG_Data` ");
                    queryBuilder.append("WHERE `UUID` = ? ");
                    queryBuilder.append("LIMIT 1;");
                    preparedStatement = connection.prepareStatement(queryBuilder.toString());
                    preparedStatement.setString(1, stat.getUUID().toString());
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet != null && resultSet.next()) {
                        stat.setNormalKills(resultSet.getInt("NKills"));
                        stat.setNormalWins(resultSet.getInt("NWins"));
                        stat.setNormalGoals(resultSet.getInt("NGoals"));
                        stat.setFourKills(resultSet.getInt("FKills"));
                        stat.setFourWins(resultSet.getInt("FWins"));
                        stat.setFourGoals(resultSet.getInt("FGoals"));
                        stat.setCoins(resultSet.getInt("Coins"));
                        if (!resultSet.getString("Inventory").equals("none")) {
                            try {
                                stat.setHotbar(
                                        plugin.getKit().fromBase64(resultSet.getString("Inventory")).getContents());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            stat.setHotbar(null);
                        }
                        stat.setXp(resultSet.getInt("XP"));
                        stat.setPlaced(resultSet.getInt("Placed"));
                        stat.setBroken(resultSet.getInt("Broken"));
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                plugin.getTop().createInfo(stat.getPlayer());
                                plugin.getSb().createLobbyBoard(stat.getPlayer());
                                for (Player on : plugin.getSb().getSB().keySet()) {
                                    plugin.getSb().update(on);
                                }
                            }
                        }, 0);
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
            queryBuilder.append("UPDATE `BG_Data` SET ");
            queryBuilder.append(
                    "`NKills` = ?, `NWins` = ?, `NGoals` = ?, `FKills` = ?, `FWins` = ?, `FGoals` = ?, `Coins` = ?, `Inventory` = ?, `XP` = ?, `Placed` = ?, `Broken` = ? ");
            queryBuilder.append("WHERE `UUID` = ?;");
            preparedStatement = connection.prepareStatement(queryBuilder.toString());
            preparedStatement.setInt(1, stat.getNormalKills());
            preparedStatement.setInt(2, stat.getNormalWins());
            preparedStatement.setInt(3, stat.getNormalGoals());
            preparedStatement.setInt(4, stat.getFourKills());
            preparedStatement.setInt(5, stat.getFourWins());
            preparedStatement.setInt(6, stat.getFourGoals());
            preparedStatement.setInt(7, stat.getCoins());
            if (stat.getHotbar() != null) {
                preparedStatement.setString(8, plugin.getKit().itemStackArrayToBase64(stat.getHotbar()));
            } else {
                preparedStatement.setString(8, "none");
            }
            preparedStatement.setInt(9, stat.getXp());
            preparedStatement.setInt(10, stat.getPlaced());
            preparedStatement.setInt(11, stat.getBroken());
            preparedStatement.setString(12, stat.getUUID());
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
                        "CREATE TABLE IF NOT EXISTS BG_Data (UUID VARCHAR(60), Name VARCHAR(60), NKills INT, NWins INT, NGoals INT, FKills INT, FWins INT, FGoals INT, Coins INT, Inventory TEXT, XP INT, Placed INT, Broken INT, Cage TEXT)");
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
