package io.github.MGDCl.TheBridge.menus;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.cosmetics.Cage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GlassMenu {

    TheBridge plugin;

    public GlassMenu(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void createGlassMenu(Player p) {
        Inventory inv = Bukkit.getServer().createInventory(null, plugin.getCages().getInt("size"),
                plugin.getCages().get("title"));
        for (Cage cage : plugin.getCm().getCages().values()) {
            if (!cage.isBuy() && !p.hasPermission(cage.getPermission())) {
                inv.setItem(cage.getSlot(), cage.getPermIcon());
            } else if (cage.isBuy() && !p.hasPermission(cage.getPermission())) {
                inv.setItem(cage.getSlot(), cage.getBuyIcon());
            } else {
                inv.setItem(cage.getSlot(), cage.getHasIcon());
            }
        }
        p.openInventory(inv);
    }

}
