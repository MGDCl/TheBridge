package io.github.MGDCl.TheBridge.controllers;

import io.github.MGDCl.TheBridge.TheBridge;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

public class WorldController {

    public TheBridge plugin;

    public WorldController(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void resetWorld(String name) {
        unloadWorld(name);
        copyWorld(new File(plugin.getDataFolder() + "/maps", name), name);
    }

    protected void unloadWorld(String world) {
        if (Bukkit.getWorld(world) == null)
            return;
        Bukkit.getServer().unloadWorld(world, false);
    }

    public World createEmptyWorld(Player p, String name) {
        if (Bukkit.getWorld(name) == null) {
            World world = null;
            final boolean loaded = loadWorld(name, World.Environment.NORMAL);
            if (loaded)
                world = Bukkit.getWorld(name);
            if (world != null) {
                world.getBlockAt(0, 80, 0).setType(Material.STONE);
                p.teleport(new Location(world, 0, 82, 0));
                p.sendMessage(plugin.getLang().get("setup.worldCreated").replaceAll("<name>", world.getName()));
                return world;
            }
        }
        return null;
    }

    public boolean loadWorld(String worldName, World.Environment environment) {
        boolean loaded = false;
        if (Bukkit.getWorld(worldName) != null) {
            final World world = Bukkit.getWorld(worldName);
            world.setDifficulty(Difficulty.NORMAL);
            world.setSpawnFlags(true, true);
            world.setPVP(true);
            world.setStorm(false);
            world.setThundering(false);
            world.setWeatherDuration(Integer.MAX_VALUE);
            world.setKeepSpawnInMemory(false);
            world.setTicksPerAnimalSpawns(1);
            world.setTicksPerMonsterSpawns(1);
            world.setAutoSave(false);
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("mobGriefing", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("showDeathMessages", "false");
            return true;
        }
        final WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.environment(environment);
        worldCreator.generateStructures(false);
        worldCreator.generator(plugin.getNms().getChunkGenerator());
        final World world = worldCreator.createWorld();
        world.setDifficulty(Difficulty.NORMAL);
        world.setSpawnFlags(true, true);
        world.setPVP(true);
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(Integer.MAX_VALUE);
        world.setKeepSpawnInMemory(false);
        world.setTicksPerAnimalSpawns(1);
        world.setTicksPerMonsterSpawns(1);
        world.setAutoSave(false);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("showDeathMessages", "false");
        for(final World w : plugin.getServer().getWorlds())
            if(w.getName().equals(world.getName())) {
                loaded = true;
                break;
            }
        return loaded;
    }

    protected void generate() {

    }

    public void copyWorld(World world) {
        world.save();
        copyFileStructure(world.getWorldFolder(), new File(plugin.getDataFolder() + "/maps/" + world.getName()));
    }

    protected void copyWorld(File originalWorld, String newWorldName) {
        copyFileStructure(originalWorld, new File(Bukkit.getWorldContainer(), newWorldName));
        final WorldCreator worldc = new WorldCreator(newWorldName);
        worldc.generateStructures(false);
        worldc.generator(plugin.getNms().getChunkGenerator());
        worldc.createWorld();
        final World world = Bukkit.getWorld(newWorldName);
        world.setTime(500);
        world.setMonsterSpawnLimit(0);
        world.setWeatherDuration(0);
        world.setAnimalSpawnLimit(0);
        world.setAmbientSpawnLimit(0);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("showDeathMessages", "false");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Restarting map " + ChatColor.YELLOW + newWorldName + ChatColor.GREEN + ".");
    }

    protected void copyFileStructure(File source, File target) {
        try {
            final ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if (!ignore.contains(source.getName()))
                if (source.isDirectory()) {
                    if (!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");
                    final String files[] = source.list();
                    for (final String file : files) {
                        final File srcFile = new File(source, file);
                        final File destFile = new File(target, file);
                        copyFileStructure(srcFile, destFile);
                    }
                } else {
                    final InputStream in = new FileInputStream(source);
                    final OutputStream out = new FileOutputStream(target);
                    final byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteWorld(String name) {
        unloadWorld(name);
        final File target = new File (plugin.getServer().getWorldContainer().getAbsolutePath(), name);
        deleteWorld(target);
    }

    protected void deleteWorld(File path) {
        if (path.exists()) {
            final File[] files = path.listFiles();
            if (files != null)
                for (final File file: files)
                    if(file.isDirectory())
                        deleteWorld(file);
                    else
                        file.delete();
        }
    }

}

