package dev.krmn.wanted;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class WantedLevelManager {
    private static final int MAX_LEVEL = 5;
    private static final WantedLevelManager instance = new WantedLevelManager();

    private File levelFile;
    private FileConfiguration wantedLevel;

    private WantedLevelManager() {
    }

    void init(Plugin plugin) {
        levelFile = new File(plugin.getDataFolder(), "wanted-level.yml");
        wantedLevel = YamlConfiguration.loadConfiguration(levelFile);
    }

    public static WantedLevelManager getInstance() {
        return instance;
    }

    public void save() throws IOException {
        wantedLevel.save(levelFile);
    }

    public int getLevel(Player target) {
        return wantedLevel.getInt(target.getUniqueId().toString());
    }

    public void setLevel(Player target, int level) {
        if (level < 0 || level > MAX_LEVEL) {
            throw new IllegalArgumentException("Wanted level out of range.");
        }
        wantedLevel.set(target.getUniqueId().toString(), level);
    }

    public void increment(Player target) {
        setLevel(target, getLevel(target) + 1);
    }

    public void decrement(Player target) {
        setLevel(target, getLevel(target) - 1);
    }
}
