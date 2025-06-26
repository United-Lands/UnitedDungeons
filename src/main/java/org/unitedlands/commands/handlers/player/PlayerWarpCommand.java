package org.unitedlands.commands.handlers.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class PlayerWarpCommand extends BaseCommandHandler {

    public PlayerWarpCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (((Player) sender).hasPermission("united.dungeon.admin"))
                return plugin.getDungeonManager().getDungeonNames();
            return plugin.getDungeonManager().getPublicDungeonNames();
        }
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Messenger.sendMessageTemplate(sender, "info-player-dungeon-warp", null, true);
            return;
        }

        Player player = (Player) sender;

        var dungeon = plugin.getDungeonManager().getDungeon(args[0]);
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found-by-name", null, true);
            return;
        }

        if (!dungeon.isPublic() && !player.hasPermission("united.dungeons.admin")) {
            Messenger.sendMessageTemplate(sender, "dungeon-status-not-public", null, true);
            return;
        }

        if (dungeon.getWarpLocation() == null) {
            Messenger.sendMessageTemplate(sender, "dungeon-status-no-warp", null, true);
            return;
        }

        Messenger.sendMessageTemplate(sender, "teleport", null, true);
        new BukkitRunnable() {
            int counter = 0;
            int maxExecutions = 3;

            Location startBlock = player.getLocation().getBlock().getLocation();

            @Override
            public void run() {
                counter++;

                if (counter <= maxExecutions) {
                    Messenger.sendMessage(player, (maxExecutions - counter + 1) + "...", true);
                }

                if (!player.getLocation().getBlock().getLocation().equals(startBlock)) {
                    Messenger.sendMessageTemplate(sender, "teleport-cancel", null, true);
                    this.cancel();
                }

                if (counter > maxExecutions) {
                    player.teleport(dungeon.getWarpLocation());
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
        return;
    }

}
