package dev.krmn.wanted;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.UUID;

public class WantedLevelManager {
    private static final int DEFAULT_MAX_LEVEL = 5;
    private static final WantedLevelManager instance = new WantedLevelManager();

    private static int maxLevel;

    private SpawnScheduler scheduler;
    private File levelFile;
    private FileConfiguration wantedLevel;
    private Objective objective;

    private WantedLevelManager() {
    }

    void init(Plugin plugin) {
        levelFile = new File(plugin.getDataFolder(), "wanted-level.yml");
        wantedLevel = YamlConfiguration.loadConfiguration(levelFile);
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        objective = scoreboard.getObjective("wanted");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("wanted", "dummy");
        }

        reloadConfig(plugin);
    }

    public static WantedLevelManager getInstance() {
        return instance;
    }

    public SpawnScheduler getScheduler() {
        return scheduler;
    }

    public void save() throws IOException {
        wantedLevel.save(levelFile);
    }

    public void reloadConfig(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        maxLevel = config.getInt("max-level", DEFAULT_MAX_LEVEL);
        for (String key : wantedLevel.getKeys(false)) {
            double level = wantedLevel.getDouble(key);
            if (level < 0) {
                wantedLevel.set(key, 0);
            } else if (level > maxLevel) {
                wantedLevel.set(key, maxLevel);
            }
        }

        if (scheduler != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                scheduler.cancel(player);
            }
            scheduler = null;
        }
        if (config.getBoolean("spawn")) {
            scheduler = new SpawnScheduler(config);
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateLevel(player);
            }
        }
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void addPlayer(Player player) {
        if (!wantedLevel.contains(player.getUniqueId().toString())) {
            setLevel(player, 0);
        } else {
            updateLevel(player);
        }
    }

    public double getLevel(Player target) {
        return wantedLevel.getDouble(target.getUniqueId().toString());
    }

    public double getLevel(UUID target) {
        return wantedLevel.getDouble(target.toString());
    }

    public void setLevel(Player target, double level) {
        if (level < 0 || level > maxLevel) {
            throw new IllegalArgumentException("Wanted level out of range.");
        }

        String uuid = target.getUniqueId().toString();
        int before = wantedLevel.getInt(uuid);
        wantedLevel.set(uuid, level);

        int after = (int) level;
        if (before != after) {
            updateLevel(target);
        }
    }

    public void setLevel(UUID target, double level) {
        if (level < 0 || level > maxLevel) {
            throw new IllegalArgumentException("Wanted level out of range.");
        }

        String uuid = target.toString();
        int before = wantedLevel.getInt(uuid);
        wantedLevel.set(uuid, level);

        Player player = Bukkit.getPlayer(target);
        if (player != null) {
            int after = (int) level;
            if (before != after) {
                updateLevel(player);
            }
        }
    }

    public void addLevel(Player target, double amount) {
        double current = getLevel(target);
        if (current + amount > maxLevel) {
            setLevel(target, maxLevel);
        } else {
            setLevel(target, current + amount);
        }
    }

    public void addLevel(UUID target, double amount) {
        double current = getLevel(target);
        if (current + amount > maxLevel) {
            setLevel(target, maxLevel);
        } else {
            setLevel(target, current + amount);
        }
    }

    private void updateLevel(Player player) {
        int level = (int) getLevel(player);
        Score score = objective.getScore(player.getName());
        score.setScore(level);
        if (scheduler != null) {
            scheduler.schedule(player, level);
        }
        try {
            Method sendPluginMessage = player.getClass().getMethod("sendPluginMessage", Plugin.class, String.class, byte[].class);
            sendPluginMessage.invoke(player, Wanted.getInstance(), "WantedLevel", ByteBuffer.allocate(4).putInt(level).array());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        player.sendMessage(ChatColor.GREEN + "手配度が変更されました: " + ChatColor.WHITE + toStars(level));
    }

    private String toStars(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append('★');
        }
        for (int i = 0; i < maxLevel - level; i++) {
            sb.append('☆');
        }
        return sb.toString();
    }
}
