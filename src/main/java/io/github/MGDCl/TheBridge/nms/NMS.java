package io.github.MGDCl.TheBridge.nms;

import io.github.MGDCl.TheBridge.packets.ParticleHandler;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

public interface NMS {

    ChunkGenerator getChunkGenerator();

    void sendTitle(Player player, int fadein, int stay, int fadeout, String title, String subtitle);

    void sendActionBar(Player player, String s);

    ParticleHandler sendParticle(ParticleHandler.ParticleType type, double speed, int count, double radius);

}
