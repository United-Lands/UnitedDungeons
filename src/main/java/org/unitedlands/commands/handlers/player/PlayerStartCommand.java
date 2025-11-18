package org.unitedlands.commands.handlers.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;

public class PlayerStartCommand extends BaseCommandHandler<UnitedDungeons> {

    public PlayerStartCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-player-dungeon-start"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-dungeon-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var room = plugin.getDungeonManager().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-room"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (dungeon.isOnCooldown()) {
            Messenger.sendMessage(sender, messageProvider.get("messages.dungeon-status-lock-cooldown"),
                    Map.of("cooldown-time", Formatter.formatDuration(dungeon.getRemainingCooldown())),
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (dungeon.isLocked()) {
            Messenger.sendMessage(sender, messageProvider.get("messages.dungeon-status-locked"),
                    Map.of("lock-time", Formatter.formatDuration(dungeon.getRemainingLockTime())),
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (!dungeon.isLockable()) {
            Messenger.sendMessage(sender, messageProvider.get("messages.dungeon-status-not-lockable"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (!room.enableLocking()) {
            Messenger.sendMessage(sender, messageProvider.get("messages.dungeon-room-not-lockable"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (dungeon.isPlayerOnLockCooldown(player.getUniqueId())) {

            Messenger.sendMessage(sender, messageProvider.get("messages.error-leader-still-on-lock-cooldown"),
                    Map.of("cooldown",
                            Formatter.formatDuration(dungeon.getPlayerRemainingLockCooldown(player.getUniqueId()))),
                    messageProvider.get("messages.prefix"));
            return;
        }

        dungeon.lockDungeon(player);
    }

}
