package io.github.MGDCl.TheBridge.game;

import com.nametagedit.plugin.NametagEdit;
import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.database.PlayerStat;
import io.github.MGDCl.TheBridge.hologram.TruenoHologram;
import io.github.MGDCl.TheBridge.hologram.TruenoHologramAPI;
import io.github.MGDCl.TheBridge.team.TeamDuo;
import io.github.MGDCl.TheBridge.utils.CenterMessage;
import io.github.MGDCl.TheBridge.utils.ItemBuilder;
import io.github.MGDCl.TheBridge.utils.Utils;
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

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

public class GameDuo {

    private final TheBridge plugin;
    private HashMap<ChatColor, TeamDuo> teams;
    private HashMap<Player, ChatColor> teamPlayer;
    private ArrayList<Player> players;
    private HashMap<Player, PlayerData> pd = new HashMap<>();
    private HashMap<Player, Integer> goals = new HashMap<>();
    private HashMap<Player, Integer> kills = new HashMap<>();
    private ArrayList<TruenoHologram> holograms = new ArrayList<>();
    private ArrayList<Location> build = new ArrayList<>();
    private ArrayList<Location> placed = new ArrayList<>();
    private String name;
    private String mode;
    private int min;
    private int max;
    private int teamSize;
    private int starting;
    private int prestart;
    private int restart;
    private Location lobby;
    private Location spect;
    private BukkitTask start;
    private BukkitTask prestarts;
    private State state;
    int time = 0;
    int tick = 0;

    public GameDuo(TheBridge plugin, String name, int min, int max, int teamSize, int starting, int prestart, int restart,
                   Location lobby, Location spect, Location buildMin, Location buildMax, Location spawnsRed,
                   Location spawnsBlue, ArrayList<Location> portalRed, ArrayList<Location> portalBlue, Location respawnsRed,
                   Location respawnsBlue, Location hRed, Location hBlue) {
        this.plugin = plugin;
        this.starting = starting;
        this.prestart = prestart;
        this.restart = restart;
        this.max = max;
        this.min = min;
        this.teamSize = teamSize;
        this.mode = teamSize + "v" + teamSize;
        this.name = name;
        this.lobby = lobby;
        this.spect = spect;
        this.teamPlayer = new HashMap<>();
        this.teams = new HashMap<>();
        this.players = new ArrayList<>();
        final int gn = Math.min(buildMin.getBlockX(), buildMax.getBlockX());
        final int gn2 = Math.max(buildMin.getBlockX(), buildMax.getBlockX());
        final int gn3 = Math.min(buildMin.getBlockZ(), buildMax.getBlockZ());
        final int gn4 = Math.max(buildMin.getBlockZ(), buildMax.getBlockZ());
        final int gn5 = Math.min(buildMin.getBlockY(), buildMax.getBlockY());
        final int gn6 = Math.max(buildMin.getBlockY(), buildMax.getBlockY());
        for (int i = gn; i <= gn2; ++i)
            for (int j = gn5; j <= gn6; ++j)
                for (int k = gn3; k <= gn4; ++k) {
                    final Location location3 = new Location(buildMin.getWorld(), i, j, k);
                    build.add(location3);
                }
        this.teams.put(ChatColor.BLUE, new TeamDuo(plugin.getConfig().getString("names.blue"), ChatColor.BLUE,
                Color.BLUE, respawnsBlue, spawnsBlue, portalBlue, hBlue));
        this.teams.put(ChatColor.RED, new TeamDuo(plugin.getConfig().getString("names.red"), ChatColor.RED, Color.RED,
                respawnsRed, spawnsRed, portalRed, hRed));
        setState(State.WAITING);
        updateSign();
    }

    public void checkCancel() {
        if (isState(State.STARTING) && players.size() <= getMin()) {
            if (start != null)
                start.cancel();
            if (prestarts != null)
                prestarts.cancel();
            setState(State.WAITING);
            starting = 30;
            updateSign();
            for (final Player on : players)
                updateSB(on);
        }
    }

    public void updateSB(Player p) {
        plugin.getSb().update(p);
    }

    public void updateSign() {
        plugin.getSim().updateGameSign(this);
    }

    public void addPlayer(Player p) {
        pd.put(p, new PlayerData(p));
        Utils.setCleanPlayer(p);
        players.add(p);
        kills.put(p, 0);
        goals.put(p, 0);
        new BukkitRunnable() {
            @Override
            public void run() {
                givePlayerItems(p);
            }
        }.runTaskLater(plugin, 20);
        p.teleport(getLobby());
        updateSign();
        if (isState(State.WAITING) || isState(State.STARTING))
            for (final Player on : players) {
                on.sendMessage(plugin.getLang().get("messages.join").replaceAll("<player>", p.getName())
                        .replaceAll("<players>", String.valueOf(players.size()))
                        .replaceAll("<max>", String.valueOf(getMax())));
                on.playSound(on.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.join")), 1.0f, 1.0f);
                updateSB(on);
            }
    }

    public void removePlayer(Player p) {
        Utils.setCleanPlayer(p);
        pd.get(p).restore();
        removeAllPlayerTeam(p);
        players.remove(p);
        NametagEdit.getApi().clearNametag(p);
        if (isState(State.WAITING) || isState(State.STARTING))
            for (final Player on : players) {
                on.sendMessage(plugin.getLang().get("messages.quit").replaceAll("<player>", p.getName())
                        .replaceAll("<players>", String.valueOf(players.size()))
                        .replaceAll("<max>", String.valueOf(getMax())));
                on.playSound(on.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.quit")), 1.0f, 1.0f);
                updateSB(on);
            }
        else if (getTeamsAlive() == 1 && players.size() >= 1)
            checkWin(p, getLastTeam(), getLastTeam().getGoals());
        updateSign();
        checkCancel();
    }

    // FIXME: Not properly giving coins
    public boolean checkWin(Player w, TeamDuo team, int goals) {
        if (isState(State.FINISH))
            return false;
        final DecimalFormat df = new DecimalFormat("#.##");
        if (getTeamsAlive() <= 1 && players.size() >= 1) {
            final TeamDuo win = getLastTeam();
            setState(State.FINISH);
            firework(getSpect(), win.getFColor());
            for (final Player r : win.getTeamPlayers())
                PlayerStat.getPlayerStat(r).addNormalWins();
            for (final Player p : players) {
                p.teleport(lobby);
                updateSB(p);
                plugin.getNms().sendTitle(p, 0, 60, 0,
                        plugin.getLang().get("titles.win.title")
                                .replaceAll("<blueGoals>", String.valueOf(teams.get(ChatColor.BLUE).getGoals()))
                                .replaceAll("<redGoals>", String.valueOf(teams.get(ChatColor.RED).getGoals()))
                                .replaceAll("<team>", team.getTeamName()).replaceAll("<color>", team.getColor() + ""),
                        plugin.getLang().get("titles.win.subtitle")
                                .replaceAll("<blueGoals>", String.valueOf(teams.get(ChatColor.BLUE).getGoals()))
                                .replaceAll("<redGoals>", String.valueOf(teams.get(ChatColor.RED).getGoals()))
                                .replaceAll("<team>", team.getTeamName()).replaceAll("<color>", team.getColor() + ""));
                for (final String msg : plugin.getLang().getList("messages.win"))
                    CenterMessage.sendCenteredMessage(p,
                            msg.replaceAll("&", "§").replaceAll("<player>", w.getName())
                                    .replaceAll("<blueGoals>", String.valueOf(teams.get(ChatColor.BLUE).getGoals()))
                                    .replaceAll("<redGoals>", String.valueOf(teams.get(ChatColor.RED).getGoals()))
                                    .replaceAll("<s>", getGoals(w) > 1 ? "es" : "")
                                    .replaceAll("<goals>", String.valueOf(getGoals(w)))
                                    .replaceAll("<health>", df.format(w.getHealth())).replaceAll("<heart>", "❤")
                                    .replaceAll("<color>", "" + team.getColor())
                                    .replaceAll("<team>", win.getTeamName().toUpperCase()));
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (final TruenoHologram th : holograms)
                        th.delete();
                    for (final Player p : spect.getWorld().getPlayers())
                        if (pd.containsKey(p))
                            plugin.getGm().removePlayerAllGames(p);
                        else
                            p.teleport(plugin.getMainLobby());
                }
            }.runTaskLater(plugin, 20 * 10);
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getGm().resetNormalGame(getName());
                }
            }.runTaskLater(plugin, 20 * 12);
            updateSign();
            return true;
        }
        if (team.getGoals() >= goals) {
            setState(State.FINISH);
            final TeamDuo win = getLastTeam();
            firework(getSpect(), team.getFColor());
            for (final Player r : win.getTeamPlayers())
                PlayerStat.getPlayerStat(r).addNormalWins();
            for (final Player p : players) {
                p.teleport(lobby);
                plugin.getNms().sendTitle(p, 0, 60, 0,
                        plugin.getLang().get("titles.win.title")
                                .replaceAll("<blueGoals>", String.valueOf(teams.get(ChatColor.BLUE).getGoals()))
                                .replaceAll("<redGoals>", String.valueOf(teams.get(ChatColor.RED).getGoals()))
                                .replaceAll("<team>", team.getTeamName()).replaceAll("<color>", team.getColor() + ""),
                        plugin.getLang().get("titles.win.subtitle")
                                .replaceAll("<blueGoals>", String.valueOf(teams.get(ChatColor.BLUE).getGoals()))
                                .replaceAll("<redGoals>", String.valueOf(teams.get(ChatColor.RED).getGoals()))
                                .replaceAll("<team>", team.getTeamName()).replaceAll("<color>", team.getColor() + ""));
                for (final String msg : plugin.getLang().getList("messages.win"))
                    CenterMessage.sendCenteredMessage(p,
                            msg.replaceAll("&", "§").replaceAll("<player>", w.getName())
                                    .replaceAll("<blueGoals>", String.valueOf(teams.get(ChatColor.BLUE).getGoals()))
                                    .replaceAll("<redGoals>", String.valueOf(teams.get(ChatColor.RED).getGoals()))
                                    .replaceAll("<s>", getGoals(w) > 1 ? "es" : "")
                                    .replaceAll("<goals>", String.valueOf(getGoals(w)))
                                    .replaceAll("<health>", df.format(w.getHealth())).replaceAll("<heart>", "❤")
                                    .replaceAll("<color>", "" + team.getColor())
                                    .replaceAll("<team>", team.getTeamName().toUpperCase()));
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (final Player p : spect.getWorld().getPlayers())
                        if (pd.containsKey(p))
                            plugin.getGm().removePlayerAllGames(p);
                        else
                            p.teleport(plugin.getMainLobby());
                }
            }.runTaskLater(plugin, 20 * 10);
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getGm().resetNormalGame(getName());
                }
            }.runTaskLater(plugin, 20 * 12);
            updateSign();
            return true;
        }
        return false;
    }

    public void celebrateGoal(TeamDuo team, Player e) {
        restart = 5;
        team.addGoal();
        addGoal(e);
        PlayerStat.getPlayerStat(e).addNormalGoals();
        if (checkWin(e, team, 5))
            return;
        firework(getSpect(), team.getFColor());
        if (plugin.isCage()) {
            for (final TeamDuo te : teams.values())
                if (!te.isCage())
                    if (te.getTeamPlayers().get(0) != null) {
                        te.createCage(plugin.getCm()
                                .getCageByName(PlayerStat.getPlayerStat(te.getTeamPlayers().get(0)).getCage()));
                        te.setCage(true);
                    } else {
                        te.createCage(plugin.getCm().getCageByName(plugin.getConfig().getString("defaultCage")));
                        te.setCage(true);
                    }
        } else
            for (final ChatColor color : teams.keySet())
                plugin.getGlm().createCage(teams.get(color).getTeamSpawn(), color);
        for (final Player p : players) {
            p.teleport(getTeamPlayer(p).getTeamSpawn().clone().add(0, 1, 0));
            updateSB(p);
        }
        checkRestart(team, team.getColor(), e);
    }

    public void firework(Location loc, Color c1) {
        time = 0;
        new BukkitRunnable() {
            @Override
            public void run() {
                final Firework fa = loc.getWorld().spawn(loc.clone().add(new Random().nextInt(5) + 1,
                        new Random().nextInt(1) + 1, new Random().nextInt(5) + 1), Firework.class);
                final FireworkMeta fam = fa.getFireworkMeta();
                final FireworkEffect.Type tipo = FireworkEffect.Type.STAR;
                final Color c2 = Color.WHITE;
                final FireworkEffect ef = FireworkEffect.builder().withColor(c1).withFade(c2).with(tipo).build();
                fam.addEffect(ef);
                fam.setPower(0);
                fa.setFireworkMeta(fam);
                time++;
                if (time == 10)
                    cancel();
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    public void checkRestart(TeamDuo team, ChatColor color, Player e) {
        final DecimalFormat df = new DecimalFormat("#.##");
        for (final Player p : players) {
            Utils.setCleanPlayer(p);
            p.setGameMode(GameMode.ADVENTURE);
            giveKit(p, getTeamPlayer(p));
            for (final String msg : plugin.getLang().getList("messages.goal"))
                CenterMessage.sendCenteredMessage(p,
                        msg.replaceAll("&", "§").replaceAll("<player>", e.getName())
                                .replaceAll("<blueGoals>", String.valueOf(teams.get(ChatColor.BLUE).getGoals()))
                                .replaceAll("<redGoals>", String.valueOf(teams.get(ChatColor.RED).getGoals()))
                                .replaceAll("<s>", getGoals(e) > 1 ? "es" : "")
                                .replaceAll("<goals>", String.valueOf(getGoals(e)))
                                .replaceAll("<health>", df.format(e.getHealth())).replaceAll("<heart>", "❤")
                                .replaceAll("<color>", "" + team.getColor())
                                .replaceAll("<team>", team.getTeamName().toUpperCase()));
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (restart == 10 || restart == 5 || restart == 4 || restart == 3 || restart == 2)
                    for (final Player p : players) {
                        p.sendMessage(plugin.getLang().get(p, "messages.restart").replaceAll("<color>", "" + color)
                                .replaceAll("<player>", e.getName()).replaceAll("<time>", String.valueOf(restart))
                                .replaceAll("<s>", "s")
                                .replaceAll("<units>", plugin.getLang().get(p, "units.seconds")));
                        p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.restart")), 1.0f,
                                1.0f);
                        plugin.getTm().sendReStartTitle(e, p, restart, true);
                    }
                if (restart == 1)
                    for (final Player p : players) {
                        p.sendMessage(plugin.getLang().get(p, "messages.restart").replaceAll("<color>", "" + color)
                                .replaceAll("<player>", e.getName()).replaceAll("<time>", String.valueOf(restart))
                                .replaceAll("<s>", "").replaceAll("<units>", plugin.getLang().get(p, "units.second")));
                        p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.restart")), 1.0f,
                                1.0f);
                        plugin.getTm().sendReStartTitle(e, p, restart, false);
                    }
                if (restart == 0) {
                    if (plugin.isCage()) {
                        for (final TeamDuo team : teams.values())
                            if (team.isCage()) {
                                team.removeCage();
                                team.setCage(false);
                            }
                    } else
                        for (final ChatColor color : teams.keySet())
                            plugin.getGlm().removeGlass(teams.get(color).getTeamSpawn());
                    for (final Player p : players)
                        p.setGameMode(GameMode.SURVIVAL);
                    cancel();
                }
                restart--;
            }
        }.runTaskTimer(get(), 0, 20);
    }

    public void updateGame() {
        tick++;
        if (isState(State.WAITING) && players.size() >= getMin()) {
            setState(State.STARTING);
            updateSign();
            for (final Player p : players)
                p.setGameMode(GameMode.ADVENTURE);
        }
        if (isState(State.STARTING)) {
            if (tick % 20 == 0)
                for (final Player p : players)
                    updateSB(p);
            if (starting == 240 || starting == 180 || starting == 120)
                for (final Player p : players) {
                    p.sendMessage(plugin.getLang().get(p, "messages.starting")
                            .replaceAll("<time>", String.valueOf(starting / 60))
                            .replaceAll("<units>", plugin.getLang().get(p, "units.minutes")));
                    p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.starting")), 1.0f,
                            1.0f);
                    plugin.getTm().sendStartTitle(p, starting, true);
                }
            else if (starting == 60)
                for (final Player p : players) {
                    p.sendMessage(plugin.getLang().get(p, "messages.starting")
                            .replaceAll("<time>", String.valueOf(starting / 60))
                            .replaceAll("<units>", plugin.getLang().get(p, "units.minute")));
                    p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.starting")), 1.0f,
                            1.0f);
                    plugin.getTm().sendStartTitle(p, starting, true);
                }
            else if (starting == 30 || starting == 15 || starting == 10 || starting == 5 || starting == 4
                    || starting == 3 || starting == 2)
                for (final Player p : players) {
                    p.sendMessage(
                            plugin.getLang().get(p, "messages.starting").replaceAll("<time>", String.valueOf(starting))
                                    .replaceAll("<units>", plugin.getLang().get(p, "units.seconds")));
                    p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.starting")), 1.0f,
                            1.0f);
                    plugin.getTm().sendStartTitle(p, starting, true);
                }
            else if (starting == 1) {
                setState(State.PREGAME);
                updateSign();
                for (final Player p : players) {
                    p.sendMessage(
                            plugin.getLang().get(p, "messages.starting").replaceAll("<time>", String.valueOf(starting))
                                    .replaceAll("<units>", plugin.getLang().get(p, "units.second")));
                    p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.starting")), 1.0f,
                            1.0f);
                    plugin.getTm().sendStartTitle(p, starting, false);
                    if (getTeamPlayer(p) == null)
                        addRandomTeam(p);
                    p.getInventory().clear();
                }
                if (plugin.isCage()) {
                    for (final TeamDuo team : teams.values())
                        if (!team.isCage())
                            if (team.getTeamPlayers().get(0) != null) {
                                team.createCage(plugin.getCm().getCageByName(
                                        PlayerStat.getPlayerStat(team.getTeamPlayers().get(0)).getCage()));
                                team.setCage(true);
                            } else {
                                team.createCage(
                                        plugin.getCm().getCageByName(plugin.getConfig().getString("defaultCage")));
                                team.setCage(true);
                            }
                } else
                    for (final ChatColor color : teams.keySet())
                        plugin.getGlm().createCage(teams.get(color).getTeamSpawn(), color);
                for (final Player p : players) {
                    p.teleport(getTeamPlayer(p).getTeamSpawn().clone().add(0, 1, 0));
                    giveKit(p, getTeamPlayer(p));
                }
                final Location hRed = teams.get(ChatColor.RED).getHologram();
                final Location hBlue = teams.get(ChatColor.BLUE).getHologram();
                if (hRed != null && hBlue != null) {
                    for (final Player p : teams.get(ChatColor.RED).getTeamPlayers()) {
                        final ArrayList<String> pRed = new ArrayList<>();
                        final TruenoHologram phRed = TruenoHologramAPI.getNewHologram();
                        for (final String portal : plugin.getLang().getList("holograms.portal.you"))
                            pRed.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.RED)
                                    .replaceAll("<team>", plugin.getLang().get("holograms.your")));
                        final ArrayList<String> pBlue = new ArrayList<>();
                        final TruenoHologram phBlue = TruenoHologramAPI.getNewHologram();
                        for (final String portal : plugin.getLang().getList("holograms.portal.enemy"))
                            pBlue.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.BLUE)
                                    .replaceAll("<team>", teams.get(ChatColor.BLUE).getTeamName()));
                        phBlue.setupPlayerHologram(p, hBlue, pBlue);
                        phBlue.display();
                        phRed.setupPlayerHologram(p, hRed, pRed);
                        phRed.display();
                        holograms.add(phRed);
                        holograms.add(phBlue);
                    }
                    for (final Player p : teams.get(ChatColor.BLUE).getTeamPlayers()) {
                        final ArrayList<String> pRed = new ArrayList<>();
                        final TruenoHologram phRed = TruenoHologramAPI.getNewHologram();
                        for (final String portal : plugin.getLang().getList("holograms.portal.you"))
                            pRed.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.BLUE)
                                    .replaceAll("<team>", plugin.getLang().get("holograms.your")));
                        final ArrayList<String> pBlue = new ArrayList<>();
                        final TruenoHologram phBlue = TruenoHologramAPI.getNewHologram();
                        for (final String portal : plugin.getLang().getList("holograms.portal.enemy"))
                            pBlue.add(portal.replaceAll("&", "§").replaceAll("<color>", "" + ChatColor.RED)
                                    .replaceAll("<team>", teams.get(ChatColor.RED).getTeamName()));
                        phBlue.setupPlayerHologram(p, hRed, pBlue);
                        phBlue.display();
                        phRed.setupPlayerHologram(p, hBlue, pRed);
                        phRed.display();
                        holograms.add(phRed);
                        holograms.add(phBlue);
                    }
                }
                teams.get(ChatColor.RED).createPortal();
                teams.get(ChatColor.BLUE).createPortal();
            }
            starting--;
        }
        if (isState(State.PREGAME)) {
            if (prestart == 10 || prestart == 5 || prestart == 4 || prestart == 3 || prestart == 2)
                for (final Player p : players) {
                    p.sendMessage(
                            plugin.getLang().get(p, "messages.prestart").replaceAll("<time>", String.valueOf(prestart))
                                    .replaceAll("<units>", plugin.getLang().get(p, "units.seconds")));
                    p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.prestart")), 1.0f,
                            1.0f);
                    plugin.getTm().sendPreTitle(p, prestart, true);
                    updateSB(p);
                }
            else if (prestart == 1)
                for (final Player p : players) {
                    p.sendMessage(
                            plugin.getLang().get(p, "messages.prestart").replaceAll("<time>", String.valueOf(prestart))
                                    .replaceAll("<units>", plugin.getLang().get(p, "units.second")));
                    p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.game.prestart")), 1.0f,
                            1.0f);
                    plugin.getTm().sendPreTitle(p, prestart, false);
                    for (final String msg : plugin.getLang().getList("messages.start"))
                        CenterMessage.sendCenteredMessage(p, msg.replaceAll("&", "§"));
                    updateSB(p);
                }
            else if (prestart == 0) {
                setState(State.INGAME);
                updateSign();
                if (plugin.isCage()) {
                    for (final TeamDuo team : teams.values())
                        if (team.isCage()) {
                            team.removeCage();
                            team.setCage(false);
                        }
                } else
                    for (final ChatColor color : teams.keySet())
                        plugin.getGlm().removeGlass(teams.get(color).getTeamSpawn());
                for (final Player p : players) {
                    updateSB(p);
                    TeamDuo t = getTeamPlayer(p);
                    p.setGameMode(GameMode.SURVIVAL);
                    NametagEdit.getApi().setPrefix(p, t.getColor() + "");
                }
            }
            prestart--;
        }
    }

    public TeamDuo getTeamPlayer(Player p) {
        return teams.get(teamPlayer.get(p));
    }

    public void addPlayerTeam(Player p, TeamDuo t) {
        t.addPlayer(p);
        teamPlayer.put(p, t.getColor());
        p.sendMessage("Sexo?");
    }

    public void addRandomTeam(Player p) {
        final TeamDuo red = teams.get(ChatColor.RED);
        final TeamDuo blue = teams.get(ChatColor.BLUE);
        if (red.getTeamSize() <= blue.getTeamSize()) {
            red.addPlayer(p);
            teamPlayer.put(p, red.getColor());
        } else {
            blue.addPlayer(p);
            teamPlayer.put(p, blue.getColor());
        }
    }

    public void removeAllPlayerTeam(Player p) {
        for (final TeamDuo t : teams.values())
            if (t.getTeamPlayers().contains(p))
                t.removePlayer(p);
    }

    public ArrayList<Location> getBuild() {
        return build;
    }

    public HashMap<ChatColor, TeamDuo> getTeams() {
        return teams;
    }

    public int getTeamsAlive() {
        int cantidad = 0;
        for (final TeamDuo team : teams.values())
            if (team.getTeamSize() > 0)
                cantidad++;
        return cantidad;
    }

    public TeamDuo getLastTeam() {
        for (final TeamDuo team : teams.values())
            if (team.getTeamSize() > 0)
                return team;
        return null;
    }

    public int getPlayers() {
        return players.size();
    }

    public ArrayList<Player> getGamePlayers() {
        return players;
    }

    public String getName() {
        return this.name;
    }

    public int getMax() {
        return this.max;
    }

    public String getMode() {
        return mode;
    }

    public int getMin() {
        return this.min;
    }

    public TheBridge get() {
        return plugin;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isState(State state) {
        if (this.state == state)
            return true;
        return false;
    }

    public enum State {
        WAITING, STARTING, PPGAME, PREGAME, INGAME, FINISH, RESTARTING;
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

    public void giveKit(Player p, TeamDuo team) {
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

    public void givePlayerItems(Player p) {
        if (teamSize > 1) {
            final ItemStack team = ItemBuilder.item(Material.PAPER, 1, (short) 0,
                    plugin.getLang().get("items.teams.nameItem"), plugin.getLang().get("items.teams.loreItem"));
            p.getInventory().setItem(0, team);
        }
        final ItemStack leave = ItemBuilder.item(Material.BED, 1, (short) 0,
                plugin.getLang().get("items.leave.nameItem"), plugin.getLang().get("items.leave.loreItem"));
        p.getInventory().setItem(8, leave);
    }

    public byte getColor(Color color) {
        if (color.equals(Color.RED))
            return 14;
        if (color.equals(Color.BLUE))
            return 11;
        return 0;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void addPlace(Location loc) {
        placed.add(loc);
    }

    public ArrayList<Location> getPlaced() {
        return placed;
    }

    public int getStarting() {
        return starting;
    }

    public Location getLobby() {
        return lobby;
    }

    public Location getSpect() {
        return spect;
    }

    public HashMap<Player, PlayerData> getPD() {
        return pd;
    }

}
