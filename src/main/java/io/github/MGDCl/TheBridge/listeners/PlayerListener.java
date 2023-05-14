package io.github.MGDCl.TheBridge.listeners;

import io.github.MGDCl.TheBridge.TheBridge;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.github.MGDCl.TheBridge.cosmetics.Cage;
import io.github.MGDCl.TheBridge.database.PlayerStat;
import io.github.MGDCl.TheBridge.game.GameDuo;
import io.github.MGDCl.TheBridge.game.GameDuo.State;
import io.github.MGDCl.TheBridge.game.GameFour;
import io.github.MGDCl.TheBridge.game.GameFour.FState;
import io.github.MGDCl.TheBridge.game.InventoryData;
import io.github.MGDCl.TheBridge.packets.ParticleHandler.ParticleType;
import io.github.MGDCl.TheBridge.sings.GameFourSign;
import io.github.MGDCl.TheBridge.sings.GameNormalSign;
import io.github.MGDCl.TheBridge.utils.ItemBuilder;
import io.github.MGDCl.TheBridge.utils.Tagged;
import io.github.MGDCl.TheBridge.utils.Utils;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {

    public static HashMap<Player, Integer> page = new HashMap<>();
    private final ArrayList<Player> bow = new ArrayList<>();
    private final ArrayList<Player> leaving = new ArrayList<>();
    TheBridge plugin;

    public PlayerListener(TheBridge plugin) {
        this.plugin = plugin;
    }

    private BukkitTask task;

    public void sendArrowParticle(Player p, Entity e) {
        if (plugin.getPam().getParticleByName("arrow", PlayerStat.getPlayerStat(p).getArrow_trail()).getTrail()
                .equalsIgnoreCase("EMPTY"))
            return;
        task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            Location loc = e.getLocation();
            if (e.isOnGround() || e.getLocation().getY() < 10 || leaving.contains(p)) {
                task.cancel();
                return;
            }
            createTrail(loc, plugin.getPam().getParticleByName("arrow", PlayerStat.getPlayerStat(p).getArrow_trail()));
        }, 0, 1);
    }

    public void sendFeetParticle(Player p) {
        if (plugin.getPam().getParticleByName("arrow", PlayerStat.getPlayerStat(p).getArrow_trail()).getTrail()
                .equalsIgnoreCase("EMPTY"))
            return;
        if (p.isFlying() || p.hasPotionEffect(PotionEffectType.INVISIBILITY) || p.getGameMode() == GameMode.SPECTATOR
                || leaving.contains(p))
            return;
        Location loc = p.getLocation();
        createTrail(loc, plugin.getPam().getParticleByName("feet", PlayerStat.getPlayerStat(p).getFeet_trail()));
    }

    public void createTrail(Location loc, io.github.MGDCl.TheBridge.cosmetics.Particle particle) {
        plugin.getNms().sendParticle(ParticleType.valueOf(particle.getTrail()), 1, particle.getAmmount(), 0.05)
                .sendToLocation(loc);
    }

    @EventHandler(ignoreCancelled = true)
    public void onShoot(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player)
            if (e.getEntity() instanceof Arrow) {
                Player p = (Player) e.getEntity().getShooter();
                if (plugin.getGm().getGameByPlayer(p) != null || plugin.getGm().getGameFourByPlayer(p) != null)
                    sendArrowParticle(p, e.getEntity());
            }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        new PlayerStat(p);
        givePlayerItems(p);
        Utils.setPlayerExperience(p, PlayerStat.getPlayerStat(p).getXp());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (plugin.getGm().getGameByPlayer(e.getPlayer()) != null
                || plugin.getGm().getGameFourByPlayer(e.getPlayer()) != null) {
            List<String> cmds = plugin.getConfig().getStringList("commands.list");
            if (e.getMessage().contains("/bridges"))
                return;
            if (e.getPlayer().hasPermission("bridges.admin") || e.getPlayer().isOp())
                return;
            if (plugin.getConfig().getBoolean("commands.whitelist") == true) {
                if (!cmds.contains(e.getMessage()))
                    e.setCancelled(true);
            } else if (plugin.getConfig().getBoolean("commands.whitelist") == false)
                if (cmds.contains(e.getMessage()))
                    e.setCancelled(true);
        }
    }

    public void givePlayerItems(Player p) {
        final ItemStack select = ItemBuilder.item(Material.valueOf(plugin.getConfig().getString("items.menu.material")),
                plugin.getConfig().getInt("items.menu.amount"), (short) plugin.getConfig().getInt("items.menu.data"),
                plugin.getConfig().getString("items.menu.nameItem").replaceAll("&", "§"),
                plugin.getConfig().getString("items.menu.loreItem").replaceAll("&", "§"));
        final ItemStack random = ItemBuilder.item(
                Material.valueOf(plugin.getConfig().getString("items.random.material")),
                plugin.getConfig().getInt("items.random.amount"),
                (short) plugin.getConfig().getInt("items.random.data"),
                plugin.getConfig().getString("items.random.nameItem").replaceAll("&", "§"),
                plugin.getConfig().getString("items.random.loreItem").replaceAll("&", "§"));
        final ItemStack hotbar = ItemBuilder.item(
                Material.valueOf(plugin.getConfig().getString("items.hotbar.material")),
                plugin.getConfig().getInt("items.hotbar.amount"),
                (short) plugin.getConfig().getInt("items.hotbar.data"),
                plugin.getConfig().getString("items.hotbar.nameItem").replaceAll("&", "§"),
                plugin.getConfig().getString("items.hotbar.loreItem").replaceAll("&", "§"));
        final ItemStack shop = ItemBuilder.item(Material.valueOf(plugin.getConfig().getString("items.shop.material")),
                plugin.getConfig().getInt("items.shop.amount"), (short) plugin.getConfig().getInt("items.shop.data"),
                plugin.getConfig().getString("items.shop.nameItem").replaceAll("&", "§"),
                plugin.getConfig().getString("items.shop.loreItem").replaceAll("&", "§"));
        final ItemStack lobby = ItemBuilder.item(Material.valueOf(plugin.getConfig().getString("items.lobby.material")),
                plugin.getConfig().getInt("items.lobby.amount"), (short) plugin.getConfig().getInt("items.lobby.data"),
                plugin.getConfig().getString("items.lobby.nameItem").replaceAll("&", "§"),
                plugin.getConfig().getString("items.lobby.loreItem").replaceAll("&", "§"));
        final ItemStack archievements = ItemBuilder.item(
                Material.valueOf(plugin.getConfig().getString("items.archievements.material")),
                plugin.getConfig().getInt("items.archievements.amount"),
                (short) plugin.getConfig().getInt("items.archievements.data"),
                plugin.getConfig().getString("items.archievements.nameItem").replaceAll("&", "§"),
                plugin.getConfig().getString("items.archievements.loreItem").replaceAll("&", "§"));
        if (plugin.getConfig().getBoolean("items.menu.enabled"))
            p.getInventory().setItem(plugin.getConfig().getInt("items.menu.slot"), select);
        if (plugin.getConfig().getBoolean("items.random.enabled"))
            p.getInventory().setItem(plugin.getConfig().getInt("items.random.slot"), random);
        if (plugin.getConfig().getBoolean("items.hotbar.enabled"))
            p.getInventory().setItem(plugin.getConfig().getInt("items.hotbar.slot"), hotbar);
        if (plugin.getConfig().getBoolean("items.shop.enabled"))
            p.getInventory().setItem(plugin.getConfig().getInt("items.shop.slot"), shop);
        if (plugin.getConfig().getBoolean("items.lobby.enabled"))
            p.getInventory().setItem(plugin.getConfig().getInt("items.lobby.slot"), lobby);
        if (plugin.getConfig().getBoolean("items.archievements.enabled"))
            p.getInventory().setItem(plugin.getConfig().getInt("items.archievements.slot"), archievements);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        final Player p = e.getPlayer();
        final Item item = e.getItemDrop();
        if (p.getOpenInventory() != null)
            if (p.getOpenInventory().getTitle().equals(plugin.getLang().get("menus.hotbar.title"))) {
                e.getItemDrop().remove();
                e.setCancelled(true);
            }
        if (plugin.getGm().getGameFourByPlayer(p) != null) {
            e.setCancelled(true);
            return;
        }
        if (plugin.getGm().getGameByPlayer(p) != null) {
            e.setCancelled(true);
            return;
        }
        if (plugin.getConfig().getBoolean("items.menu.enabled")) {
            final ItemStack select = ItemBuilder.item(
                    Material.valueOf(plugin.getConfig().getString("items.menu.material")),
                    plugin.getConfig().getInt("items.menu.amount"),
                    (short) plugin.getConfig().getInt("items.menu.data"),
                    plugin.getConfig().getString("items.menu.nameItem").replaceAll("&", "§"),
                    plugin.getConfig().getString("items.menu.loreItem").replaceAll("&", "§"));
            if (item.getItemStack().equals(select))
                e.setCancelled(true);
        }
        if (plugin.getConfig().getBoolean("items.random.enabled")) {
            final ItemStack random = ItemBuilder.item(
                    Material.valueOf(plugin.getConfig().getString("items.random.material")),
                    plugin.getConfig().getInt("items.random.amount"),
                    (short) plugin.getConfig().getInt("items.random.data"),
                    plugin.getConfig().getString("items.random.nameItem").replaceAll("&", "§"),
                    plugin.getConfig().getString("items.random.loreItem").replaceAll("&", "§"));
            if (item.getItemStack().equals(random))
                e.setCancelled(true);
        }
        if (plugin.getConfig().getBoolean("items.hotbar.enabled")) {
            final ItemStack random = ItemBuilder.item(
                    Material.valueOf(plugin.getConfig().getString("items.hotbar.material")),
                    plugin.getConfig().getInt("items.hotbar.amount"),
                    (short) plugin.getConfig().getInt("items.hotbar.data"),
                    plugin.getConfig().getString("items.hotbar.nameItem").replaceAll("&", "§"),
                    plugin.getConfig().getString("items.hotbar.loreItem").replaceAll("&", "§"));
            if (item.getItemStack().equals(random))
                e.setCancelled(true);
        }
        if (plugin.getConfig().getBoolean("items.shop.enabled")) {
            final ItemStack random = ItemBuilder.item(
                    Material.valueOf(plugin.getConfig().getString("items.shop.material")),
                    plugin.getConfig().getInt("items.shop.amount"),
                    (short) plugin.getConfig().getInt("items.shop.data"),
                    plugin.getConfig().getString("items.shop.nameItem").replaceAll("&", "§"),
                    plugin.getConfig().getString("items.shop.loreItem").replaceAll("&", "§"));
            if (item.getItemStack().equals(random))
                e.setCancelled(true);
        }
        if (plugin.getConfig().getBoolean("items.lobby.enabled")) {
            final ItemStack random = ItemBuilder.item(
                    Material.valueOf(plugin.getConfig().getString("items.lobby.material")),
                    plugin.getConfig().getInt("items.lobby.amount"),
                    (short) plugin.getConfig().getInt("items.lobby.data"),
                    plugin.getConfig().getString("items.lobby.nameItem").replaceAll("&", "§"),
                    plugin.getConfig().getString("items.lobby.loreItem").replaceAll("&", "§"));
            if (item.getItemStack().equals(random))
                e.setCancelled(true);
        }
        if (plugin.getConfig().getBoolean("items.archievements.enabled")) {
            final ItemStack random = ItemBuilder.item(
                    Material.valueOf(plugin.getConfig().getString("items.archievements.material")),
                    plugin.getConfig().getInt("items.archievements.amount"),
                    (short) plugin.getConfig().getInt("items.archievements.data"),
                    plugin.getConfig().getString("items.archievements.nameItem").replaceAll("&", "§"),
                    plugin.getConfig().getString("items.archievements.loreItem").replaceAll("&", "§"));
            if (item.getItemStack().equals(random))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (plugin.getGm().getGameByPlayer(p) != null) {
            if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR)
                return;
            final ItemStack item = p.getItemInHand();
            if (!item.hasItemMeta())
                return;
            if (item.getType().equals(Material.PAPER)
                    && item.getItemMeta().getDisplayName().equals(plugin.getLang().get("items.teams.nameItem"))) {
                e.setCancelled(true);
                plugin.getTem().openTeamNormalMenu(p);
            }
        }
        if (plugin.getGm().getGameFourByPlayer(p) != null) {
            if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR)
                return;
            final ItemStack item = p.getItemInHand();
            if (item.getType().equals(Material.BOW))
                if (bow.contains(p)) {
                    p.sendMessage(plugin.getLang().get("messages.noShoot"));
                    e.setCancelled(true);
                }
            if (!item.hasItemMeta())
                return;
            if (item.getType().equals(Material.COMPASS)
                    && item.getItemMeta().getDisplayName().equals(plugin.getLang().get("items.spectate.nameItem"))) {
                e.setCancelled(true);
                plugin.getSpm().openSpectPlayerMenu(p);
            }
            if (item.getType().equals(Material.PAPER)
                    && item.getItemMeta().getDisplayName().equals(plugin.getLang().get("items.teams.nameItem"))) {
                e.setCancelled(true);
                plugin.getTem().openTeamFourMenu(p);
            }
            if (item.getType().equals(Material.REDSTONE_COMPARATOR)
                    || item.getType().equals(Material.REDSTONE_COMPARATOR_OFF) && item.getItemMeta().getDisplayName()
                    .equals(plugin.getLang().get("items.config.nameItem"))) {
                e.setCancelled(true);
                plugin.getSom().openOptionsMenu(p);
            }
            if (item.getType().equals(Material.BED)
                    && item.getItemMeta().getDisplayName().equals(plugin.getLang().get("items.leave.nameItem"))) {
                e.setCancelled(true);
                p.chat("/bridges leave");
                p.sendMessage(plugin.getLang().get("messages.leftGame"));
                p.teleport(TheBridge.get().getMainLobby());
                if (!leaving.contains(p)) {
                    leaving.add(p);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            leaving.remove(p);
                        }
                    }.runTaskLater(plugin, 20);
                }
                return;
            }
        }
        if (plugin.getGm().getGameByPlayer(p) != null) {
            final ItemStack item = p.getItemInHand();
            if (item.getType().equals(Material.BOW))
                if (bow.contains(p)) {
                    p.sendMessage(plugin.getLang().get("messages.noShoot"));
                    e.setCancelled(true);
                }
            if (!item.hasItemMeta())
                return;
            if (item.getType().equals(Material.BED)
                    && item.getItemMeta().getDisplayName().equals(plugin.getLang().get("items.leave.nameItem"))) {
                e.setCancelled(true);
                p.chat("/bridges leave");
                p.sendMessage(plugin.getLang().get("messages.leftGame"));
                p.teleport(TheBridge.get().getMainLobby());
                if (!leaving.contains(p)) {
                    leaving.add(p);
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            leaving.remove(p);
                        }
                    }.runTaskLater(plugin, 20);
                }
                return;
            }
        }
        if (plugin.getGm().getGameFourByPlayer(p) == null && plugin.getGm().getGameByPlayer(p) == null) {
            if (p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {

                final ItemStack item = p.getItemInHand();
                if (item.hasItemMeta()) {
                    if (plugin.getConfig().getBoolean("items.menu.enabled")) {
                        final ItemStack select = ItemBuilder.item(
                                Material.valueOf(plugin.getConfig().getString("items.menu.material")),
                                plugin.getConfig().getInt("items.menu.amount"),
                                (short) plugin.getConfig().getInt("items.menu.data"),
                                plugin.getConfig().getString("items.menu.nameItem").replaceAll("&", "§"),
                                plugin.getConfig().getString("items.menu.loreItem").replaceAll("&", "§"));
                        if (item.equals(select))
                            plugin.getGmu().openSelectTypeMenu(p);
                    }
                    if (plugin.getConfig().getBoolean("items.random.enabled"))
                        if (!leaving.contains(p)) {
                            final ItemStack random = ItemBuilder.item(
                                    Material.valueOf(plugin.getConfig().getString("items.random.material")),
                                    plugin.getConfig().getInt("items.random.amount"),
                                    (short) plugin.getConfig().getInt("items.random.data"),
                                    plugin.getConfig().getString("items.random.nameItem").replaceAll("&", "§"),
                                    plugin.getConfig().getString("items.random.loreItem").replaceAll("&", "§"));
                            if (item.equals(random)) {
                                int rand = (int) (Math.random() * 1);
                                if (rand == 0) {
                                    GameDuo g = null;
                                    for (final GameDuo game : plugin.getGm().getGames()) {
                                        if (game.isState(State.FINISH) || game.isState(State.INGAME)
                                                || game.isState(State.RESTARTING) || game.isState(State.PREGAME))
                                            continue;
                                        if (game.getPlayers() == game.getMax())
                                            continue;
                                        if (game.getPlayers() < game.getMax()) {
                                            g = game;
                                            break;
                                        }
                                    }
                                    if (g != null)
                                        plugin.getGm().addPlayerGame(p, g);
                                    else
                                        p.sendMessage(plugin.getLang().get("messages.noGames"));
                                } else if (rand == 1) {
                                    GameFour g = null;
                                    for (final GameFour game : plugin.getGm().getGamesFour()) {
                                        if (game.isState(FState.FINISH) || game.isState(FState.INGAME)
                                                || game.isState(FState.RESTARTING) || game.isState(FState.PREGAME))
                                            continue;
                                        if (game.getPlayers().size() == game.getMax())
                                            continue;
                                        if (game.getPlayers().size() < game.getMax()) {
                                            g = game;
                                            break;
                                        }
                                    }
                                    if (g != null)
                                        plugin.getGm().addPlayerGameFour(p, g);
                                    else
                                        p.sendMessage(plugin.getLang().get("messages.noGames"));
                                }
                            }
                        }
                    if (plugin.getConfig().getBoolean("items.hotbar.enabled")) {
                        final ItemStack random = ItemBuilder.item(
                                Material.valueOf(plugin.getConfig().getString("items.hotbar.material")),
                                plugin.getConfig().getInt("items.hotbar.amount"),
                                (short) plugin.getConfig().getInt("items.hotbar.data"),
                                plugin.getConfig().getString("items.hotbar.nameItem").replaceAll("&", "§"),
                                plugin.getConfig().getString("items.hotbar.loreItem").replaceAll("&", "§"));
                        if (item.equals(random))
                            plugin.getHotbar().createHotbarMenu(p);
                    }
                    if (plugin.getConfig().getBoolean("items.shop.enabled")) {
                        final ItemStack random = ItemBuilder.item(
                                Material.valueOf(plugin.getConfig().getString("items.shop.material")),
                                plugin.getConfig().getInt("items.shop.amount"),
                                (short) plugin.getConfig().getInt("items.shop.data"),
                                plugin.getConfig().getString("items.shop.nameItem").replaceAll("&", "§"),
                                plugin.getConfig().getString("items.shop.loreItem").replaceAll("&", "§"));
                        if (item.equals(random))
                            plugin.getShop().openShopMenu(p);
                    }
                    if (plugin.getConfig().getBoolean("items.archievements.enabled")) {
                        final ItemStack random = ItemBuilder.item(
                                Material.valueOf(plugin.getConfig().getString("items.archievements.material")),
                                plugin.getConfig().getInt("items.archievements.amount"),
                                (short) plugin.getConfig().getInt("items.archievements.data"),
                                plugin.getConfig().getString("items.archievements.nameItem").replaceAll("&", "§"),
                                plugin.getConfig().getString("items.archievements.loreItem").replaceAll("&", "§"));
                        if (item.equals(random))
                            plugin.getArchimenu().createArchievementsMenu(p, 1);
                    }
                    if (plugin.getConfig().getBoolean("items.lobby.enabled")) {
                        final ItemStack random = ItemBuilder.item(
                                Material.valueOf(plugin.getConfig().getString("items.lobby.material")),
                                plugin.getConfig().getInt("items.lobby.amount"),
                                (short) plugin.getConfig().getInt("items.lobby.data"),
                                plugin.getConfig().getString("items.lobby.nameItem").replaceAll("&", "§"),
                                plugin.getConfig().getString("items.lobby.loreItem").replaceAll("&", "§"));
                        if (item.equals(random)) {
                            String broadcastMessage = "Yolo";
                            ByteArrayOutputStream bo = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(bo);
                            byte[] data = broadcastMessage.getBytes();
                            try {
                                out.writeUTF("Forward");
                                out.writeUTF(plugin.getConfig().getString("items.lobby.server"));
                                out.writeShort(data.length);
                                out.write(data);
                                p.sendPluginMessage(TheBridge.get(), "BungeeCord", bo.toByteArray());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                plugin.getServer().getConsoleSender()
                                        .sendMessage(ChatColor.RED + "Server " + ChatColor.YELLOW
                                                + plugin.getConfig().getString("items.lobby.server") + ChatColor.RED
                                                + " not found... Please check you config!");
                            }
                        }
                    }
                }
            }
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))
                if (e.getClickedBlock().getState() instanceof Sign) {
                    if (p.hasPermission("bridges.admin") && p.isSneaking())
                        return;
                    if (leaving.contains(p))
                        return;
                    if (plugin.getSim().getSigns().keySet().contains(e.getClickedBlock().getLocation())) {
                        final GameNormalSign sign = plugin.getSim().getSigns().get(e.getClickedBlock().getLocation());
                        final GameDuo game = plugin.getGm().getGameByName(sign.getGame());
                        if (game.isState(State.WAITING) || game.isState(State.STARTING)) {
                            if (game.getPlayers() >= game.getMax()) {
                                p.sendMessage(plugin.getLang().get("messages.gameFull"));
                                return;
                            }
                            plugin.getGm().addPlayerGame(p, game);
                            return;
                        } else
                            p.sendMessage(plugin.getLang().get("messages.gameAlreadyStart"));
                    } else if (plugin.getSim().getFourSigns().keySet().contains(e.getClickedBlock().getLocation())) {
                        final GameFourSign sign = plugin.getSim().getFourSigns().get(e.getClickedBlock().getLocation());
                        final GameFour game = plugin.getGm().getGameFourByName(sign.getGame());
                        if (game.isState(FState.WAITING) || game.isState(FState.STARTING)) {
                            if (game.getPlayers().size() >= game.getMax()) {
                                p.sendMessage(plugin.getLang().get("messages.gameFull"));
                                return;
                            }
                            plugin.getGm().addPlayerGameFour(p, game);
                            return;
                        } else
                            p.sendMessage(plugin.getLang().get("messages.gameAlreadyStart"));
                    }
                }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        boolean wait = false;
        final Player p = e.getPlayer();
        if (plugin.getGm().getGameByPlayer(p) != null) {
            final GameDuo game = plugin.getGm().getGameByPlayer(p);
            if (e.getFrom() != e.getTo())
                sendFeetParticle(p);
            if (game.isState(State.WAITING) || game.isState(State.STARTING) || game.isState(State.FINISH))
                if (p.getLocation().getY() < 30)
                    p.teleport(game.getLobby());
            if (game.isState(State.INGAME)) {
                if (!game.getTeamPlayer(p).getColor().equals(ChatColor.RED) && game.getTeams().get(ChatColor.RED)
                        .getPortal().contains(p.getLocation().getBlock().getLocation()) && wait == false) {
                    game.celebrateGoal(game.getTeamPlayer(p), p);
                    wait = true;
                    return;
                }
                if (!game.getTeamPlayer(p).getColor().equals(ChatColor.BLUE) && game.getTeams().get(ChatColor.BLUE)
                        .getPortal().contains(p.getLocation().getBlock().getLocation()) && wait == false) {
                    game.celebrateGoal(game.getTeamPlayer(p), p);
                    wait = true;
                    return;
                }
                if (p.getLocation().getY() < 30)
                    respawn(p, Tagged.getTag(p));
                wait = false;
            }
            return;
        }
        if (plugin.getGm().getGameFourByPlayer(p) != null) {
            final GameFour game = plugin.getGm().getGameFourByPlayer(p);
            if (e.getFrom() != e.getTo())
                sendFeetParticle(p);
            if (game.isState(FState.WAITING) || game.isState(FState.STARTING) || game.isState(FState.FINISH))
                if (p.getLocation().getY() < 30)
                    p.teleport(game.getLobby());
            if (game.isState(FState.INGAME)) {
                if (!game.getSpects().contains(p)) {
                    if (!game.getTeams().get(ChatColor.RED).getDeath()
                            && !game.getTeamPlayer(p).getColor().equals(ChatColor.RED)
                            && game.getTeams().get(ChatColor.RED).getPortal()
                            .contains(p.getLocation().getBlock().getLocation())) {
                        game.celebrateGoal(game.getTeamPlayer(p), game.getTeams().get(ChatColor.RED), p);
                        return;
                    }
                    if (!game.getTeams().get(ChatColor.BLUE).getDeath()
                            && !game.getTeamPlayer(p).getColor().equals(ChatColor.BLUE)
                            && game.getTeams().get(ChatColor.BLUE).getPortal()
                            .contains(p.getLocation().getBlock().getLocation())) {
                        game.celebrateGoal(game.getTeamPlayer(p), game.getTeams().get(ChatColor.BLUE), p);
                        return;
                    }
                    if (!game.getTeams().get(ChatColor.YELLOW).getDeath()
                            && !game.getTeamPlayer(p).getColor().equals(ChatColor.YELLOW)
                            && game.getTeams().get(ChatColor.YELLOW).getPortal()
                            .contains(p.getLocation().getBlock().getLocation())) {
                        game.celebrateGoal(game.getTeamPlayer(p), game.getTeams().get(ChatColor.YELLOW), p);
                        return;
                    }
                    if (!game.getTeams().get(ChatColor.GREEN).getDeath()
                            && !game.getTeamPlayer(p).getColor().equals(ChatColor.GREEN)
                            && game.getTeams().get(ChatColor.GREEN).getPortal()
                            .contains(p.getLocation().getBlock().getLocation())) {
                        game.celebrateGoal(game.getTeamPlayer(p), game.getTeams().get(ChatColor.GREEN), p);
                        return;
                    }
                }
                if (p.getLocation().getY() < 30) {
                    if (game.getSpects().contains(p)) {
                        p.teleport(game.getLobby());
                        return;
                    }
                    respawnFour(p, Tagged.getTag(p));
                }
            }
            return;
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player p = (Player) e.getEntity();
            if (plugin.getGm().getGameByPlayer(p) != null) {
                e.setCancelled(true);
                return;
            }
            if (plugin.getGm().getGameFourByPlayer(p) != null) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (!plugin.getConfig().getBoolean("chat.enabled"))
            return;
        if (plugin.getGm().getGameByPlayer(p) != null) {
            final GameDuo game = plugin.getGm().getGameByPlayer(p);
            e.setCancelled(true);
            if (game.isState(State.WAITING) || game.isState(State.STARTING) || game.isState(State.FINISH)
                    || game.isState(State.RESTARTING)) {
                for (final Player on : game.getGamePlayers())
                    on.sendMessage(plugin.getConfig().getString("chat.lobby").replaceAll("&", "§")
                            .replaceAll("<player>", p.getName()).replaceAll("<msg>", e.getMessage()));
                return;
            }
            if (game.isState(State.INGAME) || game.isState(State.PREGAME)) {
                if (e.getMessage().startsWith("!")) {
                    final String msg = e.getMessage();
                    for (final Player on : game.getGamePlayers())
                        on.sendMessage(plugin.getConfig().getString("chat.global").replaceAll("&", "§")
                                .replaceAll("<teamName>", game.getTeamPlayer(p).getTeamName())
                                .replaceAll("<teamColor>", game.getTeamPlayer(p).getColor() + "")
                                .replaceAll("<player>", p.getName()).replaceAll("<msg>", msg.replaceFirst("!", "")));
                } else
                    for (final Player on : game.getTeamPlayer(p).getTeamPlayers())
                        on.sendMessage(plugin.getConfig().getString("chat.ingame").replaceAll("&", "§")
                                .replaceAll("<teamName>", game.getTeamPlayer(p).getTeamName())
                                .replaceAll("<teamColor>", game.getTeamPlayer(p).getColor() + "")
                                .replaceAll("<player>", p.getName()).replaceAll("<msg>", e.getMessage()));
                return;
            }
        }
        if (plugin.getGm().getGameFourByPlayer(p) != null) {
            final GameFour game = plugin.getGm().getGameFourByPlayer(p);
            e.setCancelled(true);
            if (game.isState(FState.WAITING) || game.isState(FState.STARTING) || game.isState(FState.FINISH)
                    || game.isState(FState.RESTARTING)) {
                for (final Player on : game.getPlayers())
                    on.sendMessage(plugin.getConfig().getString("chat.lobby").replaceAll("&", "§")
                            .replaceAll("<player>", p.getName()).replaceAll("<msg>", e.getMessage()));
                return;
            }
            if (game.isState(FState.INGAME) || game.isState(FState.PREGAME)) {
                if (e.getMessage().startsWith("!")) {
                    final String msg = e.getMessage();
                    for (final Player on : game.getPlayers())
                        on.sendMessage(plugin.getConfig().getString("chat.global").replaceAll("&", "§")
                                .replaceAll("<teamName>", game.getTeamPlayer(p).getTeamName())
                                .replaceAll("<teamColor>", game.getTeamPlayer(p).getColor() + "")
                                .replaceAll("<player>", p.getName()).replaceAll("<msg>", msg.replaceFirst("!", "")));
                } else
                    for (final Player on : game.getTeamPlayer(p).getTeamPlayers())
                        on.sendMessage(plugin.getConfig().getString("chat.ingame").replaceAll("&", "§")
                                .replaceAll("<teamName>", game.getTeamPlayer(p).getTeamName())
                                .replaceAll("<teamColor>", game.getTeamPlayer(p).getColor() + "")
                                .replaceAll("<player>", p.getName()).replaceAll("<msg>", e.getMessage()));
                return;
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player p = (Player) e.getEntity();
            if (plugin.getGm().getGameByPlayer(p) != null) {
                final GameDuo game = plugin.getGm().getGameByPlayer(p);
                if (e.getCause().equals(DamageCause.FALL)) {
                    e.setCancelled(true);
                    return;
                }
                if (game.isState(State.WAITING) || game.isState(State.STARTING) || game.isState(State.PREGAME)
                        || game.isState(State.FINISH))
                    e.setCancelled(true);
                return;
            }
            if (plugin.getGm().getGameFourByPlayer(p) != null) {
                final GameFour game = plugin.getGm().getGameFourByPlayer(p);
                if (e.getCause().equals(DamageCause.FALL)) {
                    e.setCancelled(true);
                    return;
                }
                if (game.isState(FState.WAITING) || game.isState(FState.STARTING) || game.isState(FState.PREGAME)
                        || game.isState(FState.FINISH))
                    e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        final Player p = (Player) e.getPlayer();
        InventoryView inv = e.getView();
        if (inv.getTitle().equals(plugin.getLang().get("menus.type.title")))
            page.remove(p);
        if (inv.getTitle().equals(plugin.getLang().get("menus.hotbar.title"))) {
            InventoryData.getInventoryData(p).restore();
            InventoryData.remove(p);
        }
    }

    @EventHandler
    public void onMenu(InventoryClickEvent e) {
        final Player p = (Player) e.getWhoClicked();
        final InventoryView inv = e.getView();

        if (inv.getTitle() == null)
            return;
        if (plugin.getGm().getGameFourByPlayer(p) != null || plugin.getGm().getGameByPlayer(p) != null) {
            if (e.getSlotType().equals(SlotType.ARMOR))
                e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            if (e.getCurrentItem().getType().equals(Material.BED) && e.getCurrentItem().getItemMeta().getDisplayName()
                    .equals(plugin.getLang().get("items.leave.nameItem")))
                e.setCancelled(true);
        }
        if (plugin.getGm().getGameFourByPlayer(p) == null && plugin.getGm().getGameByPlayer(p) == null) {
            if (p.getOpenInventory() != null)
                if (p.getOpenInventory().getTitle().equals(plugin.getLang().get("menus.hotbar.title"))) {
                    if (p.getOpenInventory() != null && e.getClickedInventory() != null
                            && !e.getClickedInventory().equals(p.getOpenInventory().getTopInventory()))
                        e.setCancelled(true);
                    if (p.getItemOnCursor() != null && p.getItemOnCursor().getType() != Material.AIR) {
                        if (e.getClick().equals(ClickType.SHIFT_RIGHT) || e.getClick().equals(ClickType.SHIFT_LEFT)
                                || e.getSlotType().equals(SlotType.QUICKBAR)
                                || e.getSlotType().equals(SlotType.OUTSIDE))
                            e.setCancelled(true);
                        if (!e.getSlotType().equals(SlotType.OUTSIDE)) {
                            if (!e.getClickedInventory().getTitle().equals(plugin.getLang().get("menus.hotbar.title")))
                                e.setCancelled(true);
                            if (e.getClickedInventory().getTitle().equals(plugin.getLang().get("menus.hotbar.title")))
                                if (e.getSlot() == 53 || e.getSlot() == 52 || e.getSlot() == 51 || e.getSlot() == 48
                                        || e.getSlot() == 47 || e.getSlot() == 46 || e.getSlot() == 45)
                                    e.setCancelled(true);
                        }
                    }
                }
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            final ItemStack item = e.getCurrentItem();
            if (item.hasItemMeta()) {
                if (plugin.getConfig().getBoolean("items.menu.enabled")) {
                    final ItemStack select = ItemBuilder.item(
                            Material.valueOf(plugin.getConfig().getString("items.menu.material")),
                            plugin.getConfig().getInt("items.menu.amount"),
                            (short) plugin.getConfig().getInt("items.menu.data"),
                            plugin.getConfig().getString("items.menu.nameItem").replaceAll("&", "§"),
                            plugin.getConfig().getString("items.menu.loreItem").replaceAll("&", "§"));
                    if (item.equals(select))
                        e.setCancelled(true);
                }
                if (plugin.getConfig().getBoolean("items.random.enabled")) {
                    final ItemStack random = ItemBuilder.item(
                            Material.valueOf(plugin.getConfig().getString("items.random.material")),
                            plugin.getConfig().getInt("items.random.amount"),
                            (short) plugin.getConfig().getInt("items.random.data"),
                            plugin.getConfig().getString("items.random.nameItem").replaceAll("&", "§"),
                            plugin.getConfig().getString("items.random.loreItem").replaceAll("&", "§"));
                    if (item.equals(random))
                        e.setCancelled(true);
                }
                if (plugin.getConfig().getBoolean("items.hotbar.enabled")) {
                    final ItemStack random = ItemBuilder.item(
                            Material.valueOf(plugin.getConfig().getString("items.hotbar.material")),
                            plugin.getConfig().getInt("items.hotbar.amount"),
                            (short) plugin.getConfig().getInt("items.hotbar.data"),
                            plugin.getConfig().getString("items.hotbar.nameItem").replaceAll("&", "§"),
                            plugin.getConfig().getString("items.hotbar.loreItem").replaceAll("&", "§"));
                    if (item.equals(random))
                        e.setCancelled(true);
                }
                if (plugin.getConfig().getBoolean("items.shop.enabled")) {
                    final ItemStack random = ItemBuilder.item(
                            Material.valueOf(plugin.getConfig().getString("items.shop.material")),
                            plugin.getConfig().getInt("items.shop.amount"),
                            (short) plugin.getConfig().getInt("items.shop.data"),
                            plugin.getConfig().getString("items.shop.nameItem").replaceAll("&", "§"),
                            plugin.getConfig().getString("items.shop.loreItem").replaceAll("&", "§"));
                    if (item.equals(random))
                        e.setCancelled(true);
                }
                if (plugin.getConfig().getBoolean("items.lobby.enabled")) {
                    final ItemStack random = ItemBuilder.item(
                            Material.valueOf(plugin.getConfig().getString("items.lobby.material")),
                            plugin.getConfig().getInt("items.lobby.amount"),
                            (short) plugin.getConfig().getInt("items.lobby.data"),
                            plugin.getConfig().getString("items.lobby.nameItem").replaceAll("&", "§"),
                            plugin.getConfig().getString("items.lobby.loreItem").replaceAll("&", "§"));
                    if (item.equals(random))
                        e.setCancelled(true);
                }
                if (plugin.getConfig().getBoolean("items.archievements.enabled")) {
                    final ItemStack random = ItemBuilder.item(
                            Material.valueOf(plugin.getConfig().getString("items.archievements.material")),
                            plugin.getConfig().getInt("items.archievements.amount"),
                            (short) plugin.getConfig().getInt("items.archievements.data"),
                            plugin.getConfig().getString("items.archievements.nameItem").replaceAll("&", "§"),
                            plugin.getConfig().getString("items.archievements.loreItem").replaceAll("&", "§"));
                    if (item.equals(random))
                        e.setCancelled(true);
                }
            }
        }
        if (inv.getTitle().equals(plugin.getAchievement().get("title").replaceAll("<player>", p.getName()))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(plugin.getAchievement().get("close.name"))) {
                p.getOpenInventory().close();
                e.setCancelled(true);
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName()
                    .equals(plugin.getAchievement().get("next").replaceAll("<next>", "→"))) {
                if (!page.containsKey(p))
                    page.put(p, 2);
                else
                    page.put(p, page.get(p) + 1);
                plugin.getArchimenu().createArchievementsMenu(p, page.get(p));
                e.setCancelled(true);
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName()
                    .equals(plugin.getAchievement().get("back").replaceAll("<back>", "§?"))) {
                page.put(p, page.get(p) - 1);
                plugin.getArchimenu().createArchievementsMenu(p, page.get(p));
                e.setCancelled(true);
            }
        }
        if (inv.getTitle().equals(plugin.getLang().get("shop.title"))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(plugin.getLang().get("shop.glass.nameItem")))
                plugin.getGlam().createGlassMenu(p);
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(plugin.getLang().get("shop.trails.nameItem")))
                plugin.getTrm().createTrailMenu(p);
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(plugin.getLang().get("shop.close.nameItem")))
                p.getOpenInventory().close();
        }
        if (inv.getTitle().equals(plugin.getParticles().get("title"))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            if (e.getCurrentItem().getItemMeta().getDisplayName()
                    .equals(plugin.getLang().get("shop.trails.type.arrow.nameItem")))
                plugin.getAtm().createArrowMenu(p);
            if (e.getCurrentItem().getItemMeta().getDisplayName()
                    .equals(plugin.getLang().get("shop.trails.type.feet.nameItem")))
                plugin.getFtm().createFeetMenu(p);
        }
        if (inv.getTitle().equals(plugin.getCages().get("title"))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            for (final Cage cage : plugin.getCm().getCages().values())
                if (cage.getSlot() == e.getSlot())
                    if (!p.hasPermission(cage.getPermission())) {
                        if (!cage.isBuy())
                            p.sendMessage(plugin.getLang().get("messages.noBuy"));
                        else if (cage.getPrice() > PlayerStat.getPlayerStat(p).getCoins())
                            p.sendMessage(plugin.getLang().get("messages.noMoney"));
                        else if (cage.isBuy() && cage.getPrice() <= PlayerStat.getPlayerStat(p).getCoins()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                    plugin.getConfig().getString("permFormat").replaceAll("<name>", p.getName())
                                            .replaceAll("<permission>", cage.getPermission()));
                            PlayerStat.getPlayerStat(p).removeCoins(cage.getPrice());
                            p.getOpenInventory().close();
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    plugin.getGlam().createGlassMenu(p);
                                }
                            }.runTaskLater(plugin, 2L);
                            p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.shop.buy")), 1.0f,
                                    1.0f);
                            p.sendMessage(
                                    plugin.getLang().get("messages.buyCage").replaceAll("<cage>", cage.getName()));
                        }
                    } else {
                        p.sendMessage(
                                plugin.getLang().get("messages.selectedCage").replaceAll("<cage>", cage.getName()));
                        PlayerStat.getPlayerStat(p).setCage(cage.getId());
                    }
        }
        if (inv.getTitle().equals(plugin.getParticles().get("trails.arrow.title"))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            for (final io.github.MGDCl.TheBridge.cosmetics.Particle arrow : plugin.getPam().getArrow_trails().values())
                if (arrow.getSlot() == e.getSlot())
                    if (!p.hasPermission(arrow.getPermission())) {
                        if (!arrow.isBuy())
                            p.sendMessage(plugin.getLang().get("messages.noBuy"));
                        else if (arrow.getPrice() > PlayerStat.getPlayerStat(p).getCoins())
                            p.sendMessage(plugin.getLang().get("messages.noMoney"));
                        else if (arrow.isBuy() && arrow.getPrice() <= PlayerStat.getPlayerStat(p).getCoins()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                    plugin.getConfig().getString("permFormat").replaceAll("<name>", p.getName())
                                            .replaceAll("<permission>", arrow.getPermission()));
                            PlayerStat.getPlayerStat(p).removeCoins(arrow.getPrice());
                            p.getOpenInventory().close();
                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    plugin.getAtm().createArrowMenu(p);
                                }
                            }.runTaskLater(plugin, 2L);
                            p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.shop.buy")), 1.0f,
                                    1.0f);
                            p.sendMessage(plugin.getLang().get("messages.buyArrowTrail").replaceAll("<arrow_trail>",
                                    arrow.getName()));
                        }
                    } else {
                        p.sendMessage(plugin.getLang().get("messages.selectedArrowTrail").replaceAll("<arrow_trail>",
                                arrow.getName()));
                        PlayerStat.getPlayerStat(p).setArrow_trail(arrow.getId());
                    }
        }
        if (inv.getTitle().equals(plugin.getParticles().get("trails.feet.title"))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            for (

                    final io.github.MGDCl.TheBridge.cosmetics.Particle feet : plugin.getPam().getFeet_trails().values())
                if (feet.getSlot() == e.getSlot())
                    if (!p.hasPermission(feet.getPermission())) {
                        if (!feet.isBuy())
                            p.sendMessage(plugin.getLang().get("messages.noBuy"));
                        else if (feet.getPrice() > PlayerStat.getPlayerStat(p).getCoins())
                            p.sendMessage(plugin.getLang().get("messages.noMoney"));
                        else if (feet.isBuy() && feet.getPrice() <= PlayerStat.getPlayerStat(p).getCoins()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                    plugin.getConfig().getString("permFormat").replaceAll("<name>", p.getName())
                                            .replaceAll("<permission>", feet.getPermission()));
                            PlayerStat.getPlayerStat(p).removeCoins(feet.getPrice());
                            p.getOpenInventory().close();
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    plugin.getFtm().createFeetMenu(p);
                                }
                            }.runTaskLater(plugin, 2L);
                            p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.shop.buy")), 1.0f,
                                    1.0f);
                            p.sendMessage(plugin.getLang().get("messages.buyFeetTrail").replaceAll("<feet_trail>",
                                    feet.getName()));
                        }
                    } else {
                        p.sendMessage(plugin.getLang().get("messages.selectedFeetTrail").replaceAll("<feet_trail>",
                                feet.getName()));
                        PlayerStat.getPlayerStat(p).setArrow_trail(feet.getId());
                    }
        }
        if (inv.getTitle().equals(plugin.getLang().get("menus.type.title"))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                return;
            if (e.getCurrentItem().getItemMeta().getDisplayName()
                    .equals(plugin.getLang().get("menus.type.normal.nameItem")))
                plugin.getGmu().openGameMenu(p);
            if (e.getCurrentItem().getItemMeta().getDisplayName()
                    .equals(plugin.getLang().get("menus.type.four.nameItem")))
                plugin.getGmu().openGameFourMenu(p);
        }
        if (inv.getTitle().equals(plugin.getLang().get("menus.hotbar.title"))) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                e.setCancelled(true);
                return;
            }
            if (!e.getCurrentItem().hasItemMeta())
                return;
            if (!e.getCurrentItem().getItemMeta().hasDisplayName())
                return;
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§a↑ §eInventory"))
                e.setCancelled(true);
            if (e.getCurrentItem().getItemMeta().getDisplayName()
                    .equals(plugin.getLang().get("menus.close.nameItem"))) {
                e.setCancelled(true);
                p.getOpenInventory().close();
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(plugin.getLang().get("menus.save.nameItem"))) {
                plugin.getHotbar().saveLayout(p, e.getInventory());
                e.setCancelled(true);
            }
        }
        if (inv.getTitle().equals(plugin.getLang().get("menus.game.title"))) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR
                    || e.getSlotType().equals(SlotType.OUTSIDE))
                return;
            e.setCancelled(true);
            if (plugin.getGm().getGameByPlayer(p) == null)
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§a")) {
                    final String arena = e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§a", "");
                    final GameDuo game = plugin.getGm().getGameByName(arena);
                    if (game.getPlayers() >= game.getMax()) {
                        p.sendMessage(plugin.getLang().get("messages.gameFull"));
                        return;
                    }
                    if (game.isState(State.WAITING) || game.isState(State.STARTING))
                        plugin.getGm().addPlayerGame(p, game);
                    else
                        p.sendMessage(plugin.getLang().get("messages.gameAlreadyStart"));
                }
        }
        if (inv.getTitle().equals(plugin.getLang().get("menus.gameFour.title"))) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR
                    || e.getSlotType().equals(SlotType.OUTSIDE))
                return;
            e.setCancelled(true);
            if (plugin.getGm().getGameFourByPlayer(p) == null)
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§a")) {
                    final String arena = e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§a", "");
                    final GameFour game = plugin.getGm().getGameFourByName(arena);
                    if (game == null)
                        return;
                    if (game.getPlayers().size() >= game.getMax()) {
                        p.sendMessage(plugin.getLang().get("messages.gameFull"));
                        return;
                    }
                    if (game.isState(FState.WAITING) || game.isState(FState.STARTING))
                        plugin.getGm().addPlayerGameFour(p, game);
                    else
                        p.sendMessage(plugin.getLang().get("messages.gameAlreadyStart"));
                }
        }
        if (plugin.getGm().getGameByPlayer(p) != null)
            if (inv.getTitle().equals(plugin.getLang().get("menus.team.title"))) {
                e.setCancelled(true);
                final GameDuo game = plugin.getGm().getGameByPlayer(p);
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§9")) {
                    if (p.hasPermission("bridges.bypass")) {
                        if (game.getTeamPlayer(p) != null)
                            game.removeAllPlayerTeam(p);
                        game.addPlayerTeam(p, game.getTeams().get(ChatColor.BLUE));
                        p.sendMessage(plugin.getLang().get("messages.joinedTeam").replaceAll("<team>",
                                "§9" + plugin.getConfig().getString("names.blue")));
                        plugin.getTem().openTeamNormalMenu(p);
                        return;
                    }
                    if (game.getTeamPlayer(p) != null)
                        game.removeAllPlayerTeam(p);
                    if (game.getTeams().get(ChatColor.BLUE).getTeamSize() < game.getTeamSize() && game.getTeams()
                            .get(ChatColor.BLUE).getTeamSize() <= game.getTeams().get(ChatColor.RED).getTeamSize()) {
                        game.addPlayerTeam(p, game.getTeams().get(ChatColor.BLUE));
                        p.sendMessage(plugin.getLang().get("messages.joinedTeam").replaceAll("<team>",
                                "§9" + plugin.getConfig().getString("names.blue")));
                        plugin.getTem().openTeamNormalMenu(p);
                    } else
                        p.sendMessage(plugin.getLang().get("messages.unbalancedTeam"));
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§c")) {
                    if (p.hasPermission("bridges.bypass")) {
                        if (game.getTeamPlayer(p) != null)
                            game.removeAllPlayerTeam(p);
                        game.addPlayerTeam(p, game.getTeams().get(ChatColor.RED));
                        p.sendMessage(plugin.getLang().get("messages.joinedTeam").replaceAll("<team>",
                                "§c" + plugin.getConfig().getString("names.red")));
                        plugin.getTem().openTeamNormalMenu(p);
                        return;
                    }
                    if (game.getTeamPlayer(p) != null)
                        game.removeAllPlayerTeam(p);
                    if (game.getTeams().get(ChatColor.RED).getTeamSize() < game.getTeamSize() && game.getTeams()
                            .get(ChatColor.RED).getTeamSize() <= game.getTeams().get(ChatColor.BLUE).getTeamSize()) {
                        game.addPlayerTeam(p, game.getTeams().get(ChatColor.RED));
                        p.sendMessage(plugin.getLang().get("messages.joinedTeam").replaceAll("<team>",
                                "§c" + plugin.getConfig().getString("names.red")));
                        plugin.getTem().openTeamNormalMenu(p);
                    } else
                        p.sendMessage(plugin.getLang().get("messages.unbalancedTeam"));
                }
            }
        if (plugin.getGm().getGameFourByPlayer(p) != null) {
            if (inv.getTitle().equals(plugin.getLang().get("menus.options.title"))) {
                e.setCancelled(true);
                if (Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§7", "")) == null)
                    return;
                p.teleport(Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§7", "")));
            }
            if (e.getInventory().getName().equals(plugin.getLang().get("menus.options.title"))) {
                if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
                    return;
                e.setCancelled(true);
                if (e.getCurrentItem().getItemMeta().getDisplayName()
                        .equals(plugin.getLang().get("menus.options.velocidad.nameItem").replaceAll("<#>", "I")))
                    p.setFlySpeed(0.2f);
                if (e.getCurrentItem().getItemMeta().getDisplayName()
                        .equals(plugin.getLang().get("menus.options.velocidad.nameItem").replaceAll("<#>", "II")))
                    p.setFlySpeed(0.4f);
                if (e.getCurrentItem().getItemMeta().getDisplayName()
                        .equals(plugin.getLang().get("menus.options.velocidad.nameItem").replaceAll("<#>", "III")))
                    p.setFlySpeed(0.6f);
                if (e.getCurrentItem().getItemMeta().getDisplayName()
                        .equals(plugin.getLang().get("menus.options.velocidad.nameItem").replaceAll("<#>", "IV")))
                    p.setFlySpeed(0.8f);
                if (e.getCurrentItem().getItemMeta().getDisplayName()
                        .equals(plugin.getLang().get("menus.options.velocidad.nameItem").replaceAll("<#>", "V")))
                    p.setFlySpeed(1.0f);
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals(
                        plugin.getLang().get("menus.options.vision.nameItem").replaceAll("<type>", "§aActivar"))) {
                    plugin.getSom().settings.put(p, true);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999, 1, true));
                    plugin.getSom().openOptionsMenu(p);
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals(
                        plugin.getLang().get("menus.options.vision.nameItem").replaceAll("<type>", "§cDesactivar"))) {
                    plugin.getSom().settings.put(p, false);
                    p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    plugin.getSom().openOptionsMenu(p);
                }
            }
            if (inv.getTitle().equals(plugin.getLang().get("menus.teamFour.title"))) {
                e.setCancelled(true);
                final GameFour game = plugin.getGm().getGameFourByPlayer(p);
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§9")) {
                    if (p.hasPermission("bridges.bypass")) {
                        if (game.getTeamPlayer(p) != null)
                            game.removeAllPlayerTeam(p);
                        game.addPlayerTeam(p, game.getTeams().get(ChatColor.BLUE));
                        p.sendMessage(plugin.getLang().get("messages.joinedTeam").replaceAll("<team>",
                                "§9" + plugin.getConfig().getString("names.blue")));
                        plugin.getTem().openTeamFourMenu(p);
                        return;
                    }
                    if (game.getTeamPlayer(p) != null)
                        game.removeAllPlayerTeam(p);
                    if (game.getTeams().get(ChatColor.BLUE).getTeamSize() < game.getTeamSize()
                            && game.getTeams().get(ChatColor.BLUE).getTeamSize() <= game.getTeams().get(ChatColor.RED)
                            .getTeamSize()
                            && game.getTeams().get(ChatColor.BLUE).getTeamSize() <= game.getTeams()
                            .get(ChatColor.YELLOW).getTeamSize()
                            && game.getTeams().get(ChatColor.BLUE).getTeamSize() <= game.getTeams().get(ChatColor.GREEN)
                            .getTeamSize()) {
                        game.addPlayerTeam(p, game.getTeams().get(ChatColor.BLUE));
                        p.sendMessage(plugin.getLang().get("messages.joinedTeam").replaceAll("<team>",
                                "§9" + plugin.getConfig().getString("names.blue")));
                        plugin.getTem().openTeamFourMenu(p);
                    } else
                        p.sendMessage(plugin.getLang().get("messages.unbalancedTeam"));
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§c")) {
                    if (p.hasPermission("bridges.bypass")) {
                        if (game.getTeamPlayer(p) != null)
                            game.removeAllPlayerTeam(p);
                        game.addPlayerTeam(p, game.getTeams().get(ChatColor.RED));
                        p.sendMessage(plugin.getLang().get("messages.joinedTeam").replaceAll("<team>",
                                "§c" + plugin.getConfig().getString("names.red")));
                        plugin.getTem().openTeamFourMenu(p);
                        return;
                    }
                    if (game.getTeamPlayer(p) != null)
                        game.removeAllPlayerTeam(p);
                    if (game.getTeams().get(ChatColor.RED).getTeamSize() < game.getTeamSize()
                            && game.getTeams().get(ChatColor.RED).getTeamSize() <= game.getTeams().get(ChatColor.BLUE)
                            .getTeamSize()
                            && game.getTeams().get(ChatColor.RED).getTeamSize() <= game.getTeams().get(ChatColor.YELLOW)
                            .getTeamSize()
                            && game.getTeams().get(ChatColor.RED).getTeamSize() <= game.getTeams().get(ChatColor.GREEN)
                            .getTeamSize()) {
                        game.addPlayerTeam(p, game.getTeams().get(ChatColor.RED));
                        p.sendMessage(plugin.getLang().get("messages.joinedTeam").replaceAll("<team>",
                                "§c" + plugin.getConfig().getString("names.red")));
                        plugin.getTem().openTeamFourMenu(p);
                    } else
                        p.sendMessage(plugin.getLang().get("messages.unbalancedTeam"));
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§a")) {
                    if (p.hasPermission("bridges.bypass")) {
                        if (game.getTeamPlayer(p) != null)
                            game.removeAllPlayerTeam(p);
                        game.addPlayerTeam(p, game.getTeams().get(ChatColor.GREEN));
                        p.sendMessage(plugin.getLang().get("messages.joinedTeam").replaceAll("<team>",
                                "§a" + plugin.getConfig().getString("names.green")));
                        plugin.getTem().openTeamFourMenu(p);
                        return;
                    }
                    if (game.getTeamPlayer(p) != null)
                        game.removeAllPlayerTeam(p);
                    if (game.getTeams().get(ChatColor.GREEN).getTeamSize() < game.getTeamSize()
                            && game.getTeams().get(ChatColor.GREEN).getTeamSize() <= game.getTeams().get(ChatColor.RED)
                            .getTeamSize()
                            && game.getTeams().get(ChatColor.GREEN).getTeamSize() <= game.getTeams()
                            .get(ChatColor.YELLOW).getTeamSize()
                            && game.getTeams().get(ChatColor.GREEN).getTeamSize() <= game.getTeams().get(ChatColor.BLUE)
                            .getTeamSize()) {
                        game.addPlayerTeam(p, game.getTeams().get(ChatColor.GREEN));
                        p.sendMessage(plugin.getLang().get("messages.joinedTeam").replaceAll("<team>",
                                "§a" + plugin.getConfig().getString("names.green")));
                        plugin.getTem().openTeamFourMenu(p);
                    } else
                        p.sendMessage(plugin.getLang().get("messages.unbalancedTeam"));
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§e")) {
                    if (p.hasPermission("bridges.bypass")) {
                        if (game.getTeamPlayer(p) != null)
                            game.removeAllPlayerTeam(p);
                        game.addPlayerTeam(p, game.getTeams().get(ChatColor.YELLOW));
                        p.sendMessage(plugin.getLang().get("messages.joinedTeam").replaceAll("<team>",
                                "§e" + plugin.getConfig().getString("names.yellow")));
                        plugin.getTem().openTeamFourMenu(p);
                        return;
                    }
                    if (game.getTeamPlayer(p) != null)
                        game.removeAllPlayerTeam(p);
                    if (game.getTeams().get(ChatColor.YELLOW).getTeamSize() < game.getTeamSize()
                            && game.getTeams().get(ChatColor.YELLOW).getTeamSize() <= game.getTeams().get(ChatColor.RED)
                            .getTeamSize()
                            && game.getTeams().get(ChatColor.YELLOW).getTeamSize() <= game.getTeams()
                            .get(ChatColor.BLUE).getTeamSize()
                            && game.getTeams().get(ChatColor.YELLOW).getTeamSize() <= game.getTeams()
                            .get(ChatColor.GREEN).getTeamSize()) {
                        game.addPlayerTeam(p, game.getTeams().get(ChatColor.YELLOW));
                        p.sendMessage(plugin.getLang().get("messages.joinedTeam").replaceAll("<team>",
                                "§e" + plugin.getConfig().getString("names.yellow")));
                        plugin.getTem().openTeamFourMenu(p);
                    } else
                        p.sendMessage(plugin.getLang().get("messages.unbalancedTeam"));
                }
            }
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player p = (Player) e.getEntity();
            if (plugin.getGm().getGameFourByPlayer(p) != null || plugin.getGm().getGameByPlayer(p) != null)
                if (bow.contains(p)) {
                    p.sendMessage(plugin.getLang().get("messages.noShoot"));
                    e.setCancelled(true);
                } else {
                    timeBow(p);
                    bow.add(p);
                }
        }
    }

    public void timeBow(Player p) {
        p.setLevel(plugin.getConfig().getInt("bow.seconds"));
        p.setExp(1.0F);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (p.getLevel() - 1 < 0) {
                    p.setLevel(0);
                    p.setExp(0.0F);
                    bow.remove(p);
                    p.getInventory().addItem(new ItemStack(Material.ARROW, 1, (short) 0));
                    cancel();
                } else {
                    p.setLevel(p.getLevel() - 1);
                    p.setExp(1.0F);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (p.getExp() - 0.05F < 0) {
                                p.setExp(0.0F);
                                cancel();
                            } else
                                p.setExp(p.getExp() - 0.05F);
                        }
                    }.runTaskTimer(plugin, 0, 2);
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getDamager() instanceof Player) {
                final Player p = (Player) e.getEntity();
                final Player d = (Player) e.getDamager();
                if (plugin.getGm().getGameByPlayer(p) != null) {
                    final GameDuo game = plugin.getGm().getGameByPlayer(p);
                    if (game.isState(State.WAITING) || game.isState(State.STARTING) || game.isState(State.PREGAME)) {
                        e.setCancelled(true);
                        return;
                    }
                    if (game.getTeamPlayer(p).equals(game.getTeamPlayer(d))) {
                        e.setCancelled(true);
                        return;
                    }
                    Tagged.setTag(p, d);
                    if (e.getFinalDamage() >= p.getHealth()) {
                        e.setDamage(0);
                        respawn(p, Tagged.getTag(p));
                    }
                }
                if (plugin.getGm().getGameFourByPlayer(p) != null) {
                    final GameFour game = plugin.getGm().getGameFourByPlayer(p);
                    if (game.isState(FState.WAITING) || game.isState(FState.STARTING) || game.isState(FState.PREGAME)) {
                        e.setCancelled(true);
                        return;
                    }
                    if (game.getTeamPlayer(p).equals(game.getTeamPlayer(d))) {
                        e.setCancelled(true);
                        return;
                    }
                    Tagged.setTag(p, d);
                    if (e.getFinalDamage() >= p.getHealth()) {
                        e.setDamage(0);
                        respawnFour(p, Tagged.getTag(p));
                    }
                }
            }
            if (e.getDamager() instanceof Projectile) {
                final Projectile proj = (Projectile) e.getDamager();
                if (proj.getShooter() instanceof Player) {
                    final Player p = (Player) e.getEntity();
                    final Player d = (Player) proj.getShooter();
                    if (plugin.getGm().getGameByPlayer(p) != null) {
                        final GameDuo game = plugin.getGm().getGameByPlayer(p);
                        if (game.isState(State.WAITING) || game.isState(State.STARTING)
                                || game.isState(State.PREGAME)) {
                            e.setCancelled(true);
                            return;
                        }
                        if (game.getTeamPlayer(p).equals(game.getTeamPlayer(d))) {
                            e.setCancelled(true);
                            return;
                        }
                        Tagged.setTag(p, d);
                        if (e.getFinalDamage() >= p.getHealth()) {
                            e.setDamage(0);
                            respawn(p, Tagged.getTag(p));
                        }
                    }
                    if (plugin.getGm().getGameFourByPlayer(p) != null) {
                        final GameFour game = plugin.getGm().getGameFourByPlayer(p);
                        if (game.isState(FState.WAITING) || game.isState(FState.STARTING)
                                || game.isState(FState.PREGAME)) {
                            e.setCancelled(true);
                            return;
                        }
                        if (game.getTeamPlayer(p).equals(game.getTeamPlayer(d))) {
                            e.setCancelled(true);
                            return;
                        }
                        Tagged.setTag(p, d);
                        if (e.getFinalDamage() >= p.getHealth()) {
                            e.setDamage(0);
                            respawnFour(p, Tagged.getTag(p));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        final Player p = e.getPlayer();
        if (plugin.getGm().getGameByPlayer(p) != null)
            if (e.getItem().getType().equals(Material.GOLDEN_APPLE)) {
                p.setHealth(p.getMaxHealth());
                if (plugin.getConfig().getBoolean("remove.absorption") == true)
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.removePotionEffect(PotionEffectType.ABSORPTION);
                        }
                    }.runTaskLater(plugin, 1);
                if (plugin.getConfig().getBoolean("remove.regeneration") == true)
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.removePotionEffect(PotionEffectType.REGENERATION);
                        }
                    }.runTaskLater(plugin, 1);
            }
        if (plugin.getGm().getGameFourByPlayer(p) != null)
            if (e.getItem().getType().equals(Material.GOLDEN_APPLE)) {
                p.setHealth(p.getMaxHealth());
                if (plugin.getConfig().getBoolean("remove.absorption") == true)
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            p.removePotionEffect(PotionEffectType.ABSORPTION);
                        }
                    }.runTaskLater(plugin, 1);
                if (plugin.getConfig().getBoolean("remove.regeneration") == true)
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.removePotionEffect(PotionEffectType.REGENERATION);
                        }
                    }.runTaskLater(plugin, 1);
            }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        if (plugin.getGm().getGameByPlayer(p) != null) {
            final GameDuo game = plugin.getGm().getGameByPlayer(p);
            game.removePlayer(p);
            if (game.getTeamsAlive() == 1)
                game.checkWin(p, game.getLastTeam(), game.getLastTeam().getGoals());
        }
        if (plugin.getGm().getGameFourByPlayer(p) != null) {
            final GameFour game = plugin.getGm().getGameFourByPlayer(p);
            game.removePlayer(p);
            if (game.getTeamsAlive() == 1)
                game.checkWin(game.getLastTeam(), game.getTeamPlayer(p), game.getLastTeam().getTeamPlayers().get(0));
        }
        for (final Player on : plugin.getSb().getSB().keySet())
            plugin.getSb().update(on);
        if (InventoryData.getInventoryData(p) != null) {
            InventoryData.getInventoryData(p).restore();
            InventoryData.remove(p);
        }
        plugin.getSb().remove(p);
        plugin.getTop().removeHolo(p);
        plugin.getDb().saveData(PlayerStat.getPlayerStat(p));
        plugin.getCb().saveData(PlayerStat.getPlayerStat(p));
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        final Player p = e.getPlayer();
        if (plugin.getGm().getGameByPlayer(p) != null) {
            final GameDuo game = plugin.getGm().getGameByPlayer(p);
            game.removePlayer(p);
            if (game.getTeamsAlive() == 1)
                game.checkWin(p, game.getLastTeam(), game.getLastTeam().getGoals());
        }
        if (plugin.getGm().getGameFourByPlayer(p) != null) {
            final GameFour game = plugin.getGm().getGameFourByPlayer(p);
            game.removePlayer(p);
            if (game.getTeamsAlive() == 1)
                game.checkWin(game.getLastTeam(), game.getTeamPlayer(p), game.getLastTeam().getTeamPlayers().get(0));
        }
        for (final Player on : plugin.getSb().getSB().keySet())
            plugin.getSb().update(on);
        if (InventoryData.getInventoryData(p) != null) {
            InventoryData.getInventoryData(p).restore();
            InventoryData.remove(p);
        }
        plugin.getSb().remove(p);
        plugin.getTop().removeHolo(p);
        plugin.getDb().saveData(PlayerStat.getPlayerStat(p));
        plugin.getCb().saveData(PlayerStat.getPlayerStat(p));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        final Player p = e.getPlayer();
        if (plugin.getGm().getGameByPlayer(p) != null) {
            final GameDuo game = plugin.getGm().getGameByPlayer(p);
            if (game.isState(State.WAITING) || game.isState(State.STARTING) || game.isState(State.PREGAME)
                    || game.isState(State.PREGAME) || game.isState(State.FINISH) || game.isState(State.RESTARTING))
                e.setCancelled(true);
            if (game.isState(State.INGAME))
                if (!game.getPlaced().contains(e.getBlock().getLocation())) {
                    e.setCancelled(true);
                    p.sendMessage(plugin.getLang().get("messages.noBreak"));
                } else
                    PlayerStat.getPlayerStat(p).addBroken();
            return;
        }
        if (plugin.getGm().getGameFourByPlayer(p) != null) {
            final GameFour game = plugin.getGm().getGameFourByPlayer(p);
            if (game.isState(FState.WAITING) || game.isState(FState.STARTING) || game.isState(FState.PREGAME)
                    || game.isState(FState.PREGAME) || game.isState(FState.FINISH) || game.isState(FState.RESTARTING))
                e.setCancelled(true);
            if (game.isState(FState.INGAME))
                if (!game.getPlaced().contains(e.getBlock().getLocation())) {
                    e.setCancelled(true);
                    p.sendMessage(plugin.getLang().get("messages.noBreak"));
                } else
                    PlayerStat.getPlayerStat(p).addBroken();
            return;
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        final Player p = e.getPlayer();
        if (plugin.getGm().getGameByPlayer(p) != null) {
            final GameDuo game = plugin.getGm().getGameByPlayer(p);
            if (game.isState(State.WAITING) || game.isState(State.STARTING) || game.isState(State.PREGAME)
                    || game.isState(State.FINISH) || game.isState(State.RESTARTING))
                e.setCancelled(true);
            if (game.isState(State.INGAME))
                if (!game.getBuild().contains(e.getBlockPlaced().getLocation())) {
                    e.setCancelled(true);
                    p.sendMessage(plugin.getLang().get("messages.noPlace"));
                } else {
                    PlayerStat.getPlayerStat(p).addPlaced();
                    game.addPlace(e.getBlockPlaced().getLocation());
                }
            return;
        }
        if (plugin.getGm().getGameFourByPlayer(p) != null) {
            final GameFour game = plugin.getGm().getGameFourByPlayer(p);
            if (game.isState(FState.WAITING) || game.isState(FState.STARTING) || game.isState(FState.PREGAME)
                    || game.isState(FState.PREGAME) || game.isState(FState.FINISH) || game.isState(FState.RESTARTING))
                e.setCancelled(true);
            if (game.isState(FState.INGAME))
                if (!game.getBuild().contains(e.getBlockPlaced().getLocation())) {
                    e.setCancelled(true);
                    p.sendMessage(plugin.getLang().get("messages.noPlace"));
                } else {
                    PlayerStat.getPlayerStat(p).addPlaced();
                    game.addPlace(e.getBlockPlaced().getLocation());
                }
            return;
        }
    }

    public void respawn(Player d, Player k) {
        d.closeInventory();
        d.getInventory().clear();
        d.setHealth(d.getMaxHealth());
        if (k != null) {
            final GameDuo game = plugin.getGm().getGameByPlayer(k);
            if (game == null)
                return;
            game.addKill(k);
            PlayerStat.getPlayerStat(k).addNormalKills();
            d.teleport(game.getTeamPlayer(d).getTeamRespawn());
            game.giveKit(d, game.getTeamPlayer(d));
            if (d.getLastDamageCause() == null)
                for (final Player on : game.getGamePlayers())
                    on.sendMessage(plugin.getLang().get("deathMessages.custom")
                            .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                            .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                            .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName())
                            + plugin.getLang().get("deathMessages.player")
                            .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                            .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                            .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName()));
            else {
                final DamageCause cause = d.getLastDamageCause().getCause();
                if (cause.equals(DamageCause.VOID))
                    for (final Player on : game.getGamePlayers())
                        on.sendMessage(plugin.getLang().get("deathMessages.void")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                                .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName())
                                + plugin.getLang().get("deathMessages.player")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                                .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName()));
                else if (cause.equals(DamageCause.ENTITY_ATTACK))
                    for (final Player on : game.getGamePlayers())
                        on.sendMessage(plugin.getLang().get("deathMessages.damage")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                                .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName())
                                + plugin.getLang().get("deathMessages.player")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                                .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName()));
                else
                    for (final Player on : game.getGamePlayers())
                        on.sendMessage(plugin.getLang().get("deathMessages.custom")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                                .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName())
                                + plugin.getLang().get("deathMessages.player")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                                .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName()));
            }
        } else {
            final GameDuo game = plugin.getGm().getGameByPlayer(d);
            d.teleport(game.getTeamPlayer(d).getTeamRespawn());
            game.giveKit(d, game.getTeamPlayer(d));
            if (d.getLastDamageCause() == null)
                for (final Player on : game.getGamePlayers())
                    on.sendMessage(plugin.getLang().get("deathMessages.custom")
                            .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                            .replaceAll("<death>", d.getName()) + plugin.getLang().get("deathMessages.none"));
            else {
                final DamageCause cause = d.getLastDamageCause().getCause();
                if (cause.equals(DamageCause.VOID))
                    for (final Player on : game.getGamePlayers())
                        on.sendMessage(plugin.getLang().get("deathMessages.void")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<death>", d.getName()) + plugin.getLang().get("deathMessages.none"));
                else if (cause.equals(DamageCause.ENTITY_ATTACK))
                    for (final Player on : game.getGamePlayers())
                        on.sendMessage(plugin.getLang().get("deathMessages.damage")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<death>", d.getName()) + plugin.getLang().get("deathMessages.none"));
                else
                    for (final Player on : game.getGamePlayers())
                        on.sendMessage(plugin.getLang().get("deathMessages.custom")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<death>", d.getName()) + plugin.getLang().get("deathMessages.none"));
            }
        }
    }

    public void respawnFour(Player d, Player k) {
        d.closeInventory();
        d.getInventory().clear();
        d.setHealth(d.getMaxHealth());
        if (k != null) {
            final GameFour game = plugin.getGm().getGameFourByPlayer(k);
            game.addKill(k);
            PlayerStat.getPlayerStat(k).addFourKills();
            d.teleport(game.getTeamPlayer(d).getTeamRespawn());
            game.giveKit(d, game.getTeamPlayer(d));
            if (d.getLastDamageCause() == null)
                for (final Player on : game.getPlayers())
                    on.sendMessage(plugin.getLang().get("deathMessages.custom")
                            .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                            .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                            .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName())
                            + plugin.getLang().get("deathMessages.player")
                            .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                            .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                            .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName()));
            else {
                final DamageCause cause = d.getLastDamageCause().getCause();
                if (cause.equals(DamageCause.VOID))
                    for (final Player on : game.getPlayers())
                        on.sendMessage(plugin.getLang().get("deathMessages.void")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                                .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName())
                                + plugin.getLang().get("deathMessages.player")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                                .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName()));
                else if (cause.equals(DamageCause.ENTITY_ATTACK))
                    for (final Player on : game.getPlayers())
                        on.sendMessage(plugin.getLang().get("deathMessages.damage")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                                .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName())
                                + plugin.getLang().get("deathMessages.player")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                                .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName()));
                else
                    for (final Player on : game.getPlayers())
                        on.sendMessage(plugin.getLang().get("deathMessages.custom")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                                .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName())
                                + plugin.getLang().get("deathMessages.player")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<TKColor>", game.getTeamPlayer(k).getColor() + "")
                                .replaceAll("<death>", d.getName()).replaceAll("<player>", k.getName()));
            }
        } else {
            final GameFour game = plugin.getGm().getGameFourByPlayer(d);
            d.teleport(game.getTeamPlayer(d).getTeamRespawn());
            game.giveKit(d, game.getTeamPlayer(d));
            if (d.getLastDamageCause() == null)
                for (final Player on : game.getPlayers())
                    on.sendMessage(plugin.getLang().get("deathMessages.custom")
                            .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                            .replaceAll("<death>", d.getName()) + plugin.getLang().get("deathMessages.none"));
            else {
                final DamageCause cause = d.getLastDamageCause().getCause();
                if (cause.equals(DamageCause.VOID))
                    for (final Player on : game.getPlayers())
                        on.sendMessage(plugin.getLang().get("deathMessages.void")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<death>", d.getName()) + plugin.getLang().get("deathMessages.none"));
                else if (cause.equals(DamageCause.ENTITY_ATTACK))
                    for (final Player on : game.getPlayers())
                        on.sendMessage(plugin.getLang().get("deathMessages.damage")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<death>", d.getName()) + plugin.getLang().get("deathMessages.none"));
                else
                    for (final Player on : game.getPlayers())
                        on.sendMessage(plugin.getLang().get("deathMessages.custom")
                                .replaceAll("<TDColor>", game.getTeamPlayer(d).getColor() + "")
                                .replaceAll("<death>", d.getName()) + plugin.getLang().get("deathMessages.none"));
            }
        }
    }

}