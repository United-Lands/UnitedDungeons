package org.unitedlands.commands.handlers.room.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Room;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class RoomMoveCommand extends BaseCommandHandler {

    public RoomMoveCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("x", "y", "z");
        }
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            Messenger.sendMessageTemplate(sender, "info-room-move", null, true);
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

        var axis = args[0];
        Long distance = 0L;

        try {
            distance = Math.round(Double.parseDouble(args[1]));
        } catch (NumberFormatException ex) {
            Messenger.sendMessageTemplate(sender, "error-number-format", null, true);
            return;
        }

        for (Room otherRoom : dungeon.getRooms()) {
            if (room.equals(otherRoom))
                continue;

            if (room.getBoundingBox().overlaps(otherRoom.getBoundingBox())) {
                Messenger.sendMessageTemplate(sender, "error-room-overlap", null, true);
                return;
            }
        }

        dungeon.moveRoom(room, axis, distance);

        if (!plugin.getDungeonManager().saveDungeon(dungeon)) {
            Messenger.sendMessageTemplate(sender, "save-error", null, true);
        } else {
            Messenger.sendMessageTemplate(sender, "save-success", null, true);
        }
    }

}
