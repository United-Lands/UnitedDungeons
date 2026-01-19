package org.unitedlands.dungeons.commands.handlers.room.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.Room;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class RoomCreateCommand extends BaseCommandHandler<UnitedDungeons> {

    public RoomCreateCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1 && args.length != 3) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-room-create"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Player player = (Player) sender;
        Dungeon dungeon = plugin.getDungeonManager().getEditSessionForPlayr(player.getUniqueId());
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-edit-session"), null,
                    messageProvider.get("messages.prefix"));
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
                Messenger.sendMessage(sender, messageProvider.get("messages.warning-room-overlap"), null,
                        messageProvider.get("messages.prefix"));
            }
        }

        newRoom.setName(args[0]);
        newRoom.setDungeon(dungeon);
        dungeon.addRoom(newRoom);

        plugin.getDungeonManager().saveDungeon(dungeon, sender);

    }

}
