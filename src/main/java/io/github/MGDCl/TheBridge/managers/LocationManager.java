package io.github.MGDCl.TheBridge.managers;

import io.github.MGDCl.TheBridge.TheBridge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LocationManager {

    private Location nkills;
    private Location nwins;
    private Location ngoals;
    private Location fkills;
    private Location fwins;
    private Location fgoals;
    private Location stats;
    TheBridge plugin;

    public LocationManager(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void setMainLobby(Player p) {
        Location loc = p.getLocation();
        String world = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        plugin.getConfig().set("mainLobby", world + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch);
        plugin.saveConfig();
        plugin.reloadMainLobby();
        p.sendMessage("§aLobby has been setted.");
    }

    public void reloadLocations() {
        if (plugin.getConfig().getString("stats") != null) {
            stats = getStringLocation("stats");
        } else {
            stats = null;
        }
        if (plugin.getConfig().getString("tops.normal.kills") != null) {
            nkills = getStringLocation("tops.normal.kills");
        } else {
            nkills = null;
        }
        if (plugin.getConfig().getString("tops.normal.wins") != null) {
            nwins = getStringLocation("tops.normal.wins");
        } else {
            nwins = null;
        }
        if (plugin.getConfig().getString("tops.normal.goals") != null) {
            ngoals = getStringLocation("tops.normal.goals");
        } else {
            ngoals = null;
        }
        if (plugin.getConfig().getString("tops.four.kills") != null) {
            fkills = getStringLocation("tops.four.kills");
        } else {
            fkills = null;
        }
        if (plugin.getConfig().getString("tops.four.wins") != null) {
            fwins = getStringLocation("tops.four.wins");
        } else {
            fwins = null;
        }
        if (plugin.getConfig().getString("tops.four.goals") != null) {
            fgoals = getStringLocation("tops.four.goals");
        } else {
            fgoals = null;
        }
    }

    public Location getMainLobby() {
        String location = plugin.getConfig().getString("mainLobby");
        String [] loca = location.split(";");
        Location loc = new Location(Bukkit.getWorld(loca[0]), Double.valueOf(loca[1]), Double.valueOf(loca[2]), Double.valueOf(loca[3]), Float.valueOf(loca[4]), Float.valueOf(loca[5]));
        return loc;
    }

    public Location getStringLocation(String l) {
        String location = plugin.getConfig().getString(l);
        String [] loca = location.split(";");
        Location loc = new Location(Bukkit.getWorld(loca[0]), Double.valueOf(loca[1]), Double.valueOf(loca[2]), Double.valueOf(loca[3]), Float.valueOf(loca[4]), Float.valueOf(loca[5]));
        return loc;
    }

    public String getLocationString(Location loc){
        String world = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        return world + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
    }

    public Location getStats() {
        return stats;
    }

    public Location getNormalKills() {
        return nkills;
    }

    public Location getNormalWins() {
        return nwins;
    }

    public Location getNormalGoals() {
        return ngoals;
    }

    public Location getFourKills() {
        return fkills;
    }

    public Location getFourWins() {
        return fwins;
    }

    public Location getFourGoals() {
        return fgoals;
    }

}
