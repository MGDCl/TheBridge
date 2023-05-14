package io.github.MGDCl.TheBridge.menus;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopMenu {

    TheBridge plugin;

    public ShopMenu(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void openShopMenu(Player p) {
        Inventory inv = Bukkit.getServer().createInventory(null, plugin.getConfig().getInt("shop.size"),
                plugin.getLang().get("shop.title"));
        if (plugin.isCage()) {
            ItemStack glass = ItemBuilder.item(Material.valueOf(plugin.getConfig().getString("shop.glass.icon")),
                    plugin.getConfig().getInt("shop.glass.amount"),
                    (short) plugin.getConfig().getInt("shop.glass.data"), plugin.getLang().get("shop.glass.nameItem"),
                    plugin.getLang().get("shop.glass.loreItem"));
            inv.setItem(plugin.getConfig().getInt("shop.glass.slot"), glass);
        }
        ItemStack particle = ItemBuilder.item(Material.valueOf(plugin.getConfig().getString("shop.trails.icon")),
                plugin.getConfig().getInt("shop.trails.amount"), (short) plugin.getConfig().getInt("shop.trails.data"),
                plugin.getLang().get("shop.trails.nameItem"), plugin.getLang().get("shop.trails.loreItem"));
        ItemStack close = ItemBuilder.item(Material.valueOf(plugin.getConfig().getString("shop.close.icon")),
                plugin.getConfig().getInt("shop.close.amount"), (short) plugin.getConfig().getInt("shop.close.data"),
                plugin.getLang().get("shop.close.nameItem"), plugin.getLang().get("shop.close.loreItem"));

        inv.setItem(plugin.getConfig().getInt("shop.trails.slot"), particle);
        inv.setItem(plugin.getConfig().getInt("shop.close.slot"), close);
        p.openInventory(inv);
    }

}
