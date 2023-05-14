package io.github.MGDCl.TheBridge.menus;

import io.github.MGDCl.TheBridge.TheBridge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.MGDCl.TheBridge.game.GameDuo;
import io.github.MGDCl.TheBridge.game.GameDuo.State;
import io.github.MGDCl.TheBridge.game.GameFour;
import io.github.MGDCl.TheBridge.game.GameFour.FState;
import io.github.MGDCl.TheBridge.utils.ItemBuilder;

public class GameMenu {

    TheBridge plugin;

    public GameMenu(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void openSelectTypeMenu(Player p) {
        final Inventory inv = Bukkit.getServer().createInventory(null, 27, plugin.getLang().get(p, "menus.type.title"));
        final ItemStack normal = ItemBuilder.item(Material.STAINED_CLAY, 1, (short) 11,
                plugin.getLang().get(p, "menus.type.normal.nameItem"),
                plugin.getLang().get(p, "menus.type.normal.loreItem"));
        final ItemStack four = ItemBuilder.item(Material.STAINED_CLAY, 1, (short) 14,
                plugin.getLang().get(p, "menus.type.four.nameItem"),
                plugin.getLang().get(p, "menus.type.four.loreItem"));
        inv.setItem(11, normal);
        inv.setItem(15, four);
        p.openInventory(inv);
    }

    public void openGameMenu(Player p) {
        final Inventory inv = Bukkit.getServer().createInventory(null, 27, plugin.getLang().get(p, "menus.game.title"));
        for (final GameDuo game : plugin.getGm().getGames())
            inv.addItem(game(p, game));
        p.openInventory(inv);
    }

    public void openGameFourMenu(Player p) {
        final Inventory inv = Bukkit.getServer().createInventory(null, 27,
                plugin.getLang().get(p, "menus.gameFour.title"));
        for (final GameFour game : plugin.getGm().getGamesFour())
            inv.addItem(game(p, game));
        p.openInventory(inv);
    }

    protected ItemStack game(Player p, GameDuo game) {
        final ItemStack item = ItemBuilder.item(Material.STAINED_CLAY, 1, getState(game), "§a" + game.getName(),
                plugin.getLang().get(p, "menus.game.generalDesc")
                        .replaceAll("<players>", String.valueOf(game.getPlayers()))
                        .replaceAll("<max>", String.valueOf(game.getMax())).replaceAll("<mode>", game.getMode())
                        .replaceAll("<state>", getState(game.getState())));
        return item;
    }

    protected ItemStack game(Player p, GameFour game) {
        final ItemStack item = ItemBuilder.item(Material.STAINED_CLAY, 1, getState(game), "§a" + game.getName(),
                plugin.getLang().get(p, "menus.game.generalDesc")
                        .replaceAll("<players>", String.valueOf(game.getPlayers().size()))
                        .replaceAll("<max>", String.valueOf(game.getMax())).replaceAll("<mode>", game.getMode())
                        .replaceAll("<state>", getState(game.getState())));
        return item;
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

    protected short getState(GameDuo game) {
        if (game.isState(State.WAITING))
            return (short) plugin.getConfig().getInt("signs.waiting");
        if (game.isState(State.STARTING))
            return (short) plugin.getConfig().getInt("signs.starting");
        if (game.isState(State.PREGAME))
            return (short) plugin.getConfig().getInt("signs.pregame");
        if (game.isState(State.INGAME))
            return (short) plugin.getConfig().getInt("signs.ingame");
        if (game.isState(State.RESTARTING))
            return (short) plugin.getConfig().getInt("signs.restart");
        if (game.isState(State.FINISH))
            return (short) plugin.getConfig().getInt("signs.finish");
        return 5;
    }

    protected short getState(GameFour game) {
        if (game.isState(FState.WAITING))
            return (short) plugin.getConfig().getInt("signs.waiting");
        if (game.isState(FState.STARTING))
            return (short) plugin.getConfig().getInt("signs.starting");
        if (game.isState(FState.PREGAME))
            return (short) plugin.getConfig().getInt("signs.pregame");
        if (game.isState(FState.INGAME))
            return (short) plugin.getConfig().getInt("signs.ingame");
        if (game.isState(FState.RESTARTING))
            return (short) plugin.getConfig().getInt("signs.restart");
        if (game.isState(FState.FINISH))
            return (short) plugin.getConfig().getInt("signs.finish");
        return 5;
    }

}
