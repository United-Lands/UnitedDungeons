package org.unitedlands.commands.handlers.spawner.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Spawner;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;

public class SpawnerInfoCommand extends BaseCommandHandler {

    public SpawnerInfoCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 0) {
            Messenger.sendMessageTemplate(sender, "info-spawner-info", null, true);
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

        Spawner spawner = null;
        for (Spawner s : room.getSpawners()) {
            if (s.getLocation().getBlock().equals(player.getLocation().getBlock())) {
                spawner = s;
            }
        }
        if (spawner == null) {
            Messenger.sendMessageTemplate(sender, "error-spawner-not-found", null, true);
            return;
        }

        player.sendMessage(Formatter.getFieldValuesString(Spawner.class, spawner));
    }

}
