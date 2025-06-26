package org.unitedlands.commands.handlers.room.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Room;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class RoomCreateCommand extends BaseCommandHandler {

    public RoomCreateCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1 && args.length != 3) {
            Messenger.sendMessageTemplate(sender, "info-room-create", null, true);
            return;
        }

        Player player = (Player) sender;

        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found", null, true);
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
                Messenger.sendMessageTemplate(sender, "error-room-overlap", null, true);
                return;
            }
        }

        newRoom.setName(args[0]);
        dungeon.addRoom(newRoom);

        if (!plugin.getDungeonManager().saveDungeon(dungeon)) {
            Messenger.sendMessageTemplate(sender, "save-error", null, true);
        } else {
            Messenger.sendMessageTemplate(sender, "save-success", null, true);
        }
    }

}
