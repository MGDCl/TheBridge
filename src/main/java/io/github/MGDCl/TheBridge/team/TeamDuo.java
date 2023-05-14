package io.github.MGDCl.TheBridge.team;

import java.io.IOException;
import java.util.ArrayList;

import io.github.MGDCl.TheBridge.cosmetics.Cage;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.boydti.fawe.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.math.transform.Transform;

public class TeamDuo {

    private ArrayList<Player> players;
    private ArrayList<Location> portal;
    private Location hologram;
    private Location spawn;
    private Location respawn;
    private String teamName;
    private ChatColor color;
    private Color fcolor;
    private int goals;
    private boolean cage;
    private EditSession editSessionRed;
    private EditSession editSessionBlue;
    private Cage cages;

    public TeamDuo(String teamName, ChatColor color, Color fcolor, Location respawn, Location spawn, ArrayList<Location> portal, Location hologram) {
        this.color = color;
        this.teamName = teamName;
        this.players = new ArrayList<Player>();
        this.goals = 0;
        this.spawn = spawn;
        this.respawn = respawn;
        this.portal = portal;
        this.fcolor = fcolor;
        this.hologram = hologram;
        this.cage = false;
    }

    public void createCage(Cage cage) {
        cages = cage;
        if (color.equals(ChatColor.RED)) {
            Vector to = new Vector(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
            BukkitWorld world = new BukkitWorld(spawn.getWorld());
            TaskManager.IMP.async(new Runnable() {
                @Override
                public void run() {
                    try {
                        editSessionRed = ClipboardFormat.findByFile(cage.getFileRed()).load(cage.getFileRed()).paste(world, to, true, true, (Transform) null);
                        editSessionRed.flushQueue();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
        if (color.equals(ChatColor.BLUE)) {
            Vector to = new Vector(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
            BukkitWorld world = new BukkitWorld(spawn.getWorld());
            TaskManager.IMP.async(new Runnable() {
                @Override
                public void run() {
                    try {
                        editSessionBlue = ClipboardFormat.findByFile(cage.getFileBlue()).load(cage.getFileBlue()).paste(world, to, true, true, (Transform) null);
                        editSessionBlue.flushQueue();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    editSessionBlue.flushQueue();
                }
            });
        }
    }

    public void removeCage() {
        Vector to = new Vector(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
        BukkitWorld world = new BukkitWorld(spawn.getWorld());
        TaskManager.IMP.async(new Runnable() {
            @Override
            public void run() {
                try {
                    editSessionBlue = ClipboardFormat.findByFile(cages.getClear()).load(cages.getClear()).paste(world, to, true, true, (Transform) null);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                editSessionBlue.flushQueue();
            }
        });
    }

    public boolean isCage() {
        return cage;
    }

    public void setCage(boolean cage) {
        this.cage = cage;
    }

    public Location getHologram() {
        return hologram;
    }

    public String getTeamName() {
        return teamName;
    }

    public ChatColor getColor() {
        return color;
    }

    public Location getTeamSpawn() {
        return spawn;
    }

    public Location getTeamRespawn() {
        return respawn;
    }

    public Color getFColor() {
        return fcolor;
    }

    public ArrayList<Location> getPortal() {
        return portal;
    }

    public void createPortal() {
        for (Location loc : portal) {
            loc.getBlock().setType(Material.ENDER_PORTAL);
        }
    }

    public void deletePortal() {
        for (Location loc : portal) {
            loc.getBlock().setType(Material.AIR);
        }
    }

    public void addGoal() {
        this.goals = this.goals + 1;
    }

    public int getGoals() {
        return goals;
    }

    public void addPlayer(Player p) {
        if (!players.contains(p)) {
            players.add(p);
        }
    }

    public void removePlayer(Player p) {
        if (players.contains(p)) {
            players.remove(p);
        }
    }

    public ArrayList<Player> getTeamPlayers() {
        return players;
    }

    public int getTeamSize() {
        return players.size();
    }

}
