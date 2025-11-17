package org.unitedlands.commands.handlers.spawner.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.classes.Spawner;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.FieldHelper;
import org.unitedlands.utils.Messenger;

public class SpawnerInfoCommand extends BaseCommandHandler<UnitedDungeons> {

    public SpawnerInfoCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-spawner-info"), null,
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

        var room = plugin.getDungeonManager().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-room"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Spawner spawner = null;
        for (Spawner s : room.getSpawners()) {
            if (s.getLocation().getBlock().equals(player.getLocation().getBlock())) {
                spawner = s;
            }
        }
        if (spawner == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-spawner-not-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Messenger.sendMessage(player, FieldHelper.getFieldValuesString(Spawner.class, spawner));
    }

}
