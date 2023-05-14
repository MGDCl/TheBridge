package io.github.MGDCl.TheBridge.menus.particle;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TrailMenu {
    TheBridge plugin;

    public TrailMenu(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void createTrailMenu(Player p) {
        Inventory inv = Bukkit.getServer().createInventory(null, plugin.getParticles().getInt("size"),
                plugin.getParticles().get("title"));
        ItemStack arrow = ItemBuilder.item(
                Material.valueOf(plugin.getConfig().getString("shop.trails.type.arrow.icon")),
                plugin.getConfig().getInt("shop.trails.type.arrow.amount"),
                (short) plugin.getConfig().getInt("shop.trails.type.arrow.data"),
                plugin.getLang().get("shop.trails.type.arrow.nameItem"),
                plugin.getLang().get("shop.trails.type.arrow.loreItem"));
        ItemStack feet = ItemBuilder.item(Material.valueOf(plugin.getConfig().getString("shop.trails.type.feet.icon")),
                plugin.getConfig().getInt("shop.trails.type.feet.amount"),
                (short) plugin.getConfig().getInt("shop.trails.type.feet.data"),
                plugin.getLang().get("shop.trails.type.feet.nameItem"),
                plugin.getLang().get("shop.trails.type.feet.loreItem"));

        inv.setItem(plugin.getConfig().getInt("shop.trails.type.arrow.slot"), arrow);
        inv.setItem(plugin.getConfig().getInt("shop.trails.type.feet.slot"), feet);

        p.openInventory(inv);
    }

}
