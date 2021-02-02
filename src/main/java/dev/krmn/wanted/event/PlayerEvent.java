package dev.krmn.wanted.event;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class PlayerEvent extends WantedEvent {
    private final Map<Material, Integer> dropItems = new HashMap<>();

    public PlayerEvent(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection drops = config.getConfigurationSection("drops");
        if (drops != null) {
            drops.getValues(false)
                    .forEach((k, v) -> dropItems.put(Material.getMaterial(k.toUpperCase()), Integer.parseInt(v.toString())));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        getManager().addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        getManager().getScheduler().cancel(e.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        double level = getLevel(player);
        if (level == getManager().getMaxLevel()) {
            World world = player.getWorld();
            dropItems.forEach((k, v) -> world.dropItem(player.getLocation(), new ItemStack(k, v)));
        }
        setLevel(player, 0);
    }
}
