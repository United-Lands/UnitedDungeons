package org.unitedlands.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.unitedlands.UnitedDungeons;

public class ServerListener implements Listener {

    private final UnitedDungeons plugin;

    public ServerListener(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getEffectsManager().removeViewer(event.getPlayer());
    }
}
