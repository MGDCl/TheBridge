package io.github.MGDCl.TheBridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.MGDCl.TheBridge.cmds.SetupCMD;
import io.github.MGDCl.TheBridge.controllers.WorldController;
import io.github.MGDCl.TheBridge.database.Cosmeticbase;
import io.github.MGDCl.TheBridge.database.Database;
import io.github.MGDCl.TheBridge.database.PlayerStat;
import io.github.MGDCl.TheBridge.game.GameDuo;
import io.github.MGDCl.TheBridge.game.GameDuo.State;
import io.github.MGDCl.TheBridge.game.GameFour;
import io.github.MGDCl.TheBridge.game.GameFour.FState;
import io.github.MGDCl.TheBridge.game.InventoryData;
import io.github.MGDCl.TheBridge.game.PlayerData;
import io.github.MGDCl.TheBridge.hooks.PlaceholderHook;
import io.github.MGDCl.TheBridge.kit.Hotbar;
import io.github.MGDCl.TheBridge.kit.Kit;
import io.github.MGDCl.TheBridge.listeners.PlayerListener;
import io.github.MGDCl.TheBridge.listeners.SetupListener;
import io.github.MGDCl.TheBridge.listeners.SpectatorListener;
import io.github.MGDCl.TheBridge.managers.ArchiManager;
import io.github.MGDCl.TheBridge.managers.CageManager;
import io.github.MGDCl.TheBridge.managers.FileManager;
import io.github.MGDCl.TheBridge.managers.GameManager;
import io.github.MGDCl.TheBridge.managers.GlassManager;
import io.github.MGDCl.TheBridge.managers.LocationManager;
import io.github.MGDCl.TheBridge.managers.ParticleManager;
import io.github.MGDCl.TheBridge.managers.ScoreboardManager;
import io.github.MGDCl.TheBridge.managers.SetupManager;
import io.github.MGDCl.TheBridge.managers.SignManager;
import io.github.MGDCl.TheBridge.managers.TitleManager;
import io.github.MGDCl.TheBridge.managers.TopManager;
import io.github.MGDCl.TheBridge.menus.AchievementsMenu;
import io.github.MGDCl.TheBridge.menus.GameMenu;
import io.github.MGDCl.TheBridge.menus.GlassMenu;
import io.github.MGDCl.TheBridge.menus.ShopMenu;
import io.github.MGDCl.TheBridge.menus.SpectOptionsMenu;
import io.github.MGDCl.TheBridge.menus.SpectPlayerMenu;
import io.github.MGDCl.TheBridge.menus.TeamMenu;
import io.github.MGDCl.TheBridge.menus.particle.ArrowTrailMenu;
import io.github.MGDCl.TheBridge.menus.particle.FeetTrailMenu;
import io.github.MGDCl.TheBridge.menus.particle.TrailMenu;
import io.github.MGDCl.TheBridge.nms.GenericNMS;
import io.github.MGDCl.TheBridge.nms.NMS;
import io.github.MGDCl.TheBridge.packets.PacketMain;
import io.github.MGDCl.TheBridge.packets.ParticleHandler;
import lombok.Getter;
import lombok.Setter;

public class TheBridge extends JavaPlugin {

    private static TheBridge instance;
    @Getter
    @Setter
    private PacketMain refl;
    @Getter
    private SpectOptionsMenu som;
    @Getter
    private SpectPlayerMenu spm;
    @Getter
    private TeamMenu tem;
    @Getter
    private boolean placeholder;
    @Getter
    private TitleManager tm;
    @Getter
    private GameManager gm;
    @Getter
    private GameMenu gmu;
    @Getter
    private TrailMenu trm;
    @Getter
    private ArrowTrailMenu atm;
    @Getter
    private FeetTrailMenu ftm;
    @Getter
    private WorldController wc;
    @Getter
    private Settings lang;
    @Getter
    private Settings cages;
    @Getter
    private Settings signs;
    @Getter
    private Settings sounds;
    @Getter
    private Settings achievement;
    @Getter
    private Settings particles;
    @Getter
    private NMS nms;
    @Getter
    private FileManager fm;
    @Getter
    private SetupManager sm;
    @Getter
    private GlassManager glm;
    @Getter
    private LocationManager lm;
    @Getter
    private Location mainLobby;
    @Getter
    private Kit kit;
    @Getter
    private Database db;
    @Getter
    private TopManager top;
    @Getter
    private SignManager sim;
    @Getter
    private Hotbar hotbar;
    @Getter
    private boolean stop;
    @Getter
    private ScoreboardManager sb;
    @Getter
    private ArchiManager am;
    @Getter
    private AchievementsMenu archimenu;
    @Getter
    private CageManager cm;
    @Getter
    private ParticleManager pam;
    @Getter
    private GlassMenu glam;
    @Getter
    private boolean isCage = false;
    @Getter
    private boolean archiDisabled = false;
    @Getter
    private ShopMenu shop;
    @Getter
    private Cosmeticbase cb;

    public static TheBridge get() {
        return instance;
    }

    @Override
    public void onEnable() {
        refl = new PacketMain(this);
        ParticleHandler.load();
        instance = this;
        stop = false;
        getServer().getMessenger().registerOutgoingPluginChannel(TheBridge.get(), "BungeeCord");
        getConfig().options().copyDefaults(true);
        saveConfig();
        File s = new File(getDataFolder() + "/cages");
        if (!s.exists()) {
            s.mkdirs();
        }
        saveResources();
        lang = new Settings(this, "lang");
        signs = new Settings(this, "signs");
        cages = new Settings(this, "cages");
        sounds = new Settings(this, "sounds");
        achievement = new Settings(this, "achievement");
        particles = new Settings(this, "particles");
        if (getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
            if (!getConfig().getBoolean("cages")) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "You have " + ChatColor.RED
                        + "FAWE installed." + ChatColor.GREEN + "Cage System Enabled");
            } else {
                cm = new CageManager(this);
                isCage = true;
            }
        } else {
            if (getConfig().getBoolean("cages")) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "You don't have " + ChatColor.YELLOW
                        + "FAWE installed." + ChatColor.RED + "Cage System Disabled");
                isCage = false;
            }
        }
        archiDisabled = getConfig().getBoolean("archievements.disable");
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholder = true;
            new PlaceholderHook(this).register();
        }
        loadNMS();
        File a = new File(getDataFolder() + "/arenas");
        if (!a.exists()) {
            a.mkdirs();
        }
        File a1 = new File(getDataFolder() + "/maps");
        if (!a1.exists()) {
            a1.mkdirs();
        }
        db = new Database(this);
        cb = new Cosmeticbase(this);
        kit = new Kit();
        lm = new LocationManager(this);
        fm = new FileManager(this);
        wc = new WorldController(this);
        tm = new TitleManager(this);
        sim = new SignManager(this);
        gm = new GameManager(this);
        gmu = new GameMenu(this);
        trm = new TrailMenu(this);
        atm = new ArrowTrailMenu(this);
        ftm = new FeetTrailMenu(this);
        sm = new SetupManager(this);
        glm = new GlassManager();
        pam = new ParticleManager(this);
        top = new TopManager(this);
        tem = new TeamMenu(this);
        som = new SpectOptionsMenu(this);
        spm = new SpectPlayerMenu(this);
        hotbar = new Hotbar(this);
        sb = new ScoreboardManager(this);
        am = new ArchiManager(this);
        archimenu = new AchievementsMenu(this);
        glam = new GlassMenu(this);
        shop = new ShopMenu(this);

        lm.reloadLocations();
        top.updateTops();
        if (getConfig().getString("mainLobby") == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The main lobby hasn't been set up. Please use "
                    + ChatColor.YELLOW + "/bridges setmainlobby");
        } else {
            mainLobby = getStringLocation(getConfig().getString("mainLobby"));
        }
        getCommand("bridges").setExecutor(new SetupCMD(this));
        Bukkit.getServer().getPluginManager().registerEvents(new SetupListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new SpectatorListener(this), this);
        new BukkitRunnable() {
            @Override
            public void run() {
                top.updateTops();
            }
        }.runTaskTimer(this, 6000, 6000);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (GameDuo game : getGm().getGames()) {
                    if (game.isState(State.WAITING) || game.isState(State.STARTING) || game.isState(State.PREGAME)) {
                        game.updateGame();
                    }
                }
                for (GameFour game : getGm().getGamesFour()) {
                    if (game.isState(FState.WAITING) || game.isState(FState.STARTING) || game.isState(FState.PREGAME)) {
                        game.updateGame();
                    }
                }
            }
        }.runTaskTimer(this, 20, 20);
    }

    @Override
    public void onDisable() {
        stop = true;
        getTop().removeHolo();
        if (InventoryData.getInventoryData() != null || !InventoryData.getInventoryData().values().isEmpty()
                || !InventoryData.getInventoryData().isEmpty()) {
            for (InventoryData inv : InventoryData.getInventoryData().values()) {
                inv.restore();
            }
        }
        for (GameFour gamef : getGm().getGamesFour()) {
            for (PlayerData pd : gamef.getPD().values()) {
                pd.restore();
            }
        }
        for (GameDuo gamef : getGm().getGames()) {
            for (PlayerData pd : gamef.getPD().values()) {
                pd.restore();
            }
        }
        for (Player on : Bukkit.getOnlinePlayers()) {
            getDb().saveData(PlayerStat.getPlayerStat(on));
            getCb().saveData(PlayerStat.getPlayerStat(on));
        }
        getServer().getScheduler().cancelTasks(this);
    }

    public void loadNMS() {
        nms = new GenericNMS();
    }

    public void saveResources() {
        saveResource("clear.schematic", false);
        saveResource("normal-red.schematic", false);
        saveResource("normal-blue.schematic", false);
        saveResource("normal-green.schematic", false);
        saveResource("normal-yellow.schematic", false);
        File cage = new File(getDataFolder(), "cages");
        File c = new File(getDataFolder(), "clear.schematic");
        File r = new File(getDataFolder(), "normal-red.schematic");
        File b = new File(getDataFolder(), "normal-blue.schematic");
        File g = new File(getDataFolder(), "normal-green.schematic");
        File y = new File(getDataFolder(), "normal-yellow.schematic");
        copyFiles(c, new File(cage, "clear.schematic"));
        copyFiles(r, new File(cage, "normal-red.schematic"));
        copyFiles(b, new File(cage, "normal-blue.schematic"));
        copyFiles(g, new File(cage, "normal-green.schematic"));
        copyFiles(y, new File(cage, "normal-yellow.schematic"));
        c.delete();
        r.delete();
        g.delete();
        b.delete();
        y.delete();
    }

    public void copyFiles(File file, File file2) {
        try {
            if (!new ArrayList<String>(Arrays.asList("uid.dat", "session.dat")).contains(file.getName())) {
                if (file.isDirectory()) {
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    String[] list;
                    for (int length = (list = file.list()).length, i = 0; i < length; ++i) {
                        String s = list[i];
                        copyFiles(new File(file, s), new File(file2, s));
                    }
                } else {
                    FileOutputStream fileOutputStream;
                    try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        fileOutputStream = new FileOutputStream(file2);
                        byte[] array = new byte[1024];
                        int read;
                        while ((read = fileInputStream.read(array)) > 0) {
                            fileOutputStream.write(array, 0, read);
                        }
                    }
                    fileOutputStream.close();
                }
            }
        } catch (IOException ex) {
        }
    }

    public void reloadMainLobby() {
        mainLobby = getStringLocation(getConfig().getString("mainLobby"));
    }

    public Location getStringLocation(String location) {
        String[] l = location.split(";");
        World world = Bukkit.getWorld(l[0]);
        double x = Double.parseDouble(l[1]);
        double y = Double.parseDouble(l[2]);
        double z = Double.parseDouble(l[3]);
        float yaw = Float.parseFloat(l[4]);
        float pitch = Float.parseFloat(l[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

}