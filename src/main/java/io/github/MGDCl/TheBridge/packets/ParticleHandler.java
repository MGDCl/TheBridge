package io.github.MGDCl.TheBridge.packets;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ParticleHandler {
    private static Constructor<?> packetConstructor = null;
    private static Field playerCon = null;
    private static Method sendPacket = null;
    private static HashMap<Class<? extends Entity>, Method> handles = new HashMap<>();

    private static Class<Enum> enump = null;

    private ParticleType type;
    @Getter
    private double speed;
    @Getter
    private int count;
    @Getter
    private double radius;

    public static void load() {
        String str = getVersion().replace("v", "");
        double version = 0;
        if (!str.isEmpty()) {
            String[] array = str.split("_");
            version = Double.parseDouble(array[0] + "." + array[1]);
        }
        try {
            if (version == 1.8) {
                Bukkit.getLogger().info("[ParticleHandler] Version is " + version + " - using packet constructor");
                enump = (Class<Enum>) getNmsClass("EnumParticle");
                packetConstructor = getNmsClass("PacketPlayOutWorldParticles").getDeclaredConstructor(enump,
                        boolean.class, float.class, float.class, float.class, float.class, float.class, float.class,
                        float.class, int.class, int[].class);
            } else
                Bukkit.getLogger().info("[ParticleHandler] Hooking into the new particle system");
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("[ParticleHandler] Failed to initialize NMS components!");
        }
    }

    public ParticleHandler(ParticleType type, double speed, int count, double radius) {
        this.type = type;
        this.speed = speed;
        this.count = count;
        this.radius = radius;
    }

    public void sendToLocation(Location location) {
        String vString = getVersion().replace("v", "");
        double v = 0;
        if (!vString.isEmpty()) {
            String[] array = vString.split("_");
            v = Double.parseDouble(array[0] + "." + array[1]);
        }
        if (v == 1.8)
            try {
                Object packet = createPacket(location);
                for (Player player : Bukkit.getOnlinePlayers())
                    sendPacket(player, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        //else
        //location.getWorld().spawnParticle(Particle.valueOf(type.getParticle().toUpperCase()),
        //(float) location.getX(), (float) location.getY(), (float) location.getZ(), this.count,
        //(float) this.radius, (float) this.radius, (float) this.radius, (float) this.speed);
    }

    private Object createPacket(Location location) {
        try {
            if (this.count <= 0)
                this.count = 1;
            Object packet;
            Object particleType = enump.getEnumConstants()[type.getId()];
            packet = packetConstructor.newInstance(particleType, true, (float) location.getX(), (float) location.getY(),
                    (float) location.getZ(), (float) this.radius, (float) this.radius, (float) this.radius,
                    (float) this.speed, this.count, new int[0]);
            return packet;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("{ParticleHandler] Failed to construct particle effect packet!");
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("{ParticleHandler] Failed to construct particle effect packet!");
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("{ParticleHandler] Failed to construct particle effect packet!");
        }
        return null;
    }

    private static void sendPacket(Player p, Object packet) throws IllegalArgumentException {
        try {
            if (playerCon == null) {
                playerCon = getHandle(p).getClass().getField("playerConnection");
                for (Method m : playerCon.get(getHandle(p)).getClass().getMethods())
                    if (m.getName().equalsIgnoreCase("sendPacket"))
                        sendPacket = m;
            }
            sendPacket.invoke(playerCon.get(getHandle(p)), packet);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("[ParticleHandler] Failed to send packet!");
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("[ParticleHandler] Failed to send packet!");
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("[ParticleHandler] Failed to send packet!");
        }
    }

    private static Object getHandle(Entity entity) {
        try {
            if (handles.get(entity.getClass()) != null)
                return handles.get(entity.getClass()).invoke(entity);
            else {
                Method entity_getHandle = entity.getClass().getMethod("getHandle");
                handles.put(entity.getClass(), entity_getHandle);
                return entity_getHandle.invoke(entity);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static Class<?> getNmsClass(String name) {
        String version = getVersion();
        String className = "net.minecraft.server." + version + name;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().severe("[ParticleHandler] Failed to load NMS class " + name + "!");
        }
        return clazz;
    }

    private static String getVersion() {
        String[] array = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",");
        if (array.length == 4)
            return array[3] + ".";
        return "";
    }

    @Getter
    public enum ParticleType {

        CRIT("crit", 9, 7),
        CRIT_MAGIC("magicCrit", 10, 8),
        SMOKE_NORMAL("smoke", 11, -1),
        SMOKE_LARGE("largesmoke", 12, 22),
        SPELL("spell", 13, 11),
        SPELL_INSTANT("instantSpell", 14, 12),
        SPELL_MOB("mobSpell", 15, 9),
        SPELL_MOB_AMBIENT("mobSpellAmbient", 16, 10),
        SLIME("slime", 33, 29),
        HEART("heart", 34, 30),
        BARRIER("barrier", 35, -1),
        ITEM_CRACK("iconcrack_", 36, 33),
        BLOCK_CRACK("tilecrack_", 37, 34),
        BLOCK_DUST("blockdust_", 38, -1),
        SPELL_WITCH("witchMagic", 17, 13),
        DRIP_WATER("dripWater", 18, 27),
        DRIP_LAVA("dripLava", 19, 28),
        VILLAGER_ANGRY("angryVillager", 20, 31),
        VILLAGER_HAPPY("happyVillager", 21, 32),
        TOWN_AURA("townaura", 22, 6),
        EXPLOSION_NORMAL("explode", 0, 17),
        EXPLOSION_LARGE("largeexplode", 1, 1),
        EXPLOSION_HUGE("hugeexplosion", 2, 0),
        FIREWORKS_SPARK("fireworksSpark", 3, 2),
        WATER_BUBBLE("bubble", 4, 3),
        WATER_SPLASH("splash", 5, 21),
        WATER_WAKE("wake", 6, -1),
        SUSPENDED("suspended", 7, 4),
        SUSPENDED_DEPTH("depthsuspend", 8, 5),
        NOTE("note", 23, 24),
        PORTAL("portal", 24, 15),
        ENCHANTMENT_TABLE("enchantmenttable", 25, 16),
        FLAME("flame", 26, 18),
        LAVA("lava", 27, 19),
        FOOTSTEP("footstep", 28, 20),
        CLOUD("cloud", 29, 23),
        REDSTONE("reddust", 30, 24),
        SNOWBALL("snowballpoof", 31, 25),
        SNOW_SHOVEL("snowshovel", 32, 28),
        WATER_DROP("droplet", 39, -1),
        ITEM_TAKE("take", 40, -1),
        MOB_APPEARANCE("mobappearance", 41, -1);

        private String particle;
        private int id;
        private int legId;

        ParticleType(String particle, int id, int legId) {
            this.particle = particle;
            this.id = id;
            this.legId = legId;
        }
    }
}
