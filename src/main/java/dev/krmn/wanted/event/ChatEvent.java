package dev.krmn.wanted.event;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class ChatEvent extends WantedEvent {
    private final Map<String, Double> illegalWords = new HashMap<>();

    public ChatEvent(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection words = config.getConfigurationSection("words");
        if (words != null) {
            words.getValues(false)
                    .forEach((k, v) -> illegalWords.put(k.toLowerCase(), Double.parseDouble(v.toString())));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        illegalWords.forEach((k, v) -> {
            String message = e.getMessage().toLowerCase();
            if (message.contains(k)) {
                addLevel(e.getPlayer(), v);
            }
        });
    }
}
