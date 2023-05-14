package io.github.MGDCl.TheBridge.packets;

import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class PacketInjector {
    private static Field channel;
    private static Field networkManager;
    private static Field playerConnection;

    public static void Enabler() {
        try {
            PacketInjector.playerConnection = Reflection.getClass("{nms}.EntityPlayer").getField("playerConnection");
            PacketInjector.networkManager = Reflection.getClass("{nms}.PlayerConnection").getField("networkManager");

            PacketInjector.channel = Reflection.getClass("{nms}.NetworkManager").getField("channel");

            PacketInjector.refreshSessions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Disabler() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            removePlayer(p);
        }
    }

    public static void addPlayer(final Player p) {
        if (p == null) {
            return;
        }
        try {
            final Channel channel = PacketInjector
                    .getChannel(PacketInjector.getNetworkManager(Reflection.getNmsPlayer(p)));
            if (channel == null) {
                return;
            }
            if (channel.pipeline().get("TheBridgePck") == null) {
                final PacketHandler packetHandler = new PacketHandler(p);
                channel.pipeline().addBefore("packet_handler", "TheBridgePck", packetHandler);
            }
        } catch (final Exception t) {
            t.printStackTrace();
        }
    }

    private static Channel getChannel(final Object networkManager) {
        if (networkManager == null) {
            return null;
        }
        Channel channel = null;
        try {
            channel = (Channel) PacketInjector.channel.get(networkManager);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return channel;
    }

    private static Object getNetworkManager(final Object entityPlayer) {
        if (entityPlayer == null) {
            return null;
        }
        Object networkManager = null;
        try {
            networkManager = PacketInjector.networkManager.get(PacketInjector.playerConnection.get(entityPlayer));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return networkManager;
    }

    public static void refreshSessions() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            PacketInjector.removePlayer(player);
            PacketInjector.addPlayer(player);
        }
    }

    public static void removePlayer(final Player p) {
        if (p == null) {
            return;
        }
        try {
            final Channel channel = PacketInjector
                    .getChannel(PacketInjector.getNetworkManager(Reflection.getNmsPlayer(p)));
            if (channel == null) {
                return;
            }
            if (channel.pipeline().get("LagAssistPck") != null) {
                channel.pipeline().remove("LagAssistPck");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
