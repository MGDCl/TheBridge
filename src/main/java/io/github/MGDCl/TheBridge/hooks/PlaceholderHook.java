package io.github.MGDCl.TheBridge.hooks;

import io.github.MGDCl.TheBridge.database.PlayerStat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderHook extends PlaceholderExpansion {

    private Plugin p;

    public PlaceholderHook(Plugin p) {
        this.p = p;
    }

    @Override
    public String getAuthor() {
        return "Stefatorus";
    }

    @Override
    public String getIdentifier() {
        return "bridges";
    }

    @Override
    public String getVersion() {
        return p.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player p, String id) {
        if (p == null || !p.isOnline()) {
            return null;
        }
        PlayerStat ps = PlayerStat.getPlayerStat(p);
        if (id.equals("normal_kills")) {
            return String.valueOf(ps.getNormalKills());
        }
        if (id.equals("normal_wins")) {
            return String.valueOf(ps.getNormalWins());
        }
        if (id.equals("normal_goals")) {
            return String.valueOf(ps.getNormalGoals());
        }
        if (id.equals("four_kills")) {
            return String.valueOf(ps.getFourKills());
        }
        if (id.equals("four_wins")) {
            return String.valueOf(ps.getFourWins());
        }
        if (id.equals("four_goals")) {
            return String.valueOf(ps.getFourGoals());
        }
        if (id.equals("global_kills")) {
            return String.valueOf(ps.getFourKills() + ps.getNormalKills());
        }
        if (id.equals("global_wins")) {
            return String.valueOf(ps.getFourWins() + ps.getNormalWins());
        }
        if (id.equals("global_goals")) {
            return String.valueOf(ps.getFourGoals() + ps.getNormalGoals());
        }
        if (id.equals("coins")) {
            return String.valueOf(ps.getCoins());
        }
        if (id.equals("xp")) {
            return String.valueOf(ps.getXp());
        }
        if (id.equals("blocks_placed")) {
            return String.valueOf(ps.getPlaced());
        }
        if (id.equals("blocks_broken")) {
            return String.valueOf(ps.getBroken());
        }
        return null;
    }

}
