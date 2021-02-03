package dev.krmn.wanted;

import net.kunmc.lab.gtawanteddisplaystarplugin.GTAWantedDisplayStarPlugin;
import net.kunmc.lab.gtawanteddisplaystarplugin.api.Flag;
import net.kunmc.lab.gtawanteddisplaystarplugin.api.StarDisplayAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WantedLevelManager {
    private static final int DEFAULT_MAX_LEVEL = 5;
    private static final WantedLevelManager instance = new WantedLevelManager();
    private static final StarDisplayAPI api = GTAWantedDisplayStarPlugin.getApi();

    private int maxLevel;
    private int wantedTime;
    private boolean isOutputEnabled;
    private boolean isSpeedUpEnabled;
    private final Map<UUID, BukkitRunnable> wantedTimerMap = new HashMap<>();
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
            objective = scoreboard.registerNewObjective("wanted", "dummy", "手配度");
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
        wantedTime = config.getInt("wanted-time") * 20;
        wantedTimerMap.clear();
        isOutputEnabled = config.getBoolean("output");
        isSpeedUpEnabled = config.getBoolean("speed-up");

        if (scheduler != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                scheduler.cancel(player);
            }
            scheduler = null;
        }
        if (config.getBoolean("spawn")) {
            scheduler = new SpawnScheduler(config);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateLevel(player);
            startTimer(player, (int) getLevel(player));
        }
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void addPlayer(Player player) {
        if (!wantedLevel.contains(player.getUniqueId().toString())) {
            setLevel(player, 0);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    api.showStar(player, 0, maxLevel);
                    startTimer(player, (int) getLevel(player));
                }
            }.runTaskLater(Wanted.getInstance(), 0);
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
        startTimer(target, after);
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
            startTimer(player, after);
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

        if (level == maxLevel) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, wantedTime, 1));
        }

        if (isOutputEnabled) {
            player.sendMessage(ChatColor.GREEN + "手配度が変更されました: " + ChatColor.WHITE + toStars(level));
        }
    }

    private void startTimer(Player player, int level) {
        BukkitRunnable runnable = wantedTimerMap.get(player.getUniqueId());
        if (runnable != null) {
            runnable.cancel();
        }
        BukkitRunnable timer = new BukkitRunnable() {
            @Override
            public void run() {
                BukkitRunnable blink;
                if (isSpeedUpEnabled) {
                    blink = new BukkitRunnable() {
                        int count = 10;

                        @Override
                        public void run() {
                            if (count == 0) {
                                setLevel(player, 0);
                                api.showStar(player, 0, maxLevel);
                                cancel();
                                return;
                            }

                            api.showStar(player, level, maxLevel, Flag.BLINK.setValue(100 + count * 10));
                            count--;
                        }
                    };
                    blink.runTaskTimer(Wanted.getInstance(), 0, 40);
                } else {
                    api.showStar(player, level, maxLevel, Flag.BLINK.setValue(200));
                    blink = new BukkitRunnable() {
                        @Override
                        public void run() {
                            setLevel(player, 0);
                            api.showStar(player, 0, maxLevel);
                        }
                    };
                    blink.runTaskLater(Wanted.getInstance(), 400);
                }
                wantedTimerMap.put(player.getUniqueId(), blink);
            }
        };
        timer.runTaskLater(Wanted.getInstance(), wantedTime - 400);
        wantedTimerMap.put(player.getUniqueId(), timer);
        api.showStar(player, level, maxLevel);
    }

    private String toStars(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLevel; i++) {
            if (i < maxLevel - level) {
                sb.append('☆');
            } else {
                sb.append('★');
            }
        }
        return sb.toString();
    }
}
