package dev.krmn.wanted;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Wanted extends JavaPlugin {
    private static Wanted instance;

    @Override
    public void onEnable() {
        instance = this;

        WantedLevelManager.getInstance().init(this);
        //noinspection ConstantConditions
        getServer().getPluginCommand("wanted").setExecutor(new WantedCommand());
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
    }
}
