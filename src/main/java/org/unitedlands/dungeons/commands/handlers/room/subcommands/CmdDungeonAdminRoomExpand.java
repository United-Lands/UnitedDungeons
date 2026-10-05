package org.unitedlands.dungeons.commands.handlers.room.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.Room;
import org.unitedlands.dungeons.commands.handlers.room.CmdDungeonAdminRoom;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonAdminRoom.class,
    name = "expand",
    usage = "/uda room expand <axis> <amount>",
    catchAll = true
)
public class CmdDungeonAdminRoomExpand implements UnitedCommandExecutor {
    
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
            sendUsage(sender);
            return;
        }

        Player player = (Player) sender;
        Dungeon dungeon = DungeonManager.instance().getEditSessionForPlayr(player.getUniqueId());
        if (dungeon == null) {
            United.messenger().send(sender, "error-no-edit-session");
            return;
        }

        var room = DungeonManager.instance().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            United.messenger().send(sender, "error-not-in-room");
            return;
        }

        var axis = args[0];
        Long value = 0L;

        try {
            value = Math.round(Double.parseDouble(args[1]));
        } catch (NumberFormatException ex) {
            United.messenger().send(sender, "error-number-format");
            return;
        }

        var bbox = room.getBoundingBox();
        var newWidth = bbox.getWidthX() + (value * 2);
        var newLength = bbox.getWidthZ() + (value * 2);
        var newHeight = bbox.getHeight() + (value * 2);

        if (newWidth < 2 || newLength < 2 || newHeight < 2) {
            United.messenger().send(sender, "error-room-too-small");
            return;
        }

        for (Room otherRoom : dungeon.getRooms()) {
            if (room.equals(otherRoom))
                continue;

            if (bbox.overlaps(otherRoom.getBoundingBox())) {
                United.messenger().send(sender, "warning-room-overlap");
            }
        }

        dungeon.expandRoom(room, axis, value);

        DungeonManager.instance().saveDungeon(dungeon, sender);

    }

}
