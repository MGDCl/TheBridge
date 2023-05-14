package io.github.MGDCl.TheBridge.cosmetics;

import io.github.MGDCl.TheBridge.TheBridge;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Cage {

    TheBridge plugin;
    private ItemStack icon;
    private String id;
    private String name;
    private String permission;
    private ArrayList<String> lore;
    private File filered;
    private File fileblue;
    private File fileyellow;
    private File filegreen;
    private File clear;
    private int price;
    private int slot;
    private boolean isBuy;

    public Cage(TheBridge plugin, String path, String id) {
        this.plugin = plugin;
        this.icon = new ItemStack(Material.valueOf(plugin.getCages().get(path + ".icon")), plugin.getCages().getInt(path + ".amount"), (short)plugin.getCages().getInt(path + ".data"));
        this.lore = new ArrayList<String>();
        for (String l : plugin.getCages().getList(path + ".description")) {
            lore.add(l.replaceAll("&", "§"));
        }
        this.filered = new File(plugin.getDataFolder(), "cages/" + plugin.getCages().get(path + ".files.red"));
        this.fileblue = new File(plugin.getDataFolder(), "cages/" + plugin.getCages().get(path + ".files.blue"));
        this.fileyellow = new File(plugin.getDataFolder(), "cages/" + plugin.getCages().get(path + ".files.yellow"));
        this.filegreen = new File(plugin.getDataFolder(), "cages/" + plugin.getCages().get(path + ".files.green"));
        this.clear = new File(plugin.getDataFolder(), "cages/" + plugin.getCages().get(path + ".clearFile"));
        this.id = id;
        this.name = plugin.getCages().get(path + ".name");
        this.permission = plugin.getCages().get(path + ".permission");
        this.price = plugin.getCages().getInt(path + ".price");
        this.slot = plugin.getCages().getInt(path + ".slot");
        this.isBuy = plugin.getCages().getBoolean(path + ".isBuy");
    }

    public File getClear() {
        return clear;
    }

    public String getPermission() {
        return permission;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public ItemStack getHasIcon() {
        ItemStack perm = icon;
        ItemMeta permM = perm.getItemMeta();
        List<String> nLore = new ArrayList<String>();;
        for (String msg : plugin.getCages().getList("unlocked")) {
            if (msg.contains("<description>")) {
                for (String l : lore) {
                    nLore.add(l);
                }
            } else {
                nLore.add(msg.replaceAll("&", "§").replaceAll("<price>", String.valueOf(price)));
            }
        }
        permM.setLore(nLore);
        permM.setDisplayName("§a" + name);
        perm.setItemMeta(permM);
        return perm;
    }

    public ItemStack getPermIcon() {
        ItemStack perm = icon;
        ItemMeta permM = perm.getItemMeta();
        List<String> nLore = new ArrayList<String>();;
        for (String msg : plugin.getCages().getList("noPerm")) {
            if (msg.contains("<description>")) {
                for (String l : lore) {
                    nLore.add(l);
                }
            } else {
                nLore.add(msg.replaceAll("&", "§").replaceAll("<price>", String.valueOf(price)));
            }
        }
        permM.setLore(nLore);
        permM.setDisplayName("§c" + name);
        perm.setItemMeta(permM);
        return perm;
    }

    public ItemStack getBuyIcon() {
        ItemStack perm = icon;
        ItemMeta permM = perm.getItemMeta();
        List<String> nLore = new ArrayList<String>();;
        for (String msg : plugin.getCages().getList("locked")) {
            if (msg.contains("<description>")) {
                for (String l : lore) {
                    nLore.add(l);
                }
            } else {
                nLore.add(msg.replaceAll("&", "§").replaceAll("<price>", String.valueOf(price)));
            }
        }
        permM.setLore(nLore);
        permM.setDisplayName("§c" + name);
        perm.setItemMeta(permM);
        return perm;
    }

    public ItemStack getNormalIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getLore() {
        return lore;
    }

    public File getFileRed() {
        return filered;
    }

    public File getFileBlue() {
        return fileblue;
    }

    public File getFileYellow() {
        return fileyellow;
    }

    public File getFileGreen() {
        return filegreen;
    }

}
