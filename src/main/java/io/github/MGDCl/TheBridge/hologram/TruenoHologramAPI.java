package io.github.MGDCl.TheBridge.hologram;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class TruenoHologramAPI {

    private static String version;

    private static void setupVersion() {
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public static TruenoHologram getNewHologram() {
        if (version == null) {
            setupVersion();
        }
        if (version.equals("v1_8_R3")) {
            return new TruenoHologram_v1_8_R3();
        }
        else {
            Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Unsopported server version.");
            return null;
        }

    }

}

