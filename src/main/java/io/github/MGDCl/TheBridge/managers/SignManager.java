package io.github.MGDCl.TheBridge.managers;

import io.github.MGDCl.TheBridge.TheBridge;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;

import io.github.MGDCl.TheBridge.game.GameDuo;
import io.github.MGDCl.TheBridge.game.GameDuo.State;
import io.github.MGDCl.TheBridge.game.GameFour;
import io.github.MGDCl.TheBridge.game.GameFour.FState;
import io.github.MGDCl.TheBridge.sings.GameFourSign;
import io.github.MGDCl.TheBridge.sings.GameNormalSign;

public class SignManager {

    private final HashMap<Location, GameNormalSign> gameSign = new HashMap<>();
    private final HashMap<Location, GameFourSign> gameFourSign = new HashMap<>();
    TheBridge plugin;

    public SignManager(TheBridge plugin) {
        this.plugin = plugin;
        final ConfigurationSection conf = plugin.getSigns().getConfig().getConfigurationSection("signs.normal");
        for (final String signN : conf.getKeys(false)) {
            final Location loc = getStringLocation(plugin.getSigns().get("signs.normal." + signN + ".loc"));
            if (!(loc.getBlock().getState() instanceof Sign))
                continue;
            final Block b = getBlockFaced(loc.getBlock());
            gameSign.put(loc, new GameNormalSign(loc, signN, b));
        }
        final ConfigurationSection confF = plugin.getSigns().getConfig().getConfigurationSection("signs.four");
        for (final String signF : confF.getKeys(false)) {
            final Location loc = getStringLocation(plugin.getSigns().get("signs.four." + signF + ".loc"));
            if (!(loc.getBlock().getState() instanceof Sign))
                continue;
            final Block b = getBlockFaced(loc.getBlock());
            gameFourSign.put(loc, new GameFourSign(loc, signF, b));
        }
    }

    public HashMap<Location, GameNormalSign> getSigns() {
        return gameSign;
    }

    public HashMap<Location, GameFourSign> getFourSigns() {
        return gameFourSign;
    }

    @SuppressWarnings("deprecation")
    public Block getBlockFaced(Block b) {
        switch (b.getData()) {
            case 2:
                return b.getRelative(BlockFace.SOUTH);
            case 3:
                return b.getRelative(BlockFace.NORTH);
            case 4:
                return b.getRelative(BlockFace.EAST);
            case 5:
                return b.getRelative(BlockFace.WEST);
            default:
                return b;
        }
    }

    public void createSign(Location loc, String game, Sign sign) {
        plugin.getSigns().set("signs.normal." + game + ".loc", getLocationString(loc));
        plugin.getSigns().set("signs.normal." + game + ".game", game);
        gameSign.put(loc, new GameNormalSign(loc, game, getBlockFaced(loc.getBlock())));
        final GameDuo gname = plugin.getGm().getGameByName(game);
        sign.setLine(0,
                plugin.getLang().get("signs.normal.line-1").replaceAll("<state>", getState(gname.getState()))
                        .replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers()))
                        .replaceAll("<max>", String.valueOf(gname.getMax())));
        sign.setLine(1,
                plugin.getLang().get("signs.normal.line-2").replaceAll("<state>", getState(gname.getState()))
                        .replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers()))
                        .replaceAll("<max>", String.valueOf(gname.getMax())));
        sign.setLine(2,
                plugin.getLang().get("signs.normal.line-3").replaceAll("<state>", getState(gname.getState()))
                        .replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers()))
                        .replaceAll("<max>", String.valueOf(gname.getMax())));
        sign.setLine(3,
                plugin.getLang().get("signs.normal.line-4").replaceAll("<state>", getState(gname.getState()))
                        .replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers()))
                        .replaceAll("<max>", String.valueOf(gname.getMax())));
        sign.update(true);
        plugin.getSigns().save();
    }

    public void createFourSign(Location loc, String game, Sign sign) {
        plugin.getSigns().set("signs.four." + game + ".loc", getLocationString(loc));
        plugin.getSigns().set("signs.four." + game + ".game", game);
        gameFourSign.put(loc, new GameFourSign(loc, game, getBlockFaced(loc.getBlock())));
        final GameFour gname = plugin.getGm().getGameFourByName(game);
        sign.setLine(0,
                plugin.getLang().get("signs.four.line-1").replaceAll("<state>", getState(gname.getState()))
                        .replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers().size()))
                        .replaceAll("<max>", String.valueOf(gname.getMax())));
        sign.setLine(1,
                plugin.getLang().get("signs.four.line-2").replaceAll("<state>", getState(gname.getState()))
                        .replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers().size()))
                        .replaceAll("<max>", String.valueOf(gname.getMax())));
        sign.setLine(2,
                plugin.getLang().get("signs.four.line-3").replaceAll("<state>", getState(gname.getState()))
                        .replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers().size()))
                        .replaceAll("<max>", String.valueOf(gname.getMax())));
        sign.setLine(3,
                plugin.getLang().get("signs.four.line-4").replaceAll("<state>", getState(gname.getState()))
                        .replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers().size()))
                        .replaceAll("<max>", String.valueOf(gname.getMax())));
        sign.update(true);
        plugin.getSigns().save();
    }

    public void updateGameSign(GameDuo game) {
        for (final GameNormalSign sig : gameSign.values())
            if (sig.getGame().equals(game.getName())) {
                final Sign sign = sig.getSign();
                sign.setLine(0,
                        plugin.getLang().get("signs.normal.line-1").replaceAll("<state>", getState(game.getState()))
                                .replaceAll("<name>", game.getName())
                                .replaceAll("<players>", String.valueOf(game.getPlayers()))
                                .replaceAll("<max>", String.valueOf(game.getMax())));
                sign.setLine(1,
                        plugin.getLang().get("signs.normal.line-2").replaceAll("<state>", getState(game.getState()))
                                .replaceAll("<name>", game.getName())
                                .replaceAll("<players>", String.valueOf(game.getPlayers()))
                                .replaceAll("<max>", String.valueOf(game.getMax())));
                sign.setLine(2,
                        plugin.getLang().get("signs.normal.line-3").replaceAll("<state>", getState(game.getState()))
                                .replaceAll("<name>", game.getName())
                                .replaceAll("<players>", String.valueOf(game.getPlayers()))
                                .replaceAll("<max>", String.valueOf(game.getMax())));
                sign.setLine(3,
                        plugin.getLang().get("signs.normal.line-4").replaceAll("<state>", getState(game.getState()))
                                .replaceAll("<name>", game.getName())
                                .replaceAll("<players>", String.valueOf(game.getPlayers()))
                                .replaceAll("<max>", String.valueOf(game.getMax())));
                sign.update();
                updateBlock(game, sig.getRetract());
            }
    }

    public void updateGameFourSign(GameFour game) {
        for (final GameFourSign sig : gameFourSign.values())
            if (sig.getGame().equals(game.getName())) {
                final Sign sign = sig.getSign();
                sign.setLine(0,
                        plugin.getLang().get("signs.four.line-1").replaceAll("<state>", getState(game.getState()))
                                .replaceAll("<name>", game.getName())
                                .replaceAll("<players>", String.valueOf(game.getPlayers().size()))
                                .replaceAll("<max>", String.valueOf(game.getMax())));
                sign.setLine(1,
                        plugin.getLang().get("signs.four.line-2").replaceAll("<state>", getState(game.getState()))
                                .replaceAll("<name>", game.getName())
                                .replaceAll("<players>", String.valueOf(game.getPlayers().size()))
                                .replaceAll("<max>", String.valueOf(game.getMax())));
                sign.setLine(2,
                        plugin.getLang().get("signs.four.line-3").replaceAll("<state>", getState(game.getState()))
                                .replaceAll("<name>", game.getName())
                                .replaceAll("<players>", String.valueOf(game.getPlayers().size()))
                                .replaceAll("<max>", String.valueOf(game.getMax())));
                sign.setLine(3,
                        plugin.getLang().get("signs.four.line-4").replaceAll("<state>", getState(game.getState()))
                                .replaceAll("<name>", game.getName())
                                .replaceAll("<players>", String.valueOf(game.getPlayers().size()))
                                .replaceAll("<max>", String.valueOf(game.getMax())));
                sign.update();
                updateBlock(game, sig.getRetract());
            }
    }

    public Location getStringLocation(String location) {
        final String[] loca = location.split(";");
        final Location loc = new Location(Bukkit.getWorld(loca[0]), Double.valueOf(loca[1]), Double.valueOf(loca[2]),
                Double.valueOf(loca[3]), Float.valueOf(loca[4]), Float.valueOf(loca[5]));
        return loc;
    }

    @SuppressWarnings("deprecation")
    public void updateBlock(GameDuo game, Block b) {
        if (game.isState(State.WAITING))
            b.setData((byte) plugin.getConfig().getInt("signs.waiting"));
        if (game.isState(State.STARTING))
            b.setData((byte) plugin.getConfig().getInt("signs.starting"));
        if (game.isState(State.PREGAME))
            b.setData((byte) plugin.getConfig().getInt("signs.ingame"));
        if (game.isState(State.INGAME))
            b.setData((byte) plugin.getConfig().getInt("signs.ingame"));
        if (game.isState(State.FINISH))
            b.setData((byte) plugin.getConfig().getInt("signs.full"));
        if (game.isState(State.RESTARTING))
            b.setData((byte) plugin.getConfig().getInt("signs.restart"));
    }

    @SuppressWarnings("deprecation")
    public void updateBlock(GameFour game, Block b) {
        if (game.isState(FState.WAITING))
            b.setData((byte) plugin.getConfig().getInt("signs.waiting"));
        if (game.isState(FState.STARTING))
            b.setData((byte) plugin.getConfig().getInt("signs.starting"));
        if (game.isState(FState.PREGAME))
            b.setData((byte) plugin.getConfig().getInt("signs.ingame"));
        if (game.isState(FState.INGAME))
            b.setData((byte) plugin.getConfig().getInt("signs.ingame"));
        if (game.isState(FState.FINISH))
            b.setData((byte) plugin.getConfig().getInt("signs.full"));
        if (game.isState(FState.RESTARTING))
            b.setData((byte) plugin.getConfig().getInt("signs.restart"));
    }

    protected String getState(FState state) {
        if (state == FState.WAITING)
            return plugin.getLang().get("states.waiting");
        if (state == FState.STARTING)
            return plugin.getLang().get("states.starting");
        if (state == FState.PREGAME)
            return plugin.getLang().get("states.pregame");
        if (state == FState.INGAME)
            return plugin.getLang().get("states.ingame");
        if (state == FState.FINISH)
            return plugin.getLang().get("states.finish");
        if (state == FState.RESTARTING)
            return plugin.getLang().get("states.restarting");
        return "";
    }

    protected String getState(State state) {
        if (state == State.WAITING)
            return plugin.getLang().get("states.waiting");
        if (state == State.STARTING)
            return plugin.getLang().get("states.starting");
        if (state == State.PREGAME)
            return plugin.getLang().get("states.pregame");
        if (state == State.INGAME)
            return plugin.getLang().get("states.ingame");
        if (state == State.FINISH)
            return plugin.getLang().get("states.finish");
        if (state == State.RESTARTING)
            return plugin.getLang().get("states.restarting");
        return "";
    }

    public String getLocationString(Location loc) {
        final String world = loc.getWorld().getName();
        final double x = loc.getX();
        final double y = loc.getY();
        final double z = loc.getZ();
        final float yaw = loc.getYaw();
        final float pitch = loc.getPitch();
        return world + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
    }

}