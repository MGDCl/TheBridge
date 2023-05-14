package io.github.MGDCl.TheBridge.hologram;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface TruenoHologram {

    public void setupWorldHologram(Location loc, ArrayList<String> lines);
    public void setupPlayerHologram(Player player, Location loc, ArrayList<String> lines);
    public Location getLocation();
    public Player getPlayer();
    public void setDistanceBetweenLines(Double distance);
    public void display();
    public void update(ArrayList<String> lines);
    public void updateLine(int index, String text);
    public void removeLine(int index);
    public void delete();

}
