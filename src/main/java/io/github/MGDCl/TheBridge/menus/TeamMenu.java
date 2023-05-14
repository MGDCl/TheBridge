package io.github.MGDCl.TheBridge.menus;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.game.GameDuo;
import io.github.MGDCl.TheBridge.game.GameFour;
import io.github.MGDCl.TheBridge.team.TeamDuo;
import io.github.MGDCl.TheBridge.team.TeamFour;
import io.github.MGDCl.TheBridge.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TeamMenu {

    TheBridge plugin;

    public TeamMenu(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void openTeamFourMenu(Player p) {
        GameFour game = plugin.getGm().getGameFourByPlayer(p);
        Inventory inv = Bukkit.getServer().createInventory(null, 27, plugin.getLang().get("menus.teamFour.title"));
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack blue = ItemBuilder.item(Material.STAINED_CLAY,
                        (game.getTeams().get(ChatColor.BLUE).getTeamSize() == 0) ? 1
                                : game.getTeams().get(ChatColor.BLUE).getTeamSize(),
                        (short) 11,
                        "§9" + plugin.getLang().get("menus.teamFour.team.nameItem").replaceAll("<#>",
                                plugin.getConfig().getString("names.blue")),
                        getLore(game.getTeams().get(ChatColor.BLUE)));
                ItemStack red = ItemBuilder.item(Material.STAINED_CLAY,
                        (game.getTeams().get(ChatColor.RED).getTeamSize() == 0) ? 1
                                : game.getTeams().get(ChatColor.RED).getTeamSize(),
                        (short) 14,
                        "§c" + plugin.getLang().get("menus.teamFour.team.nameItem").replaceAll("<#>",
                                plugin.getConfig().getString("names.red")),
                        getLore(game.getTeams().get(ChatColor.RED)));
                ItemStack yellow = ItemBuilder.item(Material.STAINED_CLAY,
                        (game.getTeams().get(ChatColor.YELLOW).getTeamSize() == 0) ? 1
                                : game.getTeams().get(ChatColor.YELLOW).getTeamSize(),
                        (short) 4,
                        "§e" + plugin.getLang().get("menus.teamFour.team.nameItem").replaceAll("<#>",
                                plugin.getConfig().getString("names.yellow")),
                        getLore(game.getTeams().get(ChatColor.YELLOW)));
                ItemStack green = ItemBuilder.item(Material.STAINED_CLAY,
                        (game.getTeams().get(ChatColor.GREEN).getTeamSize() == 0) ? 1
                                : game.getTeams().get(ChatColor.GREEN).getTeamSize(),
                        (short) 5,
                        "§a" + plugin.getLang().get("menus.teamFour.team.nameItem").replaceAll("<#>",
                                plugin.getConfig().getString("names.green")),
                        getLore(game.getTeams().get(ChatColor.GREEN)));
                inv.setItem(10, blue);
                inv.setItem(12, red);
                inv.setItem(14, yellow);
                inv.setItem(16, green);
                if (!p.getOpenInventory().getTitle().equals(plugin.getLang().get("menus.teamFour.title"))) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 0);
        p.openInventory(inv);
    }

    public void openTeamNormalMenu(Player p) {
        GameDuo game = plugin.getGm().getGameByPlayer(p);
        Inventory inv = Bukkit.getServer().createInventory(null, 27, plugin.getLang().get("menus.team.title"));
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack blue = ItemBuilder.item(Material.STAINED_CLAY,
                        (game.getTeams().get(ChatColor.BLUE).getTeamSize() == 0) ? 1
                                : game.getTeams().get(ChatColor.BLUE).getTeamSize(),
                        (short) 11,
                        "§9" + plugin.getLang().get("menus.team.team.nameItem").replaceAll("<#>",
                                plugin.getConfig().getString("names.blue")),
                        getLore(game.getTeams().get(ChatColor.BLUE)));
                ItemStack red = ItemBuilder.item(Material.STAINED_CLAY,
                        (game.getTeams().get(ChatColor.RED).getTeamSize() == 0) ? 1
                                : game.getTeams().get(ChatColor.RED).getTeamSize(),
                        (short) 14,
                        "§c" + plugin.getLang().get("menus.team.team.nameItem").replaceAll("<#>",
                                plugin.getConfig().getString("names.red")),
                        getLore(game.getTeams().get(ChatColor.RED)));
                inv.setItem(11, blue);
                inv.setItem(15, red);
                if (!p.getOpenInventory().getTitle().equals(plugin.getLang().get("menus.team.title"))) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 0);
        p.openInventory(inv);
    }

    public List<String> getLore(TeamDuo team) {
        List<String> lore = new ArrayList<String>();
        for (String msg : plugin.getLang().getList("menus.team.team.loreItem")) {
            if (msg.contains("<teamPlayers>")) {
                for (Player p : team.getTeamPlayers()) {
                    lore.add("§7- " + team.getColor() + p.getName());
                }
            } else {
                lore.add(msg.replaceAll("&", "§"));
            }
        }
        return lore;
    }

    public List<String> getLore(TeamFour team) {
        List<String> lore = new ArrayList<String>();
        for (String msg : plugin.getLang().getList("menus.teamFour.team.loreItem")) {
            if (msg.contains("<teamPlayers>")) {
                for (Player p : team.getTeamPlayers()) {
                    lore.add("§7- " + team.getColor() + p.getName());
                }
            } else {
                lore.add(msg.replaceAll("&", "§"));
            }
        }
        return lore;
    }

}
