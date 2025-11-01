package org.unitedlands.listeners;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
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

    @EventHandler(ignoreCancelled = true)
    private void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (event.getHand() != EquipmentSlot.HAND)
            return;

        var block = event.getClickedBlock();
        if (block == null || block.getType() != Material.YELLOW_SHULKER_BOX)
            return;

        var chest = plugin.getChestManager().getChestAtLocation(block.getLocation());
        if (chest == null)
            return;

        var player = event.getPlayer();
        var inventory = chest.getInventory(player.getUniqueId());
        if (inventory != null)
        {
            event.setCancelled(true);
            player.openInventory(inventory);
            chest.getLocation().getWorld().playSound(chest.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1, 1);

        }
    }

}
