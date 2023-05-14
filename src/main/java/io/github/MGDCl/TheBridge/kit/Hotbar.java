package io.github.MGDCl.TheBridge.kit;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.database.PlayerStat;
import io.github.MGDCl.TheBridge.game.InventoryData;
import io.github.MGDCl.TheBridge.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Hotbar {

    TheBridge plugin;

    public Hotbar(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void createHotbarMenu(Player p) {
        Inventory inv = Bukkit.getServer().createInventory(null, 54, plugin.getLang().get("menus.hotbar.title"));
        ItemStack[] items = plugin.getGm().getKit().getContents();
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
        ItemMeta glassM = glass.getItemMeta();
        glassM.setDisplayName("§a↑ §eInventory");
        List<String> lore = new ArrayList<String>();
        lore.add("§a↓ §eHotbar");
        glassM.setLore(lore);
        glass.setItemMeta(glassM);
        ItemStack save = ItemBuilder.item(Material.CHEST, 1, (short)0, plugin.getLang().get("menus.save.nameItem"), plugin.getLang().get("menus.save.loreItem"));
        ItemStack close = ItemBuilder.item(Material.BARRIER, 1, (short)0, plugin.getLang().get("menus.close.nameItem"), plugin.getLang().get("menus.close.loreItem"));
        for (int i = 0; i < 9; i++) {
            if (items[i] != null) {
                inv.setItem(i + 36, items[i]);
            }
        }
        for (int i = 27; i < 36; i++) {
            inv.setItem(i, glass);
        }
        inv.setItem(49, save);
        inv.setItem(50, close);
        new InventoryData(p);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.getOpenInventory().getTitle().equals(plugin.getLang().get("menus.hotbar.title"))) {
                    InventoryData itd = InventoryData.getInventoryData(p);
                    if (itd == null) {
                        return;
                    }
                    InventoryData.remove(p);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 0);
        p.openInventory(inv);
    }

    public void saveLayout(Player p, Inventory inv) {
        Inventory pinv = Bukkit.getServer().createInventory(null, 36);
        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
                pinv.setItem(i + 9, new ItemStack(Material.BARRIER));
            } else {
                pinv.setItem(i + 9, inv.getItem(i));
            }
        }
        for (int i = 36; i < 45; i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
                pinv.setItem(i - 36, new ItemStack(Material.BARRIER));
            } else {
                pinv.setItem(i - 36, inv.getItem(i));
            }
        }
        PlayerStat ps = PlayerStat.getPlayerStat(p);
        ps.setHotbar(pinv.getContents());
        p.sendMessage("Guardaste tu kit");
    }

}
