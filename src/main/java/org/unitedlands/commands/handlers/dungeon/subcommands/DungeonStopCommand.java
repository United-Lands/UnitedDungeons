package org.unitedlands.commands.handlers.dungeon.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.Spawner;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class DungeonStopCommand extends BaseCommandHandler {

    public DungeonStopCommand(UnitedDungeons plugin) {
        super(plugin);
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
            dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
            if (dungeon == null) {
                Messenger.sendMessageTemplate(sender, "error-no-dungeon-found", null, true);
                return;
            }
        } else if (args.length == 1) {
            dungeon = plugin.getDungeonManager().getDungeon(args[0]);
            if (dungeon == null) {
                Messenger.sendMessageTemplate(sender, "error-no-dungeon-found-by-name", null, true);
                return;
            }
        } else {
            Messenger.sendMessageTemplate(sender, "info-dungeon-stop", null, true);
            return;
        }

        if (dungeon.getSpawners() != null) {
            for (Spawner s : dungeon.getSpawners()) {
                plugin.getMobManager().removeAllSpawnerMobs(s);
            }
        }

        dungeon.setActive(false);
        dungeon.reset();

        Messenger.sendMessageTemplate(sender, "dungeon-stopped", null, true);

        if (!plugin.getDungeonManager().saveDungeon(dungeon)) {
            Messenger.sendMessageTemplate(sender, "save-error", null, true);
        } else {
            Messenger.sendMessageTemplate(sender, "save-success", null, true);
        }
    }

}
