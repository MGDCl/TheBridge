package io.github.MGDCl.TheBridge.utils;

import org.bukkit.Bukkit;

public class VersionMgr {


    public static boolean isV1_8() {
        return Bukkit.getVersion().contains("1.8");
    }

    public static boolean isV1_13() {
        return Bukkit.getVersion().contains("1.13");
    }

    public static boolean isV1_12() {
        if (Bukkit.getVersion().contains("1.8")) {
            return false;
        }
        if (Bukkit.getVersion().contains("1.9")) {
            return false;
        }
        if (Bukkit.getVersion().contains("1.10")) {
            return false;
        }
        if (Bukkit.getVersion().contains("1.11")) {
            return false;
        }
        return true;
    }

    public static boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public static String ChunkExistsName() {
        return isV1_13() ? "f" : "e";
    }


}
