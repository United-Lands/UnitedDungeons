package org.unitedlands.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.utils.Messenger;

public class PlayerEventListeners implements Listener {

    private final UnitedDungeons plugin;

    public PlayerEventListeners(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {

        if (event.getEntity().getShooter() instanceof Player) {

            var type = event.getEntityType();
            if (!(type == EntityType.ENDER_PEARL) && !(type == EntityType.WIND_CHARGE))
                return;

            Player player = (Player) event.getEntity().getShooter();

            var dungeon = plugin.getDungeonManager().getPlayerDungeon(player);
            if (dungeon != null) {
                if (type == EntityType.ENDER_PEARL && dungeon.disableEnderpearls()) {
                    Messenger.sendMessageTemplate(player, "enderpearl-disabled", null, true);
                    event.setCancelled(true);
                } else if (type == EntityType.WIND_CHARGE && dungeon.disableWindcharge()) {
                    Messenger.sendMessageTemplate(player, "windcharge-disabled", null, true);
                    event.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player) {
            var player = (Player) event.getEntity();
            if (event.isGliding()) {
                var dungeon = plugin.getDungeonManager().getPlayerDungeon(player);
                if (dungeon != null && dungeon.disableElytra()) {
                    event.setCancelled(true);
                    Messenger.sendMessageTemplate(player, "elytra-disabled", null, true);
                }
            }
        }
    }

}
