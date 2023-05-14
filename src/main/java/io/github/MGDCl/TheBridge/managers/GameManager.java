package io.github.MGDCl.TheBridge.managers;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.game.GameDuo;
import io.github.MGDCl.TheBridge.game.GameFour;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameManager {

    private ArrayList<GameDuo> normalGames;
    private ArrayList<GameFour> fourGames;
    private HashMap<Player, GameDuo> playerGame;
    private HashMap<Player, GameFour> playerGameFour;
    private Inventory kit;
    TheBridge plugin;

    public GameManager(TheBridge plugin) {
        this.plugin = plugin;
        this.normalGames = new ArrayList<>();
        this.fourGames = new ArrayList<>();
        this.playerGame = new HashMap<Player, GameDuo>();
        this.playerGameFour = new HashMap<Player, GameFour>();
        this.kit = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
        if (plugin.getConfig().getString("kit") == null) {
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.RED + "You don't have a default kit set up. Use " + ChatColor.YELLOW + "/bridges setkit");
        } else {
            try {
                kit.setContents(plugin.getKit().fromBase64(plugin.getConfig().getString("kit")).getContents());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.loadGames();
    }

    public void reloadKit() {
        try {
            kit.setContents(plugin.getKit().fromBase64(plugin.getConfig().getString("kit")).getContents());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Inventory getKit() {
        return kit;
    }

    public void resetNormalGame(String file) {
        GameDuo prev = null;
        for (GameDuo game : normalGames) {
            if (game.getName().equals(file)) {
                prev = game;
            }
        }
        normalGames.remove(prev);
        File arena = new File(plugin.getDataFolder() + "/arenas/" + file + ".yml");
        FileConfiguration yml = YamlConfiguration.loadConfiguration(arena);
        String name = yml.getString("name");
        if (yml.getString("locations.lobby") == null) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The map " + ChatColor.YELLOW + name + " "
                    + ChatColor.RED + "doesn't have lobby location set up.");
        }
        if (yml.getString("locations.spect") == null) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The map " + ChatColor.YELLOW + name + " "
                    + ChatColor.RED + "doesn't have spectator location set up.");
        }
        Location hRed = null;
        Location hBlue = null;
        if (yml.get("locations.holograms.red") != null) {
            hRed = getStringLocation(yml.getString("locations.holograms.red"));
        }
        if (yml.get("locations.holograms.blue") != null) {
            hBlue = getStringLocation(yml.getString("locations.holograms.blue"));
        }
        plugin.getWc().resetWorld(name);
        int min = yml.getInt("minPlayers");
        int teamSize = yml.getInt("teamSize");
        int starting = yml.getInt("timers.starting");
        int prestart = yml.getInt("timers.prestart");
        int restart = yml.getInt("timers.restart");
        String lobby = yml.getString("locations.lobby");
        String spect = yml.getString("locations.spect");
        String buildMin = yml.getString("locations.build.min");
        String buildMax = yml.getString("locations.build.max");
        String spawnsRed = yml.getString("locations.spawns.red");
        String spawnsBlue = yml.getString("locations.spawns.blue");
        String respawnsRed = yml.getString("locations.respawns.red");
        String respawnsBlue = yml.getString("locations.respawns.blue");
        ArrayList<Location> portalRed = new ArrayList<Location>();
        for (String locRed : yml.getStringList("locations.portal.red")) {
            portalRed.add(getStringLocation(locRed));
        }
        ArrayList<Location> portalBlue = new ArrayList<Location>();
        for (String locBlue : yml.getStringList("locations.portal.blue")) {
            portalBlue.add(getStringLocation(locBlue));
        }
        normalGames.add(new GameDuo(plugin, name, min, 2 * teamSize, teamSize, starting, prestart, restart,
                getStringLocation(lobby), getStringLocation(spect), getStringLocation(buildMin),
                getStringLocation(buildMax), getStringLocation(spawnsRed), getStringLocation(spawnsBlue), portalRed,
                portalBlue, getStringLocation(respawnsRed), getStringLocation(respawnsBlue), hRed, hBlue));
    }

    public void resetFourGame(String file) {
        GameFour prev = null;
        for (GameFour game : fourGames) {
            if (game.getName().equals(file)) {
                prev = game;
            }
        }
        fourGames.remove(prev);
        File arena = new File(plugin.getDataFolder() + "/arenas/" + file + ".yml");
        FileConfiguration yml = YamlConfiguration.loadConfiguration(arena);
        String name = yml.getString("name");
        plugin.getWc().resetWorld(name);
        Location hRed = null;
        Location hBlue = null;
        Location hGreen = null;
        Location hYellow = null;
        if (yml.get("locations.holograms.red") != null) {
            hRed = getStringLocation(yml.getString("locations.holograms.red"));
        }
        if (yml.get("locations.holograms.blue") != null) {
            hBlue = getStringLocation(yml.getString("locations.holograms.blue"));
        }
        if (yml.get("locations.holograms.green") != null) {
            hGreen = getStringLocation(yml.getString("locations.holograms.green"));
        }
        if (yml.get("locations.holograms.yellow") != null) {
            hYellow = getStringLocation(yml.getString("locations.holograms.yellow"));
        }
        int min = yml.getInt("minPlayers");
        int teamSize = yml.getInt("teamSize");
        int starting = yml.getInt("timers.starting");
        int prestart = yml.getInt("timers.prestart");
        String lobby = yml.getString("locations.lobby");
        String spect = yml.getString("locations.spect");
        String buildMin = yml.getString("locations.build.min");
        String buildMax = yml.getString("locations.build.max");
        String spawnsRed = yml.getString("locations.spawns.red");
        String spawnsBlue = yml.getString("locations.spawns.blue");
        String spawnsGreen = yml.getString("locations.spawns.green");
        String spawnsYellow = yml.getString("locations.spawns.yellow");
        String respawnsRed = yml.getString("locations.respawns.red");
        String respawnsBlue = yml.getString("locations.respawns.blue");
        String respawnsGreen = yml.getString("locations.respawns.green");
        String respawnsYellow = yml.getString("locations.respawns.yellow");
        ArrayList<Location> portalRed = new ArrayList<Location>();
        for (String locRed : yml.getStringList("locations.portal.red")) {
            portalRed.add(getStringLocation(locRed));
        }
        ArrayList<Location> portalBlue = new ArrayList<Location>();
        for (String locBlue : yml.getStringList("locations.portal.blue")) {
            portalBlue.add(getStringLocation(locBlue));
        }
        ArrayList<Location> portalGreen = new ArrayList<Location>();
        for (String locGreen : yml.getStringList("locations.portal.green")) {
            portalGreen.add(getStringLocation(locGreen));
        }
        ArrayList<Location> portalYellow = new ArrayList<Location>();
        for (String locYellow : yml.getStringList("locations.portal.yellow")) {
            portalYellow.add(getStringLocation(locYellow));
        }
        fourGames.add(new GameFour(plugin, name, min, 4 * teamSize, teamSize, starting, prestart,
                getStringLocation(lobby), getStringLocation(spect), getStringLocation(buildMin),
                getStringLocation(buildMax), getStringLocation(spawnsRed), getStringLocation(spawnsBlue),
                getStringLocation(spawnsGreen), getStringLocation(spawnsYellow), getStringLocation(respawnsRed),
                getStringLocation(respawnsBlue), getStringLocation(respawnsGreen), getStringLocation(respawnsYellow),
                portalRed, portalBlue, portalGreen, portalYellow, hRed, hBlue, hGreen, hYellow));
    }

    public void loadGames() {
        File folder = new File(plugin.getDataFolder() + "/arenas");
        File[] listFiles = folder.listFiles();
        for (int f = 0; f < listFiles.length; f++) {
            if (listFiles[f].isFile()) {
                File arena = listFiles[f];
                FileConfiguration yml = YamlConfiguration.loadConfiguration(arena);
                if (yml.getString("mode").toLowerCase().equals("normal")) {
                    String name = yml.getString("name");
                    if (yml.getString("locations.lobby") == null) {
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The map " + ChatColor.YELLOW
                                + name + " " + ChatColor.RED + "doesn't have lobby location set up.");
                        continue;
                    }
                    if (yml.getString("locations.spect") == null) {
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The map " + ChatColor.YELLOW
                                + name + " " + ChatColor.RED + "doesn't have spectator location set up.");
                        continue;
                    }
                    plugin.getWc().resetWorld(name);
                    Location hRed = null;
                    Location hBlue = null;
                    if (yml.get("locations.holograms.red") != null) {
                        hRed = getStringLocation(yml.getString("locations.holograms.red"));
                    }
                    if (yml.get("locations.holograms.blue") != null) {
                        hBlue = getStringLocation(yml.getString("locations.holograms.blue"));
                    }
                    int min = yml.getInt("minPlayers");
                    int teamSize = yml.getInt("teamSize");
                    int starting = yml.getInt("timers.starting");
                    int prestart = yml.getInt("timers.prestart");
                    int restart = yml.getInt("timers.restart");
                    String lobby = yml.getString("locations.lobby");
                    String spect = yml.getString("locations.spect");
                    String buildMin = yml.getString("locations.build.min");
                    String buildMax = yml.getString("locations.build.max");
                    String spawnsRed = yml.getString("locations.spawns.red");
                    String spawnsBlue = yml.getString("locations.spawns.blue");
                    String respawnsRed = yml.getString("locations.respawns.red");
                    String respawnsBlue = yml.getString("locations.respawns.blue");
                    ArrayList<Location> portalRed = new ArrayList<Location>();
                    for (String locRed : yml.getStringList("locations.portal.red")) {
                        portalRed.add(getStringLocation(locRed));
                    }
                    ArrayList<Location> portalBlue = new ArrayList<Location>();
                    for (String locBlue : yml.getStringList("locations.portal.blue")) {
                        portalBlue.add(getStringLocation(locBlue));
                    }
                    normalGames.add(new GameDuo(plugin, name, min, 2 * teamSize, teamSize, starting, prestart, restart,
                            getStringLocation(lobby), getStringLocation(spect), getStringLocation(buildMin),
                            getStringLocation(buildMax), getStringLocation(spawnsRed), getStringLocation(spawnsBlue),
                            portalRed, portalBlue, getStringLocation(respawnsRed), getStringLocation(respawnsBlue),
                            hRed, hBlue));
                }
                if (yml.getString("mode").toLowerCase().equals("four")) {
                    String name = yml.getString("name");
                    plugin.getWc().resetWorld(name);
                    int min = yml.getInt("minPlayers");
                    Location hRed = null;
                    Location hBlue = null;
                    Location hGreen = null;
                    Location hYellow = null;
                    if (yml.get("locations.holograms.red") != null) {
                        hRed = getStringLocation(yml.getString("locations.holograms.red"));
                    }
                    if (yml.get("locations.holograms.blue") != null) {
                        hBlue = getStringLocation(yml.getString("locations.holograms.blue"));
                    }
                    if (yml.get("locations.holograms.green") != null) {
                        hGreen = getStringLocation(yml.getString("locations.holograms.green"));
                    }
                    if (yml.get("locations.holograms.yellow") != null) {
                        hYellow = getStringLocation(yml.getString("locations.holograms.yellow"));
                    }
                    int teamSize = yml.getInt("teamSize");
                    int starting = yml.getInt("timers.starting");
                    int prestart = yml.getInt("timers.prestart");
                    String lobby = yml.getString("locations.lobby");
                    String spect = yml.getString("locations.spect");
                    String buildMin = yml.getString("locations.build.min");
                    String buildMax = yml.getString("locations.build.max");
                    String spawnsRed = yml.getString("locations.spawns.red");
                    String spawnsBlue = yml.getString("locations.spawns.blue");
                    String spawnsGreen = yml.getString("locations.spawns.green");
                    String spawnsYellow = yml.getString("locations.spawns.yellow");
                    String respawnsRed = yml.getString("locations.respawns.red");
                    String respawnsBlue = yml.getString("locations.respawns.blue");
                    String respawnsGreen = yml.getString("locations.respawns.green");
                    String respawnsYellow = yml.getString("locations.respawns.yellow");
                    ArrayList<Location> portalRed = new ArrayList<Location>();
                    for (String locRed : yml.getStringList("locations.portal.red")) {
                        portalRed.add(getStringLocation(locRed));
                    }
                    ArrayList<Location> portalBlue = new ArrayList<Location>();
                    for (String locBlue : yml.getStringList("locations.portal.blue")) {
                        portalBlue.add(getStringLocation(locBlue));
                    }
                    ArrayList<Location> portalGreen = new ArrayList<Location>();
                    for (String locGreen : yml.getStringList("locations.portal.green")) {
                        portalGreen.add(getStringLocation(locGreen));
                    }
                    ArrayList<Location> portalYellow = new ArrayList<Location>();
                    for (String locYellow : yml.getStringList("locations.portal.yellow")) {
                        portalYellow.add(getStringLocation(locYellow));
                    }
                    fourGames.add(new GameFour(plugin, name, min, 4 * teamSize, teamSize, starting, prestart,
                            getStringLocation(lobby), getStringLocation(spect), getStringLocation(buildMin),
                            getStringLocation(buildMax), getStringLocation(spawnsRed), getStringLocation(spawnsBlue),
                            getStringLocation(spawnsGreen), getStringLocation(spawnsYellow),
                            getStringLocation(respawnsRed), getStringLocation(respawnsBlue),
                            getStringLocation(respawnsGreen), getStringLocation(respawnsYellow), portalRed, portalBlue,
                            portalGreen, portalYellow, hRed, hBlue, hGreen, hYellow));
                }
            }
        }
    }

    public void addPlayerGame(Player p, GameDuo game) {
        playerGame.put(p, game);
        game.addPlayer(p);
    }

    public void addPlayerGameFour(Player p, GameFour game) {
        playerGameFour.put(p, game);
        game.addPlayer(p);
    }

    public void removePlayerGame(Player p, GameDuo game) {
        game.removePlayer(p);
        playerGame.remove(p);
        plugin.getSb().update(p);
    }

    public void removePlayerGameFour(Player p, GameFour game) {
        game.removePlayer(p);
        playerGameFour.remove(p);
        plugin.getSb().update(p);
    }

    public void removePlayerAllGames(Player p) {
        for (GameDuo game : normalGames) {
            if (game.getGamePlayers().contains(p)) {
                game.removePlayer(p);
                playerGame.remove(p);
                plugin.getSb().update(p);
            }
        }
    }

    public void removePlayerFourGames(Player p) {
        for (GameFour game : fourGames) {
            if (game.getPlayers().contains(p)) {
                game.removePlayer(p);
                playerGameFour.remove(p);
                plugin.getSb().update(p);
            }
        }
    }

    public GameDuo getGameByPlayer(Player p) {
        if (playerGame.containsKey(p)) {
            return playerGame.get(p);
        }
        return null;
    }

    public GameFour getGameFourByPlayer(Player p) {
        if (playerGameFour.containsKey(p)) {
            return playerGameFour.get(p);
        }
        return null;
    }

    public GameDuo getGameByName(String name) {
        for (GameDuo game : normalGames) {
            if (game.getName().equals(name)) {
                return game;
            }
        }
        return null;
    }

    public GameFour getGameFourByName(String name) {
        for (GameFour game : fourGames) {
            if (game.getName().equals(name)) {
                return game;
            }
        }
        return null;
    }

    public Location getStringLocation(String location) {
        String[] l = location.split(";");
        World world = Bukkit.getWorld(l[0]);
        double x = Double.parseDouble(l[1]);
        double y = Double.parseDouble(l[2]);
        double z = Double.parseDouble(l[3]);
        float yaw = Float.parseFloat(l[4]);
        float pitch = Float.parseFloat(l[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public ArrayList<GameDuo> getGames() {
        return normalGames;
    }

    public ArrayList<GameFour> getGamesFour() {
        return fourGames;
    }

}
