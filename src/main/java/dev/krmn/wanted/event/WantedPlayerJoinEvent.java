package dev.krmn.wanted.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class WantedPlayerJoinEvent extends WantedEvent {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        setLevel(e.getPlayer(), 0);
    }
}
