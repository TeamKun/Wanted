package dev.krmn.wanted;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public class WantedLevelManager {
    private static final int DEFAULT_MAX_LEVEL = 5;
    private static final WantedLevelManager instance = new WantedLevelManager();

    private static int maxLevel;

    private SpawnScheduler scheduler;
    private File levelFile;
    private FileConfiguration wantedLevel;

    private WantedLevelManager() {
    }

    void init(Plugin plugin) {
        reloadConfig(plugin);

        levelFile = new File(plugin.getDataFolder(), "wanted-level.yml");
        wantedLevel = YamlConfiguration.loadConfiguration(levelFile);
    }

    public static WantedLevelManager getInstance() {
        return instance;
    }

    public void save() throws IOException {
        wantedLevel.save(levelFile);
    }

    public void reloadConfig(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        maxLevel = config.getInt("max-level", DEFAULT_MAX_LEVEL);
        scheduler = config.getBoolean("spawn") ? new SpawnScheduler(config) : null;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public double getLevel(Player target) {
        return wantedLevel.getDouble(target.getUniqueId().toString());
    }

    public void setLevel(Player target, double level) {
        if (level < 0 || level > maxLevel) {
            throw new IllegalArgumentException("Wanted level out of range.");
        }

        String uuid = target.getUniqueId().toString();
        int before = wantedLevel.getInt(uuid);
        int after = (int) level;
        if (before != after && scheduler != null) {
            scheduler.schedule(target, after);
            try {
                Method sendPluginMessage = target.getClass().getMethod("sendPluginMessage", Plugin.class, String.class, byte[].class);
                sendPluginMessage.invoke(target, Wanted.getInstance(), "WantedLevel", ByteBuffer.allocate(4).putInt(after).array());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                Bukkit.getLogger().severe(e.getMessage());
            }
        }

        wantedLevel.set(uuid, level);
    }

    public void addLevel(Player target, double amount) {
        double current = getLevel(target);
        if (current + amount > maxLevel) {
            setLevel(target, maxLevel);
        } else {
            setLevel(target, current + amount);
        }
    }
}
