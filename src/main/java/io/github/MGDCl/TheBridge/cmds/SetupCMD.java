package io.github.MGDCl.TheBridge.cmds;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.database.PlayerStat;
import io.github.MGDCl.TheBridge.fanciful.FancyMessage;
import io.github.MGDCl.TheBridge.game.GameDuo;
import io.github.MGDCl.TheBridge.game.GameFour;
import io.github.MGDCl.TheBridge.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SetupCMD implements CommandExecutor {

    TheBridge plugin;

    public SetupCMD(TheBridge plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player) sender;
            if (args.length < 1) {
                if (p.hasPermission("bridges.admin"))
                    sendHelp(p);
                else
                    sendPlayerCommands(p);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "hotbar":
                    if (!p.hasPermission("bridges.hotbar")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (plugin.getConfig().getString("kit") == null) {
                        p.sendMessage(plugin.getLang().get("messages.noHaveKit"));
                        return true;
                    }
                    plugin.getHotbar().createHotbarMenu(p);
                    break;
                case "archievements":
                    if (!p.hasPermission("bridges.archievements")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (plugin.isArchiDisabled()) {
                        p.sendMessage(plugin.getLang().get("messages.archiDisabled"));
                        return true;
                    }
                    if (args.length < 1) {
                        sendHelp(p);
                        return true;
                    }
                    if (plugin.getGm().getGameByPlayer(p) != null || plugin.getGm().getGameFourByPlayer(p) != null) {
                        p.sendMessage(plugin.getLang().get("messages.alreadyGame"));
                        return true;
                    }
                    plugin.getArchimenu().createArchievementsMenu(p, 1);
                    break;
                case "ach":
                    if (!p.hasPermission("bridges.archievements")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (plugin.isArchiDisabled()) {
                        p.sendMessage(plugin.getLang().get("messages.archiDisabled"));
                        return true;
                    }
                    if (args.length < 1) {
                        sendHelp(p);
                        return true;
                    }
                    if (plugin.getGm().getGameByPlayer(p) != null || plugin.getGm().getGameFourByPlayer(p) != null) {
                        p.sendMessage(plugin.getLang().get("messages.alreadyGame"));
                        return true;
                    }
                    plugin.getArchimenu().createArchievementsMenu(p, 1);
                    break;
                case "menu":
                    if (!p.hasPermission("bridges.menu")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 1) {
                        sendHelp(p);
                        return true;
                    }
                    if (plugin.getGm().getGameByPlayer(p) != null || plugin.getGm().getGameFourByPlayer(p) != null) {
                        p.sendMessage(plugin.getLang().get("messages.alreadyGame"));
                        return true;
                    }
                    plugin.getGmu().openSelectTypeMenu(p);
                    break;
                case "shop":
                    if (!p.hasPermission("bridges.shop")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 1) {
                        sendHelp(p);
                        return true;
                    }
                    if (plugin.getGm().getGameByPlayer(p) != null || plugin.getGm().getGameFourByPlayer(p) != null) {
                        p.sendMessage(plugin.getLang().get("messages.alreadyGame"));
                        return true;
                    }
                    plugin.getShop().openShopMenu(p);
                    break;
                case "cages":
                    if (!p.hasPermission("bridges.cages")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 1) {
                        sendHelp(p);
                        return true;
                    }
                    if (plugin.getGm().getGameByPlayer(p) != null || plugin.getGm().getGameFourByPlayer(p) != null) {
                        p.sendMessage(plugin.getLang().get("messages.alreadyGame"));
                        return true;
                    }
                    if (plugin.isCage())
                        plugin.getGlam().createGlassMenu(p);
                    else
                        p.sendMessage(plugin.getLang().get("messages.noEnabedGlass"));
                    break;
                case "random":
                    if (!p.hasPermission("bridges.random")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 2) {
                        sendHelp(p);
                        return true;
                    }
                    if (plugin.getGm().getGameByPlayer(p) != null || plugin.getGm().getGameFourByPlayer(p) != null) {
                        p.sendMessage(plugin.getLang().get("messages.alreadyGame"));
                        return true;
                    }
                    switch (args[1].toLowerCase()) {
                        case "normal":
                            for (final GameDuo game : plugin.getGm().getGames()) {
                                GameDuo g = null;
                                if (game.isState(GameDuo.State.FINISH) || game.isState(GameDuo.State.INGAME)
                                        || game.isState(GameDuo.State.RESTARTING) || game.isState(GameDuo.State.PREGAME))
                                    continue;
                                if (game.getPlayers() == game.getMax())
                                    continue;
                                if (game.getPlayers() < game.getMax())
                                    g = game;
                                if (g != null) {
                                    plugin.getGm().addPlayerGame(p, game);
                                    return true;
                                } else
                                    p.sendMessage(plugin.getLang().get("messages.noGames"));
                            }
                            break;
                        case "four":
                            for (final GameFour game : plugin.getGm().getGamesFour()) {
                                GameFour g = null;
                                if (game.isState(GameFour.FState.FINISH) || game.isState(GameFour.FState.INGAME)
                                        || game.isState(GameFour.FState.RESTARTING) || game.isState(GameFour.FState.PREGAME))
                                    continue;
                                if (game.getPlayers().size() == game.getMax())
                                    continue;
                                if (game.getPlayers().size() < game.getMax())
                                    g = game;
                                if (g != null) {
                                    plugin.getGm().addPlayerGameFour(p, game);
                                    return true;
                                } else
                                    p.sendMessage(plugin.getLang().get("messages.noGames"));
                            }
                            break;
                    }
                    break;
                case "settop":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 2) {
                        sendHelp(p);
                        return true;
                    }
                    switch (args[1].toLowerCase()) {
                        case "normal":
                            if (args.length < 3) {
                                sendHelp(p);
                                return true;
                            }
                            switch (args[2].toLowerCase()) {
                                case "kills":
                                    final String kills = plugin.getLm().getLocationString(p.getLocation());
                                    plugin.getConfig().set("tops.normal.kills", kills);
                                    plugin.saveConfig();
                                    plugin.getLm().reloadLocations();
                                    p.sendMessage("§aNormal TOP Kills setted.");
                                    break;
                                case "wins":
                                    final String wins = plugin.getLm().getLocationString(p.getLocation());
                                    plugin.getConfig().set("tops.normal.wins", wins);
                                    plugin.saveConfig();
                                    plugin.getLm().reloadLocations();
                                    p.sendMessage("§aNormal TOP Wins setted.");
                                    break;
                                case "goals":
                                    final String goals3 = plugin.getLm().getLocationString(p.getLocation());
                                    plugin.getConfig().set("tops.normal.goals", goals3);
                                    plugin.saveConfig();
                                    plugin.getLm().reloadLocations();
                                    p.sendMessage("§aNormal TOP Goals setted.");
                                    break;
                            }
                            break;
                        case "four":
                            switch (args[2].toLowerCase()) {
                                case "kills":
                                    final String kills2 = plugin.getLm().getLocationString(p.getLocation());
                                    plugin.getConfig().set("tops.four.kills", kills2);
                                    plugin.saveConfig();
                                    plugin.getLm().reloadLocations();
                                    p.sendMessage("§aFour TOP Kills setted.");
                                    break;
                                case "wins":
                                    final String wins2 = plugin.getLm().getLocationString(p.getLocation());
                                    plugin.getConfig().set("tops.four.wins", wins2);
                                    plugin.saveConfig();
                                    plugin.getLm().reloadLocations();
                                    p.sendMessage("§aFour TOP Wins setted.");
                                    break;
                                case "goals":
                                    final String goals2 = plugin.getLm().getLocationString(p.getLocation());
                                    plugin.getConfig().set("tops.four.goals", goals2);
                                    plugin.saveConfig();
                                    plugin.getLm().reloadLocations();
                                    p.sendMessage("§aFour TOP Goals setted.");
                                    break;
                            }
                            break;
                    }
                    break;
                case "setstats":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    final String stats = plugin.getLm().getLocationString(p.getLocation());
                    plugin.getConfig().set("stats", stats);
                    plugin.saveConfig();
                    plugin.getLm().reloadLocations();
                    p.sendMessage("§aStats setted.");
                    break;
                case "help":
                    if (!p.hasPermission("bridges.admin")) {
                        if (args.length < 2) {
                            sendPlayerCommands(p);
                            return true;
                        }
                        return true;
                    }
                    if (args.length < 2) {
                        sendHelp(p);
                        return true;
                    }
                    switch (args[1].toLowerCase()) {
                        case "player":
                            sendPlayerCommands(p);
                            break;
                        case "lobby":
                            sendLobbyCommands(p);
                            break;
                        case "all":
                            sendAllTypesCommands(p);
                            break;
                        case "normal":
                            sendNormalTypeCommands(p);
                            break;
                        case "four":
                            sendFourTypeCommands(p);
                            break;
                    }
                    break;
                case "leave":
                    if (plugin.getGm().getGameByPlayer(p) == null && plugin.getGm().getGameFourByPlayer(p) == null) {
                        p.sendMessage(plugin.getLang().get("messages.noGame"));
                        return true;
                    }
                    if (plugin.getGm().getGameByPlayer(p) != null) {
                        final GameDuo game = plugin.getGm().getGameByPlayer(p);
                        plugin.getGm().removePlayerGame(p, game);
                        return true;
                    }
                    if (plugin.getGm().getGameFourByPlayer(p) != null) {
                        final GameFour game = plugin.getGm().getGameFourByPlayer(p);
                        plugin.getGm().removePlayerGameFour(p, game);
                        return true;
                    }
                    break;
                case "setmainlobby":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    plugin.getLm().setMainLobby(p);
                    break;
                case "setkit":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    final Inventory inv = Bukkit.getServer().createInventory(null, 36);
                    for (int i = 0; i < 35; i++) {
                        if (p.getInventory().getItem(i) == null
                                || p.getInventory().getItem(i).getType() == Material.AIR) {
                            inv.setItem(i, new ItemStack(Material.BARRIER, 1, (short) 0));
                            continue;
                        }
                        inv.setItem(i, p.getInventory().getItem(i));
                    }
                    final String kit = plugin.getKit().toBase64(inv);
                    plugin.getConfig().set("kit", kit);
                    plugin.saveConfig();
                    plugin.getGm().reloadKit();
                    p.sendMessage("§aKit saved and reloaded.");
                    break;
                case "join":
                    if (!p.hasPermission("bridges.join")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 2) {
                        sendHelp(p);
                        return true;
                    }
                    final String name100 = args[1];
                    if (!plugin.getFm().arenaExists(name100)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    if (plugin.getGm().getGameByPlayer(p) != null || plugin.getGm().getGameFourByPlayer(p) != null) {
                        p.sendMessage(plugin.getLang().get("messages.alreadyGame"));
                        return true;
                    }
                    if (plugin.getGm().getGameByName(name100) != null) {
                        final GameDuo game = plugin.getGm().getGameByName(name100);
                        if (game.getPlayers() >= game.getMax()) {
                            p.sendMessage(plugin.getLang().get("messages.gameFull"));
                            return true;
                        }
                        plugin.getGm().addPlayerGame(p, game);
                        return true;
                    }
                    if (plugin.getGm().getGameFourByName(name100) != null) {
                        final GameFour game = plugin.getGm().getGameFourByName(name100);
                        if (game.getPlayers().size() >= game.getMax()) {
                            p.sendMessage(plugin.getLang().get("messages.gameFull"));
                            return true;
                        }
                        plugin.getGm().addPlayerGameFour(p, game);
                        return true;
                    }
                    break;
                case "create":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 2) {
                        sendHelp(p);
                        return true;
                    }
                    final String name = args[1];
                    if (Bukkit.getWorld(name) != null)
                        if (Bukkit.getWorlds().contains(Bukkit.getWorld(name))) {
                            plugin.getFm().createNewFile(name);
                            final File c = new File(plugin.getDataFolder() + "/arenas", name + ".yml");
                            final YamlConfiguration config = YamlConfiguration.loadConfiguration(c);
                            config.set("enabled", false);
                            config.set("name", name);
                            try {
                                config.save(c);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("§aArena §e" + name + "§a has been created.");
                            return true;
                        }
                    if (plugin.getFm().arenaExists(name)) {
                        p.sendMessage("§cThis arena already exists.");
                        return true;
                    }
                    plugin.getWc().createEmptyWorld(p, name);
                    plugin.getFm().createNewFile(name);
                    final File c = new File(plugin.getDataFolder() + "/arenas", name + ".yml");
                    final YamlConfiguration config = YamlConfiguration.loadConfiguration(c);
                    config.set("enabled", false);
                    config.set("name", name);
                    try {
                        config.save(c);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage("§aArena §e" + name + "§a has been created.");
                    break;
                case "save":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    plugin.getWc().copyWorld(p.getWorld());
                    p.sendMessage("§aArena §e" + p.getWorld().getName() + "§a has been saved.");
                    break;
                case "wand":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    final ItemStack wand = ItemBuilder.item(Material.BLAZE_ROD, 1, (short) 0, "§eSetup TheBridge", "");
                    p.getInventory().addItem(wand);
                    break;
                case "settype":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 3) {
                        sendHelp(p);
                        return true;
                    }
                    final String name1 = args[1];
                    switch (args[2].toLowerCase()) {
                        case "four":
                            final String type1 = args[2].toLowerCase();
                            if (!plugin.getFm().arenaExists(name1)) {
                                p.sendMessage("§cThis arena doest exists.");
                                return true;
                            }
                            final File c1 = new File(plugin.getDataFolder() + "/arenas", name1 + ".yml");
                            final YamlConfiguration config1 = YamlConfiguration.loadConfiguration(c1);
                            config1.set("mode", type1);
                            try {
                                config1.save(c1);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("§aArena type of §e" + name1 + "§a has been setted to §e" + type1 + "§a.");
                            break;
                        case "normal":
                            final String type2 = args[2].toLowerCase();
                            if (!plugin.getFm().arenaExists(name1)) {
                                p.sendMessage("§cThis arena doest exists.");
                                return true;
                            }
                            final File c2 = new File(plugin.getDataFolder() + "/arenas", name1 + ".yml");
                            final YamlConfiguration config2 = YamlConfiguration.loadConfiguration(c2);
                            config2.set("mode", type2);
                            try {
                                config2.save(c2);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("§aArena type of §e" + name1 + "§a has been setted to §e" + type2 + "§a.");
                            break;
                    }
                    break;
                case "setspawn":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 3) {
                        sendHelp(p);
                        return true;
                    }
                    final String name2 = args[1];
                    if (!plugin.getFm().arenaExists(name2)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final File c3 = new File(plugin.getDataFolder() + "/arenas", name2 + ".yml");
                    final YamlConfiguration config3 = YamlConfiguration.loadConfiguration(c3);
                    switch (args[2].toLowerCase()) {
                        case "blue":
                            config3.set("locations.spawns.blue", getLocationString(p.getLocation()));
                            try {
                                config3.save(c3);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("§aSpawn location for §eBLUE §ahas been setted.");
                            break;
                        case "red":
                            config3.set("locations.spawns.red", getLocationString(p.getLocation()));
                            try {
                                config3.save(c3);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("§aSpawn location for §eRED §ahas been setted.");
                            break;
                        case "green":
                            config3.set("locations.spawns.green", getLocationString(p.getLocation()));
                            try {
                                config3.save(c3);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("§aSpawn location for §eGREEN §ahas been setted.");
                            break;
                        case "yellow":
                            config3.set("locations.spawns.yellow", getLocationString(p.getLocation()));
                            try {
                                config3.save(c3);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("§aSpawn location for §eYELLOW §ahas been setted.");
                            break;
                    }
                    break;
                case "setrespawn":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 3) {
                        sendHelp(p);
                        return true;
                    }
                    final String name3 = args[1];
                    if (!plugin.getFm().arenaExists(name3)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final File c4 = new File(plugin.getDataFolder() + "/arenas", name3 + ".yml");
                    final YamlConfiguration config4 = YamlConfiguration.loadConfiguration(c4);
                    switch (args[2].toLowerCase()) {
                        case "blue":
                            config4.set("locations.respawns.blue", getLocationString(p.getLocation()));
                            try {
                                config4.save(c4);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("§aRespawn location for §eBLUE §ahas been setted.");
                            break;
                        case "red":
                            config4.set("locations.respawns.red", getLocationString(p.getLocation()));
                            try {
                                config4.save(c4);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("§aRespawn location for §eRED §ahas been setted.");
                            break;
                        case "green":
                            config4.set("locations.respawns.green", getLocationString(p.getLocation()));
                            try {
                                config4.save(c4);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("§aRespawn location for §eGREEN §ahas been setted.");
                            break;
                        case "yellow":
                            config4.set("locations.respawns.yellow", getLocationString(p.getLocation()));
                            try {
                                config4.save(c4);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("§aRespawn location for §eYELLOW §ahas been setted.");
                            break;
                    }
                    break;
                case "setportal":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 3) {
                        sendHelp(p);
                        return true;
                    }
                    final String name4 = args[1];
                    if (!plugin.getFm().arenaExists(name4)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final File c5 = new File(plugin.getDataFolder() + "/arenas", name4 + ".yml");
                    final YamlConfiguration config5 = YamlConfiguration.loadConfiguration(c5);
                    if (!plugin.getSm().getSelect().containsKey(p)) {
                        p.sendMessage("§cYou have not selected anything.");
                        return true;
                    }
                    if (!plugin.getSm().getSelect().get(p).containsKey("MIN")) {
                        p.sendMessage("§cYou have not selected the minimum.");
                        return true;
                    }
                    if (!plugin.getSm().getSelect().get(p).containsKey("MAX")) {
                        p.sendMessage("§cYou have not selected the minimum.");
                        return true;
                    }
                    switch (args[2].toLowerCase()) {
                        case "red":
                            final ArrayList<String> portal = new ArrayList<>();
                            final Location location = plugin.getSm().getSelect().get(p).get("MIN");
                            final Location location2 = plugin.getSm().getSelect().get(p).get("MAX");
                            final int n = Math.min(location.getBlockX(), location2.getBlockX());
                            final int n2 = Math.max(location.getBlockX(), location2.getBlockX());
                            final int n3 = Math.min(location.getBlockZ(), location2.getBlockZ());
                            final int n4 = Math.max(location.getBlockZ(), location2.getBlockZ());
                            final int n5 = Math.min(location.getBlockY(), location2.getBlockY());
                            final int n6 = Math.max(location.getBlockY(), location2.getBlockY());
                            for (int i = n; i <= n2; ++i)
                                for (int j = n5; j <= n6; ++j)
                                    for (int k = n3; k <= n4; ++k) {
                                        final Location location3 = new Location(p.getWorld(), i, j, k);
                                        if (location3.getBlock().getType() == Material.AIR)
                                            continue;
                                        if (location3.getBlock().getType() == Material.OBSIDIAN)
                                            portal.add(getLocationString(location3));
                                    }
                            config5.set("locations.portal.red", portal);
                            config5.set("locations.holograms.red", getLocationString(p.getLocation()));
                            try {
                                config5.save(c5);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            plugin.getSm().getSelect().remove(p);
                            p.sendMessage("§aPortal location for §eRED §ahas been setted.");
                            break;
                        case "blue":
                            final ArrayList<String> bportal = new ArrayList<>();
                            final Location blocation = plugin.getSm().getSelect().get(p).get("MIN");
                            final Location blocation2 = plugin.getSm().getSelect().get(p).get("MAX");
                            final int bn = Math.min(blocation.getBlockX(), blocation2.getBlockX());
                            final int bn2 = Math.max(blocation.getBlockX(), blocation2.getBlockX());
                            final int bn3 = Math.min(blocation.getBlockZ(), blocation2.getBlockZ());
                            final int bn4 = Math.max(blocation.getBlockZ(), blocation2.getBlockZ());
                            final int bn5 = Math.min(blocation.getBlockY(), blocation2.getBlockY());
                            final int bn6 = Math.max(blocation.getBlockY(), blocation2.getBlockY());
                            for (int i = bn; i <= bn2; ++i)
                                for (int j = bn5; j <= bn6; ++j)
                                    for (int k = bn3; k <= bn4; ++k) {
                                        final Location location3 = new Location(p.getWorld(), i, j, k);
                                        if (p.getWorld().getBlockAt(location3).getType() == Material.OBSIDIAN)
                                            bportal.add(getLocationString(location3));
                                    }
                            config5.set("locations.portal.blue", bportal);
                            config5.set("locations.holograms.blue", getLocationString(p.getLocation()));
                            try {
                                config5.save(c5);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            plugin.getSm().getSelect().remove(p);
                            p.sendMessage("§aPortal location for §eBLUE §ahas been setted.");
                            break;
                        case "green":
                            final ArrayList<String> gportal = new ArrayList<>();
                            final Location glocation = plugin.getSm().getSelect().get(p).get("MIN");
                            final Location glocation2 = plugin.getSm().getSelect().get(p).get("MAX");
                            final int gn = Math.min(glocation.getBlockX(), glocation2.getBlockX());
                            final int gn2 = Math.max(glocation.getBlockX(), glocation2.getBlockX());
                            final int gn3 = Math.min(glocation.getBlockZ(), glocation2.getBlockZ());
                            final int gn4 = Math.max(glocation.getBlockZ(), glocation2.getBlockZ());
                            final int gn5 = Math.min(glocation.getBlockY(), glocation2.getBlockY());
                            final int gn6 = Math.max(glocation.getBlockY(), glocation2.getBlockY());
                            for (int i = gn; i <= gn2; ++i)
                                for (int j = gn5; j <= gn6; ++j)
                                    for (int k = gn3; k <= gn4; ++k) {
                                        final Location location3 = new Location(p.getWorld(), i, j, k);
                                        if (p.getWorld().getBlockAt(location3).getType() == Material.OBSIDIAN)
                                            gportal.add(getLocationString(location3));
                                    }
                            config5.set("locations.portal.green", gportal);
                            config5.set("locations.holograms.green", getLocationString(p.getLocation()));
                            try {
                                config5.save(c5);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            plugin.getSm().getSelect().remove(p);
                            p.sendMessage("§aPortal location for §eGREEN §ahas been setted.");
                            break;
                        case "yellow":
                            final ArrayList<String> yportal = new ArrayList<>();
                            final Location ylocation = plugin.getSm().getSelect().get(p).get("MIN");
                            final Location ylocation2 = plugin.getSm().getSelect().get(p).get("MAX");
                            final int yn = Math.min(ylocation.getBlockX(), ylocation2.getBlockX());
                            final int yn2 = Math.max(ylocation.getBlockX(), ylocation2.getBlockX());
                            final int yn3 = Math.min(ylocation.getBlockZ(), ylocation2.getBlockZ());
                            final int yn4 = Math.max(ylocation.getBlockZ(), ylocation2.getBlockZ());
                            final int yn5 = Math.min(ylocation.getBlockY(), ylocation2.getBlockY());
                            final int yn6 = Math.max(ylocation.getBlockY(), ylocation2.getBlockY());
                            for (int i = yn; i <= yn2; ++i)
                                for (int j = yn5; j <= yn6; ++j)
                                    for (int k = yn3; k <= yn4; ++k) {
                                        final Location location3 = new Location(p.getWorld(), i, j, k);
                                        if (p.getWorld().getBlockAt(location3).getType() == Material.OBSIDIAN)
                                            yportal.add(getLocationString(location3));
                                    }
                            config5.set("locations.portal.yellow", yportal);
                            config5.set("locations.holograms.yellow", getLocationString(p.getLocation()));
                            try {
                                config5.save(c5);
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            plugin.getSm().getSelect().remove(p);
                            p.sendMessage("§aPortal location for §eYELLOW §ahas been setted.");
                            break;
                    }
                    break;
                case "setbuild":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 2) {
                        sendHelp(p);
                        return true;
                    }
                    final String name5 = args[1];
                    if (!plugin.getFm().arenaExists(name5)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final File c6 = new File(plugin.getDataFolder() + "/arenas", name5 + ".yml");
                    final YamlConfiguration config6 = YamlConfiguration.loadConfiguration(c6);
                    if (!plugin.getSm().getSelect().containsKey(p)) {
                        p.sendMessage("§cYou have not selected anything.");
                        return true;
                    }
                    if (!plugin.getSm().getSelect().get(p).containsKey("MIN")) {
                        p.sendMessage("§cYou have not selected the minimum.");
                        return true;
                    }
                    if (!plugin.getSm().getSelect().get(p).containsKey("MAX")) {
                        p.sendMessage("§cYou have not selected the minimum.");
                        return true;
                    }
                    final Location l1 = plugin.getSm().getSelect().get(p).get("MIN");
                    final Location l2 = plugin.getSm().getSelect().get(p).get("MAX");
                    config6.set("locations.build.min", getLocationString(l1));
                    config6.set("locations.build.max", getLocationString(l2));
                    try {
                        config6.save(c6);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    plugin.getSm().getSelect().remove(p);
                    p.sendMessage("§aBuild location has been setted.");
                    break;
                case "setlobby":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 2) {
                        sendHelp(p);
                        return true;
                    }
                    final String name6 = args[1];
                    if (!plugin.getFm().arenaExists(name6)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final File c7 = new File(plugin.getDataFolder() + "/arenas", name6 + ".yml");
                    final YamlConfiguration config7 = YamlConfiguration.loadConfiguration(c7);
                    config7.set("locations.lobby", getLocationString(p.getLocation()));
                    try {
                        config7.save(c7);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage("§aLobby location has been setted.");
                    break;
                case "setspect":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 2) {
                        sendHelp(p);
                        return true;
                    }
                    final String name7 = args[1];
                    if (!plugin.getFm().arenaExists(name7)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final File c8 = new File(plugin.getDataFolder() + "/arenas", name7 + ".yml");
                    final YamlConfiguration config8 = YamlConfiguration.loadConfiguration(c8);
                    config8.set("locations.spect", getLocationString(p.getLocation()));
                    try {
                        config8.save(c8);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage("§aSpect location has been setted.");
                    break;
                case "setmin":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 3) {
                        sendHelp(p);
                        return true;
                    }
                    final String name8 = args[1];
                    if (!plugin.getFm().arenaExists(name8)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final int min = Integer.parseInt(args[2]);
                    final File c9 = new File(plugin.getDataFolder() + "/arenas", name8 + ".yml");
                    final YamlConfiguration config9 = YamlConfiguration.loadConfiguration(c9);
                    config9.set("minPlayers", min);
                    try {
                        config9.save(c9);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage("§aMin players has been setted. To §e" + min + "§a.");
                    break;
                case "setstarting":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 3) {
                        sendHelp(p);
                        return true;
                    }
                    final String name13 = args[1];
                    if (!plugin.getFm().arenaExists(name13)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final int starting = Integer.parseInt(args[2]);
                    final File c13 = new File(plugin.getDataFolder() + "/arenas", name13 + ".yml");
                    final YamlConfiguration config13 = YamlConfiguration.loadConfiguration(c13);
                    config13.set("timers.starting", starting);
                    try {
                        config13.save(c13);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage("§aStarting time has been setted. To §e" + starting + "§a.");
                    break;
                case "setprestarting":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 3) {
                        sendHelp(p);
                        return true;
                    }
                    final String name14 = args[1];
                    if (!plugin.getFm().arenaExists(name14)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final int prestart = Integer.parseInt(args[2]);
                    final File c14 = new File(plugin.getDataFolder() + "/arenas", name14 + ".yml");
                    final YamlConfiguration config14 = YamlConfiguration.loadConfiguration(c14);
                    config14.set("timers.prestart", prestart);
                    try {
                        config14.save(c14);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage("§aPrestart time has been setted. To §e" + prestart + "§a.");
                    break;
                case "setrestart":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 3) {
                        sendHelp(p);
                        return true;
                    }
                    final String name15 = args[1];
                    if (!plugin.getFm().arenaExists(name15)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final int restart = Integer.parseInt(args[2]);
                    final File c15 = new File(plugin.getDataFolder() + "/arenas", name15 + ".yml");
                    final YamlConfiguration config15 = YamlConfiguration.loadConfiguration(c15);
                    config15.set("timers.restart", restart);
                    try {
                        config15.save(c15);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage("§aRestart time has been setted. To §e" + restart + "§a.");
                    break;
                case "setteamsize":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 3) {
                        sendHelp(p);
                        return true;
                    }
                    final String name9 = args[1];
                    if (!plugin.getFm().arenaExists(name9)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final int teamSize = Integer.parseInt(args[2]);
                    final File c10 = new File(plugin.getDataFolder() + "/arenas", name9 + ".yml");
                    final YamlConfiguration config10 = YamlConfiguration.loadConfiguration(c10);
                    config10.set("teamSize", teamSize);
                    try {
                        config10.save(c10);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage("§aTeam size has been setted. To §e" + teamSize + "§a.");
                    break;
                case "setmaxgoals":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 3) {
                        sendHelp(p);
                        return true;
                    }
                    final String name10 = args[1];
                    if (!plugin.getFm().arenaExists(name10)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final int goals = Integer.parseInt(args[2]);
                    final File c11 = new File(plugin.getDataFolder() + "/arenas", name10 + ".yml");
                    final YamlConfiguration config11 = YamlConfiguration.loadConfiguration(c11);
                    config11.set("maxGoals", goals);
                    try {
                        config11.save(c11);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage("§aMax goals has been setted. To §e" + goals + "§a.");
                    break;
                case "setstartlife":
                    if (!p.hasPermission("bridges.admin")) {
                        p.sendMessage(plugin.getLang().get("messages.noPermission"));
                        return true;
                    }
                    if (args.length < 3) {
                        sendHelp(p);
                        return true;
                    }
                    final String name11 = args[1];
                    if (!plugin.getFm().arenaExists(name11)) {
                        p.sendMessage("§cThis arena doest exists.");
                        return true;
                    }
                    final int lifes = Integer.parseInt(args[2]);
                    final File c12 = new File(plugin.getDataFolder() + "/arenas", name11 + ".yml");
                    final YamlConfiguration config12 = YamlConfiguration.loadConfiguration(c12);
                    config12.set("defaultLife", lifes);
                    try {
                        config12.save(c12);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage("§aDefault lifes has been setted. To §e" + lifes + "§a.");
                    break;
            }
        } else {
            if (args.length < 1) {
                sendStatsHelp(sender);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "player":
                    if (args.length < 4) {
                        sendStatsHelp(sender);
                        return true;
                    }
                    Player pl = Bukkit.getPlayer(args[1]);
                    if (pl == null) {
                        sender.sendMessage("Player with that name not found");
                        return true;
                    }
                    PlayerStat ps = PlayerStat.getPlayerStat(pl);
                    if (ps == null) {
                        sender.sendMessage("Data for that player not found!");
                        return true;
                    }
                    switch (args[2].toLowerCase()) {
                        case "give":
                            if (args[3].equalsIgnoreCase("exp")) {
                                ps.addXP(Integer.valueOf(args[4]));
                                sender.sendMessage("Exp updated");
                                return true;
                            }
                            if (args[3].equalsIgnoreCase("coins")) {
                                ps.addCoins(Integer.valueOf(args[4]));
                                sender.sendMessage("Coins updated");
                                return true;
                            }
                            break;
                        case "take":
                            if (args[3].equalsIgnoreCase("exp")) {
                                ps.removeXP(Integer.valueOf(args[4]));
                                sender.sendMessage("Exp updated");
                                return true;
                            }
                            if (args[3].equalsIgnoreCase("coins")) {
                                ps.removeCoins(Integer.valueOf(args[4]));
                                sender.sendMessage("Coins updated");
                                return true;
                            }
                            break;
                        case "set":
                            if (args[3].equalsIgnoreCase("exp")) {
                                ps.setXp(Integer.valueOf(args[4]));
                                sender.sendMessage("Exp updated");
                                return true;
                            }
                            if (args[3].equalsIgnoreCase("coins")) {
                                ps.setCoins(Integer.valueOf(args[4]));
                                sender.sendMessage("Coins updated");
                                return true;
                            }
                            break;
                    }
                    break;
            }
        }
        return false;

    }

    private void sendStatsHelp(CommandSender p) {
        p.sendMessage("------------TheBridge---------------");
        p.sendMessage("bridges player <player> <give/take/set> <exp/coins> <ammount>");
        p.sendMessage("------------------------------------");
    }

    public void sendHelp(Player p) {
        p.sendMessage("§7§m---------------------------");
        new FancyMessage("§a§lPLAYER COMMANDS").tooltip("§bShow player commands. §7(Click here)")
                .command("/bridges help player").send(p);
        if (p.hasPermission("bridges.admin")) {
            new FancyMessage("§a§lSETUP COMMANDS - ALL TYPES").tooltip("§bShow commands for all types. §7(Click here)")
                    .command("/bridges help all").send(p);
            new FancyMessage("§a§lSETUP COMMANDS - LOBBY").tooltip("§bShow commands for lobby. §7(Click here)")
                    .command("/bridges help lobby").send(p);
            new FancyMessage("§a§lSETUP COMMANDS - NORMAL").tooltip("§bShow commands for normal type. §7(Click here)")
                    .command("/bridges help normal").send(p);
            new FancyMessage("§a§lSETUP COMMANDS - FOUR").tooltip("§bShow commands for four type. §7(Click here)")
                    .command("/bridges help four").send(p);
        }
        p.sendMessage("§7§m---------------------------");
    }

    public void sendLobbyCommands(Player p) {
        p.sendMessage("§7§m---------------------------");
        new FancyMessage("§b/bridges settop {normal/four} {kills/wins/goals}").tooltip("§eSet tops location.").send(p);
        new FancyMessage("§b/bridges setstats").tooltip("§eSet stats location.").send(p);
        new FancyMessage("§b/bridges setkit").tooltip("§eSet default kit.").send(p);
        new FancyMessage("§b/bridges setmainlobby").tooltip("§eSet main lobby location.").send(p);
        p.sendMessage("§7§m---------------------------");
    }

    public void sendPlayerCommands(Player p) {
        p.sendMessage("§7§m---------------------------");
        new FancyMessage("§b/bridges join <arena>").tooltip("§eJoin a game.").send(p);
        new FancyMessage("§b/bridges menu").tooltip("§eOpen the arena selector.").send(p);
        new FancyMessage("§b/bridges ach").tooltip("§eOpen the achievements menu.").send(p);
        new FancyMessage("§b/bridges cages").tooltip("§eOpen the cages menu.").send(p);
        new FancyMessage("§b/bridges random [normal/four]").tooltip("§eJoin a random and highest game.").send(p);
        new FancyMessage("§b/bridges leave").tooltip("§eLeave the game.").send(p);
        p.sendMessage("§7§m---------------------------");
    }

    public void sendAllTypesCommands(Player p) {
        p.sendMessage("§7§m---------------------------");
        new FancyMessage("§b/bridges wand").tooltip("§eGives you the rod to select.").send(p);
        new FancyMessage("§b/bridges create <name>").tooltip("§eCreate new arena in a empty World.").send(p);
        new FancyMessage("§b/bridges settype <name> NORMAL/FOUR").tooltip("§eSet type of Arena.").send(p);
        new FancyMessage("§b/bridges setstarting <name> <amount>").tooltip("§eSet starting time to arena.").send(p);
        new FancyMessage("§b/bridges setprestarting <name> <amount>").tooltip("§eSet prestarting time to arena.")
                .send(p);
        new FancyMessage("§b/bridges setrestart <name> <amount>").tooltip("§eSet restart time to arena.").send(p);
        new FancyMessage("§b/bridges setspect <name>").tooltip("§eSet Spect location.").send(p);
        new FancyMessage("§b/bridges setlobby <name>").tooltip("§eSet Lobby location.").send(p);
        new FancyMessage("§b/bridges setteamsize <name> <amount>").tooltip("§eSet team size to arena.").send(p);
        new FancyMessage("§b/bridges setmin <name> <amount>").tooltip("§eSet min players to arena.").send(p);
        new FancyMessage("§b/bridges setbuild <name>")
                .tooltip("§eSet build location. §c(You first need has selected MIN and MAX locations)").send(p);
        new FancyMessage("§b/bridges save <name>").tooltip("§eSave the sand in the Maps folder.").send(p);
        p.sendMessage("§7§m---------------------------");
    }

    public void sendNormalTypeCommands(Player p) {
        p.sendMessage("§7§m---------------------------");
        new FancyMessage("§b/bridges setspawn <name> BLUE/RED").tooltip("§eSet the spawn point of the cage.").send(p);
        new FancyMessage("§b/bridges setrespawn <name> BLUE/RED").tooltip("§eSet the spawn point of respawn.").send(p);
        new FancyMessage("§b/bridges setportal <name> BLUE/RED")
                .tooltip("§eSet portal of team. §c(You need has selected MIN and MAX locations)").send(p);
        new FancyMessage("§b/bridges setmaxgoals <name> <amount>").tooltip("§eSet max goals to arena.").send(p);
        p.sendMessage("§7§m---------------------------");
    }

    public void sendFourTypeCommands(Player p) {
        p.sendMessage("§7§m---------------------------");
        new FancyMessage("§b/bridges setspawn <name> BLUE/RED/GREEN/YELLOW")
                .tooltip("§eSet the spawn point of the cage.").send(p);
        new FancyMessage("§b/bridges setrespawn <name> BLUE/RED/GREEN/YELLOW")
                .tooltip("§eSet the spawn point of respawn.").send(p);
        new FancyMessage("§b/bridges setportal <name> BLUE/RED/GREEN/YELLOW")
                .tooltip("§eSet portal of team. §c(You need has selected MIN and MAX locations)").send(p);
        new FancyMessage("§b/bridges setstartlife <name> <amount>").tooltip("§eSet start lifes to arena.").send(p);
        p.sendMessage("§7§m---------------------------");
    }

    public String getLocationString(Location loc) {
        final String world = loc.getWorld().getName();
        final double x = loc.getX();
        final double y = loc.getY();
        final double z = loc.getZ();
        final float yaw = loc.getYaw();
        final float pitch = loc.getPitch();
        return world + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
    }

}
