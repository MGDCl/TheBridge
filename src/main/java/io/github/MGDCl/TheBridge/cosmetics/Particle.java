package io.github.MGDCl.TheBridge.cosmetics;

import io.github.MGDCl.TheBridge.TheBridge;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Particle {
    TheBridge plugin;

    private ItemStack icon;
    @Getter
    private String id;
    @Getter
    private String name;
    @Getter
    private String permission;
    @Getter
    private String trail;
    @Getter
    private int ammount;
    @Getter
    private ArrayList<String> lore;
    @Getter
    private int price;
    @Getter
    private int slot;
    @Getter
    private boolean isBuy;

    public Particle(TheBridge plugin, String path, String id) {
        this.plugin = plugin;
        this.icon = new ItemStack(Material.valueOf(plugin.getParticles().get(path + ".icon")),
                plugin.getParticles().getInt(path + ".amount"), (short) plugin.getParticles().getInt(path + ".data"));
        this.lore = new ArrayList<>();
        for (String l : plugin.getParticles().getList(path + ".description"))
            lore.add(l.replaceAll("&", "§"));
        this.id = id;
        this.name = plugin.getParticles().get(path + ".name");
        this.permission = plugin.getParticles().get(path + ".permission");
        this.price = plugin.getParticles().getInt(path + ".price");
        this.slot = plugin.getParticles().getInt(path + ".slot");
        this.isBuy = plugin.getParticles().getBoolean(path + ".isBuy");
        this.trail = plugin.getParticles().get(path + ".effect.type");
        this.ammount = plugin.getParticles().getInt(path + ".effect.ammount");
    }

    public ItemStack getHasIcon() {
        ItemStack perm = icon;
        ItemMeta permM = perm.getItemMeta();
        List<String> nLore = new ArrayList<>();

        for (String msg : plugin.getParticles().getList("unlocked"))
            if (msg.contains("<description>"))
                for (String l : lore)
                    nLore.add(l);
            else
                nLore.add(msg.replaceAll("&", "§").replaceAll("<price>", String.valueOf(price)));
        permM.setLore(nLore);
        permM.setDisplayName("§a" + name);
        perm.setItemMeta(permM);
        return perm;
    }

    public ItemStack getPermIcon() {
        ItemStack perm = icon;
        ItemMeta permM = perm.getItemMeta();
        List<String> nLore = new ArrayList<>();

        for (String msg : plugin.getParticles().getList("noPerm"))
            if (msg.contains("<description>"))
                for (String l : lore)
                    nLore.add(l);
            else
                nLore.add(msg.replaceAll("&", "§").replaceAll("<price>", String.valueOf(price)));
        permM.setLore(nLore);
        permM.setDisplayName("§c" + name);
        perm.setItemMeta(permM);
        return perm;
    }

    public ItemStack getBuyIcon() {
        ItemStack perm = icon;
        ItemMeta permM = perm.getItemMeta();
        List<String> nLore = new ArrayList<>();

        for (String msg : plugin.getParticles().getList("locked"))
            if (msg.contains("<description>"))
                for (String l : lore)
                    nLore.add(l);
            else
                nLore.add(msg.replaceAll("&", "§").replaceAll("<price>", String.valueOf(price)));
        permM.setLore(nLore);
        permM.setDisplayName("§c" + name);
        perm.setItemMeta(permM);
        return perm;
    }

    public ItemStack getNormalIcon() {
        return icon;
    }

}

