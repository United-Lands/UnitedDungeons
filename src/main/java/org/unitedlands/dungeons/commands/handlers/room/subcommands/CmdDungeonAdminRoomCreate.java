package org.unitedlands.dungeons.commands.handlers.room.subcommands;

import java.util.ArrayList;
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
    name = "create",
    usage = "/uda room create <room_name> [width] [length]",
    catchAll = true
)
public class CmdDungeonAdminRoomCreate implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1 && args.length != 3) {
            sendUsage(sender);
            return;
        }

        Player player = (Player) sender;
        Dungeon dungeon = DungeonManager.instance().getEditSessionForPlayr(player.getUniqueId());
        if (dungeon == null) {
            United.messenger().send(sender, "error-no-edit-session");
            return;
        }

        var roomLocation = player.getLocation().add(0, 4, 0);
        Room newRoom = null;

        if (args.length == 3) {
            var width = 8;
            var length = 8;
            try {
                width = Integer.parseInt(args[1]);
                length = Integer.parseInt(args[2]);
                newRoom = new Room(roomLocation, width, length, 8);
            } catch (Exception ex) {
                newRoom = new Room(roomLocation, 8, 8, 8);
            }
        } else {
            newRoom = new Room(roomLocation, 8, 8, 8);
        }

        for (Room otherRoom : dungeon.getRooms()) {
            if (otherRoom.getBoundingBox().overlaps(newRoom.getBoundingBox())) {
                United.messenger().send(sender, "warning-room-overlap");
            }
        }

        newRoom.setName(args[0]);
        newRoom.setDungeon(dungeon);
        dungeon.addRoom(newRoom);

        DungeonManager.instance().saveDungeon(dungeon, sender);

    }

}
