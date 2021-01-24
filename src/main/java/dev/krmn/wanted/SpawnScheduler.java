package dev.krmn.wanted;

import dev.krmn.wanted.ast.Node;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class SpawnScheduler {
    private final Map<UUID, BukkitRunnable> scheduleMap = new HashMap<>();
    private final List<EntityType> day;
    private final List<EntityType> night;
    private final int range;
    private final int offset;
    private final int limit;
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
        this.limit = config.getInt("limit");
        this.intervalExp = new Parser(config.getString("interval")).parse();
        this.amountExp = new Parser(config.getString("amount")).parse();
    }

    public void schedule(Player player, int level) {
        cancel(player);

        if (level > 0) {
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
            spawner.runTaskTimer(Wanted.getInstance(), 0, (long) (intervalExp.eval(variables) * 20));

            scheduleMap.put(player.getUniqueId(), spawner);
        }
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
        if (world.getNearbyLivingEntities(location, range + offset).size() > limit) {
            return;
        }

        Random random = new Random();
        double theta = Math.toRadians(random.nextDouble() * 360);
        double r = random.nextDouble() * range + offset;
        double x = Math.cos(theta) * r;
        double z = Math.sin(theta) * r;
        Location spawnLocation = new Location(world, location.getX() + x, location.getY() - 10, location.getZ() + z);

        EntityType entityType;
        if (world.getTime() < 12000) {
            entityType = day.get(random.nextInt(day.size()));
        } else {
            entityType = night.get(random.nextInt(night.size()));
        }

        Material material = spawnLocation.getBlock().getType();
        if (material == Material.AIR) {
            return;
        }

        while (material != Material.AIR) {
            spawnLocation.add(0, 1, 0);
            material = spawnLocation.getBlock().getType();
            if (material == Material.WATER || material == Material.LAVA) {
                return;
            }
        }

        Entity entity = world.spawnEntity(spawnLocation, entityType);
        if (entity instanceof Mob) {
            ((Mob) entity).setTarget(player);
        }
    }
}
