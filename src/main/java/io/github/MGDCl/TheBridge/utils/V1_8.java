package io.github.MGDCl.TheBridge.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_8 {

    public static void sendActionbar(Player player, String s) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + s + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        CraftPlayer cfp = (CraftPlayer) player;
        cfp.getHandle().playerConnection.sendPacket(ppoc);
    }

}
