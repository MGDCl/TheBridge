package io.github.MGDCl.TheBridge.managers;

import io.github.MGDCl.TheBridge.TheBridge;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SetupManager {

    private HashMap<Player, HashMap<String, Location>> build = new HashMap<Player, HashMap<String, Location>>();
    TheBridge plugin;

    public SetupManager(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void setMin(Player p, Location loc) {
        if (!build.containsKey(p)) {
            build.put(p, new HashMap<String, Location>());
            build.get(p).put("MIN", loc);
            p.sendMessage(plugin.getLang().get("setup.minSet").replaceAll("<world>", loc.getWorld().getName()).replaceAll("<x>", String.valueOf(loc.getY())).replaceAll("<y>", String.valueOf(loc.getY())).replaceAll("<z>", String.valueOf(loc.getZ())).replaceAll("<yaw>", String.valueOf(loc.getYaw())).replaceAll("<pitch>", String.valueOf(loc.getPitch())));
        } else {
            build.get(p).put("MIN", loc);
            p.sendMessage(plugin.getLang().get("setup.minSet").replaceAll("<world>", loc.getWorld().getName()).replaceAll("<x>", String.valueOf(loc.getY())).replaceAll("<y>", String.valueOf(loc.getY())).replaceAll("<z>", String.valueOf(loc.getZ())).replaceAll("<yaw>", String.valueOf(loc.getYaw())).replaceAll("<pitch>", String.valueOf(loc.getPitch())));
        }
    }

    public void setMax(Player p, Location loc) {
        if (!build.containsKey(p)) {
            build.put(p, new HashMap<String, Location>());
            build.get(p).put("MAX", loc);
            p.sendMessage(plugin.getLang().get("setup.maxSet").replaceAll("<world>", loc.getWorld().getName()).replaceAll("<x>", String.valueOf(loc.getY())).replaceAll("<y>", String.valueOf(loc.getY())).replaceAll("<z>", String.valueOf(loc.getZ())).replaceAll("<yaw>", String.valueOf(loc.getYaw())).replaceAll("<pitch>", String.valueOf(loc.getPitch())));
        } else {
            build.get(p).put("MAX", loc);
            p.sendMessage(plugin.getLang().get("setup.maxSet").replaceAll("<world>", loc.getWorld().getName()).replaceAll("<x>", String.valueOf(loc.getY())).replaceAll("<y>", String.valueOf(loc.getY())).replaceAll("<z>", String.valueOf(loc.getZ())).replaceAll("<yaw>", String.valueOf(loc.getYaw())).replaceAll("<pitch>", String.valueOf(loc.getPitch())));
        }
    }

    public HashMap<Player, HashMap<String, Location>> getSelect(){
        return build;
    }

}
