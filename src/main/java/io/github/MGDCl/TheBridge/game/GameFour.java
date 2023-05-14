package io.github.MGDCl.TheBridge.game;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.database.PlayerStat;
import io.github.MGDCl.TheBridge.hologram.TruenoHologram;
import io.github.MGDCl.TheBridge.hologram.TruenoHologramAPI;
import io.github.MGDCl.TheBridge.team.TeamFour;
import io.github.MGDCl.TheBridge.utils.CenterMessage;
import io.github.MGDCl.TheBridge.utils.ItemBuilder;
import io.github.MGDCl.TheBridge.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameFour {

    private TheBridge plugin;
    private HashMap<ChatColor, TeamFour> teams;
    private HashMap<Player, ChatColor> teamPlayer;
    private ArrayList<Player> players;
    private ArrayList<Player> spects;
    private HashMap<Player, PlayerData> pd = new HashMap<Player, PlayerData>();
    private HashMap<Player, Integer> goals = new HashMap<Player, Integer>();
    private HashMap<Player, Integer> kills = new HashMap<Player, Integer>();
    private ArrayList<Location> build = new ArrayList<Location>();
    private ArrayList<Location> placed = new ArrayList<Location>();
    private ArrayList<TruenoHologram> holograms = new ArrayList<TruenoHologram>();
    private Location lobby;
    private Location spect;
    private String name;
    private String mode;
    private int min;
    private int max;
    private int teamSize;
    private int starting;
    private int prestart;
    private BukkitTask start;
    private FState state;

    public GameFour(TheBridge plugin, String name, int min, int max, int teamSize, int starting, int prestart,
                    Location lobby, Location spect, Location buildMin, Location buildMax, Location spawnRed, Location spawnBlue,
                    Location spawnGreen, Location spawnYellow, Location respawnRed, Location respawnBlue, Location respawnGreen,
                    Location respawnYellow, ArrayList<Location> portalRed, ArrayList<Location> portalBlue,
                    ArrayList<Location> portalGreen, ArrayList<Location> portalYellow, Location hRed, Location hBlue,
                    Location hGreen, Location hYellow) {
        this.plugin = plugin;
        this.name = name;
        this.starting = starting;
        this.prestart = prestart;
        this.mode = teamSize + "v" + teamSize + "v" + teamSize + "v" + teamSize;
        this.max = max;
        this.min = min;
        this.lobby = lobby;
        this.spect = spect;
        this.teamSize = teamSize;
        this.teamPlayer = new HashMap<>();
        this.teams = new HashMap<>();
        this.players = new ArrayList<>();
        this.spects = new ArrayList<>();
        int gn = Math.min(buildMin.getBlockX(), buildMax.getBlockX());
        int gn2 = Math.max(buildMin.getBlockX(), buildMax.getBlockX());
        int gn3 = Math.min(buildMin.getBlockZ(), buildMax.getBlockZ());
        int gn4 = Math.max(buildMin.getBlockZ(), buildMax.getBlockZ());
        int gn5 = Math.min(buildMin.getBlockY(), buildMax.getBlockY());
        int gn6 = Math.max(buildMin.getBlockY(), buildMax.getBlockY());
        for (int i = gn; i <= gn2; ++i) {
            for (int j = gn5; j <= gn6; ++j) {
                for (int k = gn3; k <= gn4; ++k) {
                    Location location3 = new Location(buildMin.getWorld(), (double) i, (double) j, (double) k);
                    build.add(location3);
                }
            }
        }
        this.teams.put(ChatColor.BLUE, new TeamFour(this, ChatColor.BLUE, Color.BLUE,
                plugin.getConfig().getString("names.blue"), spawnBlue, respawnBlue, portalBlue, hBlue));
        this.teams.put(ChatColor.RED, new TeamFour(this, ChatColor.RED, Color.RED,
                plugin.getConfig().getString("names.red"), spawnRed, respawnRed, portalRed, hRed));
        this.teams.put(ChatColor.GREEN, new TeamFour(this, ChatColor.GREEN, Color.GREEN,
                plugin.getConfig().getString("names.green"), spawnGreen, respawnGreen, portalGreen, hGreen));
        this.teams.put(ChatColor.YELLOW, new TeamFour(this, ChatColor.YELLOW, Color.YELLOW,
                plugin.getConfig().getString("names.yellow"), spawnYellow, respawnYellow, portalYellow, hYellow));
        setState(FState.WAITING);
        updateSign();
    }

    public void checkCancel() {
        if (isState(FState.STARTING) && players.size() <= getMin()) {
            if (start != null) {
                start.cancel();
            }
            setState(FState.WAITING);
            starting = 30;
            updateSign();
            for (Player on : players) {
                updateSB(on);
            }
        }
    }

    public void updateSB(Player p) {
        plugin.getSb().update(p);
    }

    public void updateSign() {
        plugin.getSim().updateGameFourSign(this);
    }

    public String getMode() {
        return mode;
    }

    public boolean checkWin(TeamFour win, TeamFour death, Player e) {

        if (getTeamsAlive() <= 1 && !isState(FState.FINISH)) {
            setState(FState.FINISH);
            updateSign();
            DecimalFormat df = new DecimalFormat("#.##");
            for (Player w : win.getTeamPlayers()) {
                PlayerStat.getPlayerStat(w).addFourWins();
            }
            for (Player p : players) {
                plugin.getNms().sendTitle(p, 0, 60, 0,
                        plugin.getLang().get("titles.winFour.title").replaceAll("<life>", win.getLifeString())
                                .replaceAll("<team>", win.getTeamName()).replaceAll("<color>", win.getColor() + ""),
                        plugin.getLang().get("titles.winFour.subtitle").replaceAll("<life>", win.getLifeString())
                                .replaceAll("<team>", win.getTeamName()).replaceAll("<color>", win.getColor() + ""));
                for (String msg : plugin.getLang().getList("messages.winFour")) {
                    CenterMessage.sendCenteredMessage(p, msg.replaceAll("&", "§").replaceAll("<player>", e.getName())
                            .replaceAll("<health>", df.format(e.getHealth())).replaceAll("<team>", win.getTeamName())
                            .replaceAll("<goals>", String.valueOf(goals.get(e)))
                            .replaceAll("<es>", (goals.get(e) > 1) ? "es" : "")
                            .replaceAll("<red>", teams.get(ChatColor.RED).getTeamName())
                            .replaceAll("<redLife>", teams.get(ChatColor.RED).getLifeString())
                            .replaceAll("<blue>", teams.get(ChatColor.BLUE).getTeamName())
                            .replaceAll("<blueLife>", teams.get(ChatColor.BLUE).getLifeString())
                            .replaceAll("<yellow>", teams.get(ChatColor.YELLOW).getTeamName())
                            .replaceAll("<yellowLife>", teams.get(ChatColor.YELLOW).getLifeString())
                            .replaceAll("<green>", teams.get(ChatColor.GREEN).getTeamName()).replaceAll("<heart>", "❤")
                            .replaceAll("<greenLife>", teams.get(ChatColor.GREEN).getLifeString())
                            .replaceAll("<teamStole>", (death.getTeamName() == null) ? "Stole" : death.getTeamName())
                            .replaceAll("<color>",
                                    (win.getColor() == null) ? ChatColor.WHITE + "" : win.getColor() + ""));
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (TruenoHologram th : holograms) {
                        th.delete();
                    }
                    for (Player p : spect.getWorld().getPlayers()) {
                        if (pd.containsKey(p)) {
                            for (Player on : Bukkit.getOnlinePlayers()) {
                                on.showPlayer(p);
                                p.showPlayer(on);
                            }
                            plugin.getGm().removePlayerFourGames(p);
                        } else {
                            p.teleport(plugin.getMainLobby());
                        }
                    }
                }
            }.runTaskLater(plugin, 20 * 10);
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getGm().resetFourGame(getName());
                }
            }.runTaskLater(plugin, 20 * 12);
            return true;
        }
        return false;
    }

    public void celebrateGoal(TeamFour team, TeamFour death, Player e) {
        DecimalFormat df = new DecimalFormat("#.##");
        addGoal(e);
        PlayerStat.getPlayerStat(e).addFourGoals();
        firework(getSpect(), team.getFColor());
        e.teleport(team.getTeamRespawn());
        if (getTeamsAlive() == 2) {
            death.removeLife(1);
            if (death.getLife() == 0) {
                death.killTeam();
                if (checkWin(team, death, e)) {
                    return;
                }
                for (Player p : players) {
                    plugin.getNms().sendTitle(p, 0, 20, 0,
                            plugin.getLang().get("titles.goalFinal.title").replaceAll("<player>", e.getName())
                                    .replaceAll("<heart>", "❤").replaceAll("<teamOther>", team.getTeamName())
                                    .replaceAll("<teamStole>", death.getTeamName())
                                    .replaceAll("<color>", team.getColor() + ""),
                            plugin.getLang().get("titles.goalFinal.subtitle").replaceAll("<player>", e.getName())
                                    .replaceAll("<heart>", "❤").replaceAll("<teamOther>", team.getTeamName())
                                    .replaceAll("<teamStole>", death.getTeamName())
                                    .replaceAll("<color>", team.getColor() + ""));
                }
            } else {
                for (Player p : players) {
                    plugin.getNms().sendTitle(p, 0, 20, 0,
                            plugin.getLang().get("titles.goalNormal.title").replaceAll("<player>", e.getName())
                                    .replaceAll("<heart>", "❤").replaceAll("<teamOther>", team.getTeamName())
                                    .replaceAll("<teamStole>", death.getTeamName())
                                    .replaceAll("<color>", team.getColor() + ""),
                            plugin.getLang().get("titles.goalNormal.subtitle").replaceAll("<player>", e.getName())
                                    .replaceAll("<heart>", "â§¤").replaceAll("<teamOther>", team.getTeamName())
                                    .replaceAll("<teamStole>", death.getTeamName())
                                    .replaceAll("<color>", team.getColor() + ""));
                }
            }
            for (Player p : players) {
                updateSB(p);
                for (String msg : plugin.getLang().getList("messages.final")) {
                    CenterMessage.sendCenteredMessage(p,
                            msg.replaceAll("&", "§").replaceAll("<teamStole>", death.getTeamName())
                                    .replaceAll("<goals>", String.valueOf(goals.get(e)))
                                    .replaceAll("<es>", (goals.get(e) > 1) ? "es" : "")
                                    .replaceAll("<teamOther>", team.getTeamName())
                                    .replaceAll("<color>", "" + team.getColor()).replaceAll("<player>", e.getName())
                                    .replaceAll("<health>", df.format(e.getHealth()))
                                    .replaceAll("<team>", team.getTeamName())
                                    .replaceAll("<red>", teams.get(ChatColor.RED).getTeamName())
                                    .replaceAll("<redLife>", teams.get(ChatColor.RED).getLifeString())
                                    .replaceAll("<blue>", teams.get(ChatColor.BLUE).getTeamName())
                                    .replaceAll("<blueLife>", teams.get(ChatColor.BLUE).getLifeString())
                                    .replaceAll("<yellow>", teams.get(ChatColor.YELLOW).getTeamName())
                                    .replaceAll("<yellowLife>", teams.get(ChatColor.YELLOW).getLifeString())
                                    .replaceAll("<green>", teams.get(ChatColor.GREEN).getTeamName())
                                    .replaceAll("<greenLife>", teams.get(ChatColor.GREEN).getLifeString())
                                    .replaceAll("<heart>", "❤"));
                }
            }
        } else {
            team.addLife(1);
            death.removeLife(1);
            if (death.getLife() == 0) {
                death.killTeam();
                if (checkWin(team, death, e)) {
                    return;
                }
                for (Player p : players) {
                    plugin.getNms().sendTitle(p, 0, 20, 0,
                            plugin.getLang().get("titles.goalFinal.title").replaceAll("<player>", e.getName())
                                    .replaceAll("<heart>", "❤").replaceAll("<teamOther>", team.getTeamName())
                                    .replaceAll("<teamStole>", death.getTeamName())
                                    .replaceAll("<color>", team.getColor() + ""),
                            plugin.getLang().get("titles.goalFinal.subtitle").replaceAll("<player>", e.getName())
                                    .replaceAll("<heart>", "❤").replaceAll("<teamOther>", team.getTeamName())
                                    .replaceAll("<teamStole>", death.getTeamName())
                                    .replaceAll("<color>", team.getColor() + ""));
                }

            } else {
                for (Player p : players) {
                    plugin.getNms().sendTitle(p, 0, 20, 0,
                            plugin.getLang().get("titles.goalNormal.title").replaceAll("<player>", e.getName())
                                    .replaceAll("<heart>", "❤").replaceAll("<teamOther>", team.getTeamName())
                                    .replaceAll("<teamStole>", death.getTeamName())
                                    .replaceAll("<color>", team.getColor() + ""),
                            plugin.getLang().get("titles.goalNormal.subtitle").replaceAll("<player>", e.getName())
                                    .replaceAll("<heart>", "❤").replaceAll("<teamOther>", team.getTeamName())
                                    .replaceAll("<teamStole>", death.getTeamName())
                                    .replaceAll("<color>", team.getColor() + ""));
                }
            }
            for (Player p : players) {
                updateSB(p);
                for (String msg : plugin.getLang().getList("messages.stole")) {
                    CenterMessage.sendCenteredMessage(p,
                            msg.replaceAll("&", "§").replaceAll("<teamStole>", death.getTeamName())
                                    .replaceAll("<goals>", String.valueOf(goals.get(e)))
                                    .replaceAll("<es>", (goals.get(e) > 1) ? "es" : "")
                                    .replaceAll("<color>", "" + team.getColor())
                                    .replaceAll("<teamOther>", team.getTeamName()).replaceAll("<player>", e.getName())
                                    .replaceAll("<health>", df.format(e.getHealth()))
                                    .replaceAll("<red>", teams.get(ChatColor.RED).getTeamName())
                                    .replaceAll("<redLife>", teams.get(ChatColor.RED).getLifeString())
                                    .replaceAll("<blue>", teams.get(ChatColor.BLUE).getTeamName())
                                    .replaceAll("<blueLife>", teams.get(ChatColor.BLUE).getLifeString())
                                    .replaceAll("<yellow>", teams.get(ChatColor.YELLOW).getTeamName())
                                    .replaceAll("<yellowLife>", teams.get(ChatColor.YELLOW).getLifeString())
                                    .replaceAll("<green>", teams.get(ChatColor.GREEN).getTeamName())
                                    .replaceAll("<greenLife>", teams.get(ChatColor.GREEN).getLifeString())
                                    .replaceAll("<heart>", "❤"));
                }
            }
        }
    }

    public void firework(Location loc, Color c1) {
        Firework fa = (Firework) loc.getWorld().spawn(
                loc.clone().add(new Random().nextInt(5) + 1, new Random().nextInt(1) + 1, new Random().nextInt(5) + 1),
                Firework.class);
        FireworkMeta fam = fa.getFireworkMeta();
        FireworkEffect.Type tipo = FireworkEffect.Type.STAR;
        Color c2 = Color.WHITE;
        FireworkEffect ef = FireworkEffect.builder().withColor(c1).withFade(c2).with(tipo).build();
        fam.addEffect(ef);
        fam.setPower(0);
        fa.setFireworkMeta(fam);
        new BukkitRunnable() {
            @Override
            public void run() {
                Firework fa = (Firework) loc.getWorld().spawn(loc.clone().add(new Random().nextInt(5) + 1,
                        new Random().nextInt(1) + 1, new Random().nextInt(5) + 1), Firework.class);
                FireworkMeta fam = fa.getFireworkMeta();
                FireworkEffect.Type tipo = FireworkEffect.Type.STAR;
                Color c2 = Color.WHITE;
                FireworkEffect ef = FireworkEffect.builder().withColor(c1).withFade(c2).with(tipo).build();
                fam.addEffect(ef);
                fam.setPower(0);
                fa.setFireworkMeta(fam);
            }
        }.runTaskLater(plugin, 2);
    }

    public void addPlayer(Player p) {
        pd.put(p, new PlayerData(p));
        Utils.setCleanPlayer(p);
        players.add(p);
        kills.put(p, 0);
        goals.put(p, 0);
        p.teleport(getLobby());
        if (isState(FState.WAITING) || isState(FState.STARTING)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    givePlayerItems(p);
                }
            }.runTaskLater(plugin, 20);
            for (Player on : players) {
                on.sendMessage(plugin.getLang().get("messages.join").replaceAll("<player>", p.getName())
                        .replaceAll("<players>", String.valueOf(players.size()))
                        .replaceAll("<max>", String.valueOf(getMax())));
                on.playSound(on.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.join")), 1.0f, 1.0f);
                updateSB(on);
            }
        }
        updateSign();
    }

    public void removePlayer(Player p) {
        Utils.setCleanPlayer(p);
        pd.get(p).restore();
        removeAllPlayerTeam(p);
        players.remove(p);
        if (isState(FState.WAITING) || isState(FState.STARTING)) {
            for (Player on : players) {
                on.sendMessage(plugin.getLang().get("messages.quit").replaceAll("<player>", p.getName())
                        .replaceAll("<players>", String.valueOf(players.size()))
                        .replaceAll("<max>", String.valueOf(getMax())));
                on.playSound(on.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.quit")), 1.0f, 1.0f);
                updateSB(on);
            }
        } else {
            if (getTeamsAlive() == 1) {
                checkWin(getLastTeam(), getTeamPlayer(p), getLastTeam().getTeamPlayers().get(0));
            }
        }
        updateSign();
        checkCancel();
    }

    public void updateGame() {
        if (isState(FState.WAITING) && players.size() >= getMin()) {
            setState(FState.STARTING);
            updateSign();
            for (Player p : players) {
                p.setGameMode(GameMode.ADVENTURE);
            }
        }
        if (isState(FState.STARTING)) {
            for (Player p : players) {
                updateSB(p);
            }
            if (starting == 240 || starting == 180 || starting == 120) {
                for (Player p : players) {
                    p.sendMessage(plugin.getLang().get(p, "messages.starting")
                            .replaceAll("<time>", String.valueOf(starting / 60))
                            .replaceAll("<units>", plugin.getLang().get(p, "units.minutes")));
                    p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.starting")), 1.0f,
                            1.0f);
                    plugin.getTm().sendStartTitle(p, starting, true);
                }
            }
            if (starting == 60) {
                for (Player p : players) {
                    p.sendMessage(plugin.getLang().get(p, "messages.starting")
                            .replaceAll("<time>", String.valueOf(starting / 60))
                            .replaceAll("<units>", plugin.getLang().get(p, "units.minute")));
                    p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.starting")), 1.0f,
                            1.0f);
                    plugin.getTm().sendStartTitle(p, starting, true);
                }
            }
            if (starting == 30 || starting == 15 || starting == 10 || starting == 5 || starting == 4 || starting == 3
                    || starting == 2) {
                for (Player p : players) {
                    p.sendMessage(
                            plugin.getLang().get(p, "messages.starting").replaceAll("<time>", String.valueOf(starting))
                                    .replaceAll("<units>", plugin.getLang().get(p, "units.seconds")));
                    p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.starting")), 1.0f,
                            1.0f);
                    plugin.getTm().sendStartTitle(p, starting, true);
                }
            }
            if (starting == 1) {
                setState(FState.PREGAME);
                updateSign();
                for (Player p : players) {
                    p.sendMessage(
                            plugin.getLang().get(p, "messages.starting").replaceAll("<time>", String.valueOf(starting))
                                    .replaceAll("<units>", plugin.getLang().get(p, "units.second")));
                    p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.starting")), 1.0f,
                            1.0f);
                    p.getInventory().clear();
                    p.getOpenInventory().close();
                    plugin.getTm().sendStartTitle(p, starting, false);
                    if (getTeamPlayer(p) == null) {
                        addRandomTeam(p);
                    }
                }
                if (plugin.isCage()) {
                    for (TeamFour team : teams.values()) {
                        if (!team.isCage()) {
                            if (team.getTeamPlayers().size() > 0 && team.getTeamPlayers().get(0) != null) {
                                team.createCage(plugin.getCm().getCageByName(
                                        PlayerStat.getPlayerStat(team.getTeamPlayers().get(0)).getCage()));
                                team.setCage(true);
                            } else {
                                team.createCage(
                                        plugin.getCm().getCageByName(plugin.getConfig().getString("defaultCage")));
                                team.setCage(true);
                            }
                        }
                    }
                } else {
                    for (ChatColor color : teams.keySet()) {
                        plugin.getGlm().createCage(teams.get(color).getTeamSpawn(), color);
                    }
                }
                for (Player p : players) {
                    p.teleport(getTeamPlayer(p).getTeamSpawn().clone().add(0, 1, 0));
                    giveKit(p, getTeamPlayer(p));
                }
                Location hRed = teams.get(ChatColor.RED).getHologram();
                Location hBlue = teams.get(ChatColor.BLUE).getHologram();
                Location hGreen = teams.get(ChatColor.GREEN).getHologram();
                Location hYellow = teams.get(ChatColor.YELLOW).getHologram();
                if (hRed != null && hBlue != null && hGreen != null && hYellow != null) {
                    for (Player p : teams.get(ChatColor.RED).getTeamPlayers()) {
                        ArrayList<String> pRed = new ArrayList<String>();
                        TruenoHologram phRed = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.you")) {
                            pRed.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.RED)
                                    .replaceAll("<team>", plugin.getLang().get("holograms.your")));
                        }
                        ArrayList<String> pBlue = new ArrayList<String>();
                        TruenoHologram phBlue = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.enemy")) {
                            pBlue.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.BLUE)
                                    .replaceAll("<team>", teams.get(ChatColor.BLUE).getTeamName()));
                        }
                        ArrayList<String> pGreen = new ArrayList<String>();
                        TruenoHologram phGreen = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.enemy")) {
                            pGreen.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.GREEN)
                                    .replaceAll("<team>", teams.get(ChatColor.GREEN).getTeamName()));
                        }
                        ArrayList<String> pYellow = new ArrayList<String>();
                        TruenoHologram phYellow = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.enemy")) {
                            pYellow.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.YELLOW)
                                    .replaceAll("<team>", teams.get(ChatColor.YELLOW).getTeamName()));
                        }
                        phBlue.setupPlayerHologram(p, hBlue, pBlue);
                        phBlue.display();
                        phRed.setupPlayerHologram(p, hRed, pRed);
                        phRed.display();
                        phGreen.setupPlayerHologram(p, hGreen, pGreen);
                        phGreen.display();
                        phYellow.setupPlayerHologram(p, hYellow, pYellow);
                        phYellow.display();
                        holograms.add(phRed);
                        holograms.add(phBlue);
                        holograms.add(phGreen);
                        holograms.add(phYellow);
                    }
                    for (Player p : teams.get(ChatColor.BLUE).getTeamPlayers()) {
                        ArrayList<String> pRed = new ArrayList<String>();
                        TruenoHologram phRed = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.enemy")) {
                            pRed.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.RED)
                                    .replaceAll("<team>", teams.get(ChatColor.RED).getTeamName()));
                        }
                        ArrayList<String> pBlue = new ArrayList<String>();
                        TruenoHologram phBlue = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.you")) {
                            pBlue.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.BLUE)
                                    .replaceAll("<team>", plugin.getLang().get("holograms.your")));
                        }
                        ArrayList<String> pGreen = new ArrayList<String>();
                        TruenoHologram phGreen = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.enemy")) {
                            pGreen.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.GREEN)
                                    .replaceAll("<team>", teams.get(ChatColor.GREEN).getTeamName()));
                        }
                        ArrayList<String> pYellow = new ArrayList<String>();
                        TruenoHologram phYellow = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.enemy")) {
                            pYellow.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.YELLOW)
                                    .replaceAll("<team>", teams.get(ChatColor.YELLOW).getTeamName()));
                        }
                        phBlue.setupPlayerHologram(p, hBlue, pBlue);
                        phBlue.display();
                        phRed.setupPlayerHologram(p, hRed, pRed);
                        phRed.display();
                        phGreen.setupPlayerHologram(p, hGreen, pGreen);
                        phGreen.display();
                        phYellow.setupPlayerHologram(p, hYellow, pYellow);
                        phYellow.display();
                        holograms.add(phRed);
                        holograms.add(phBlue);
                        holograms.add(phGreen);
                        holograms.add(phYellow);
                    }
                    for (Player p : teams.get(ChatColor.GREEN).getTeamPlayers()) {
                        ArrayList<String> pRed = new ArrayList<String>();
                        TruenoHologram phRed = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.enemy")) {
                            pRed.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.RED)
                                    .replaceAll("<team>", teams.get(ChatColor.RED).getTeamName()));
                        }
                        ArrayList<String> pBlue = new ArrayList<String>();
                        TruenoHologram phBlue = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.enemy")) {
                            pBlue.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.BLUE)
                                    .replaceAll("<team>", teams.get(ChatColor.BLUE).getTeamName()));
                        }
                        ArrayList<String> pGreen = new ArrayList<String>();
                        TruenoHologram phGreen = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.you")) {
                            pGreen.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.GREEN)
                                    .replaceAll("<team>", plugin.getLang().get("holograms.your")));
                        }
                        ArrayList<String> pYellow = new ArrayList<String>();
                        TruenoHologram phYellow = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.enemy")) {
                            pYellow.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.YELLOW)
                                    .replaceAll("<team>", teams.get(ChatColor.YELLOW).getTeamName()));
                        }
                        phBlue.setupPlayerHologram(p, hBlue, pBlue);
                        phBlue.display();
                        phRed.setupPlayerHologram(p, hRed, pRed);
                        phRed.display();
                        phGreen.setupPlayerHologram(p, hGreen, pGreen);
                        phGreen.display();
                        phYellow.setupPlayerHologram(p, hYellow, pYellow);
                        phYellow.display();
                        holograms.add(phRed);
                        holograms.add(phBlue);
                        holograms.add(phGreen);
                        holograms.add(phYellow);
                    }
                    for (Player p : teams.get(ChatColor.YELLOW).getTeamPlayers()) {
                        ArrayList<String> pRed = new ArrayList<String>();
                        TruenoHologram phRed = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.enemy")) {
                            pRed.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.RED)
                                    .replaceAll("<team>", teams.get(ChatColor.RED).getTeamName()));
                        }
                        ArrayList<String> pBlue = new ArrayList<String>();
                        TruenoHologram phBlue = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.enemy")) {
                            pBlue.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.BLUE)
                                    .replaceAll("<team>", teams.get(ChatColor.BLUE).getTeamName()));
                        }
                        ArrayList<String> pGreen = new ArrayList<String>();
                        TruenoHologram phGreen = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.enemy")) {
                            pGreen.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.GREEN)
                                    .replaceAll("<team>", teams.get(ChatColor.GREEN).getTeamName()));
                        }
                        ArrayList<String> pYellow = new ArrayList<String>();
                        TruenoHologram phYellow = TruenoHologramAPI.getNewHologram();
                        for (String portal : plugin.getLang().getList("holograms.portal.you")) {
                            pYellow.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.YELLOW)
                                    .replaceAll("<team>", plugin.getLang().get("holograms.your")));
                        }
                        phBlue.setupPlayerHologram(p, hBlue, pBlue);
                        phBlue.display();
                        phRed.setupPlayerHologram(p, hRed, pRed);
                        phRed.display();
                        phGreen.setupPlayerHologram(p, hGreen, pGreen);
                        phGreen.display();
                        phYellow.setupPlayerHologram(p, hYellow, pYellow);
                        phYellow.display();
                        holograms.add(phRed);
                        holograms.add(phBlue);
                        holograms.add(phGreen);
                        holograms.add(phYellow);
                    }
                }
                teams.get(ChatColor.RED).createPortal();
                teams.get(ChatColor.BLUE).createPortal();
                teams.get(ChatColor.YELLOW).createPortal();
                teams.get(ChatColor.GREEN).createPortal();
            }
            starting--;
        }
        if (isState(FState.PREGAME)) {
            for (Player p : players) {
                updateSB(p);
            }
            if (prestart == 10 || prestart == 5 || prestart == 4 || prestart == 3 || prestart == 2) {
                for (Player p : players) {
                    p.sendMessage(
                            plugin.getLang().get(p, "messages.prestart").replaceAll("<time>", String.valueOf(prestart))
                                    .replaceAll("<units>", plugin.getLang().get(p, "units.seconds")));
                    p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.prestart")), 1.0f,
                            1.0f);
                    plugin.getTm().sendPreTitle(p, prestart, true);
                }
            }
            if (prestart == 1) {
                for (Player p : players) {
                    p.sendMessage(
                            plugin.getLang().get(p, "messages.prestart").replaceAll("<time>", String.valueOf(prestart))
                                    .replaceAll("<units>", plugin.getLang().get(p, "units.second")));
                    p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.prestart")), 1.0f,
                            1.0f);
                    plugin.getTm().sendPreTitle(p, prestart, false);
                    for (String msg : plugin.getLang().getList("messages.startFour")) {
                        CenterMessage.sendCenteredMessage(p, msg.replaceAll("&", "§").replaceAll("<heart>", "❤"));
                    }
                }
            }
            if (prestart == 0) {
                setState(FState.INGAME);
                updateSign();
                for (Player p : players) {
                    p.setGameMode(GameMode.SURVIVAL);
                }
                if (plugin.isCage()) {
                    for (TeamFour team : teams.values()) {
                        if (team.isCage()) {
                            team.removeCage();
                            team.setCage(false);
                        }
                    }
                } else {
                    for (ChatColor color : teams.keySet()) {
                        plugin.getGlm().removeGlass(teams.get(color).getTeamSpawn());
                    }
                }
            }
            prestart--;
        }
    }

    public void addPlayerTeam(Player p, TeamFour t) {
        t.addPlayer(p);
        teamPlayer.put(p, t.getColor());
    }

    public void addRandomTeam(Player p) {
        TeamFour red = teams.get(ChatColor.RED);
        TeamFour blue = teams.get(ChatColor.BLUE);
        TeamFour green = teams.get(ChatColor.GREEN);
        TeamFour yellow = teams.get(ChatColor.YELLOW);
        if (red.getTeamSize() <= blue.getTeamSize() && red.getTeamSize() <= green.getTeamSize()
                && red.getTeamSize() <= yellow.getTeamSize()) {
            red.addPlayer(p);
            teamPlayer.put(p, red.getColor());
        } else if (blue.getTeamSize() <= red.getTeamSize() && blue.getTeamSize() <= green.getTeamSize()
                && blue.getTeamSize() <= yellow.getTeamSize()) {
            blue.addPlayer(p);
            teamPlayer.put(p, blue.getColor());
        } else if (green.getTeamSize() <= red.getTeamSize() && green.getTeamSize() <= blue.getTeamSize()
                && green.getTeamSize() <= yellow.getTeamSize()) {
            green.addPlayer(p);
            teamPlayer.put(p, green.getColor());
        } else {
            yellow.addPlayer(p);
            teamPlayer.put(p, yellow.getColor());
        }
    }

    public TeamFour getTeamPlayer(Player p) {
        return teams.get(teamPlayer.get(p));
    }

    public HashMap<ChatColor, TeamFour> getTeams() {
        return teams;
    }

    public Location getLobby() {
        return lobby;
    }

    public Location getSpect() {
        return spect;
    }

    public String getName() {
        return name;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public TheBridge get() {
        return plugin;
    }

    public FState getState() {
        return this.state;
    }

    public void setState(FState state) {
        this.state = state;
    }

    public boolean isState(FState state) {
        if (this.state == state) {
            return true;
        }
        return false;
    }

    public int getGoals(Player p) {
        return goals.get(p);
    }

    public int getKills(Player p) {
        return kills.get(p);
    }

    public void addGoal(Player p) {
        goals.put(p, goals.get(p) + 1);
        updateSB(p);
    }

    public void addKill(Player p) {
        kills.put(p, kills.get(p) + 1);
        updateSB(p);
    }

    public enum FState {
        WAITING, STARTING, PREGAME, INGAME, FINISH, RESTARTING;
    }

    public int getTeamsAlive() {
        int cantidad = 0;
        for (TeamFour team : teams.values()) {
            if (team.getTeamSize() == 0) {
                team.setDeath(true);
                continue;
            }
            if (!team.getDeath() || team.getTeamSize() > 0) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public TeamFour getLastTeam() {
        for (TeamFour team : teams.values()) {
            if (team.getTeamSize() > 0) {
                return team;
            }
        }
        return null;
    }

    public void removeAllPlayerTeam(Player p) {
        for (TeamFour t : teams.values()) {
            if (t.getTeamPlayers().contains(p)) {
                t.removePlayer(p);
            }
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Player> getSpects() {
        return spects;
    }

    public void setSpect(Player p) {
        Utils.setCleanPlayer(p);
        removeAllPlayerTeam(p);
        spects.add(p);
        for (Player on : players) {
            on.hidePlayer(p);
        }
        for (Player on : spects) {
            on.hidePlayer(p);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                giveSpectItems(p);
            }
        }.runTaskLater(plugin, 5);
    }

    public void giveSpectItems(Player p) {
        ItemStack team = ItemBuilder.item(Material.COMPASS, 1, (short) 0,
                plugin.getLang().get("items.spectate.nameItem"), plugin.getLang().get("items.spectate.loreItem"));
        ItemStack option = ItemBuilder.item(Material.REDSTONE_COMPARATOR, 1, (short) 0,
                plugin.getLang().get("items.config.nameItem"), plugin.getLang().get("items.config.loreItem"));
        ItemStack leave = ItemBuilder.item(Material.BED, 1, (short) 0, plugin.getLang().get("items.leave.nameItem"),
                plugin.getLang().get("items.leave.loreItem"));
        p.getInventory().setItem(0, team);
        p.getInventory().setItem(4, option);
        p.getInventory().setItem(8, leave);
    }

    public void givePlayerItems(Player p) {
        ItemStack team = ItemBuilder.item(Material.PAPER, 1, (short) 0, plugin.getLang().get("items.teams.nameItem"),
                plugin.getLang().get("items.teams.loreItem"));
        ItemStack leave = ItemBuilder.item(Material.BED, 1, (short) 0, plugin.getLang().get("items.leave.nameItem"),
                plugin.getLang().get("items.leave.loreItem"));
        p.getInventory().setItem(0, team);
        p.getInventory().setItem(8, leave);
    }

    public void giveKit(Player p, TeamFour team) {
        final ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        final LeatherArmorMeta helmetM = (LeatherArmorMeta) helmet.getItemMeta();
        helmetM.setColor(team.getFColor());
        helmet.setItemMeta(helmetM);
        final ItemStack peche = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        final LeatherArmorMeta pecheM = (LeatherArmorMeta) peche.getItemMeta();
        pecheM.setColor(team.getFColor());
        peche.setItemMeta(pecheM);
        final ItemStack panta = new ItemStack(Material.IRON_LEGGINGS, 1);
        final ItemMeta pantaM = panta.getItemMeta();
        panta.setItemMeta(pantaM);
        final ItemStack boots = new ItemStack(Material.IRON_BOOTS, 1);
        final ItemMeta bootsM = boots.getItemMeta();
        boots.setItemMeta(bootsM);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(peche);
        p.getInventory().setLeggings(panta);
        p.getInventory().setBoots(boots);
        if (PlayerStat.getPlayerStat(p).getHotbar() == null) {
            final ItemStack[] items = plugin.getGm().getKit().getContents();
            for (int slot = 0; slot < 35; slot++) {
                final ItemStack item = items[slot];
                if (item == null || item.getType() == Material.BARRIER) {
                    p.getInventory().setItem(slot, new ItemStack(Material.AIR));
                    continue;
                }
                if (item.getType().equals(Material.STAINED_CLAY)
                        || item.getType().equals(Material.getMaterial("CLAY"))) {
                    final ItemStack item2 = new ItemStack(item.getType(), item.getAmount(), getColor(team.getFColor()));
                    p.getInventory().setItem(slot, item2);
                    continue;
                }
                p.getInventory().setItem(slot, item);
            }
        } else {
            final ItemStack[] items = PlayerStat.getPlayerStat(p).getHotbar();
            for (int slot = 0; slot < 35; slot++) {
                final ItemStack item = items[slot];
                if (item == null || item.getType() == Material.BARRIER) {
                    p.getInventory().setItem(slot, new ItemStack(Material.AIR));
                    continue;
                }
                if (item.getType().equals(Material.STAINED_CLAY)
                        || item.getType().equals(Material.getMaterial("CLAY"))) {
                    final ItemStack item2 = new ItemStack(item.getType(), item.getAmount(), getColor(team.getFColor()));
                    p.getInventory().setItem(slot, item2);
                    continue;
                }
                p.getInventory().setItem(slot, item);
            }
        }
    }

    public void addPlace(Location loc) {
        placed.add(loc);
    }

    public ArrayList<Location> getPlaced() {
        return placed;
    }

    public byte getColor(Color color) {
        if (color.equals(Color.RED)) {
            return 14;
        }
        if (color.equals(Color.BLUE)) {
            return 11;
        }
        if (color.equals(Color.YELLOW)) {
            return 4;
        }
        if (color.equals(Color.GREEN)) {
            return 5;
        }
        return 0;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public ArrayList<Location> getBuild() {
        return build;
    }

    public int getStarting() {
        return starting;
    }

    public HashMap<Player, PlayerData> getPD() {
        return pd;
    }

}
