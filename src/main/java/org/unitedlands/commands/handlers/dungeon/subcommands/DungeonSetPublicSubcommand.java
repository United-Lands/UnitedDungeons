package org.unitedlands.commands.handlers.dungeon.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class DungeonSetPublicSubcommand extends BaseCommandHandler {

    public DungeonSetPublicSubcommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return Arrays.asList("true", "false");
        return new ArrayList<String>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            Messenger.sendMessageTemplate(sender, "info-dungeon-set-public", null, true);
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found", null, true);
            return;
        }

        if (dungeon.getWarpLocation() == null) {
            Messenger.sendMessageTemplate(sender, "error-public-no-warp", null, true);
            return;
        }

        if (dungeon.getRooms() == null || dungeon.getRooms().size() == 0) {
            Messenger.sendMessageTemplate(sender, "error-public-no-rooms", null, true);
            return;
        }

        Boolean setPublic = Boolean.parseBoolean(args[0]);
        dungeon.setPublic(setPublic);

        if (!plugin.getDungeonManager().saveDungeon(dungeon)) {
            Messenger.sendMessageTemplate(sender, "save-error", null, true);
        } else {
            Messenger.sendMessageTemplate(sender, "save-success", null, true);
        }
    }

}
