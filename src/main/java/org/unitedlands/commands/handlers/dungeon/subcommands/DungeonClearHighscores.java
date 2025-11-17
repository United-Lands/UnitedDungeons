package org.unitedlands.commands.handlers.dungeon.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.HighScore;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class DungeonClearHighscores extends BaseCommandHandler<UnitedDungeons> {

    public DungeonClearHighscores(UnitedDungeons plugin, IMessageProvider messageProvider) {
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

        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-clear-highscores"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        dungeon = plugin.getDungeonManager().getDungeon(args[0]);
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-dungeon-found-by-name"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        dungeon.setHighscores(new ArrayList<HighScore>());

        Messenger.sendMessage(sender, messageProvider.get("messages.dungeon-highscores-cleared"), null,
                messageProvider.get("messages.prefix"));

        plugin.getDungeonManager().saveDungeon(dungeon, sender);

    }
}
