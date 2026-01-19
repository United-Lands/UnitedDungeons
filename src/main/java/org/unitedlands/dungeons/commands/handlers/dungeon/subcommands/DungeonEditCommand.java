package org.unitedlands.dungeons.commands.handlers.dungeon.subcommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class DungeonEditCommand extends BaseCommandHandler<UnitedDungeons> {

    public DungeonEditCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.getDungeonManager().getDungeonNames();
        }
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-dungeon-edit"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Player player = (Player) sender;
        Dungeon dungeon = plugin.getDungeonManager().getDungeon(args[0]);
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-dungeon-found-by-name"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        plugin.getDungeonManager().registerEditSessionForPlayer(player.getUniqueId(), dungeon);
        Messenger.sendMessage(sender, messageProvider.get("messages.dungeon-edit-start"), Map.of("dungeon-name", dungeon.getName()),
                messageProvider.get("messages.prefix"));
    }

}
