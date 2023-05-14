package io.github.MGDCl.TheBridge.listeners;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.game.GameFour;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SpectatorListener implements Listener {

    public TheBridge plugin;
    public SpectatorListener(TheBridge plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        final GameFour game = plugin.getGm().getGameFourByPlayer(e.getPlayer());
        if (game == null)
            return;
        if (game.getSpects().contains(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        final GameFour game = plugin.getGm().getGameFourByPlayer(e.getPlayer());
        if (game == null)
            return;
        if (game.getSpects().contains(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onChest(PlayerInteractEvent e) {
        final GameFour game = plugin.getGm().getGameFourByPlayer(e.getPlayer());
        if (game == null)
            return;
        if (game.getSpects().contains(e.getPlayer()))
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType() == Material.CHEST || e.getAction().equals(Action.LEFT_CLICK_BLOCK) && e.getClickedBlock().getType() == Material.CHEST)
                e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        final GameFour game = plugin.getGm().getGameFourByPlayer((Player)e.getWhoClicked());
        if (game == null)
            return;
        if (game.getSpects().contains(e.getWhoClicked())) {
            if (e.getClick().isShiftClick()) {
                final Inventory clicked = e.getClickedInventory();
                if (clicked == e.getWhoClicked().getInventory()) {
                    final ItemStack clickedOn = e.getCurrentItem();
                    if (clickedOn != null)
                        e.setCancelled(true);
                }
            }
            final Inventory clicked = e.getClickedInventory();
            if (clicked != e.getWhoClicked().getInventory()) {
                final ItemStack onCursor = e.getCursor();
                if (onCursor != null)
                    e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        final GameFour game = plugin.getGm().getGameFourByPlayer((Player)e.getWhoClicked());
        if (game == null)
            return;
        if (game.getSpects().contains(e.getWhoClicked()))
            e.setCancelled(true);
    }

    @EventHandler
    protected void onBlockCanBuild(BlockCanBuildEvent e) {
        if (!e.isBuildable()) {
            final Location blockL = e.getBlock().getLocation();
            boolean allowed = false;
            for (final Player target : Bukkit.getServer().getOnlinePlayers()) {
                final GameFour game = plugin.getGm().getGameFourByPlayer(target);
                if (game == null)
                    return;
                if (target.getWorld().equals(e.getBlock().getWorld()) && game.getSpects().contains(target)) {
                    final Location playerL = target.getLocation();
                    if (playerL.getX() > blockL.getBlockX()-1 && playerL.getX() < blockL.getBlockX()+1)
                        if (playerL.getZ() > blockL.getBlockZ()-1 && playerL.getZ() < blockL.getBlockZ()+1)
                            if (playerL.getY() > blockL.getBlockY()-2 && playerL.getY() < blockL.getBlockY()+1)
                                if (game.getSpects().contains(target)) {
                                    allowed = true;
                                    target.teleport(e.getBlock().getLocation().add(0, 5, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                } else {
                                    allowed = false;
                                    break;
                                }

                }
            }
            e.setBuildable(allowed);
        }
    }

    @EventHandler
    public void onPlayer(BlockPlaceEvent e) {
        final Location location = e.getBlock().getLocation();
        final GameFour game = plugin.getGm().getGameFourByPlayer(e.getPlayer());
        if (game == null)
            return;
        for (final Player on : Bukkit.getOnlinePlayers())
            if (game.getSpects().contains(on)) {
                final Location location2 = on.getLocation();
                if (location2.getX() > location.getBlockX() - 1 && location2.getX() < location.getBlockX() + 1 && location2.getZ() > location.getBlockZ() - 1 && location2.getZ() < location.getBlockZ() + 1 && location2.getY() > location.getBlockY() - 2 && location2.getY() < location.getBlockY() + 1)
                    on.teleport(on.getLocation().add(5, 5, 5));
            }
    }

    @EventHandler
    protected void onEntityDamageEvent(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            final GameFour game = plugin.getGm().getGameFourByPlayer((Player)e.getDamager());
            if (game == null)
                return;
            if ((!e.getDamager().hasMetadata("NPC") && game.getSpects().contains(e.getDamager())) || (!e.getEntity().hasMetadata("NPC") && game.getSpects().contains(e.getEntity())))
                e.setCancelled(true);
        } else if (!(e.getEntity() instanceof Player) && e.getDamager() instanceof Player) {
            final GameFour game = plugin.getGm().getGameFourByPlayer((Player)e.getDamager());
            if (game == null)
                return;
            if (!e.getDamager().hasMetadata("NPC") && game.getSpects().contains(e.getDamager()))
                e.setCancelled(true);
        } else if (e.getEntity() instanceof Player && !(e.getDamager() instanceof Player)) {
            final GameFour game = plugin.getGm().getGameFourByPlayer((Player)e.getEntity());
            if (game == null)
                return;
            if (!e.getEntity().hasMetadata("NPC") && game.getSpects().contains(e.getEntity()))
                e.setCancelled(true);
        }
        GameFour game = null;
        if (e.getEntity() instanceof Player)
            game = plugin.getGm().getGameFourByPlayer((Player)e.getEntity());
        if (game == null)
            return;
        if(e.getDamager() instanceof Projectile
                && !(e.getDamager() instanceof ThrownPotion)
                && !e.getEntity().hasMetadata("NPC")
                && game.getSpects().contains(e.getEntity())) {

            e.setCancelled(true);
            e.getDamager().remove();
            final Player spectatorInvolved = (Player) e.getEntity();
            final boolean wasFlying = spectatorInvolved.isFlying();
            final Location initialSpectatorLocation = spectatorInvolved.getLocation();

            final Vector initialProjectileVelocity = e.getDamager().getVelocity();
            final Location initialProjectileLocation = e.getDamager().getLocation();
            final Projectile proj = (Projectile) e.getDamager();



            if (spectatorInvolved != proj.getShooter()) {
                spectatorInvolved.setAllowFlight(true);
                spectatorInvolved.setFlying(true);
                spectatorInvolved.teleport(initialSpectatorLocation.clone().add(0, 6, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (proj instanceof Arrow) {
                        final Arrow arrow = initialProjectileLocation.getWorld().spawn(initialProjectileLocation, Arrow.class);
                        arrow.setBounce(false);
                        arrow.setVelocity(initialProjectileVelocity);
                        arrow.setShooter(proj.getShooter());
                    } else if (proj instanceof Snowball) {
                        final Snowball snowball = initialProjectileLocation.getWorld().spawn(initialProjectileLocation, Snowball.class);
                        snowball.setVelocity(initialProjectileVelocity);
                        snowball.setShooter(proj.getShooter());
                    } else if (proj instanceof Egg) {
                        final Egg egg = initialProjectileLocation.getWorld().spawn(initialProjectileLocation, Egg.class);
                        egg.setVelocity(initialProjectileVelocity);
                        egg.setShooter(proj.getShooter());
                    } else if (proj instanceof EnderPearl) {
                        final Player p = (Player) proj.getShooter();
                        p.launchProjectile(EnderPearl.class, initialProjectileVelocity);
                    }
                }, 1L);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    spectatorInvolved.teleport(new Location(initialSpectatorLocation.getWorld(), initialSpectatorLocation.getX(), initialSpectatorLocation.getY(), initialSpectatorLocation.getZ(), spectatorInvolved.getLocation().getYaw(), spectatorInvolved.getLocation().getPitch()), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    spectatorInvolved.setAllowFlight(true);
                    spectatorInvolved.setFlying(wasFlying);
                }, 5L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    protected void onPotionSplash(PotionSplashEvent e) {
        final ArrayList<UUID> spectatorsAffected = new ArrayList<>();
        GameFour game = null;
        if (e.getEntity().getShooter() instanceof Player)
            game = plugin.getGm().getGameFourByPlayer((Player)e.getEntity().getShooter());

        for(final LivingEntity player : e.getAffectedEntities())
            if(player instanceof Player && !player.hasMetadata("NPC") && game.getSpects().contains(player))
                spectatorsAffected.add(player.getUniqueId());
        if(!spectatorsAffected.isEmpty()) {
            Boolean teleportationNeeded = false;
            for(final Entity entity : e.getEntity().getNearbyEntities(2, 2, 2))
                if(entity instanceof Player && !entity.hasMetadata("NPC") && game.getSpects().contains(entity))
                    teleportationNeeded = true;
            final HashMap<UUID,Boolean> oldFlyMode = new HashMap<>();
            for(final UUID spectatorUUID : spectatorsAffected) {
                final Player spectator = Bukkit.getServer().getPlayer(spectatorUUID);
                e.setIntensity(spectator, 0);
                if(teleportationNeeded) {
                    oldFlyMode.put(spectator.getUniqueId(), spectator.isFlying());
                    spectator.setAllowFlight(true);
                    spectator.setFlying(true);

                    spectator.teleport(spectator.getLocation().add(0, 10, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }
            if(teleportationNeeded) {

                final Location initialProjectileLocation = e.getEntity().getLocation();
                final Vector initialProjectileVelocity = e.getEntity().getVelocity();

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                    final ThrownPotion clonedEntity = (ThrownPotion) e.getEntity().getWorld().spawnEntity(initialProjectileLocation, e.getEntity().getType());
                    clonedEntity.setShooter(e.getEntity().getShooter());
                    clonedEntity.setTicksLived(e.getEntity().getTicksLived());
                    clonedEntity.setFallDistance(e.getEntity().getFallDistance());
                    clonedEntity.setBounce(e.getEntity().doesBounce());
                    if(e.getEntity().getPassenger() != null)
                        clonedEntity.setPassenger(e.getEntity().getPassenger());
                    clonedEntity.setItem(e.getEntity().getItem());
                    clonedEntity.setVelocity(initialProjectileVelocity);
                    e.getEntity().remove();
                }, 1L);

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                    for(final UUID spectatorUUID : spectatorsAffected) {
                        final Player spectator = Bukkit.getServer().getPlayer(spectatorUUID);

                        spectator.teleport(spectator.getLocation().add(0, -10, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        spectator.setAllowFlight(true);
                        spectator.setFlying(oldFlyMode.get(spectatorUUID));
                    }
                }, 5L);

                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    protected void onPlayerPickupItem(PlayerPickupItemEvent e) {
        final GameFour game = plugin.getGm().getGameFourByPlayer(e.getPlayer());
        if (game == null)
            return;
        if (game.getSpects().contains(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    protected void onEntityTarget(EntityTargetEvent e) {
        GameFour game = null;
        if (e.getTarget() instanceof Player)
            game = plugin.getGm().getGameFourByPlayer((Player)e.getTarget());
        if (game == null)
            return;
        if (e.getTarget() != null && e.getTarget() instanceof Player && !e.getTarget().hasMetadata("NPC") && game.getSpects().contains(e.getTarget()))
            e.setCancelled(true);
        if (e.getTarget() instanceof Player && e.getTarget() != null && game.getSpects().contains(e.getTarget()))
            if (e.getEntity() instanceof ExperienceOrb) {
                repellExpOrb((Player) e.getTarget(), (ExperienceOrb) e.getEntity());
                e.setCancelled(true);
                e.setTarget(null);
            }
    }

    @EventHandler
    protected void onBlockDamage(BlockDamageEvent e) {
        final GameFour game = plugin.getGm().getGameFourByPlayer(e.getPlayer());
        if (game == null)
            return;
        if (game.getSpects().contains(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    protected void onEntityDamage(EntityDamageEvent e) {
        GameFour game = null;
        if (e.getEntity() instanceof Player)
            game = plugin.getGm().getGameFourByPlayer((Player)e.getEntity());
        if (game == null)
            return;
        if (e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC") && game.getSpects().contains(e.getEntity())) {
            e.setCancelled(true);
            e.getEntity().setFireTicks(0);
        }
    }

    @EventHandler
    protected void onFoodLevelChange(FoodLevelChangeEvent e) {
        GameFour game = null;
        if (e.getEntity() instanceof Player)
            game = plugin.getGm().getGameFourByPlayer((Player)e.getEntity());
        if (game == null)
            return;
        if (e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC") && game.getSpects().contains(e.getEntity())) {
            e.setCancelled(true);
            ((Player) e.getEntity()).setFoodLevel(20);
            ((Player) e.getEntity()).setSaturation(20);
        }
    }
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        final GameFour game = plugin.getGm().getGameFourByPlayer((Player)e.getWhoClicked());
        if (game == null)
            return;
        if (game.getSpects().contains(e.getWhoClicked()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent e) {
        GameFour game = null;
        if (e.getEntered() instanceof Player)
            game = plugin.getGm().getGameFourByPlayer((Player)e.getEntered());
        if (game == null)
            return;
        if (e.getEntered() instanceof Player && game.getSpects().contains(e.getEntered()))
            e.setCancelled(true);
    }

    void repellExpOrb(Player player, ExperienceOrb orb) {
        final Location pLoc = player.getLocation();
        final Location oLoc = orb.getLocation();
        final Vector dir = oLoc.toVector().subtract(pLoc.toVector());
        final double dx = Math.abs(dir.getX());
        final double dz = Math.abs(dir.getZ());
        if ( (dx == 0.0) && (dz == 0.0))
            dir.setX(0.001);
        if ((dx < 3.0) && (dz < 3.0)){
            final Vector nDir = dir.normalize();
            final Vector newV = nDir.clone().multiply(0.3);
            newV.setY(0);
            orb.setVelocity(newV);
            if ((dx < 1.0) && (dz < 1.0))
                orb.teleport(oLoc.clone().add(nDir.multiply(1.0)), PlayerTeleportEvent.TeleportCause.PLUGIN);
            if ((dx < 0.5) && (dz < 0.5))
                orb.remove();
        }
    }

}
