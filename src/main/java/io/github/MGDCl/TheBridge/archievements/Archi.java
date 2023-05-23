package io.github.MGDCl.TheBridge.archievements;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.database.PlayerStat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Archi {

    TheBridge plugin;
    private String name;
    private String description;
    private int max;
    private int xp;
    private int money;
    private ArchiType type;
    private ArrayList<String> cmds = new ArrayList<String>();
    private ArrayList<String> message = new ArrayList<String>();

    public Archi(TheBridge plugin, String path) {
        this.plugin = plugin;
        this.type = ArchiType.valueOf(plugin.getAchievement().get(path + ".type"));
        this.name = plugin.getAchievement().get(path + ".name");
        this.description = plugin.getAchievement().get(path + ".description");
        this.max = plugin.getAchievement().getInt(path + ".amount");
        this.xp = plugin.getAchievement().getInt(path + ".rewards.xp");
        this.money = plugin.getAchievement().getInt(path + ".rewards.money");
        for (String d : plugin.getAchievement().getList(path + ".messages")) {
            message.add(d);
        }
        for (String d : plugin.getAchievement().getList(path + ".commands")) {
            if (!d.equals("none")) {
                cmds.add(d);
            }
        }
    }

    public void execute(Player p) {//TODO enviar mensajes de recompensas
        for (String cmd : cmds) {
            cmd = cmd.replace("%player%", p.getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
        }
        PlayerStat ps = PlayerStat.getPlayerStat(p);
        ps.addCoins(money);
        ps.addXP(xp);
        p.playSound(p.getLocation(), Sound.valueOf(plugin.getSounds().get("sounds.archievement.reward")), 1.0f, 1.0f);
        for (String msg : message) {
            p.sendMessage(msg.replaceAll("&", "§"));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public ArchiType getType() {
        return type;
    }

    public void setType(ArchiType type) {
        this.type = type;
    }

    public ArrayList<String> getCmds() {
        return cmds;
    }

    public void setCmds(ArrayList<String> cmds) {
        this.cmds = cmds;
    }

    public ArrayList<String> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<String> message) {
        this.message = message;
    }

}
