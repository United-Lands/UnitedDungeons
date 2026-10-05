package org.unitedlands.dungeons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.dungeons.managers.EffectsManager;
import org.unitedlands.utils.United;

public class ServerListener implements Listener {


    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        DungeonManager.instance().loadDungeons();
        DungeonManager.instance().startChecks();
        United.logger().info("UnitedDungeons initialized.");
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        EffectsManager.instance().removeViewer(event.getPlayer());
    }
}
