package io.github.MGDCl.TheBridge.menus;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.archievements.Archi;
import io.github.MGDCl.TheBridge.archievements.ArchiType;
import io.github.MGDCl.TheBridge.database.PlayerStat;
import io.github.MGDCl.TheBridge.listeners.PlayerListener;
import io.github.MGDCl.TheBridge.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AchievementsMenu {

    TheBridge plugin;

    public AchievementsMenu(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void createArchievementsMenu(Player p, int page) {
        PlayerStat ps = PlayerStat.getPlayerStat(p);
        Inventory inv = Bukkit.getServer().createInventory(null, 54,
                plugin.getAchievement().get("title").replaceAll("<player>", p.getName()));
        ItemStack signKill = ItemBuilder.item(Material.valueOf(plugin.getAchievement().get("kills.item")),
                plugin.getAchievement().getInt("kills.amount"), (short) plugin.getAchievement().getInt("kills.data"),
                plugin.getAchievement().get("kills.name"), plugin.getAchievement().get("kills.lore"));
        ItemStack signWin = ItemBuilder.item(Material.valueOf(plugin.getAchievement().get("wins.item")),
                plugin.getAchievement().getInt("wins.amount"), (short) plugin.getAchievement().getInt("wins.data"),
                plugin.getAchievement().get("wins.name"), plugin.getAchievement().get("wins.lore"));
        ItemStack signGoals = ItemBuilder.item(Material.valueOf(plugin.getAchievement().get("goals.item")),
                plugin.getAchievement().getInt("goals.amount"), (short) plugin.getAchievement().getInt("goals.data"),
                plugin.getAchievement().get("goals.name"), plugin.getAchievement().get("goals.lore"));
        ItemStack signPlaced = ItemBuilder.item(Material.valueOf(plugin.getAchievement().get("blocks_placed.item")),
                plugin.getAchievement().getInt("blocks_placed.amount"),
                (short) plugin.getAchievement().getInt("blocks_placed.data"),
                plugin.getAchievement().get("blocks_placed.name"), plugin.getAchievement().get("blocks_placed.lore"));
        ItemStack signBroken = ItemBuilder.item(Material.valueOf(plugin.getAchievement().get("blocks_bloken.item")),
                plugin.getAchievement().getInt("blocks_bloken.amount"),
                (short) plugin.getAchievement().getInt("blocks_bloken.data"),
                plugin.getAchievement().get("blocks_bloken.name"), plugin.getAchievement().get("blocks_bloken.lore"));
        ItemStack gLeft = ItemBuilder.item(Material.STAINED_GLASS_PANE, 1, (short) 7,
                plugin.getAchievement().get("archivements").replaceAll("<arrow>", "→"), "");
        ItemStack gRight = ItemBuilder.item(Material.STAINED_GLASS_PANE, 1, (short) 7,
                plugin.getAchievement().get("archivements").replaceAll("<arrow>", "§?"), "");
        ItemStack next = ItemBuilder.createSkull(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal(),
                plugin.getAchievement().get("next").replaceAll("<next>", "→"), "",
                "creeyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19");
        ItemStack back = ItemBuilder.createSkull(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal(),
                plugin.getAchievement().get("back").replaceAll("<back>", "§?"), "",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=");
        ItemStack close = ItemBuilder.item(Material.valueOf(plugin.getAchievement().get("close.item")),
                plugin.getAchievement().getInt("close.amount"), (short) plugin.getAchievement().getInt("close.data"),
                plugin.getAchievement().get("close.name"), plugin.getAchievement().get("close.lore"));
        inv.setItem(0, gLeft);
        inv.setItem(9, gLeft);
        inv.setItem(18, gLeft);
        inv.setItem(27, gLeft);
        inv.setItem(36, gLeft);
        inv.setItem(8, gRight);
        inv.setItem(17, gRight);
        inv.setItem(26, gRight);
        inv.setItem(35, gRight);
        inv.setItem(44, gRight);
        inv.setItem(2, signKill);
        inv.setItem(3, signWin);
        inv.setItem(4, signGoals);
        inv.setItem(5, signPlaced);
        inv.setItem(6, signBroken);
        if (plugin.getAm().getArchiements().get(ArchiType.KILLS).size() >= (page + 1) * 4
                && plugin.getAm().getArchiements().get(ArchiType.WINS).size() >= (page + 1) * 4
                && plugin.getAm().getArchiements().get(ArchiType.GOALS).size() >= (page + 1) * 4
                && plugin.getAm().getArchiements().get(ArchiType.BLOCKS_PLACED).size() >= (page + 1) * 4
                && plugin.getAm().getArchiements().get(ArchiType.BLOCKS_BROKEN).size() >= (page + 1) * 4) {
            inv.setItem(53, next);
        }
        if (PlayerListener.page.containsKey(p)) {
            inv.setItem(45, back);
        }
        inv.setItem(49, close);
        int min = (page - 1) * 4;
        for (int i = 1; i < 5; i++) {
            Archi archi = plugin.getAm().getArchiements().get(ArchiType.KILLS).get((i - 1) + min);
            if (ps.getFourKills() + ps.getNormalKills() <= archi.getMax()) {
                ItemStack locked = plugin.getAm().getLocked().clone();
                ItemMeta lockedM = locked.getItemMeta();
                List<String> loreNew = new ArrayList<String>();
                for (String msg : lockedM.getLore()) {
                    loreNew.add(msg.replaceAll("&", "§").replaceAll("<money>", String.valueOf(archi.getMoney()))
                            .replaceAll("<xp>", String.valueOf(archi.getXp()))
                            .replaceAll("<max>", String.valueOf(archi.getMax())).replaceAll("<name>", archi.getName())
                            .replaceAll("<description>", archi.getDescription())
                            .replaceAll("<current>", String.valueOf(ps.getFourKills() + ps.getNormalKills())));
                }
                lockedM.setDisplayName("§c" + archi.getName());
                lockedM.getLore().clear();
                lockedM.setLore(loreNew);
                locked.setItemMeta(lockedM);
                inv.setItem(2 + (9 * (i)), locked);
            } else {
                ItemStack unlocked = plugin.getAm().getUnlocked().clone();
                ItemMeta unlockedM = unlocked.getItemMeta();
                List<String> loreNew = new ArrayList<String>();
                for (String msg : unlockedM.getLore()) {
                    loreNew.add(msg.replaceAll("&", "§").replaceAll("<money>", String.valueOf(archi.getMoney()))
                            .replaceAll("<xp>", String.valueOf(archi.getXp())).replaceAll("<name>", archi.getName())
                            .replaceAll("<description>", archi.getDescription())
                            .replaceAll("<current>", String.valueOf(ps.getFourKills() + ps.getNormalKills())));
                }
                unlockedM.setDisplayName("§a" + archi.getName());
                unlockedM.getLore().clear();
                unlockedM.setLore(loreNew);
                unlocked.setItemMeta(unlockedM);
                inv.setItem(2 + (9 * (i)), unlocked);
            }
        }
        for (int i = 1; i < 5; i++) {
            Archi archi = plugin.getAm().getArchiements().get(ArchiType.WINS).get((i - 1) + min);
            if (ps.getFourWins() + ps.getNormalWins() <= archi.getMax()) {
                ItemStack locked = plugin.getAm().getLocked().clone();
                ItemMeta lockedM = locked.getItemMeta();
                List<String> loreNew = new ArrayList<String>();
                for (String msg : lockedM.getLore()) {
                    loreNew.add(msg.replaceAll("&", "§").replaceAll("<money>", String.valueOf(archi.getMoney()))
                            .replaceAll("<xp>", String.valueOf(archi.getXp()))
                            .replaceAll("<max>", String.valueOf(archi.getMax())).replaceAll("<name>", archi.getName())
                            .replaceAll("<description>", archi.getDescription())
                            .replaceAll("<current>", String.valueOf(ps.getFourWins() + ps.getNormalWins())));
                }
                lockedM.setDisplayName("§c" + archi.getName());
                lockedM.getLore().clear();
                lockedM.setLore(loreNew);
                locked.setItemMeta(lockedM);
                inv.setItem(3 + (9 * (i)), locked);
            } else {
                ItemStack unlocked = plugin.getAm().getUnlocked().clone();
                ItemMeta unlockedM = unlocked.getItemMeta();
                List<String> loreNew = new ArrayList<String>();
                for (String msg : unlockedM.getLore()) {
                    loreNew.add(msg.replaceAll("&", "§").replaceAll("<money>", String.valueOf(archi.getMoney()))
                            .replaceAll("<xp>", String.valueOf(archi.getXp())).replaceAll("<name>", archi.getName())
                            .replaceAll("<description>", archi.getDescription())
                            .replaceAll("<current>", String.valueOf(ps.getFourWins() + ps.getNormalWins())));
                }
                unlockedM.setDisplayName("§a" + archi.getName());
                unlockedM.getLore().clear();
                unlockedM.setLore(loreNew);
                unlocked.setItemMeta(unlockedM);
                inv.setItem(3 + (9 * (i)), unlocked);
            }
        }
        for (int i = 1; i < 5; i++) {
            Archi archi = plugin.getAm().getArchiements().get(ArchiType.GOALS).get((i - 1) + min);
            if (ps.getFourGoals() + ps.getNormalGoals() <= archi.getMax()) {
                ItemStack locked = plugin.getAm().getLocked().clone();
                ItemMeta lockedM = locked.getItemMeta();
                List<String> loreNew = new ArrayList<String>();
                for (String msg : lockedM.getLore()) {
                    loreNew.add(msg.replaceAll("&", "§").replaceAll("<money>", String.valueOf(archi.getMoney()))
                            .replaceAll("<xp>", String.valueOf(archi.getXp()))
                            .replaceAll("<max>", String.valueOf(archi.getMax())).replaceAll("<name>", archi.getName())
                            .replaceAll("<description>", archi.getDescription())
                            .replaceAll("<current>", String.valueOf(ps.getFourGoals() + ps.getNormalGoals())));
                }
                lockedM.setDisplayName("§c" + archi.getName());
                lockedM.getLore().clear();
                lockedM.setLore(loreNew);
                locked.setItemMeta(lockedM);
                inv.setItem(4 + (9 * (i)), locked);
            } else {
                ItemStack unlocked = plugin.getAm().getUnlocked().clone();
                ItemMeta unlockedM = unlocked.getItemMeta();
                List<String> loreNew = new ArrayList<String>();
                for (String msg : unlockedM.getLore()) {
                    loreNew.add(msg.replaceAll("&", "§").replaceAll("<money>", String.valueOf(archi.getMoney()))
                            .replaceAll("<xp>", String.valueOf(archi.getXp())).replaceAll("<name>", archi.getName())
                            .replaceAll("<description>", archi.getDescription())
                            .replaceAll("<current>", String.valueOf(ps.getFourGoals() + ps.getNormalGoals())));
                }
                unlockedM.setDisplayName("§a" + archi.getName());
                unlockedM.getLore().clear();
                unlockedM.setLore(loreNew);
                unlocked.setItemMeta(unlockedM);
                inv.setItem(4 + (9 * (i)), unlocked);
            }
        }
        for (int i = 1; i < 5; i++) {
            Archi archi = plugin.getAm().getArchiements().get(ArchiType.BLOCKS_PLACED).get((i - 1) + min);
            if (ps.getPlaced() <= archi.getMax()) {
                ItemStack locked = plugin.getAm().getLocked().clone();
                ItemMeta lockedM = locked.getItemMeta();
                List<String> loreNew = new ArrayList<String>();
                for (String msg : lockedM.getLore()) {
                    loreNew.add(msg.replaceAll("&", "§").replaceAll("<money>", String.valueOf(archi.getMoney()))
                            .replaceAll("<xp>", String.valueOf(archi.getXp()))
                            .replaceAll("<max>", String.valueOf(archi.getMax())).replaceAll("<name>", archi.getName())
                            .replaceAll("<description>", archi.getDescription())
                            .replaceAll("<current>", String.valueOf(ps.getPlaced())));
                }
                lockedM.setDisplayName("§c" + archi.getName());
                lockedM.getLore().clear();
                lockedM.setLore(loreNew);
                locked.setItemMeta(lockedM);
                inv.setItem(5 + (9 * (i)), locked);
            } else {
                ItemStack unlocked = plugin.getAm().getUnlocked().clone();
                ItemMeta unlockedM = unlocked.getItemMeta();
                List<String> loreNew = new ArrayList<String>();
                for (String msg : unlockedM.getLore()) {
                    loreNew.add(msg.replaceAll("&", "§").replaceAll("<money>", String.valueOf(archi.getMoney()))
                            .replaceAll("<xp>", String.valueOf(archi.getXp())).replaceAll("<name>", archi.getName())
                            .replaceAll("<description>", archi.getDescription())
                            .replaceAll("<current>", String.valueOf(ps.getPlaced())));
                }
                unlockedM.setDisplayName("§a" + archi.getName());
                unlockedM.getLore().clear();
                unlockedM.setLore(loreNew);
                unlocked.setItemMeta(unlockedM);
                inv.setItem(5 + (9 * (i)), unlocked);
            }
        }
        for (int i = 1; i < 5; i++) {
            Archi archi = plugin.getAm().getArchiements().get(ArchiType.BLOCKS_BROKEN).get((i - 1) + min);
            if (ps.getBroken() <= archi.getMax()) {
                ItemStack locked = plugin.getAm().getLocked().clone();
                ItemMeta lockedM = locked.getItemMeta();
                List<String> loreNew = new ArrayList<String>();
                for (String msg : lockedM.getLore()) {
                    loreNew.add(msg.replaceAll("&", "§").replaceAll("<money>", String.valueOf(archi.getMoney()))
                            .replaceAll("<xp>", String.valueOf(archi.getXp()))
                            .replaceAll("<max>", String.valueOf(archi.getMax())).replaceAll("<name>", archi.getName())
                            .replaceAll("<description>", archi.getDescription())
                            .replaceAll("<current>", String.valueOf(ps.getBroken())));
                }
                lockedM.setDisplayName("§c" + archi.getName());
                lockedM.getLore().clear();
                lockedM.setLore(loreNew);
                locked.setItemMeta(lockedM);
                inv.setItem(6 + (9 * (i)), locked);
            } else {
                ItemStack unlocked = plugin.getAm().getUnlocked().clone();
                ItemMeta unlockedM = unlocked.getItemMeta();
                List<String> loreNew = new ArrayList<String>();
                for (String msg : unlockedM.getLore()) {
                    loreNew.add(msg.replaceAll("&", "§").replaceAll("<money>", String.valueOf(archi.getMoney()))
                            .replaceAll("<xp>", String.valueOf(archi.getXp())).replaceAll("<name>", archi.getName())
                            .replaceAll("<description>", archi.getDescription())
                            .replaceAll("<current>", String.valueOf(ps.getBroken())));
                }
                unlockedM.setDisplayName("§a" + archi.getName());
                unlockedM.getLore().clear();
                unlockedM.setLore(loreNew);
                unlocked.setItemMeta(unlockedM);
                inv.setItem(6 + (9 * (i)), unlocked);
            }
        }
        p.openInventory(inv);
    }

}
