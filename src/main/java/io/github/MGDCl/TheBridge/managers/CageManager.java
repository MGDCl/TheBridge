package io.github.MGDCl.TheBridge.managers;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.cosmetics.Cage;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CageManager {

    TheBridge plugin;
    private HashMap<String, Cage> cages = new HashMap<String, Cage>();
    private List<String> unlocked = null;
    private List<String> locked = null;
    private List<String> noPerm = null;

    public CageManager(TheBridge plugin) {
        this.plugin = plugin;
        loadCages();
    }

    public void loadCages() {
        List<String> un = new ArrayList<String>();
        for (String u : plugin.getCages().getList("unlocked")) {
            un.add(u);
        }
        unlocked = un;
        List<String> lo = new ArrayList<String>();
        for (String l : plugin.getCages().getList("locked")) {
            lo.add(l);
        }
        locked = lo;
        List<String> pe = new ArrayList<String>();
        for (String p : plugin.getCages().getList("noPerm")) {
            pe.add(p);
        }
        noPerm = pe;
        ConfigurationSection conf = plugin.getCages().getConfig().getConfigurationSection("cages");
        for (String cage : conf.getKeys(false)) {
            cages.put(cage, new Cage(plugin, "cages." + cage, cage));
        }
    }

    public Cage getCageByName(String name) {
        if (cages.containsKey(name)) {
            return cages.get(name);
        }
        return null;
    }

    public HashMap<String, Cage> getCages() {
        return cages;
    }

    public List<String> getUnlocked() {
        return unlocked;
    }

    public List<String> getLocked() {
        return locked;
    }

    public List<String> getNoPerm() {
        return noPerm;
    }

}
