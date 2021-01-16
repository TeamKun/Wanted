package dev.krmn.wanted.event;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageEvent extends WantedEvent {
    public DamageEvent(FileConfiguration config) {
        super(config);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        String s = "\\${2,3}.*s";
    }
}
