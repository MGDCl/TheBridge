package io.github.MGDCl.TheBridge.managers;

import io.github.MGDCl.TheBridge.TheBridge;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TitleManager {

    TheBridge plugin;
    private final String preTitle;
    private final String preSubTitle;
    private final String startTitle;
    private final String startSubTitle;
    private final String restartTitle;
    private final String restartSubTitle;
    private final int pfadein;
    private final int pstayin;
    private final int pfadeout;
    private final int sfadein;
    private final int sstayin;
    private final int sfadeout;
    private final int rfadein;
    private final int rstayin;
    private final int rfadeout;

    public TitleManager(TheBridge plugin) {
        this.plugin = plugin;
        this.preTitle = plugin.getLang().get("titles.prestart.title");
        this.preSubTitle = plugin.getLang().get("titles.prestart.subtitle");
        this.pfadein = plugin.getConfig().getInt("title.prestart.fadein");
        this.pstayin = plugin.getConfig().getInt("title.prestart.stayin");
        this.pfadeout = plugin.getConfig().getInt("title.prestart.fadeout");
        this.startTitle = plugin.getLang().get("titles.start.title");
        this.startSubTitle = plugin.getLang().get("titles.start.subtitle");
        this.sfadein = plugin.getConfig().getInt("title.start.fadein");
        this.sstayin = plugin.getConfig().getInt("title.start.stayin");
        this.sfadeout = plugin.getConfig().getInt("title.start.fadeout");
        this.restartTitle = plugin.getLang().get("titles.restart.title");
        this.restartSubTitle = plugin.getLang().get("titles.restart.subtitle");
        this.rfadein = plugin.getConfig().getInt("title.restart.fadein");
        this.rstayin = plugin.getConfig().getInt("title.restart.stayin");
        this.rfadeout = plugin.getConfig().getInt("title.restart.fadeout");
    }

    public void sendPreTitle(Player p, int time, boolean s) {
        if (s)
            plugin.getNms().sendTitle(p, pfadein, pstayin, pfadeout, preTitle.replaceAll("<none>", "").replaceAll("<time>", String.valueOf(time)).replaceAll("<s>", "s"), preSubTitle.replaceAll("<none>", "").replaceAll("<time>", String.valueOf(time)).replaceAll("<s>", "s"));
        else
            plugin.getNms().sendTitle(p, pfadein, pstayin, pfadeout, preTitle.replaceAll("<none>", "").replaceAll("<time>", String.valueOf(time)).replaceAll("<s>", ""), preSubTitle.replaceAll("<none>", "").replaceAll("<time>", String.valueOf(time)).replaceAll("<s>", ""));
    }

    public void sendStartTitle(Player p, int time, boolean s) {
        if (s)
            plugin.getNms().sendTitle(p, sfadein, sstayin, sfadeout, startTitle.replaceAll("<none>", "").replaceAll("<time>", getColor(time) + String.valueOf(time)).replaceAll("<s>", "s"), startSubTitle.replaceAll("<none>", "").replaceAll("<time>", getColor(time) + String.valueOf(time)).replaceAll("<s>", "s"));
        else
            plugin.getNms().sendTitle(p, sfadein, sstayin, sfadeout, startTitle.replaceAll("<none>", "").replaceAll("<time>", getColor(time) + String.valueOf(time)).replaceAll("<s>", ""), startSubTitle.replaceAll("<none>", "").replaceAll("<time>", getColor(time) + String.valueOf(time)).replaceAll("<s>", ""));
    }

    public void sendReStartTitle(final Player w, final Player p, final int time, final boolean s) {
        if (s)
            this.plugin.getNms().sendTitle(p, this.rfadein, this.rstayin, this.rfadeout, this.restartTitle.replaceAll("<color>", new StringBuilder().append(this.plugin.getGm().getGameByPlayer(p).getTeamPlayer(w).getColor()).toString()).replaceAll("<player>", w.getName()).replaceAll("<none>", "").replaceAll("<time>", String.valueOf(time)).replaceAll("<s>", "s"), this.restartSubTitle.replaceAll("<color>", new StringBuilder().append(this.plugin.getGm().getGameByPlayer(p).getTeamPlayer(w).getColor()).toString()).replaceAll("<player>", w.getName()).replaceAll("<none>", "").replaceAll("<time>", String.valueOf(time)).replaceAll("<s>", "s"));
        else
            this.plugin.getNms().sendTitle(p, this.rfadein, this.rstayin, this.rfadeout, this.restartTitle.replaceAll("<color>", new StringBuilder().append(this.plugin.getGm().getGameByPlayer(p).getTeamPlayer(w).getColor()).toString()).replaceAll("<player>", w.getName()).replaceAll("<none>", "").replaceAll("<time>", String.valueOf(time)).replaceAll("<s>", ""), this.restartSubTitle.replaceAll("<color>", new StringBuilder().append(this.plugin.getGm().getGameByPlayer(p).getTeamPlayer(w).getColor()).toString()).replaceAll("<player>", w.getName()).replaceAll("<none>", "").replaceAll("<time>", String.valueOf(time)).replaceAll("<s>", ""));
    }

    private ChatColor getColor(int time) {
        if (time > 10)
            return ChatColor.GREEN;
        return ChatColor.RED;
    }

}
