package io.github.MGDCl.TheBridge.managers;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.cosmetics.Particle;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParticleManager {

    TheBridge plugin;
    @Getter
    private HashMap<String, Particle> arrow_trails = new HashMap<String, Particle>();
    @Getter
    private HashMap<String, Particle> feet_trails = new HashMap<String, Particle>();
    @Getter
    private List<String> unlocked = null;
    @Getter
    private List<String> locked = null;
    @Getter
    private List<String> noPerm = null;

    public ParticleManager(TheBridge plugin) {
        this.plugin = plugin;
        loadParticles();
    }

    public void loadParticles() {
        List<String> un = new ArrayList<String>();
        for (String u : plugin.getParticles().getList("unlocked")) {
            un.add(u);
        }
        unlocked = un;
        List<String> lo = new ArrayList<String>();
        for (String l : plugin.getParticles().getList("locked")) {
            lo.add(l);
        }
        locked = lo;
        List<String> pe = new ArrayList<String>();
        for (String p : plugin.getParticles().getList("noPerm")) {
            pe.add(p);
        }
        noPerm = pe;
        ConfigurationSection conf = plugin.getParticles().getConfig().getConfigurationSection("trails.arrow.effects");
        for (String arrow : conf.getKeys(false)) {
            arrow_trails.put(arrow, new Particle(plugin, "trails.arrow.effects." + arrow, arrow));
        }
        ConfigurationSection conf1 = plugin.getParticles().getConfig().getConfigurationSection("trails.feet.effects");
        for (String feet : conf1.getKeys(false)) {
            feet_trails.put(feet, new Particle(plugin, "trails.feet.effects." + feet, feet));
        }
    }

    public Particle getParticleByName(String type, String name) {
        if (arrow_trails.containsKey(name) || feet_trails.containsKey(name)) {
            switch (type.toUpperCase()) {
                case "FEET":
                    return feet_trails.get(name);
                case "ARROW":
                    return arrow_trails.get(name);
                default:
                    return null;
            }
        }
        return null;
    }

}
