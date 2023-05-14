package io.github.MGDCl.TheBridge.packets;

import io.github.MGDCl.TheBridge.client.ClientPacket;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

public class PacketHandler extends ChannelDuplexHandler {
    private Player p;

    public PacketHandler(final Player p) {
        this.p = p;
    }

    // OUTPUT MANGLER
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (ClientPacket.hidePacket(p, ctx, msg, promise)) {
            return;
        }
        super.write(ctx, msg, promise);
    }

    // INPUT MANGLER
    @Override
    public void channelRead(ChannelHandlerContext c, Object msg) throws Exception {
        super.channelRead(c, msg);
    }
}
