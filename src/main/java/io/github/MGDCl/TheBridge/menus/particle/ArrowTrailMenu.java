package io.github.MGDCl.TheBridge.menus.particle;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.cosmetics.Particle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ArrowTrailMenu {
    TheBridge plugin;

    public ArrowTrailMenu(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void createArrowMenu(Player p) {
        Inventory inv = Bukkit.getServer().createInventory(null, plugin.getParticles().getInt("trails.arrow.size"),
                plugin.getParticles().get("trails.arrow.title"));
        for (Particle arrow : plugin.getPam().getArrow_trails().values()) {
            if (!arrow.isBuy() && !p.hasPermission(arrow.getPermission())) {
                inv.setItem(arrow.getSlot(), arrow.getPermIcon());
            } else if (arrow.isBuy() && !p.hasPermission(arrow.getPermission())) {
                inv.setItem(arrow.getSlot(), arrow.getBuyIcon());
            } else {
                inv.setItem(arrow.getSlot(), arrow.getHasIcon());
            }
        }
        p.openInventory(inv);
    }

}
