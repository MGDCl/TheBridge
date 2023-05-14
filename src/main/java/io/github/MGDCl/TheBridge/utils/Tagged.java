package io.github.MGDCl.TheBridge.utils;

import io.github.MGDCl.TheBridge.TheBridge;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class Tagged {

    private static HashMap<Player, Player> tag = new HashMap<Player, Player>();
    private static HashMap<Player, BukkitTask> tagTask = new HashMap<Player, BukkitTask>();

    public static void setTag(Player p, Player tagged) {
        if (!tag.containsKey(p)) {
            tag.put(p, tagged);
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    tag.remove(p);
                    cancel();
                }
            }.runTaskLater(TheBridge.get(), 5 * 20);
            tagTask.put(p, task);
        } else {
            tag.remove(p);
            tagTask.get(p).cancel();
            tag.put(p, tagged);
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    tag.remove(p);
                    cancel();
                }
            }.runTaskLater(TheBridge.get(), 5 * 20);
            tagTask.put(p, task);
        }
    }

    public static Player getTag(Player p) {
        if (tag.containsKey(p)) {
            return tag.get(p);
        }
        return null;
    }

}
