package org.unitedlands.dungeons.commands.handlers.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;

public class PlayerHighscoresCommand extends BaseCommandHandler<UnitedDungeons> {

    public PlayerHighscoresCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (((Player) sender).hasPermission("united.dungeon.admin"))
                return plugin.getDungeonManager().getDungeonNames();
            return plugin.getDungeonManager().getPublicDungeonNames();
        }
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-player-highscores"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var dungeon = plugin.getDungeonManager().getDungeon(args[0]);
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-dungeon-found-by-name"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var highscores = dungeon.getHighscores();
        Map<String, String> replacements = new HashMap<>();
        replacements.put("time1", "-");
        replacements.put("time2", "-");
        replacements.put("time3", "-");
        replacements.put("time4", "-");
        replacements.put("time5", "-");
        replacements.put("players1", "-");
        replacements.put("players2", "-");
        replacements.put("players3", "-");
        replacements.put("players4", "-");
        replacements.put("players5", "-");

        for (int i = 0; i < highscores.size(); i++) {
            replacements.put("time" + (i + 1), Formatter.formatDuration(highscores.get(i).getTime()));
            replacements.put("players" + (i + 1), highscores.get(i).getPlayers());
        }

        replacements.put("dungeon-name", dungeon.getCleanName());

        Messenger.sendMessage(sender, messageProvider.getList("messages.dungeon-highscores"), replacements);

    }

}
