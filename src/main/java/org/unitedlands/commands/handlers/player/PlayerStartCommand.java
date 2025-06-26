package org.unitedlands.commands.handlers.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;

public class PlayerStartCommand extends BaseCommandHandler {

    public PlayerStartCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessageTemplate(sender, "info-player-dungeon-start", null, true);
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found", null, true);
            return;
        }

        var room = plugin.getDungeonManager().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            Messenger.sendMessageTemplate(sender, "error-not-in-room", null, true);
            return;
        }

        if (dungeon.isLocked()) {
            Messenger.sendMessageTemplate(sender, "dungeon-status-locked",
                    Map.of("lock-time", Formatter.formatDuration(dungeon.getRemainingLockTime())), true);
            return;
        }

        if (!dungeon.isLockable()) {
            Messenger.sendMessageTemplate(sender, "dungeon-status-not-lockable", null, true);
            return;
        }

        if (!room.enableLocking()) {
            Messenger.sendMessageTemplate(sender, "dungeon-room-not-lockable", null, true);
            return;
        }

        dungeon.lockDungeon(player);
    }

}
