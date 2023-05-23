package io.github.MGDCl.TheBridge.database;

import io.github.MGDCl.TheBridge.TheBridge;
import io.github.MGDCl.TheBridge.archievements.ArchiType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStat {

    public static HashMap<Player, PlayerStat> players = new HashMap<Player, PlayerStat>();
    @Getter
    private Player player;
    private UUID uuid;
    @Getter
    private String name;
    @Getter
    @Setter
    private ItemStack[] hotbar;
    @Getter
    @Setter
    private String cage;
    @Getter
    @Setter
    private String arrow_trail;
    @Getter
    @Setter
    private String feet_trail;
    @Getter
    @Setter
    private int normalWins;
    @Getter
    @Setter
    private int fourWins;
    @Getter
    @Setter
    private int normalKills;
    @Getter
    @Setter
    private int fourKills;
    @Getter
    @Setter
    private int normalDeaths;
    @Getter
    @Setter
    private int fourDeaths;
    @Getter
    @Setter
    private int normalGoals;
    @Getter
    @Setter
    private int fourGoals;
    @Getter
    @Setter
    private int coins;
    @Getter
    @Setter
    private int xp;
    @Getter
    @Setter
    private int broken;
    @Getter
    @Setter
    private int placed;

    public PlayerStat(Player p) {
        this.player = p;
        this.name = p.getName();
        this.uuid = p.getUniqueId();
        TheBridge.get().getDb().loadData(this);
        TheBridge.get().getCb().loadData(this);
        players.put(p, this);
    }

    public static PlayerStat getPlayerStat(Player p) {
        return players.get(p);
    }

    public void addBroken() {
        broken = broken + 1;
        if (!TheBridge.get().isArchiDisabled()) {
            TheBridge.get().getAm().check(player, broken, ArchiType.BLOCKS_BROKEN);
        }
    }

    public void addPlaced() {
        placed = placed + 1;
        if (!TheBridge.get().isArchiDisabled()) {
            TheBridge.get().getAm().check(player, placed, ArchiType.BLOCKS_PLACED);
        }
    }

    public void addNormalKills() {
        normalKills = normalKills + 1;
        xp += TheBridge.get().getConfig().getInt("xp.normal.kill");
        coins += TheBridge.get().getConfig().getInt("coins.normal.kill");
        if (!TheBridge.get().isArchiDisabled()) {
            TheBridge.get().getAm().check(player, normalKills + fourKills, ArchiType.KILLS);
        }
    }

    public void addNormalWins() {
        normalWins = normalWins + 1;
        xp += TheBridge.get().getConfig().getInt("xp.normal.win");
        coins += TheBridge.get().getConfig().getInt("coins.normal.win");
        if (!TheBridge.get().isArchiDisabled()) {
            TheBridge.get().getAm().check(player, normalWins + fourWins, ArchiType.WINS);
        }
    }

    public void addNormalGoals() {
        normalGoals = normalGoals + 1;
        xp += TheBridge.get().getConfig().getInt("xp.normal.goals");
        coins += TheBridge.get().getConfig().getInt("coins.normal.goals");
        if (!TheBridge.get().isArchiDisabled()) {
            TheBridge.get().getAm().check(player, normalGoals + fourGoals, ArchiType.GOALS);
        }
    }

    public void addFourKills() {
        fourKills = fourKills + 1;
        xp += TheBridge.get().getConfig().getInt("xp.four.kill");
        coins += TheBridge.get().getConfig().getInt("coins.four.kill");
        if (!TheBridge.get().isArchiDisabled()) {
            TheBridge.get().getAm().check(player, normalKills + fourKills, ArchiType.KILLS);
        }
    }

    public void addFourWins() {
        fourWins = fourWins + 1;
        xp += TheBridge.get().getConfig().getInt("coins.four.win");
        coins += TheBridge.get().getConfig().getInt("coins.four.win");
        if (!TheBridge.get().isArchiDisabled()) {
            TheBridge.get().getAm().check(player, normalWins + fourWins, ArchiType.WINS);
        }
    }

    public void addFourGoals() {
        fourGoals = fourGoals + 1;
        xp += coins + TheBridge.get().getConfig().getInt("xp.four.goals");
        coins += TheBridge.get().getConfig().getInt("coins.four.goals");
        if (!TheBridge.get().isArchiDisabled()) {
            TheBridge.get().getAm().check(player, normalGoals + fourGoals, ArchiType.GOALS);
        }
    }

    public String getUUID() {
        return uuid.toString();
    }

    public void addCoins(int coins) {
        this.coins = this.coins + coins;
    }

    public void removeCoins(int coins) {
        this.coins = this.coins - coins;
    }

    public void addXP(int xp) {
        this.xp = this.xp + xp;
    }

    public void removeXP(int xp) {
        this.xp = this.xp - xp;
    }

}
