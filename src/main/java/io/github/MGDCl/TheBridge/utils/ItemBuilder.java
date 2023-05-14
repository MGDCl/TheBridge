package io.github.MGDCl.TheBridge.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    public static ItemStack item(Material material, String displayName, String s) {
        final ItemStack itemStack = new ItemStack(material);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<String>() : Arrays.asList(s.split("\\n")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack item(Material material, int n, short n2, String displayName, String s) {
        final ItemStack itemStack = new ItemStack(material, n, n2);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<String>() : Arrays.asList(s.split("\\n")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack item(Material material, int n, short n2, String displayName, String s, Enchantment enchant, int level) {
        final ItemStack itemStack = new ItemStack(material, n, n2);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.addEnchant(enchant, level, true);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<String>() : Arrays.asList(s.split("\\n")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack item(Material material, int n, short n2, String displayName, List<String> s) {
        final ItemStack itemStack = new ItemStack(material, n, n2);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(s);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack item(Material material, int n, short n2, String displayName, List<String> s, Enchantment enchant, int level) {
        final ItemStack itemStack = new ItemStack(material, n, n2);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(s);
        itemMeta.addEnchant(enchant, level, true);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack skull(Material material, int n, short n2, String displayName, String s, String owner) {
        final ItemStack itemStack = new ItemStack(material, n, n2);
        final SkullMeta skullMeta = (SkullMeta)itemStack.getItemMeta();
        skullMeta.setOwner(owner);
        skullMeta.setDisplayName(displayName);
        skullMeta.setLore(s.isEmpty() ? new ArrayList<String>() : Arrays.asList(s.split("\\n")));
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    public static ItemStack createSkull(Material material, int n, short n2, String displayName, String s, String uri){
        if (!uri.startsWith("http://textures.minecraft.net/texture/"))
            uri = "http://textures.minecraft.net/texture/" + uri;
        final ItemStack head = new ItemStack(material, n, n2);
        final SkullMeta skullMeta = (SkullMeta)head.getItemMeta();
        skullMeta.setDisplayName(displayName);
        skullMeta.setLore(s.isEmpty() ? new ArrayList<String>() : Arrays.asList(s.split("\\n")));
        if (uri.isEmpty())
            return head;
        final GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        final byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", uri).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        try {
            final Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(skullMeta);
        return head;
    }

    public static ItemStack lore(Material material, short n, int n2, List<String> lore) {
        final ItemStack itemStack = new ItemStack(material, n2, n);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack lore(ItemStack itemStack, List<String> lore) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack lore(Material material, short n, int n2, String s) {
        final ItemStack itemStack = new ItemStack(material, n2, n);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final ArrayList<String> lore = new ArrayList<>();
        lore.add(s);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack lore(ItemStack itemStack, String s) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.add(s);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack name(Material material, short n, int n2, String displayName) {
        final ItemStack itemStack = new ItemStack(material, n2, n);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack name(ItemStack itemStack, String displayName) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack nameLore(Material material, short n, int n2, List<String> lore, String displayName) {
        final ItemStack itemStack = new ItemStack(material, n2, n);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack nameLore(ItemStack itemStack, List<String> lore, String displayName) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack nameLore(Material material, short n, int n2, String s, String displayName) {
        final ItemStack itemStack = new ItemStack(material, n2, n);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        final ArrayList<String> lore = new ArrayList<>();
        lore.add(s);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack nameLore(ItemStack itemStack, String s, String displayName) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        final ArrayList<String> lore = new ArrayList<>();
        lore.add(s);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
