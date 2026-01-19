package org.unitedlands.dungeons.commands.handlers.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class PlayerEntranceCommand extends BaseCommandHandler<UnitedDungeons> {

    public PlayerEntranceCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-chest-remove"), null, messageProvider.get("messages.prefix"));
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-dungeon-found"), null, messageProvider.get("messages.prefix"));
            return;
        }

        var room = plugin.getDungeonManager().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-room"), null, messageProvider.get("messages.prefix"));
            return;
        }

        Messenger.sendMessage(sender, messageProvider.get("messages.teleport"), null, messageProvider.get("messages.prefix"));

        new BukkitRunnable() {
            int counter = 0;
            int maxExecutions = 3;

            Location startBlock = player.getLocation().getBlock().getLocation();

            @Override
            public void run() {
                counter++;

                if (counter <= maxExecutions) {
                    Messenger.sendMessage(sender, (maxExecutions - counter + 1) + "...", null, messageProvider.get("messages.prefix"));
                }

                if (!player.getLocation().getBlock().getLocation().equals(startBlock)) {
                    Messenger.sendMessage(sender, messageProvider.get("messages.teleport-cancel"), null, messageProvider.get("messages.prefix"));
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
