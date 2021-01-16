package dev.krmn.wanted;

import dev.krmn.wanted.event.WantedEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class Wanted extends JavaPlugin {
    private static Wanted instance;

    @Override
    public void onEnable() {
        instance = this;
        WantedLevelManager.getInstance().init(this);
        //noinspection ConstantConditions
        getServer().getPluginCommand("wanted").setExecutor(new WantedCommand());
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

    }
}
