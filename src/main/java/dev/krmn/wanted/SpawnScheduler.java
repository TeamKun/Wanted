package dev.krmn.wanted;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnScheduler {
    private Map<UUID, BukkitRunnable> scheduleMap = new HashMap<>();

    public SpawnScheduler() {
    }

    public void schedule(Player player, int interval) {

    }
}
