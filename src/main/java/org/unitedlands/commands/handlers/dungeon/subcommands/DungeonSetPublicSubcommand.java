package org.unitedlands.commands.handlers.dungeon.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class DungeonSetPublicSubcommand extends BaseCommandHandler<UnitedDungeons> {

    public DungeonSetPublicSubcommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
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
            Messenger.sendMessage(sender, messageProvider.get("messages.info-dungeon-set-public"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-dungeon-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (dungeon.getWarpLocation() == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-public-no-warp"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (dungeon.getRooms() == null || dungeon.getRooms().size() == 0) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-public-no-rooms"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Boolean setPublic = Boolean.parseBoolean(args[0]);
        dungeon.setPublic(setPublic);

        plugin.getDungeonManager().saveDungeon(dungeon, sender);

    }

}
