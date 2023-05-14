package io.github.MGDCl.TheBridge.sings;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class GameNormalSign {

    private Sign sign;
    private String game;
    private Block retract;

    public GameNormalSign(Location loc, String game, Block retract) {
        setSign((Sign)loc.getBlock().getState());
        setGame(game);
        setRetract(retract);
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public Block getRetract() {
        return retract;
    }

    public void setRetract(Block retract) {
        this.retract = retract;
    }

}
