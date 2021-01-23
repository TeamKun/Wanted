package dev.krmn.wanted;

import dev.krmn.wanted.event.ChatEvent;
import dev.krmn.wanted.event.DamageEvent;
import dev.krmn.wanted.event.InteractEvent;
import dev.krmn.wanted.event.PlayerEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class Wanted extends JavaPlugin {
    private static Wanted instance;

    @Override
    public void onEnable() {
        instance = this;

        getServer().getMessenger().registerOutgoingPluginChannel(this, "WantedLevel");

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                Files.copy(getResource("config.yml"), configFile.toPath());
            } catch (IOException e) {
                getLogger().severe(e.getMessage());
            }
        }

        WantedLevelManager.getInstance().init(this);

        WantedCommand command = new WantedCommand();
        getServer().getPluginCommand("wanted").setExecutor(command);
        getServer().getPluginCommand("wanted").setTabCompleter(command);
        registerEvents();
    }

    @Override
    public void onDisable() {
        try {
            WantedLevelManager.getInstance().save();
        } catch (IOException e) {
            getLogger().severe(e.getMessage());
        }
    }

    public static Wanted getInstance() {
        return instance;
    }

    public void reload() {
        reloadConfig();
        WantedLevelManager.getInstance().reloadConfig(this);
        HandlerList.unregisterAll(this);
        registerEvents();
    }

    private void registerEvents() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new ChatEvent(this), this);
        manager.registerEvents(new DamageEvent(this), this);
        manager.registerEvents(new InteractEvent(this), this);
        manager.registerEvents(new PlayerEvent(), this);
    }
}
