package io.github.MGDCl.TheBridge.listeners;

import io.github.MGDCl.TheBridge.TheBridge;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import io.github.MGDCl.TheBridge.game.GameDuo;
import io.github.MGDCl.TheBridge.game.GameFour;
import io.github.MGDCl.TheBridge.game.GameDuo.State;
import io.github.MGDCl.TheBridge.game.GameFour.FState;;

public class SetupListener implements Listener {

    TheBridge plugin;

    public SetupListener(TheBridge plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
            return;
        }
        ItemStack item = p.getItemInHand();
        if (item.getType() == Material.BLAZE_ROD && item.getItemMeta().getDisplayName().equalsIgnoreCase("§eSetup TheBridge")) {
            if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                plugin.getSm().setMax(p, e.getClickedBlock().getLocation());
                e.setCancelled(true);
            }
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                plugin.getSm().setMin(p, e.getClickedBlock().getLocation());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("bridges.admin")) {
            return;
        }
        if (e.getLine(0).equals("[bridges]")) {
            if (e.getLine(1).toLowerCase().equals("normal")) {
                String game = e.getLine(2);
                Sign sign = (Sign)e.getBlock().getState();
                if (plugin.getGm().getGameByName(game) == null) {
                    p.sendMessage("§cThis Game not exits.");
                    return;
                }
                GameDuo gname = plugin.getGm().getGameByName(game);
                plugin.getSim().createSign(e.getBlock().getLocation(), game, sign);
                e.setLine(0, plugin.getLang().get("signs.normal.line-1").replaceAll("<state>", getState(gname.getState())).replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers())).replaceAll("<max>", String.valueOf(gname.getMax())));
                e.setLine(1, plugin.getLang().get("signs.normal.line-2").replaceAll("<state>", getState(gname.getState())).replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers())).replaceAll("<max>", String.valueOf(gname.getMax())));
                e.setLine(2, plugin.getLang().get("signs.normal.line-3").replaceAll("<state>", getState(gname.getState())).replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers())).replaceAll("<max>", String.valueOf(gname.getMax())));
                e.setLine(3, plugin.getLang().get("signs.normal.line-4").replaceAll("<state>", getState(gname.getState())).replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers())).replaceAll("<max>", String.valueOf(gname.getMax())));
                p.sendMessage("§aSign Normal added.");
            } else if (e.getLine(1).toLowerCase().equals("four")) {
                String game = e.getLine(2);
                Sign sign = (Sign)e.getBlock().getState();
                if (plugin.getGm().getGameFourByName(game) == null) {
                    p.sendMessage("§cThis Game not exits.");
                    return;
                }
                GameFour gname = plugin.getGm().getGameFourByName(game);
                plugin.getSim().createFourSign(e.getBlock().getLocation(), game, sign);
                e.setLine(0, plugin.getLang().get("signs.four.line-1").replaceAll("<state>", getState(gname.getState())).replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers().size())).replaceAll("<max>", String.valueOf(gname.getMax())));
                e.setLine(1, plugin.getLang().get("signs.four.line-2").replaceAll("<state>", getState(gname.getState())).replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers().size())).replaceAll("<max>", String.valueOf(gname.getMax())));
                e.setLine(2, plugin.getLang().get("signs.four.line-3").replaceAll("<state>", getState(gname.getState())).replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers().size())).replaceAll("<max>", String.valueOf(gname.getMax())));
                e.setLine(3, plugin.getLang().get("signs.four.line-4").replaceAll("<state>", getState(gname.getState())).replaceAll("<name>", game).replaceAll("<players>", String.valueOf(gname.getPlayers().size())).replaceAll("<max>", String.valueOf(gname.getMax())));
                p.sendMessage("§aSign Four added.");
            }
        }
    }

    protected String getState(State state) {
        if (state == State.WAITING) {
            return plugin.getLang().get("states.waiting");
        }
        if (state == State.STARTING) {
            return plugin.getLang().get("states.starting");
        }
        if (state == State.PREGAME) {
            return plugin.getLang().get("states.pregame");
        }
        if (state == State.INGAME) {
            return plugin.getLang().get("states.ingame");
        }
        if (state == State.FINISH) {
            return plugin.getLang().get("states.finish");
        }
        if (state == State.RESTARTING) {
            return plugin.getLang().get("states.restarting");
        }
        return "";
    }

    protected String getState(FState state) {
        if (state == FState.WAITING) {
            return plugin.getLang().get("states.waiting");
        }
        if (state == FState.STARTING) {
            return plugin.getLang().get("states.starting");
        }
        if (state == FState.PREGAME) {
            return plugin.getLang().get("states.pregame");
        }
        if (state == FState.INGAME) {
            return plugin.getLang().get("states.ingame");
        }
        if (state == FState.FINISH) {
            return plugin.getLang().get("states.finish");
        }
        if (state == FState.RESTARTING) {
            return plugin.getLang().get("states.restarting");
        }
        return "";
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("bridges.admin")) {
            return;
        }
        if (plugin.getSim().getSigns().keySet().contains(e.getBlock().getLocation())) {
            plugin.getSigns().set("signs.normal." + plugin.getSim().getSigns().get(e.getBlock().getLocation()).getGame(), null);
            plugin.getSigns().save();
            p.sendMessage("§cSign normal removed.");
        } else if (plugin.getSim().getFourSigns().keySet().contains(e.getBlock().getLocation())) {
            plugin.getSigns().set("signs.four." + plugin.getSim().getFourSigns().get(e.getBlock().getLocation()).getGame(), null);
            plugin.getSigns().save();
            p.sendMessage("§cSign four removed.");
        }
    }

    public String getLocationString(Location loc){
        String world = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        return world + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
    }

}
