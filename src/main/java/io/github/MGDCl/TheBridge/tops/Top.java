package io.github.MGDCl.TheBridge.tops;

import io.github.MGDCl.TheBridge.TheBridge;

public class Top {

    private BoardType type;
    private int amount;
    private int top;
    private String name;

    public Top(String name, int amount, BoardType type) {
        this.type = type;
        this.amount = amount;
        this.name = name;
        this.top = TheBridge.get().getTop().getTopNumber(this);
        TheBridge.get().getTop().addTop(this);
    }

    public BoardType getType() {
        return type;
    }

    public int getTop() {
        return top;
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

}
