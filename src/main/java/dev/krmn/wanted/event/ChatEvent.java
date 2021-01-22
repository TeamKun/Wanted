package dev.krmn.wanted.event;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class ChatEvent extends WantedEvent {
    private final double wantedLevel;
    private final List<String> illegalWords;

    public ChatEvent(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        wantedLevel = config.getDouble("level.word");
        illegalWords = config.getStringList("words")
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (illegalWords.stream().anyMatch(e.getMessage().toLowerCase()::contains)) {
            addLevel(e.getPlayer(), wantedLevel);
        }
    }
}
