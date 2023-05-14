package io.github.MGDCl.TheBridge.menus;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.game.GameFour;
import io.github.MGDCl.TheBridge.team.TeamFour;
import io.github.MGDCl.TheBridge.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class SpectPlayerMenu {

    TheBridge plugin;
    DecimalFormat df = new DecimalFormat("##.##");

    public SpectPlayerMenu(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void openSpectPlayerMenu(Player p) {
        GameFour game = plugin.getGm().getGameFourByPlayer(p);
        Inventory inv = Bukkit.getServer().createInventory(null, 54, plugin.getLang().get("menus.options.title"));
        ItemStack wool1 = ItemBuilder.item(Material.STAINED_CLAY, 1, (short) 11,
                plugin.getLang().get("menus.teleport.team.nameItem").replaceAll("<color>", "" + ChatColor.BLUE)
                        .replaceAll("<team>", game.getTeams().get(ChatColor.BLUE).getTeamName()),
                plugin.getLang().get("menus.teleport.team.loreItem"));
        ItemStack wool2 = ItemBuilder.item(Material.STAINED_CLAY, 1, (short) 14,
                plugin.getLang().get("menus.teleport.team.nameItem").replaceAll("<color>", "" + ChatColor.RED)
                        .replaceAll("<team>", game.getTeams().get(ChatColor.RED).getTeamName()),
                plugin.getLang().get("menus.teleport.team.loreItem"));
        ItemStack wool3 = ItemBuilder.item(Material.STAINED_CLAY, 1, (short) 4,
                plugin.getLang().get("menus.teleport.team.nameItem").replaceAll("<color>", "" + ChatColor.YELLOW)
                        .replaceAll("<team>", game.getTeams().get(ChatColor.YELLOW).getTeamName()),
                plugin.getLang().get("menus.teleport.team.loreItem"));
        ItemStack wool4 = ItemBuilder.item(Material.STAINED_CLAY, 1, (short) 5,
                plugin.getLang().get("menus.teleport.team.nameItem").replaceAll("<color>", "" + ChatColor.GREEN)
                        .replaceAll("<team>", game.getTeams().get(ChatColor.GREEN).getTeamName()),
                plugin.getLang().get("menus.teleport.team.loreItem"));
        if (!game.getTeams().get(ChatColor.BLUE).getDeath()) {
            TeamFour team = game.getTeams().get(ChatColor.BLUE);
            for (int i = 0; i < team.getTeamPlayers().size(); i++) {
                if (team.getTeamPlayers().get(i) != null) {
                    Player p1 = team.getTeamPlayers().get(i);
                    ItemStack ip1 = ItemBuilder.skull(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal(),
                            plugin.getLang().get("menus.teleport.player.nameItem").replaceAll("<player>",
                                    "§7" + p1.getName()),
                            plugin.getLang().get("menus.teleport.player.loreItem").replaceAll("<health>",
                                    df.format(p1.getHealth())),
                            p1.getName());
                    inv.setItem(i + 10, ip1);
                }
            }
        }
        if (!game.getTeams().get(ChatColor.RED).getDeath()) {
            TeamFour team = game.getTeams().get(ChatColor.RED);
            for (int i = 0; i < team.getTeamPlayers().size(); i++) {
                if (team.getTeamPlayers().get(i) != null) {
                    Player p1 = team.getTeamPlayers().get(i);
                    ItemStack ip1 = ItemBuilder.skull(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal(),
                            plugin.getLang().get("menus.teleport.player.nameItem").replaceAll("<player>",
                                    "§7" + p1.getName()),
                            plugin.getLang().get("menus.teleport.player.loreItem").replaceAll("<health>",
                                    df.format(p1.getHealth())),
                            p1.getName());
                    inv.setItem(i + 19, ip1);
                }
            }
        }
        if (!game.getTeams().get(ChatColor.YELLOW).getDeath()) {
            TeamFour team = game.getTeams().get(ChatColor.YELLOW);
            for (int i = 0; i < team.getTeamPlayers().size(); i++) {
                if (team.getTeamPlayers().get(i) != null) {
                    Player p1 = team.getTeamPlayers().get(i);
                    ItemStack ip1 = ItemBuilder.skull(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal(),
                            plugin.getLang().get("menus.teleport.player.nameItem").replaceAll("<player>",
                                    "§7" + p1.getName()),
                            plugin.getLang().get("menus.teleport.player.loreItem").replaceAll("<health>",
                                    df.format(p1.getHealth())),
                            p1.getName());
                    inv.setItem(i + 28, ip1);
                }
            }
        }
        if (!game.getTeams().get(ChatColor.GREEN).getDeath()) {
            TeamFour team = game.getTeams().get(ChatColor.GREEN);
            for (int i = 0; i < team.getTeamPlayers().size(); i++) {
                if (team.getTeamPlayers().get(i) != null) {
                    Player p1 = team.getTeamPlayers().get(i);
                    ItemStack ip1 = ItemBuilder.skull(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal(),
                            plugin.getLang().get("menus.teleport.player.nameItem").replaceAll("<player>",
                                    "§7" + p1.getName()),
                            plugin.getLang().get("menus.teleport.player.loreItem").replaceAll("<health>",
                                    df.format(p1.getHealth())),
                            p1.getName());
                    inv.setItem(i + 36, ip1);
                }
            }
        }
        inv.setItem(9, wool1);
        inv.setItem(18, wool2);
        inv.setItem(27, wool3);
        inv.setItem(36, wool4);
        p.openInventory(inv);
    }

}
