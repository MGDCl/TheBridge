package io.github.MGDCl.TheBridge.nms;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.github.MGDCl.TheBridge.packets.ParticleHandler;
import io.github.MGDCl.TheBridge.packets.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class GenericNMS implements NMS {

    @Override
    public ChunkGenerator getChunkGenerator() {
        return new ChunkGenerator() {
            @Override
            public List<BlockPopulator> getDefaultPopulators(World world) {
                return Arrays.asList(new BlockPopulator[0]);
            }

            @Override
            public boolean canSpawn(World world, int x, int z) {
                return true;
            }

            @Override
            public byte[] generate(World world, Random random, int x, int z) {
                return new byte[32768];
            }

            @Override
            public Location getFixedSpawnLocation(World world, Random random) {
                return new Location(world, 0.0D, 83.0D, 0.0D);
            }

            @Override
            public ChunkData generateChunkData(World world, Random random, int cx, int cz, BiomeGrid biome) {
                final ChunkGenerator.ChunkData chunkData = this.createChunkData(world);
                for (int x = 0; x < 16; ++x)
                    for (int z = 0; z < 16; ++z)
                        biome.setBiome(x, z, bm());
                if (0 >= cx << 4 && 0 < cx + 1 << 4 && 0 >= cz << 4 && 0 < cz + 1 << 4)
                    chunkData.setBlock(0, 81, 0, Material.STONE);
                return chunkData;
            }
        };
    }

    private static Biome bm() {
        String serverVersion = null;
        Biome b;
        try {
            serverVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException ex) {
            b = Biome.PLAINS;
        }
        final String s = serverVersion;
        switch (s) {
            case "v1_8_R1":
            case "v1_8_R2":
            case "v1_8_R3": {
                b = Biome.PLAINS;
                break;
            }
            case "v1_9_R1":
            case "v1_13_R1": {
                b = Biome.valueOf("VOID");
                break;
            }
            default: {
                b = Biome.valueOf("THE_VOID");
                break;
            }
        }
        return b;
    }

    @Override
    public void sendTitle(Player player, int fadein, int stay, int fadeout, String title, String subtitle) {
        Reflection.sendTitle(player, fadein, stay, fadeout, title, subtitle);

    }

    @Override
    public void sendActionBar(Player player, String s) {
        Reflection.sendAction(player, s);

    }

    @Override
    public ParticleHandler sendParticle(ParticleHandler.ParticleType type, double speed, int count, double radius) {
        return new ParticleHandler(type, speed, count, radius);
    }

}
