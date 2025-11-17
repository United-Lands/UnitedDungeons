package org.unitedlands.commands.handlers.dungeon.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class DungeonCreateCommand extends BaseCommandHandler<UnitedDungeons> {

    public DungeonCreateCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-dungeon-create"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Player player = (Player) sender;

        var currentDungeons = plugin.getDungeonManager().getDungeons();
        int maxDungeons = plugin.getConfig().getInt("general.max-dungeons", 0);

        if (maxDungeons != -1 && currentDungeons.size() >= maxDungeons) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-max-dungeons"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var location = player.getLocation();
        var minDistance = plugin.getConfig().getDouble("general.min-dungeon-distance", 0);
        for (var otherDungeon : currentDungeons) {
            var distance = location.distance(otherDungeon.getLocation());
            if (distance < minDistance) {
                Messenger.sendMessage(sender, messageProvider.get("messages.error-dungeon-too-close"), null,
                        messageProvider.get("messages.prefix"));
                return;
            }
        }

        var name = args[0].trim();
        var existingDungeon = plugin.getDungeonManager().getDungeon(name);
        if (existingDungeon != null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-dungeon-with-same-name"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var dungeon = new Dungeon(player.getLocation());
        dungeon.setName(name);

        plugin.getDungeonManager().addDungeon(dungeon);

        plugin.getDungeonManager().saveDungeon(dungeon, sender);

    }

}
