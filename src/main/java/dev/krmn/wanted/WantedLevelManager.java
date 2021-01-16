package dev.krmn.wanted;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class WantedLevelManager {
    private static final int DEFAULT_MAX_LEVEL = 5;
    private static final WantedLevelManager instance = new WantedLevelManager();

    private static int maxLevel;

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
        maxLevel = plugin.getConfig().getInt("max-level", DEFAULT_MAX_LEVEL);
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
        wantedLevel.set(target.getUniqueId().toString(), level);
    }

    public void addLevel(Player target, double level) {
        double current = getLevel(target);
        if (current + level > maxLevel) {
            setLevel(target, maxLevel);
        } else {
            setLevel(target, current + level);
        }
    }
}
