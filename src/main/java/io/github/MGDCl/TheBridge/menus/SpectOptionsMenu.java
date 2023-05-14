package io.github.MGDCl.TheBridge.menus;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class SpectOptionsMenu {

    public HashMap<Player, Boolean> settings = new HashMap<Player, Boolean>();
    public TheBridge plugin;

    public SpectOptionsMenu(TheBridge plugin) {
        this.plugin = plugin;
    }

    public void openOptionsMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 36, plugin.getLang().get("menus.options.title"));
        ItemStack velo1 = ItemBuilder.item(Material.LEATHER_BOOTS, 1, (short) 0,
                plugin.getLang().get("menus.options.velocidad.nameItem").replaceAll("<#>", "I"),
                plugin.getLang().get("menus.options.velocidad.loreItem").replaceAll("<#>", "I"));
        ItemStack velo2 = ItemBuilder.item(Material.GOLD_BOOTS, 1, (short) 0,
                plugin.getLang().get("menus.options.velocidad.nameItem").replaceAll("<#>", "II"),
                plugin.getLang().get("menus.options.velocidad.loreItem").replaceAll("<#>", "II"));
        ItemStack velo3 = ItemBuilder.item(Material.CHAINMAIL_BOOTS, 1, (short) 0,
                plugin.getLang().get("menus.options.velocidad.nameItem").replaceAll("<#>", "III"),
                plugin.getLang().get("menus.options.velocidad.loreItem").replaceAll("<#>", "III"));
        ItemStack velo4 = ItemBuilder.item(Material.IRON_BOOTS, 1, (short) 0,
                plugin.getLang().get("menus.options.velocidad.nameItem").replaceAll("<#>", "IV"),
                plugin.getLang().get("menus.options.velocidad.loreItem").replaceAll("<#>", "IV"));
        ItemStack velo5 = ItemBuilder.item(Material.DIAMOND_BOOTS, 1, (short) 0,
                plugin.getLang().get("menus.options.velocidad.nameItem").replaceAll("<#>", "V"),
                plugin.getLang().get("menus.options.velocidad.loreItem").replaceAll("<#>", "V"));
        ItemStack visNo = ItemBuilder.item(Material.EYE_OF_ENDER, 1, (short) 0,
                plugin.getLang().get("menus.options.vision.nameItem").replaceAll("<type>",
                        plugin.getLang().get("menus.options.enable")),
                plugin.getLang().get("menus.options.vision.loreItem").replaceAll("<type>",
                        plugin.getLang().get("menus.options.enable")));
        ItemStack visSi = ItemBuilder.item(Material.ENDER_PEARL, 1, (short) 0,
                plugin.getLang().get("menus.options.vision.nameItem").replaceAll("<type>",
                        plugin.getLang().get("menus.options.disable")),
                plugin.getLang().get("menus.options.vision.loreItem").replaceAll("<type>",
                        plugin.getLang().get("menus.options.disable")));
        inv.setItem(11, velo1);
        inv.setItem(12, velo2);
        inv.setItem(13, velo3);
        inv.setItem(14, velo4);
        inv.setItem(15, velo5);
        if (settings.containsKey(p)) {
            if (settings.get(p) == true) {
                inv.setItem(22, visNo);
            } else {
                inv.setItem(22, visSi);
            }
        } else {
            settings.put(p, false);
            inv.setItem(22, visSi);
        }
        p.openInventory(inv);
    }

}
