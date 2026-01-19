package org.unitedlands.dungeons.commands.handlers.room.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.Room;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class RoomMoveCommand extends BaseCommandHandler<UnitedDungeons> {

    public RoomMoveCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
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
            Messenger.sendMessage(sender, messageProvider.get("messages.info-room-move"), null,
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

        var room = plugin.getDungeonManager().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-room"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var axis = args[0];
        Long distance = 0L;

        try {
            distance = Math.round(Double.parseDouble(args[1]));
        } catch (NumberFormatException ex) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-number-format"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        for (Room otherRoom : dungeon.getRooms()) {
            if (room.equals(otherRoom))
                continue;

            if (room.getBoundingBox().overlaps(otherRoom.getBoundingBox())) {
                Messenger.sendMessage(sender, messageProvider.get("messages.warning-room-overlap"), null,
                        messageProvider.get("messages.prefix"));
            }
        }

        dungeon.moveRoom(room, axis, distance);

        plugin.getDungeonManager().saveDungeon(dungeon, sender);

    }

}
