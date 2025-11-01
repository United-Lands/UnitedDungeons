package org.unitedlands.commands.handlers.dungeon.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.HighScore;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class DungeonClearHighscores extends BaseCommandHandler {

    public DungeonClearHighscores(UnitedDungeons plugin) {
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

        if (args.length != 1) {
            Messenger.sendMessageTemplate(sender, "info-clear-highscores", null, true);
            return;
        }

        dungeon = plugin.getDungeonManager().getDungeon(args[0]);
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found-by-name", null, true);
            return;
        }

        dungeon.setHighscores(new ArrayList<HighScore>());

        Messenger.sendMessageTemplate(sender, "dungeon-highscores-cleared", null, true);

        if (!plugin.getDungeonManager().saveDungeon(dungeon)) {
            Messenger.sendMessageTemplate(sender, "save-error", null, true);
        } else {
            Messenger.sendMessageTemplate(sender, "save-success", null, true);
        }
    }
}
