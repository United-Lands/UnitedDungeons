package org.unitedlands.dungeons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.utils.Logger;

public class ServerListener implements Listener {

    private final UnitedDungeons plugin;

    public ServerListener(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        plugin.getDungeonManager().loadDungeons();
        plugin.getDungeonManager().startChecks();
        Logger.log("UnitedDungeons initialized.", "UnitedDungeons");
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getEffectsManager().removeViewer(event.getPlayer());
    }
}
