package io.github.MGDCl.TheBridge.game;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.database.PlayerStat;
import io.github.MGDCl.TheBridge.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private double health;
    private boolean fly;
    private int food;
    private Inventory inv;
    private ItemStack[] armor;
    private Scoreboard sb;
    private GameMode gm;
    private boolean restored;
    private float flySpeed;

    public PlayerData(Player p) {
        this.uuid = p.getUniqueId();
        this.sb = p.getScoreboard();
        this.food = p.getFoodLevel();
        this.health = p.getHealth();
        this.gm = p.getGameMode();
        this.armor = p.getInventory().getArmorContents();
        this.flySpeed = p.getFlySpeed();
        this.fly = p.getAllowFlight();
        this.restored = false;
        this.inv = Bukkit.createInventory(null, InventoryType.PLAYER, p.getName());
        this.inv.setContents(p.getInventory().getContents());
    }

    public void restore() {
        if (!restored) {
            Player p = this.getPlayer();
            if (p == null)
                return;
            restored = true;
            p.closeInventory();
            p.setGameMode(gm);
            p.getInventory().clear();
            p.getInventory().setContents(inv.getContents());
            p.getInventory().setArmorContents(armor);
            p.setFoodLevel(food);
            p.setHealth(health);
            p.resetPlayerTime();
            p.resetPlayerWeather();
            p.setFlySpeed(flySpeed);
            p.setAllowFlight(fly);
            p.setFireTicks(0);
            p.setScoreboard(sb);
            Location respawn = TheBridge.get().getMainLobby();
            p.teleport(respawn, PlayerTeleportEvent.TeleportCause.END_PORTAL);
            Utils.setPlayerExperience(p, PlayerStat.getPlayerStat(p).getXp());
            if (!TheBridge.get().isStop())
                TheBridge.get().getTop().createInfo(p);
        }
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

}
