package io.github.MGDCl.TheBridge.hologram;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;

public class TruenoHologram_v1_8_R3 implements TruenoHologram{

    private Location location;
    private ArrayList<String> lines;
    private double linesdistance = 0.30;

    private ArrayList<ArmorStand> armor_lines = new ArrayList<ArmorStand>();
    private ArrayList<EntityArmorStand> NmsArmorLines = new ArrayList<EntityArmorStand>();

    private Player player = null;

    @Override
    public void setupWorldHologram(Location loc, ArrayList<String> lines) {
        this.location = loc.clone();
        this.lines = lines;

    }

    @Override
    public void setupPlayerHologram(Player player, Location loc, ArrayList<String> lines) {
        this.player = player;
        this.location = loc.clone();
        this.lines = lines;

    }

    @Override
    public Location getLocation(){
        return this.location;
    }

    @Override
    public Player getPlayer(){
        return player;
    }

    private void NmsDestroy(EntityArmorStand hololine){
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(hololine.getId());
        ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
    }

    private Location getNmsLocation(EntityArmorStand hololine){
        return new Location(hololine.getWorld().getWorld(), hololine.locX, hololine.locY, hololine.locZ);
    }

    private void NmsSpawn(EntityArmorStand stand, String line, Location loc){
        stand.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        stand.setCustomName(line);
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setSmall(true);
        stand.setInvisible(true);
        stand.setBasePlate(false);
        stand.setArms(false);
        if(!line.equals("")){
            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(stand);
            ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    private void spawn(){
        int ind = 0;
        for(String line : lines){
            Location finalLoc = location.clone();
            finalLoc.setY(location.getY()+(linesdistance*lines.size()));
            if(this.player!=null){
                if(ind>0) finalLoc = getNmsLocation(NmsArmorLines.get(ind-1)); finalLoc.setY(finalLoc.getY()-linesdistance);
                WorldServer s = ((CraftWorld)this.location.getWorld()).getHandle();
                EntityArmorStand stand = new EntityArmorStand(s);
                NmsSpawn(stand, line, finalLoc);
                NmsArmorLines.add(stand);
            }
            else{
                if(ind>0) finalLoc = armor_lines.get(ind-1).getLocation(); finalLoc.setY(finalLoc.getY()-linesdistance);
                ArmorStand Armorline = (ArmorStand) location.getWorld().spawnEntity(finalLoc, EntityType.ARMOR_STAND);
                Armorline.setBasePlate(false);
                Armorline.setCustomNameVisible(true);
                Armorline.setGravity(false);
                Armorline.setCanPickupItems(false);
                Armorline.setCustomName(line);
                Armorline.setSmall(true);
                Armorline.setVisible(false);
                armor_lines.add(Armorline);
                if(line.equals("")) Armorline.remove();
            }
            ind++;
        }
    }

    private void despawn(){
        if(this.player!=null){
            for(EntityArmorStand nmsStand : NmsArmorLines){
                NmsDestroy(nmsStand);
            }
            NmsArmorLines.clear();
        }
        else{
            for(ArmorStand line : armor_lines){
                line.remove();
            }
            armor_lines.clear();
        }
    }

    public void setDistanceBetweenLines(Double distance){
        this.linesdistance = distance;
    }

    public void display(){
        spawn();
    }

    @Override
    public void update(ArrayList<String> lines){
        if(this.player != null){
            int ind = 0;
            for(String newline : lines){
                if(this.lines.size()>= ind){
                    String oldline = this.lines.get(ind);
                    if(!newline.equals(oldline)){
                        if(!newline.equals("")){
                            final EntityArmorStand oldstand = NmsArmorLines.get(ind);
                            Location finalLoc = location.clone();
                            finalLoc.setY(location.getY()+(linesdistance*lines.size()));
                            if(ind>0) finalLoc = getNmsLocation(NmsArmorLines.get(ind-1)); finalLoc.setY(finalLoc.getY()-linesdistance);
                            WorldServer s = ((CraftWorld)this.location.getWorld()).getHandle();
                            EntityArmorStand stand = new EntityArmorStand(s);
                            NmsSpawn(stand, newline, finalLoc);
                            this.NmsArmorLines.set(ind, stand);
                            this.lines.set(ind, newline);
                            NmsDestroy(oldstand);
                        }
                        else{
                            this.lines.set(ind, newline);
                            final EntityArmorStand oldstand = NmsArmorLines.get(ind);
                            NmsDestroy(oldstand);
                        }
                    }
                    ind++;
                }
                else{
                    Location finalLoc = location.clone();
                    finalLoc.setY(location.getY()+(linesdistance*lines.size()));
                    if(ind>0) finalLoc = getNmsLocation(NmsArmorLines.get(ind-1)); finalLoc.setY(finalLoc.getY()-linesdistance);
                    WorldServer s = ((CraftWorld)this.location.getWorld()).getHandle();
                    EntityArmorStand stand = new EntityArmorStand(s);
                    NmsSpawn(stand, newline, finalLoc);
                    this.NmsArmorLines.add(stand);
                    this.lines.add(newline);
                }
            }
            if(lines.size() > this.lines.size()){
                int dif = lines.size() - this.lines.size();
                for(int in = 0; in <=dif; in++){
                    int arrayind = (this.lines.size()-1)-in;
                    this.lines.remove(arrayind);
                    NmsDestroy(this.NmsArmorLines.get(arrayind));
                    this.NmsArmorLines.remove(arrayind);
                }
            }
        }
        else{
            int ind = 0;
            for(String newline : lines){
                if(this.lines.size()>= ind){
                    String oldline = this.lines.get(ind);
                    if(!newline.equals(oldline)){
                        if(newline != ""){
                            this.armor_lines.get(ind).setCustomName(newline);
                        }
                        else{
                            this.lines.set(ind, newline);
                            final ArmorStand oldstand = armor_lines.get(ind);
                            oldstand.remove();
                        }
                    }
                    ind++;
                }
                else{
                    Location finalLoc = location.clone();
                    finalLoc.setY(location.getY()+(linesdistance*lines.size()));
                    if(ind>0) finalLoc = armor_lines.get(ind-1).getLocation(); finalLoc.setY(finalLoc.getY()-linesdistance);
                    ArmorStand Armorline = (ArmorStand) location.getWorld().spawnEntity(finalLoc, EntityType.ARMOR_STAND);
                    Armorline.setBasePlate(false);
                    Armorline.setCustomNameVisible(true);
                    Armorline.setGravity(false);
                    Armorline.setCanPickupItems(false);
                    Armorline.setCustomName(newline);
                    Armorline.setSmall(true);
                    Armorline.setVisible(false);
                    armor_lines.add(Armorline);
                    this.lines.add(newline);
                }
            }
            if(lines.size() > this.lines.size()){
                int dif = lines.size() - this.lines.size();
                for(int in = 0; in <=dif; in++){
                    int arrayind = (this.lines.size()-1)-in;
                    this.lines.remove(arrayind);
                    this.armor_lines.get(arrayind).remove();
                    this.armor_lines.remove(arrayind);
                }
            }
        }
    }

    @Override
    public void updateLine(int index, String text){
        if(this.lines.size() >= index){
            int realindex = (this.lines.size()-1)-index;
            String oldtext = this.lines.get(realindex);
            if(!text.equals(oldtext)){
                if(this.player != null){
                    if(text != ""){
                        final EntityArmorStand oldstand = NmsArmorLines.get(realindex);
                        Location finalLoc = location.clone();
                        finalLoc.setY(location.getY()+(linesdistance*lines.size()));
                        if(realindex>0) finalLoc = getNmsLocation(NmsArmorLines.get(realindex-1)); finalLoc.setY(finalLoc.getY()-linesdistance);
                        WorldServer s = ((CraftWorld)this.location.getWorld()).getHandle();
                        EntityArmorStand stand = new EntityArmorStand(s);
                        NmsSpawn(stand, text, finalLoc);
                        this.NmsArmorLines.set(realindex, stand);
                        NmsDestroy(oldstand);
                    }
                    else{
                        this.lines.set(realindex, text);
                        final EntityArmorStand oldstand = NmsArmorLines.get(realindex);
                        NmsDestroy(oldstand);
                    }
                }
                else{
                    if(text != ""){
                        this.armor_lines.get(realindex).setCustomName(text);
                    }
                    else{
                        final ArmorStand oldstand = armor_lines.get(realindex);
                        oldstand.remove();
                    }
                }
                this.lines.set(realindex, text);
            }
        }

    }

    @Override
    public void removeLine(int index){
        if(this.lines.size() >= index){
            int realindex = (this.lines.size()-1)-index;
            if(this.player != null){
                final EntityArmorStand stand = NmsArmorLines.get(realindex);
                this.NmsArmorLines.remove(stand);
                NmsDestroy(stand);
            }
            else{
                this.armor_lines.get(realindex).remove();
            }
            this.lines.remove(realindex);
        }
    }

    @Override
    public void delete(){
        despawn();
        this.player = null;
        this.NmsArmorLines = new ArrayList<EntityArmorStand>();
        this.armor_lines = new ArrayList<ArmorStand>();
        this.lines = new ArrayList<String>();
        this.location = null;
    }

}
