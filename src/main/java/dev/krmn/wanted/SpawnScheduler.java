package dev.krmn.wanted;

import com.destroystokyo.paper.entity.SentientNPC;
import dev.krmn.wanted.ast.Node;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class SpawnScheduler {
    private final Map<UUID, BukkitRunnable> scheduleMap = new HashMap<>();
    private final List<EntityType> day;
    private final List<EntityType> night;
    private final int range;
    private final int offset;
    private final Node intervalExp;
    private final Node amountExp;

    public SpawnScheduler(FileConfiguration config) {
        this.day = config.getStringList("mobs.day")
                .stream()
                .map(String::toUpperCase)
                .map(EntityType::valueOf)
                .collect(Collectors.toList());
        this.night = config.getStringList("mobs.night")
                .stream()
                .map(String::toUpperCase)
                .map(EntityType::valueOf)
                .collect(Collectors.toList());
        this.range = config.getInt("range");
        this.offset = config.getInt("offset");
        this.intervalExp = new Parser(config.getString("interval")).parse();
        this.amountExp = new Parser(config.getString("amount")).parse();
    }

    public void schedule(Player player, int level) {
        cancel(player);

        BukkitRunnable spawner = new BukkitRunnable() {
            @Override
            public void run() {
                Map<String, Double> variables = new HashMap<>();
                variables.put("level", (double) level);
                variables.put("random", Math.random());
                int amount = (int) amountExp.eval(variables);

                for (int i = 0; i < amount; i++) {
                    spawnEntity(player);
                }
            }
        };

        Map<String, Double> variables = new HashMap<>();
        variables.put("level", (double) level);
        variables.put("random", Math.random());
        spawner.runTaskTimer(Wanted.getInstance(), 0, (long) intervalExp.eval(variables));

        scheduleMap.put(player.getUniqueId(), spawner);
    }

    public void cancel(Player player) {
        BukkitRunnable runnable = scheduleMap.get(player.getUniqueId());
        if (runnable != null) {
            runnable.cancel();
        }
    }

    private void spawnEntity(Player player) {
        Location location = player.getLocation().toCenterLocation();
        World world = location.getWorld();
        Random random = new Random();
        double theta = Math.toRadians(random.nextDouble() * 360);
        double r = random.nextDouble() * range + offset;
        double x = Math.cos(theta) * r;
        double z = Math.sin(theta) * r;
        Location spawnLocation = new Location(world, x, location.getY(), z);

        EntityType entityType;
        if (world.getTime() < 12000) {
            entityType = day.get(random.nextInt(day.size()));
        } else {
            entityType = night.get(random.nextInt(night.size()));
        }

        Material material = spawnLocation.getBlock().getType();
        int direction = material == Material.AIR ? -1 : 1;
        int total = range + offset;
        int count = 0;
        do {
            double y = spawnLocation.getY();
            if (y < 1 || y > 250) {
                return;
            }
            spawnLocation.setY(y + direction);

            material = spawnLocation.getBlock().getType();
            if (material == Material.WATER || material == Material.STATIONARY_WATER ||
                    material == Material.LAVA || material == Material.STATIONARY_LAVA) {
                return;
            }

            count++;
        } while (material != Material.AIR && count <= total);

        Entity entity = world.spawnEntity(spawnLocation.add(0, -direction, 0), entityType);
        if (entity instanceof SentientNPC) {
            ((SentientNPC) entity).setTarget(player);
        }
    }
}
