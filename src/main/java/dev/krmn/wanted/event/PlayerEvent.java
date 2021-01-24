package dev.krmn.wanted.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvent extends WantedEvent {
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
        setLevel(e.getEntity(), 0);
    }
}
