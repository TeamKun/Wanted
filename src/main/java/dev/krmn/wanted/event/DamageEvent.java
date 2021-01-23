package dev.krmn.wanted.event;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

public class DamageEvent extends WantedEvent {
    private final double playerLevel;
    private final double mobLevel;

    public DamageEvent(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        playerLevel = config.getDouble("level.player");
        mobLevel = config.getDouble("level.mob");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            Entity entity = event.getEntity();
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (livingEntity.getHealth() - event.getDamage() > 0) {
                    return;
                }

                addLevel(player, livingEntity instanceof Player ? playerLevel : mobLevel);
            }
        }
    }
}
