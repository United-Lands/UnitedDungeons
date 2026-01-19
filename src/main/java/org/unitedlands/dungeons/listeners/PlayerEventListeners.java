package org.unitedlands.dungeons.listeners;

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
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

public class PlayerEventListeners implements Listener {

    private final UnitedDungeons plugin;
    private final MessageProvider messageProvider;

    public PlayerEventListeners(UnitedDungeons plugin, MessageProvider messageProvider) {
        this.plugin = plugin;
        this.messageProvider = messageProvider;
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
                    Messenger.sendMessage(player, messageProvider.get("messages.enderpearl-disabled"), null, messageProvider.get("messages.prefix"));
                    event.setCancelled(true);
                } else if (type == EntityType.WIND_CHARGE && dungeon.disableWindcharge()) {
                    Messenger.sendMessage(player, messageProvider.get("messages.windcharge-disabled"), null, messageProvider.get("messages.prefix"));
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
                    Messenger.sendMessage(player, messageProvider.get("messages.elytra-disabled"), null, messageProvider.get("messages.prefix"));
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
        if (block == null)
            return;

        var chest = plugin.getChestManager().getLootChestAtLocation(block.getLocation());
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
