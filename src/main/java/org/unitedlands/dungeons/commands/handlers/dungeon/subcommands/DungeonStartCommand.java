package org.unitedlands.dungeons.commands.handlers.dungeon.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class DungeonStartCommand extends BaseCommandHandler<UnitedDungeons> {

    public DungeonStartCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
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
        Dungeon dungeon = null;
        Player player = (Player) sender; 

        if (args.length == 0) {
            dungeon = plugin.getDungeonManager().getEditSessionForPlayr(player.getUniqueId());
            if (dungeon == null) {
                Messenger.sendMessage(sender, messageProvider.get("messages.error-no-edit-session"), null,
                        messageProvider.get("messages.prefix"));
                return;
            }
        } else if (args.length == 1) {
            dungeon = plugin.getDungeonManager().getDungeon(args[0]);
            if (dungeon == null) {
                Messenger.sendMessage(sender, messageProvider.get("messages.error-no-dungeon-found-by-name"), null,
                        messageProvider.get("messages.prefix"));
                return;
            }
        } else {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-dungeon-start"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (dungeon.getWarpLocation() == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-start-no-warp"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (dungeon.getRooms() == null || dungeon.getRooms().size() == 0) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-start-no-rooms"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        dungeon.setActive(true);
        dungeon.reset();

        Messenger.sendMessage(sender, messageProvider.get("messages.dungeon-started"), null,
                messageProvider.get("messages.prefix"));

        plugin.getDungeonManager().saveDungeon(dungeon, sender);

    }
}
