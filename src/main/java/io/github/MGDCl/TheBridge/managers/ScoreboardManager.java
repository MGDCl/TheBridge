package io.github.MGDCl.TheBridge.managers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import io.github.MGDCl.TheBridge.TheBridge;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import io.github.MGDCl.TheBridge.game.GameDuo;
import io.github.MGDCl.TheBridge.game.GameFour;
import io.github.MGDCl.TheBridge.game.GameDuo.State;
import io.github.MGDCl.TheBridge.game.GameFour.FState;
import io.github.MGDCl.TheBridge.utils.ScoreboardUtil;
import me.clip.placeholderapi.PlaceholderAPI;

public class ScoreboardManager {

    private HashMap<Player, String> sb = new HashMap<Player, String>();
    private HashMap<Player, ScoreboardUtil> score = new HashMap<Player, ScoreboardUtil>();
    TheBridge plugin;

    public ScoreboardManager(TheBridge plugin) {
        this.plugin = plugin;
    }

    public HashMap<Player, String> getSB() {
        return sb;
    }

    public void createLobbyBoard(Player p) {
        if(!plugin.getLang().getBoolean("scoreboards.main-enabled")) {
            return;
        }
        sb.put(p, "lobby");
        p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreboardUtil scoreboardUtil = new ScoreboardUtil("all", "starting", false);
        scoreboardUtil.setName(plugin.getLang().get("scoreboards.main-title"));
        String titulo = plugin.getLang().get(p, "scoreboards.main");
        String[] title = titulo.split("\\n");
        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
            scoreboardUtil.lines(n, lobby(p, title[n2]));
        }
        scoreboardUtil.build(p);
        score.put(p, scoreboardUtil);
    }

    public void createWaitingBoard(Player p) {
        sb.put(p, "waiting");
        GameDuo game = plugin.getGm().getGameByPlayer(p);
        p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreboardUtil scoreboardUtil = new ScoreboardUtil("lobby", "starting", false);
        scoreboardUtil.setName(plugin.getLang().get("scoreboards.lobby-title"));
        String titulo = plugin.getLang().get(p, "scoreboards.lobby");
        String[] title = titulo.split("\\n");
        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
            scoreboardUtil.lines(n, game(p, title[n2], game));
        }
        scoreboardUtil.build(p);
        score.put(p, scoreboardUtil);
    }

    public void createStartingBoard(Player p) {
        sb.put(p, "starting");
        GameDuo game = plugin.getGm().getGameByPlayer(p);
        p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreboardUtil scoreboardUtil = new ScoreboardUtil("starting", "starting", false);
        scoreboardUtil.setName(plugin.getLang().get("scoreboards.starting-title"));
        String titulo = plugin.getLang().get(p, "scoreboards.starting");
        String[] title = titulo.split("\\n");
        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
            scoreboardUtil.lines(n, game(p, title[n2], game));
        }
        scoreboardUtil.build(p);
        score.put(p, scoreboardUtil);
    }

    public void createGameBoard(Player p) {
        sb.put(p, "game");
        GameDuo game = plugin.getGm().getGameByPlayer(p);
        p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreboardUtil scoreboardUtil = new ScoreboardUtil("game-normal", "starting", false);
        scoreboardUtil.setName(plugin.getLang().get("scoreboards.game-normal-title"));
        String titulo = plugin.getLang().get(p, "scoreboards.game-normal");
        String[] title = titulo.split("\\n");
        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
            scoreboardUtil.lines(n, game(p, title[n2], game));
        }
        scoreboardUtil.build(p);
        score.put(p, scoreboardUtil);
    }

    public void createWaitingFourBoard(Player p) {
        sb.put(p, "waiting");
        GameFour game = plugin.getGm().getGameFourByPlayer(p);
        p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreboardUtil scoreboardUtil = new ScoreboardUtil("lobby", "starting", false);
        scoreboardUtil.setName(plugin.getLang().get("scoreboards.lobby-title"));
        String titulo = plugin.getLang().get(p, "scoreboards.lobby");
        String[] title = titulo.split("\\n");
        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
            scoreboardUtil.lines(n, gameFour(p, title[n2], game));
        }
        scoreboardUtil.build(p);
        score.put(p, scoreboardUtil);
    }

    public void createStartingFourBoard(Player p) {
        sb.put(p, "starting");
        GameFour game = plugin.getGm().getGameFourByPlayer(p);
        p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreboardUtil scoreboardUtil = new ScoreboardUtil("starting", "starting", false);
        scoreboardUtil.setName(plugin.getLang().get("scoreboards.starting-title"));
        String titulo = plugin.getLang().get(p, "scoreboards.starting");
        String[] title = titulo.split("\\n");
        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
            scoreboardUtil.lines(n, gameFour(p, title[n2], game));
        }
        scoreboardUtil.build(p);
        score.put(p, scoreboardUtil);
    }

    public void createGameFourBoard(Player p) {
        sb.put(p, "game");
        GameFour game = plugin.getGm().getGameFourByPlayer(p);
        p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        ScoreboardUtil scoreboardUtil = new ScoreboardUtil("game-four", "starting", false);
        scoreboardUtil.setName(plugin.getLang().get("scoreboards.game-four-title"));
        String titulo = plugin.getLang().get(p, "scoreboards.game-four");
        String[] title = titulo.split("\\n");
        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
            scoreboardUtil.lines(n, gameFour(p, title[n2], game));
        }
        scoreboardUtil.build(p);
        score.put(p, scoreboardUtil);
    }

    public void update(Player p) {
        if (p == null || !p.isOnline()) {
            return;
        }
        ScoreboardUtil scoreboardUtil = score.get(p);
        if (plugin.getGm().getGameByPlayer(p) == null && plugin.getGm().getGameFourByPlayer(p) == null) {
            if (sb.get(p).equals("lobby")) {
                scoreboardUtil.setName(plugin.getLang().get("scoreboards.main-title"));
                String titulo = plugin.getLang().get(p, "scoreboards.main");
                String[] title = titulo.split("\\n");
                for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
                    scoreboardUtil.lines(n, lobby(p, title[n2]));
                }
            } else {
                createLobbyBoard(p);
            }
        } else {
            if (plugin.getGm().getGameByPlayer(p) != null) {
                GameDuo game = plugin.getGm().getGameByPlayer(p);
                if (game.isState(State.WAITING)) {
                    if (sb.get(p).equals("waiting")) {
                        scoreboardUtil.setName(plugin.getLang().get("scoreboards.lobby-title"));
                        String titulo = plugin.getLang().get(p, "scoreboards.lobby");
                        String[] title = titulo.split("\\n");
                        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
                            scoreboardUtil.lines(n, game(p, title[n2], game));
                        }
                    } else {
                        createWaitingBoard(p);
                    }
                } else if (game.isState(State.STARTING)) {
                    if (sb.get(p).equals("starting")) {
                        scoreboardUtil.setName(plugin.getLang().get("scoreboards.starting-title"));
                        String titulo = plugin.getLang().get(p, "scoreboards.starting");
                        String[] title = titulo.split("\\n");
                        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
                            scoreboardUtil.lines(n, game(p, title[n2], game));
                        }
                    } else {
                        createStartingBoard(p);
                    }
                } else if (game.isState(State.INGAME) || game.isState(State.PREGAME) || game.isState(State.FINISH)
                        || game.isState(State.RESTARTING)) {
                    if (sb.get(p).equals("game")) {
                        scoreboardUtil.setName(plugin.getLang().get("scoreboards.game-normal-title"));
                        String titulo = plugin.getLang().get(p, "scoreboards.game-normal");
                        String[] title = titulo.split("\\n");
                        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
                            scoreboardUtil.lines(n, game(p, title[n2], game));
                        }
                    } else {
                        createGameBoard(p);
                    }
                }
            } else if (plugin.getGm().getGameFourByPlayer(p) != null) {
                GameFour game = plugin.getGm().getGameFourByPlayer(p);
                if (game.isState(FState.WAITING)) {
                    if (sb.get(p).equals("waiting")) {
                        scoreboardUtil.setName(plugin.getLang().get("scoreboards.lobby-title"));
                        String titulo = plugin.getLang().get(p, "scoreboards.lobby");
                        String[] title = titulo.split("\\n");
                        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
                            scoreboardUtil.lines(n, gameFour(p, title[n2], game));
                        }
                    } else {
                        createWaitingFourBoard(p);
                    }
                } else if (game.isState(FState.STARTING)) {
                    if (sb.get(p).equals("starting")) {
                        scoreboardUtil.setName(plugin.getLang().get("scoreboards.starting-title"));
                        String titulo = plugin.getLang().get(p, "scoreboards.starting");
                        String[] title = titulo.split("\\n");
                        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
                            scoreboardUtil.lines(n, gameFour(p, title[n2], game));
                        }
                    } else {
                        createStartingFourBoard(p);
                    }
                } else if (game.isState(FState.INGAME) || game.isState(FState.PREGAME) || game.isState(FState.FINISH)
                        || game.isState(FState.RESTARTING)) {
                    if (sb.get(p).equals("game")) {
                        scoreboardUtil.setName(plugin.getLang().get("scoreboards.game-four-title"));
                        String titulo = plugin.getLang().get(p, "scoreboards.game-four");
                        String[] title = titulo.split("\\n");
                        for (Integer n = 1, n2 = title.length - 1; n < title.length + 1; ++n, --n2) {
                            scoreboardUtil.lines(n, gameFour(p, title[n2], game));
                        }
                    } else {
                        createGameFourBoard(p);
                    }
                }
            }
        }
    }

    Date now = new Date();
    SimpleDateFormat sm = new SimpleDateFormat("dd/MM/yy");

    public String lobby(Player p, String c) {
        if (plugin.isPlaceholder()) {
            return PlaceholderAPI.setPlaceholders(p,
                    c.replaceAll("<online>", String.valueOf(Bukkit.getOnlinePlayers().size())).replaceAll("<player>",
                            p.getName()));
        } else {
            return c.replaceAll("<online>", String.valueOf(Bukkit.getOnlinePlayers().size())).replaceAll("<player>",
                    p.getName());
        }
    }

    public String game(Player p, String c, GameDuo game) {
        if (plugin.isPlaceholder()) {
            return PlaceholderAPI.setPlaceholders(p, c.replaceAll("<goals>", String.valueOf(game.getGoals(p)))
                    .replaceAll("<kills>", String.valueOf(game.getKills(p))).replaceAll("<date>", sm.format(now))
                    .replaceAll("<blueGoals>", String.valueOf(game.getTeams().get(ChatColor.BLUE).getGoals()))
                    .replaceAll("<redGoals>", String.valueOf(game.getTeams().get(ChatColor.RED).getGoals()))
                    .replaceAll("<time>", String.valueOf(game.getStarting()))
                    .replaceAll("<s>", (game.getStarting() > 1) ? "s" : "")
                    .replaceAll("<max>", String.valueOf(game.getMax()))
                    .replaceAll("<players>", String.valueOf(game.getPlayers())).replaceAll("<map>", game.getName())
                    .replaceAll("<mode>", game.getMode())
                    .replaceAll("<server>", plugin.getConfig().getString("server")));
        } else {
            return c.replaceAll("<goals>", String.valueOf(game.getGoals(p)))
                    .replaceAll("<kills>", String.valueOf(game.getKills(p))).replaceAll("<date>", sm.format(now))
                    .replaceAll("<blueGoals>", String.valueOf(game.getTeams().get(ChatColor.BLUE).getGoals()))
                    .replaceAll("<redGoals>", String.valueOf(game.getTeams().get(ChatColor.RED).getGoals()))
                    .replaceAll("<time>", String.valueOf(game.getStarting()))
                    .replaceAll("<max>", String.valueOf(game.getMax()))
                    .replaceAll("<players>", String.valueOf(game.getPlayers())).replaceAll("<map>", game.getName())
                    .replaceAll("<mode>", game.getMode())
                    .replaceAll("<server>", plugin.getConfig().getString("server"));
        }
    }

    public String gameFour(Player p, String c, GameFour game) {
        if (plugin.isPlaceholder()) {
            return PlaceholderAPI.setPlaceholders(p, c.replaceAll("<red>", game.getTeams().get(ChatColor.RED).getAlly())
                    .replaceAll("<redLife>", game.getTeams().get(ChatColor.RED).getLifeString())
                    .replaceAll("<blue>", game.getTeams().get(ChatColor.BLUE).getAlly())
                    .replaceAll("<blueLife>", game.getTeams().get(ChatColor.BLUE).getLifeString())
                    .replaceAll("<yellow>", game.getTeams().get(ChatColor.YELLOW).getAlly())
                    .replaceAll("<yellowLife>", game.getTeams().get(ChatColor.YELLOW).getLifeString())
                    .replaceAll("<green>", game.getTeams().get(ChatColor.GREEN).getAlly())
                    .replaceAll("<greenLife>", game.getTeams().get(ChatColor.GREEN).getLifeString())
                    .replaceAll("<goals>", String.valueOf(game.getGoals(p)))
                    .replaceAll("<kills>", String.valueOf(game.getKills(p))).replaceAll("<date>", sm.format(now))
                    .replaceAll("<server>", plugin.getConfig().getString("server"))
                    .replaceAll("<players>", String.valueOf(game.getPlayers().size()))
                    .replaceAll("<max>", String.valueOf(game.getMax())).replaceAll("<mode>", game.getMode())
                    .replaceAll("<map>", game.getName()).replaceAll("<time>", String.valueOf(game.getStarting()))
                    .replaceAll("<s>", (game.getStarting() > 1) ? "s" : ""));
        }
        return c.replaceAll("<red>", game.getTeams().get(ChatColor.RED).getAlly())
                .replaceAll("<redLife>", game.getTeams().get(ChatColor.RED).getLifeString())
                .replaceAll("<blue>", game.getTeams().get(ChatColor.BLUE).getAlly())
                .replaceAll("<blueLife>", game.getTeams().get(ChatColor.BLUE).getLifeString())
                .replaceAll("<yellow>", game.getTeams().get(ChatColor.YELLOW).getAlly())
                .replaceAll("<yellowLife>", game.getTeams().get(ChatColor.YELLOW).getLifeString())
                .replaceAll("<green>", game.getTeams().get(ChatColor.GREEN).getAlly())
                .replaceAll("<greenLife>", game.getTeams().get(ChatColor.GREEN).getLifeString())
                .replaceAll("<goals>", String.valueOf(game.getGoals(p)))
                .replaceAll("<kills>", String.valueOf(game.getKills(p))).replaceAll("<date>", sm.format(now))
                .replaceAll("<server>", plugin.getConfig().getString("server"))
                .replaceAll("<players>", String.valueOf(game.getPlayers().size()))
                .replaceAll("<max>", String.valueOf(game.getMax())).replaceAll("<mode>", game.getMode())
                .replaceAll("<map>", game.getName()).replaceAll("<time>", String.valueOf(game.getStarting()))
                .replaceAll("<s>", (game.getStarting() > 1) ? "s" : "");
    }

    public void remove(Player p) {
        sb.remove(p);
        score.remove(p);
    }

}