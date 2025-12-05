package org.unitedlands.commands.handlers.room.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.Room;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.FieldHelper;
import org.unitedlands.utils.Messenger;

public class RoomInfoCommand extends BaseCommandHandler<UnitedDungeons> {

    public RoomInfoCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-room-info"), null, messageProvider.get("messages.prefix"));
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
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-room"), null, messageProvider.get("messages.prefix"));
            return;
        }

        Messenger.sendMessage(player, FieldHelper.getFieldValuesString(Room.class, room));

    }

}
