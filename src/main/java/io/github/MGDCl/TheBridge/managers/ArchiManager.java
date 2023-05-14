package io.github.MGDCl.TheBridge.managers;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.archievements.Archi;
import io.github.MGDCl.TheBridge.archievements.ArchiType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArchiManager {

    TheBridge plugin;
    private HashMap<ArchiType, ArrayList<Archi>> archiements = new HashMap<ArchiType, ArrayList<Archi>>();
    private ItemStack unlocked = null;
    private ItemStack locked = null;

    public ArchiManager(TheBridge plugin) {
        this.plugin = plugin;
        loadArchievements();
    }

    public void loadArchievements() {
        ItemStack unlocked1 = new ItemStack(Material.valueOf(plugin.getAchievement().get("unlocked.item")), plugin.getAchievement().getInt("unlocked.amount"), (short)plugin.getAchievement().getInt("unlocked.data"));
        ItemMeta unlockedM = unlocked1.getItemMeta();
        unlockedM.setDisplayName(plugin.getAchievement().get("unlocked.name"));
        List<String> un = new ArrayList<String>();
        for (String u : plugin.getAchievement().getList("unlocked.lore")) {
            un.add(u);
        }
        unlockedM.setLore(un);
        unlocked1.setItemMeta(unlockedM);
        unlocked = unlocked1;
        ItemStack locked1 = new ItemStack(Material.valueOf(plugin.getAchievement().get("locked.item")), plugin.getAchievement().getInt("locked.amount"), (short)plugin.getAchievement().getInt("locked.data"));
        ItemMeta lockedM = locked1.getItemMeta();
        lockedM.setDisplayName(plugin.getAchievement().get("locked.name"));
        List<String> lo = new ArrayList<String>();
        for (String l : plugin.getAchievement().getList("locked.lore")) {
            lo.add(l);
        }
        lockedM.setLore(lo);
        locked1.setItemMeta(lockedM);
        locked = locked1;
        ConfigurationSection conf = plugin.getAchievement().getConfig().getConfigurationSection("archievements");
        for (String msg : conf.getKeys(false)) {
            ArchiType type = ArchiType.valueOf(plugin.getAchievement().get("archievements." + msg + ".type"));
            if (!archiements.containsKey(type)) {
                archiements.put(type, new ArrayList<Archi>());
                archiements.get(type).add(new Archi(plugin, "archievements." + msg));
            } else {
                archiements.get(type).add(new Archi(plugin, "archievements." + msg));
            }
        }
    }

    public void check(Player p, int amount, ArchiType type) {
        for (Archi archi : archiements.get(type)) {
            if (archi.getMax() == amount) {
                archi.execute(p);
            }
        }
    }

    public ItemStack getUnlocked() {
        return unlocked;
    }

    public ItemStack getLocked() {
        return locked;
    }

    public HashMap<ArchiType, ArrayList<Archi>> getArchiements() {
        return archiements;
    }

}
