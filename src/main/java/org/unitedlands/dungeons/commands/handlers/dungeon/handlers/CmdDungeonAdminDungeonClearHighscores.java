package org.unitedlands.dungeons.commands.handlers.dungeon.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.HighScore;
import org.unitedlands.dungeons.commands.handlers.dungeon.CmdDungeonAdminDungeon;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;


@UnitedSubCommand (
    parent = CmdDungeonAdminDungeon.class,
    name = "clearhighscores",
    usage = "/uda clearhighscores <dungeon_name>"
)
public class CmdDungeonAdminDungeonClearHighscores implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return DungeonManager.instance().getDungeonNames();
        }

        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        Dungeon dungeon = null;

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        dungeon = DungeonManager.instance().getDungeon(args[0]);
        if (dungeon == null) {
            United.messenger().send(sender, "error-no-dungeon-found-by-name");
            return;
        }

        dungeon.setHighscores(new ArrayList<HighScore>());

        United.messenger().send(sender, "dungeon-highscores-cleared");

        DungeonManager.instance().saveDungeon(dungeon, sender);
    }
}
