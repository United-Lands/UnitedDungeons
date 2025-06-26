package org.unitedlands.commands.handlers.room.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class RoomStateCommand extends BaseCommandHandler {

    public RoomStateCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("complete", "reset", "edit");
        }
        return new ArrayList<String>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            Messenger.sendMessageTemplate(sender, "info-room-state", null, true);
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

        switch (args[0]) {
            case "complete":
                room.complete();
                break;
            case "reset":
                room.reset();
                break;
            case "edit":
                room.edit();
                break;
        }

    }

}
