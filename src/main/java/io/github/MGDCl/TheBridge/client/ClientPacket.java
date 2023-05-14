package io.github.MGDCl.TheBridge.client;

import org.bukkit.entity.Player;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ClientPacket {

    public static boolean hidePacket(Player p, ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        return false;

    }

}

