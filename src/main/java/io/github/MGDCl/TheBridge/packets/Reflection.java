package io.github.MGDCl.TheBridge.packets;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import io.github.MGDCl.TheBridge.utils.V1_8;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.scoreboard.Scoreboard;
import org.json.simple.JSONObject;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Reflection {

    static String version = ServerPackage.getServerVersion();

    public enum Classes {

        CraftWorld(), CraftBlock(), CraftPlayer(), Material(), MapMeta(), WorldServer(), PacketPlayOutTitle(),
        IChatBaseComponent();

        private Class<?> type;

        public Class<?> getType() {
            return type;
        }
    }

    public enum Methods {

        setMapId(), getMapId(), getPlayerHandle(), getBlockType(), getChunkProviderServer(), getIChatBaseComponent();

        private Method mthd;

        public Method getMethod() {
            return mthd;
        }
    }

    public static void Enabler() {

        // PUTTING CLASSES IN ENUM.
        Classes.CraftWorld.type = getClass("{cb}.CraftWorld");
        Classes.CraftBlock.type = getClass("{cb}.block.CraftBlock");
        Classes.CraftPlayer.type = getClass("{cb}.entity.CraftPlayer");
        Classes.Material.type = getClass("{b}.Material");
        Classes.MapMeta.type = getClass("{b}.inventory.meta.MapMeta");
        Classes.WorldServer.type = getClass("{nms}.WorldServer");
        Classes.IChatBaseComponent.type = getClass("{nms}.IChatBaseComponent");
        Classes.PacketPlayOutTitle.type = getClass("{nms}.PacketPlayOutTitle");

        // PUTTING METHODS IN ENUM.
        Methods.setMapId.mthd = getMethod(Classes.MapMeta.getType(), "setMapId", int.class);
        Methods.getMapId.mthd = getMethod(Classes.MapMeta.getType(), "getMapId");
        Methods.getPlayerHandle.mthd = getMethod(Classes.CraftPlayer.getType(), "getHandle");
        Methods.getBlockType.mthd = getMethod(Classes.CraftBlock.getType(), "getType");
        Methods.getChunkProviderServer.mthd = getMethod(Classes.WorldServer.getType(), "getChunkProviderServer");
        Methods.getIChatBaseComponent.mthd = getMethod(Classes.IChatBaseComponent.getType().getDeclaredClasses()[0],
                "a", String.class);
    }

    public static void sendAction(Player player, String s) {
        if (Bukkit.getVersion().contains("1.8"))
            V1_8.sendActionbar(player, s);
    }

    public static void sendTitle(Player p, int fadein, int stay, int fadeout, String title, String subtitle) {
        try {
            final Object enumTitle = Classes.PacketPlayOutTitle.getType().getDeclaredClasses()[0].getField("TITLE")
                    .get(null);
            final Object enumSubtitle = Classes.PacketPlayOutTitle.getType().getDeclaredClasses()[0]
                    .getField("SUBTITLE").get(null);

            final Object titlebase = runMethod(null, Methods.getIChatBaseComponent.getMethod(),
                    "{\"text\": \"" + title + "\"}");
            final Object subtitlebase = runMethod(null, Methods.getIChatBaseComponent.getMethod(),
                    "{\"text\": \"" + subtitle + "\"}");

            final Class<?> packetcls = Classes.PacketPlayOutTitle.getType();
            final Constructor<?> constr = packetcls.getConstructor(
                    Classes.PacketPlayOutTitle.getType().getDeclaredClasses()[0], Classes.IChatBaseComponent.getType(),
                    int.class, int.class, int.class);

            final Object packetTitle = constr.newInstance(enumTitle, titlebase, fadein, stay, fadeout);
            final Object packetSubtitle = constr.newInstance(enumSubtitle, subtitlebase, fadein, stay, fadeout);

            sendPlayerPacket(p, packetTitle);
            sendPlayerPacket(p, packetSubtitle);
        }

        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    static JSONObject convert(String text) {
        final JSONObject json = new JSONObject();
        json.put("text", text);
        return json;
    }

    public static Class<?> getClass(String classname) {
        try {
            final String path = classname.replace("{nms}", "net.minecraft.server." + version)
                    .replace("{nm}", "net.minecraft." + version).replace("{cb}", "org.bukkit.craftbukkit." + version)
                    .replace("{b}", "org.bukkit");
            return Class.forName(path);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getCraftWorld(World w) {
        final Class<?> crwclass = Classes.CraftWorld.getType();
        System.out.println(crwclass.getName());
        final Object craftworld = crwclass.cast(w);
        return craftworld;
    }

    public static Object getWorldServer(Object craftWorld) {
        try {
            return getFieldValue(craftWorld, "world");
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getChunkProvider(Object worldServer) {
        try {
            return runMethod(worldServer, Methods.getChunkProviderServer.getMethod());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isTile(Block b) {
        return !b.getState().getClass().getSimpleName().toLowerCase().contains("craftblockstate");
    }

    public static Entity getEntity(Location l) {
        final Collection<Entity> ents = l.getWorld().getNearbyEntities(l, 1, 1, 1);
        for (final Entity ent : ents)
            return ent;
        return null;
    }

    public static void setmapId(ItemStack s, int id) {
        final MapMeta mapm = (MapMeta) s.getItemMeta();
        try {
            runMethod(mapm, Methods.setMapId.getMethod(), id);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        s.setItemMeta(mapm);
    }

    public static int getMapId(ItemStack s) {
        final MapMeta mapm = (MapMeta) s.getItemMeta();
        try {
            return (int) runMethod(mapm, Methods.getMapId.getMethod());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Object getNmsPlayer(Player p) {
        if (p == null)
            return null;
        Method getHandle;
        try {
            getHandle = p.getClass().getMethod("getHandle");
            return getHandle.invoke(p);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getNmsScoreboard(Scoreboard s) throws Exception {
        final Method getHandle = s.getClass().getMethod("getHandle");
        return getHandle.invoke(s);
    }

    public static Object getFieldValue(Object instance, String fieldName) throws Exception {
        final Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field field, Object obj) {
        try {
            return (T) field.get(obj);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) throws Exception {
        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... resl) {
        Method method;
        try {
            method = clazz.getDeclaredMethod(methodName, resl);
            method.setAccessible(true);
            return method;
        } catch (final Exception e) {
            return null;
        }
    }

    public static Object runMethod(Object obj, Method m, Object... resl) throws Exception {
        return m.invoke(obj, resl);
    }

    public static Object runMethod(Object obj, String name, Object... resl) throws Exception {
        final Class<?>[] classes = new Class<?>[resl.length];
        for (int i = 0; i < resl.length; i++)
            classes[i] = resl[i].getClass();
        return getMethod(obj.getClass(), name, classes).invoke(obj, resl);
    }

    public static void setValue(Object instance, String field, Object value) {
        try {
            final Field f = instance.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    public static void sendAllPacket(Object packet) throws Exception {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            final Object nmsPlayer = getNmsPlayer(p);
            final Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            connection.getClass().getMethod("sendPacket", getClass("{nms}.Packet")).invoke(connection, packet);
        }
    }

    public static int getPing(Player p) {
        try {
            final Object entityPlayer = Methods.getPlayerHandle.getMethod().invoke(p);
            return (int) getFieldValue(entityPlayer, "ping");
        } catch (final Exception e) {
            return -1;
        }
    }

    public static void sendListPacket(List<String> players, Object packet) {
        try {
            for (final String name : players) {
                final Object nmsPlayer = getNmsPlayer(Bukkit.getPlayer(name));
                final Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
                connection.getClass().getMethod("sendPacket", getClass("{nms}.Packet")).invoke(connection, packet);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPlayerPacket(Player p, Object packet) throws Exception {
        final Object nmsPlayer = getNmsPlayer(p);
        final Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
        connection.getClass().getMethod("sendPacket", getClass("{nms}.Packet")).invoke(connection, packet);
    }

    public static PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;

        try {
            final Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);

            command = c.newInstance(name, plugin);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return command;
    }

    public static CommandMap getCommandMap() {
        CommandMap commandMap = null;

        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                final Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);

                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (final NoSuchFieldException e) {
            e.printStackTrace();
        } catch (final SecurityException e) {
            e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }

        return commandMap;
    }
}
