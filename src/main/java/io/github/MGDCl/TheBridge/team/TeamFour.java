package io.github.MGDCl.TheBridge.team;

import io.github.MGDCl.TheBridge.TheBridge;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.math.transform.Transform;

import io.github.MGDCl.TheBridge.cosmetics.Cage;
import io.github.MGDCl.TheBridge.game.GameFour;
import io.github.MGDCl.TheBridge.game.GameFour.FState;

import java.io.IOException;
import java.util.ArrayList;

public class TeamFour {

    private ArrayList<Player> players;
    private ArrayList<Location> portal;
    private Location hologram;
    private Location teamSpawn;
    private Location teamRespawn;
    private int life;
    private boolean death;
    private GameFour game;
    private ChatColor color;
    private Color fcolor;
    private String teamName;
    private String ally;
    private boolean cage;
    private EditSession editSessionRed;
    private EditSession editSessionBlue;
    private EditSession editSessionYellow;
    private EditSession editSessionGreen;
    private Cage cages;

    public TeamFour(GameFour game, ChatColor color, Color fcolor, String teamName, Location teamSpawn, Location teamRespawn, ArrayList<Location> portal, Location hologram) {
        this.players = new ArrayList<Player>();
        this.life = 2;
        this.game = game;
        this.fcolor = fcolor;
        this.death = false;
        this.teamSpawn = teamSpawn;
        this.teamRespawn = teamRespawn;
        this.portal = portal;
        this.color = color;
        this.teamName = teamName;
        this.hologram = hologram;
        this.ally = teamName.substring(0, 1);
        this.cage = false;
    }

    public void createCage(Cage cage) {
        cages = cage;
        if (color.equals(ChatColor.RED)) {
            Vector to = new Vector(teamSpawn.getBlockX(), teamSpawn.getBlockY(), teamSpawn.getBlockZ());
            BukkitWorld world = new BukkitWorld(teamSpawn.getWorld());
            try {
                editSessionRed = ClipboardFormat.findByFile(cage.getFileRed()).load(cage.getFileRed()).paste(world, to, true, true, (Transform) null);
                editSessionRed.flushQueue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (color.equals(ChatColor.BLUE)) {
            Vector to = new Vector(teamSpawn.getBlockX(), teamSpawn.getBlockY(), teamSpawn.getBlockZ());
            BukkitWorld world = new BukkitWorld(teamSpawn.getWorld());
            try {
                editSessionBlue = ClipboardFormat.findByFile(cage.getFileBlue()).load(cage.getFileBlue()).paste(world, to, true, true, (Transform) null);
                editSessionBlue.flushQueue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (color.equals(ChatColor.YELLOW)) {
            Vector to = new Vector(teamSpawn.getBlockX(), teamSpawn.getBlockY(), teamSpawn.getBlockZ());
            BukkitWorld world = new BukkitWorld(teamSpawn.getWorld());
            try {
                editSessionYellow = ClipboardFormat.findByFile(cage.getFileYellow()).load(cage.getFileYellow()).paste(world, to, true, true, (Transform) null);
                editSessionYellow.flushQueue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (color.equals(ChatColor.GREEN)) {
            Vector to = new Vector(teamSpawn.getBlockX(), teamSpawn.getBlockY(), teamSpawn.getBlockZ());
            BukkitWorld world = new BukkitWorld(teamSpawn.getWorld());
            try {
                editSessionGreen = ClipboardFormat.findByFile(cage.getFileGreen()).load(cage.getFileGreen()).paste(world, to, true, true, (Transform) null);
                editSessionGreen.flushQueue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeCage() {
        Vector to = new Vector(teamSpawn.getBlockX(), teamSpawn.getBlockY(), teamSpawn.getBlockZ());
        BukkitWorld world = new BukkitWorld(teamSpawn.getWorld());
        try {
            editSessionBlue = ClipboardFormat.findByFile(cages.getClear()).load(cages.getClear()).paste(world, to, true, true, (Transform) null);
            editSessionBlue.flushQueue();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public ArrayList<Location> getPortal() {
        return portal;
    }

    public void setPortal(ArrayList<Location> portal) {
        this.portal = portal;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public Color getFColor() {
        return this.fcolor;
    }

    public String getTeamName() {
        return color + this.teamName;
    }

    public String getAlly() {
        return color + this.ally;
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

    public void addPlayer(Player p) {
        if (!players.contains(p)) {
            players.add(p);
        }
    }

    public void removePlayer(Player p) {
        if (players.contains(p)) {
            players.remove(p);
        }
        if (players.size() == 0 && game.isState(FState.INGAME) && game.isState(FState.PREGAME)) {
            killTeam();
        }
    }

    public void killTeam() {
        setDeath(true);
        ArrayList<Player> temp = new ArrayList<Player>();
        for (Player p : players) {
            p.playSound(p.getLocation(), Sound.valueOf(TheBridge.get().getSounds().get("sounds.teamFour.death")), 1.0f, 1.0f);
            for (Player on : game.getPlayers()) {
                on.sendMessage(TheBridge.get().getLang().get("messages.eliminated").replaceAll("<color>", "" + color).replaceAll("<player>", p.getName()));
            }
            temp.add(p);
        }
        players.clear();
        for (Player p : temp) {
            game.setSpect(p);
        }
    }

    public void addLife(int life) {
        this.life = this.life + life;
    }

    public void removeLife(int life) {
        this.life = this.life - life;
    }

    public boolean getDeath() {
        return this.death;
    }

    public void setDeath(boolean b) {
        this.death = b;
    }

    public String getLifeString() {
        if (getDeath()) {
            return "§8❤❤❤❤❤❤❤❤";
        }
        String c = "";
        String r = "";
        for (int i = 0; i < life; i++) {
            c = c + "❤";
        }
        for (int i = life; i < 8; i++) {
            r = r + "❤";
        }
        String li = color + c + "§7" + r;
        return li;
    }

    public int getLife() {
        return this.life;
    }

    public ArrayList<Player> getTeamPlayers() {
        return players;
    }

    public int getTeamSize() {
        return players.size();
    }

    public Location getTeamSpawn() {
        return teamSpawn;
    }

    public Location getTeamRespawn() {
        return teamRespawn;
    }

}
