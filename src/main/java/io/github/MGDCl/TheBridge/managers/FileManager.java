package io.github.MGDCl.TheBridge.managers;

import io.github.MGDCl.TheBridge.TheBridge;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    TheBridge plugin;

    public FileManager(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void createNewFile(String file) {
        File arenayml = new File(plugin.getDataFolder() + "/arenas", file + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(arenayml);
        try {
            config.save(arenayml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean arenaExists(String file) {
        File folder = new File(plugin.getDataFolder() + "/arenas");
        File[] listFiles = folder.listFiles();
        String name = "";
        for (int f = 0; f < listFiles.length; f++) {
            if (listFiles[f].isFile()) {
                File arena = listFiles[f];
                name = arena.getName().replaceAll(".yml", "");
                if (name.equals(file)) {
                    return true;
                }
            }
        }
        return false;
    }

}