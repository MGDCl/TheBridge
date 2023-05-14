package io.github.MGDCl.TheBridge.menus.particle;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.cosmetics.Particle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class FeetTrailMenu {
    TheBridge plugin;

    public FeetTrailMenu(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void createFeetMenu(Player p) {
        Inventory inv = Bukkit.getServer().createInventory(null, plugin.getParticles().getInt("trails.feet.size"),
                plugin.getParticles().get("trails.feet.title"));
        for (Particle feet : plugin.getPam().getFeet_trails().values()) {
            if (!feet.isBuy() && !p.hasPermission(feet.getPermission())) {
                inv.setItem(feet.getSlot(), feet.getPermIcon());
            } else if (feet.isBuy() && !p.hasPermission(feet.getPermission())) {
                inv.setItem(feet.getSlot(), feet.getBuyIcon());
            } else {
                inv.setItem(feet.getSlot(), feet.getHasIcon());
            }
        }
        p.openInventory(inv);
    }

}