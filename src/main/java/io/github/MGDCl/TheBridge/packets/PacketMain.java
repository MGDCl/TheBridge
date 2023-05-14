package io.github.MGDCl.TheBridge.packets;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PacketMain implements Listener {

    public PacketMain(Plugin p) {
        PacketInjector.Enabler();
        Reflection.Enabler();
        p.getServer().getPluginManager().registerEvents(this, p);
        Bukkit.getLogger().info("    §e[§a✔§e] §fInjecting PacketListener.");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PacketInjector.addPlayer(p);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PacketInjector.removePlayer(p);
    }

}
