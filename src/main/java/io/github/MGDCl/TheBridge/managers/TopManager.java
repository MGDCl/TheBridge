package io.github.MGDCl.TheBridge.managers;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.hologram.TruenoHologram;
import io.github.MGDCl.TheBridge.hologram.TruenoHologramAPI;
import io.github.MGDCl.TheBridge.tops.BoardType;
import io.github.MGDCl.TheBridge.tops.Top;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class TopManager {

    private HashMap<Integer, Top> nkills = new HashMap<Integer, Top>();
    private HashMap<Integer, Top> nwins = new HashMap<Integer, Top>();
    private HashMap<Integer, Top> ngoals = new HashMap<Integer, Top>();
    private HashMap<Integer, Top> fkills = new HashMap<Integer, Top>();
    private HashMap<Integer, Top> fwins = new HashMap<Integer, Top>();
    private HashMap<Integer, Top> fgoals = new HashMap<Integer, Top>();
    private ArrayList<String> nkillslines = new ArrayList<String>();
    private ArrayList<String> nwinslines = new ArrayList<String>();
    private ArrayList<String> ngoalslines = new ArrayList<String>();
    private ArrayList<String> fkillslines = new ArrayList<String>();
    private ArrayList<String> fwinslines = new ArrayList<String>();
    private ArrayList<String> fgoalslines = new ArrayList<String>();
    private HashMap<Player, ArrayList<TruenoHologram>> playerHolo = new HashMap<Player, ArrayList<TruenoHologram>>();
    private HashMap<BoardType, TruenoHologram> holo = new HashMap<BoardType, TruenoHologram>();
    TheBridge plugin;

    public TopManager(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void updateTops() {
        plugin.getLm().reloadLocations();
        nkills.clear();
        nwins.clear();
        ngoals.clear();
        fkills.clear();
        fwins.clear();
        fgoals.clear();
        plugin.getDb().loadNormalKills();
        plugin.getDb().loadNormalWins();
        plugin.getDb().loadNormalGoals();
        plugin.getDb().loadFourKills();
        plugin.getDb().loadFourWins();
        plugin.getDb().loadFourGoals();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[TheBridge] Tops Updated");
    }

    public void createInfo(Player p) {
        if (!playerHolo.containsKey(p)) {
            playerHolo.put(p, new ArrayList<TruenoHologram>());
        }
        removeHolo(p);
        if (plugin.getLm().getStats() != null) {
            ArrayList<String> stats = new ArrayList<String>();
            Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    for (String msg2 : plugin.getLang().getList("stats")) {
                        stats.add(PlaceholderAPI.setPlaceholders(p, ChatColor.translateAlternateColorCodes('&', msg2)));
                    }
                    TruenoHologram hologram2 = TruenoHologramAPI.getNewHologram();
                    hologram2.setupPlayerHologram(p, plugin.getLm().getStats(), stats);
                    hologram2.display();
                    playerHolo.get(p).add(hologram2);
                }
            }, 20L);
        }
    }

    public void removeHolo(Player p) {
        for (TruenoHologram holo : playerHolo.get(p)) {
            holo.delete();
        }
    }

    public void removeHolo() {
        for (TruenoHologram holo : holo.values()) {
            holo.delete();
        }
    }

    public void removeHolo(BoardType type) {
        if (holo.containsKey(type)) {
            holo.get(type).delete();
        }
    }

    public void createTop(Location location, BoardType type) {
        if (type.equals(BoardType.NORMAL_KILLS)) {
            removeHolo(type);
            nkillslines.clear();
            for (String line : plugin.getLang().getList("tops.normal.kills")) {
                if (line.contains("<top>")) {
                    for (Top top : nkills.values()) {
                        nkillslines.add(ChatColor.YELLOW + "" + ChatColor.BOLD + top.getTop() + ".- " + ChatColor.GRAY
                                + top.getName() + " - " + ChatColor.GOLD + "" + top.getAmount());
                    }
                } else {
                    nkillslines.add(ChatColor.translateAlternateColorCodes('&', line));
                }
            }
            TruenoHologram hologram = TruenoHologramAPI.getNewHologram();
            hologram.setDistanceBetweenLines(0.35);
            hologram.setupWorldHologram(location, nkillslines);
            hologram.display();
            holo.put(type, hologram);
        }
        if (type.equals(BoardType.NORMAL_WINS)) {
            removeHolo(type);
            nwinslines.clear();
            for (String line : plugin.getLang().getList("tops.normal.wins")) {
                if (line.contains("<top>")) {
                    for (Top top : nwins.values()) {
                        nwinslines.add(ChatColor.YELLOW + "" + ChatColor.BOLD + top.getTop() + ".- " + ChatColor.GRAY
                                + top.getName() + " - " + ChatColor.GOLD + "" + top.getAmount());
                    }
                } else {
                    nwinslines.add(ChatColor.translateAlternateColorCodes('&', line));
                }
            }
            TruenoHologram hologram = TruenoHologramAPI.getNewHologram();
            hologram.setDistanceBetweenLines(0.35);
            hologram.setupWorldHologram(location, nwinslines);
            hologram.display();
            holo.put(type, hologram);
        }
        if (type.equals(BoardType.NORMAL_GOALS)) {
            removeHolo(type);
            ngoalslines.clear();
            for (String line : plugin.getLang().getList("tops.normal.goals")) {
                if (line.contains("<top>")) {
                    for (Top top : ngoals.values()) {
                        ngoalslines.add(ChatColor.YELLOW + "" + ChatColor.BOLD + top.getTop() + ".- " + ChatColor.GRAY
                                + top.getName() + " - " + ChatColor.GOLD + "" + top.getAmount());
                    }
                } else {
                    ngoalslines.add(ChatColor.translateAlternateColorCodes('&', line));
                }
            }
            TruenoHologram hologram = TruenoHologramAPI.getNewHologram();
            hologram.setDistanceBetweenLines(0.35);
            hologram.setupWorldHologram(location, ngoalslines);
            hologram.display();
            holo.put(type, hologram);
        }
        if (type.equals(BoardType.FOUR_KILLS)) {
            removeHolo(type);
            fkillslines.clear();
            for (String line : plugin.getLang().getList("tops.four.kills")) {
                if (line.contains("<top>")) {
                    for (Top top : fkills.values()) {
                        fkillslines.add(ChatColor.YELLOW + "" + ChatColor.BOLD + top.getTop() + ".- " + ChatColor.GRAY
                                + top.getName() + " - " + ChatColor.GOLD + "" + top.getAmount());
                    }
                } else {
                    fkillslines.add(ChatColor.translateAlternateColorCodes('&', line));
                }
            }
            TruenoHologram hologram = TruenoHologramAPI.getNewHologram();
            hologram.setDistanceBetweenLines(0.35);
            hologram.setupWorldHologram(location, fkillslines);
            hologram.display();
            holo.put(type, hologram);
        }
        if (type.equals(BoardType.FOUR_WINS)) {
            removeHolo(type);
            fwinslines.clear();
            for (String line : plugin.getLang().getList("tops.four.wins")) {
                if (line.contains("<top>")) {
                    for (Top top : fwins.values()) {
                        fwinslines.add(ChatColor.YELLOW + "" + ChatColor.BOLD + top.getTop() + ".- " + ChatColor.GRAY
                                + top.getName() + " - " + ChatColor.GOLD + "" + top.getAmount());
                    }
                } else {
                    fwinslines.add(ChatColor.translateAlternateColorCodes('&', line));
                }
            }
            TruenoHologram hologram = TruenoHologramAPI.getNewHologram();
            hologram.setDistanceBetweenLines(0.35);
            hologram.setupWorldHologram(location, fwinslines);
            hologram.display();
            holo.put(type, hologram);
        }
        if (type.equals(BoardType.FOUR_GOALS)) {
            removeHolo(type);
            fgoalslines.clear();
            for (String line : plugin.getLang().getList("tops.four.goals")) {
                if (line.contains("<top>")) {
                    for (Top top : fgoals.values()) {
                        fgoalslines.add(ChatColor.YELLOW + "" + ChatColor.BOLD + top.getTop() + ".- " + ChatColor.GRAY
                                + top.getName() + " - " + ChatColor.GOLD + "" + top.getAmount());
                    }
                } else {
                    fgoalslines.add(ChatColor.translateAlternateColorCodes('&', line));
                }
            }
            TruenoHologram hologram = TruenoHologramAPI.getNewHologram();
            hologram.setDistanceBetweenLines(0.35);
            hologram.setupWorldHologram(location, fgoalslines);
            hologram.display();
            holo.put(type, hologram);
        }
    }

    public void addTop(Top top) {
        if (top.getType().equals(BoardType.NORMAL_KILLS)) {
            nkills.put(getTopNumber(top), top);
        } else if (top.getType().equals(BoardType.NORMAL_WINS)) {
            nwins.put(getTopNumber(top), top);
        } else if (top.getType().equals(BoardType.NORMAL_GOALS)) {
            ngoals.put(getTopNumber(top), top);
        } else if (top.getType().equals(BoardType.FOUR_KILLS)) {
            fkills.put(getTopNumber(top), top);
        } else if (top.getType().equals(BoardType.FOUR_WINS)) {
            fwins.put(getTopNumber(top), top);
        } else if (top.getType().equals(BoardType.FOUR_GOALS)) {
            fgoals.put(getTopNumber(top), top);
        }
    }

    public int getTopNumber(Top top) {
        if (top.getType().equals(BoardType.NORMAL_KILLS)) {
            return nkills.size() + 1;
        }
        if (top.getType().equals(BoardType.NORMAL_WINS)) {
            return nwins.size() + 1;
        }
        if (top.getType().equals(BoardType.NORMAL_GOALS)) {
            return ngoals.size() + 1;
        }
        if (top.getType().equals(BoardType.FOUR_KILLS)) {
            return fkills.size() + 1;
        }
        if (top.getType().equals(BoardType.FOUR_WINS)) {
            return fwins.size() + 1;
        }
        if (top.getType().equals(BoardType.FOUR_GOALS)) {
            return fgoals.size() + 1;
        }
        return 0;
    }

    public HashMap<Player, ArrayList<TruenoHologram>> getHolograms() {
        return playerHolo;
    }

}
