package dev.krmn.wanted.event;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

public abstract class WantedEvent implements Listener {
    private final FileConfiguration config;

    public WantedEvent(FileConfiguration config) {
        this.config = config;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
