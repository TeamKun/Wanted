package dev.krmn.wanted.event;

import dev.krmn.wanted.WantedLevelManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class WantedEvent implements Listener {
    private final WantedLevelManager manager = WantedLevelManager.getInstance();

    public WantedLevelManager getManager() {
        return manager;
    }

    protected void getLevel(Player player) {
        manager.getLevel(player);
    }

    protected void setLevel(Player player, double level) {
        manager.setLevel(player, level);
    }

    protected void addLevel(Player player, double amount) {
        manager.addLevel(player, amount);
    }
}
