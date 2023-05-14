package io.github.MGDCl.TheBridge.game;

import io.github.MGDCl.TheBridge.TheBridge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class InventoryData {

    private static HashMap<Player, InventoryData> inventoryData = new HashMap<>();
    private UUID uuid;
    private Inventory inv;
    private ItemStack[] armor;
    private boolean restored;

    public InventoryData(Player p) {
        this.uuid = p.getUniqueId();
        this.armor = p.getInventory().getArmorContents();
        this.restored = false;
        this.inv = Bukkit.createInventory(null, InventoryType.PLAYER, p.getName());
        this.inv.setContents(p.getInventory().getContents());
        inventoryData.put(p, this);
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
    }

    public void restore() {
        if (!restored) {
            Player p = this.getPlayer();
            if (p == null)
                return;
            if (TheBridge.get().isStop()) {
                p.getInventory().setContents(inv.getContents());
                p.getInventory().setArmorContents(armor);
                p.updateInventory();
                return;
            }
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            restored = true;
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.getInventory().setContents(inv.getContents());
                    p.getInventory().setArmorContents(armor);
                    p.updateInventory();
                }
            }.runTaskLater(TheBridge.get(), 1);
        }
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

    public static void remove(Player p) {
        inventoryData.remove(p);
    }

    public static HashMap<Player, InventoryData> getInventoryData() {
        return inventoryData;
    }

    public static InventoryData getInventoryData(Player p) {
        if (inventoryData.containsKey(p))
            return inventoryData.get(p);
        return null;
    }

}
