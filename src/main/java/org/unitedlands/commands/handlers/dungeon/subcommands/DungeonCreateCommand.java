package org.unitedlands.commands.handlers.dungeon.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class DungeonCreateCommand extends BaseCommandHandler {

    public DungeonCreateCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            Messenger.sendMessageTemplate(sender, "info-dungeon-create", null, true);
            return;
        }

        Player player = (Player) sender;

        var currentDungeons = plugin.getDungeonManager().getDungeons();
        int maxDungeons = plugin.getConfig().getInt("general.max-dungeons", 0);

        if (maxDungeons != -1 && currentDungeons.size() >= maxDungeons) {
            Messenger.sendMessageTemplate(sender, "error-max-dungeons", null, true);
            return;
        }

        var location = player.getLocation();
        var minDistance = plugin.getConfig().getDouble("general.min-dungeon-distance", 0);
        for (var otherDungeon : currentDungeons) {
            var distance = location.distance(otherDungeon.getLocation());
            if (distance < minDistance) {
                Messenger.sendMessageTemplate(sender, "error-dungeon-too-close", null, true);
                return;
            }
        }

        var name = args[0].trim();
        var existingDungeon = plugin.getDungeonManager().getDungeon(name);
        if (existingDungeon != null) {
            Messenger.sendMessageTemplate(sender, "error-dungeon-with-same-name", null, true);
            return;
        }

        var dungeon = new Dungeon(player.getLocation());
        dungeon.setName(name);

        plugin.getDungeonManager().addDungeon(dungeon);

        if (!plugin.getDungeonManager().saveDungeon(dungeon)) {
            Messenger.sendMessageTemplate(sender, "save-error", null, true);
        } else {
            Messenger.sendMessageTemplate(sender, "save-success", null, true);
        }
    }

}
