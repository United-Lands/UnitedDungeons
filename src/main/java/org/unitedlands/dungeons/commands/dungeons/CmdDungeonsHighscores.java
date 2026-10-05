package org.unitedlands.dungeons.commands.dungeons;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.CmdDungeons;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeons.class,
    name = "highscores",
    playerOnly = true
)
public class CmdDungeonsHighscores implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (((Player) sender).hasPermission("united.dungeon.admin"))
                return DungeonManager.instance().getDungeonNames();
            return DungeonManager.instance().getPublicDungeonNames();
        }
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            United.messenger().send(sender, "info-player-highscores");
            return;
        }

        var dungeon = DungeonManager.instance().getDungeon(args[0]);
        if (dungeon == null) {
            United.messenger().send(sender, "error-no-dungeon-found-by-name");
            return;
        }


        ArrayList<String> highscoreArray = new ArrayList<>();
        var highscores = dungeon.getHighscores();
        for (int i = 0; i < highscores.size(); i++) {
            highscoreArray.add(United.formatter().formatDuration(highscores.get(i).getTime()));
            highscoreArray.add(highscores.get(i).getPlayers());
        }

        United.messenger().send(sender, "dungeon-highscores", highscoreArray.toArray());

    }

}
