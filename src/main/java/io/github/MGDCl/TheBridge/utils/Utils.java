package io.github.MGDCl.TheBridge.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class Utils {

    public static void setCleanPlayer(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.setGameMode(GameMode.SURVIVAL);
        p.setSaturation(0);
        p.setFoodLevel(20);
        p.setMaxHealth(20);
        p.setHealth(p.getMaxHealth());
        p.setFireTicks(0);
        p.setExp(0);
        p.setLevel(0);
        for (final PotionEffect e : p.getActivePotionEffects())
            p.removePotionEffect(e.getType());
    }

    public static void setPlayerExperience(Player p, int amount) {
        if (amount <= 352) {
            final int level = (int) Math.floor(quadraticEquationRoot(1, 6, 0-amount));
            final double nextLevel = 2 * level + 7;
            final double levelExp = (level * level) + 6 * level;
            final double leftOver = amount - levelExp;
            p.setLevel(level);
            p.setExp((float) (leftOver/nextLevel));
        } else if (amount <= 1507) {
            final int level = (int) Math.floor(quadraticEquationRoot(2.5, -40.5, 360-amount));
            final double nextLevel = 5 * level - 38;
            final double levelExp = (int) (2.5 * (level * level) - 40.5 * level + 360);
            final double leftOver = amount - levelExp;
            p.setLevel(level);
            p.setExp((float) (leftOver/nextLevel));
        } else {
            final int level = (int) Math.floor(quadraticEquationRoot(4.5, -162.5, 2220-amount));
            final double nextLevel = 9 * level - 158;
            final double levelExp = (int) (4.5 * (level * level) - 162.5 * level + 2220);
            final double leftOver = amount - levelExp;
            p.setLevel(level);
            p.setExp((float) (leftOver/nextLevel));
        }
    }

    private static double quadraticEquationRoot(double a, double b, double c) {
        double root1, root2;
        root1 = (-b + Math.sqrt(Math.pow(b, 2) - 4*a*c)) / (2*a);
        root2 = (-b - Math.sqrt(Math.pow(b, 2) - 4*a*c)) / (2*a);
        return Math.max(root1, root2);
    }

}
