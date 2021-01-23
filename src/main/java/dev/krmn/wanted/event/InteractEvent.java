package dev.krmn.wanted.event;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class InteractEvent extends WantedEvent {
    private final double wantedLevel;
    private final List<Material> illegalItems;

    public InteractEvent(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        wantedLevel = config.getDouble("level.item");
        illegalItems = config.getStringList("items")
                .stream()
                .map(String::toUpperCase)
                .map(Material::getMaterial)
                .collect(Collectors.toList());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (illegalItems.contains(e.getMaterial())) {
            addLevel(e.getPlayer(), wantedLevel);
        }
    }
}
