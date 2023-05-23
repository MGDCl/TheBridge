package io.github.MGDCl.TheBridge;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class Settings {

    private FileConfiguration config;
    private File file;
    private TheBridge u;

    public Settings(TheBridge u, String s) {
        this.u = u;
        this.file = new File(this.u.getDataFolder(), s + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
        Reader reader = new InputStreamReader(
                u.getResource(String.valueOf(String.valueOf(String.valueOf(s))) + ".yml"));
        YamlConfiguration loadConfiguration = YamlConfiguration.loadConfiguration(reader);
        try {
            if (!this.file.exists()) {
                this.config.addDefaults((Configuration) loadConfiguration);
                this.config.options().copyDefaults(true);
                this.config.save(this.file);
            } else {
                this.config.addDefaults((Configuration) loadConfiguration);
                this.config.options().copyDefaults(true);
                this.config.save(this.file);
                this.config.load(this.file);
            }
        } catch (IOException | InvalidConfigurationException ex) {
        }
    }

    protected void sDefault(String s, String s2) {
        if (!this.config.contains(s)) {
            this.config.set(s, (Object) s2);
            this.save();
        }
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException ex) {
        }
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    protected File getFile() {
        return this.file;
    }

    public String get(String s) {
        return this.config.getString(s).replaceAll("&", "§");
    }

    public String get(Player p, String s) {
        if (u.isPlaceholder()) {
            return PlaceholderAPI.setPlaceholders(p, config.getString(s).replaceAll("&", "§"));
        }
        return config.getString(s).replaceAll("&", "§");
    }

    public int getInt(String s) {
        return this.config.getInt(s);
    }

    public List<String> getList(String s) {
        return (List<String>) this.config.getStringList(s);
    }

    public List<String> getList(Player p, String s) {
        if (u.isPlaceholder()) {
            return PlaceholderAPI.setPlaceholders(p, (List<String>) config.getStringList(s));
        }
        return (List<String>) config.getStringList(s);
    }

    public boolean isSet(String s) {
        return this.config.isSet(s);
    }

    public void set(String s, Object o) {
        this.config.set(s, o);
    }

    public boolean getBoolean(String s) {
        return this.config.getBoolean(s);
    }
}
