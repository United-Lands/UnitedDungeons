package org.unitedlands.dungeons.commands.admin.dungeon.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.commands.admin.dungeon.CmdDungeonAdminDungeon;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonAdminDungeon.class,
    name = "create",
    usage = "/uda dungeon create <dungeon_name>"
)
public class CmdDungeonAdminDungeonCreate implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        Player player = (Player) sender;

        var name = args[0].trim();
        var existingDungeon = DungeonManager.instance().getDungeon(name);
        if (existingDungeon != null) {
            United.messenger().send(sender, "error-dungeon-with-same-name");
            return;
        }

        var dungeon = new Dungeon(player.getLocation());
        dungeon.setName(name);

        DungeonManager.instance().addDungeon(dungeon);
        DungeonManager.instance().registerEditSessionForPlayer(player.getUniqueId(), dungeon);

        DungeonManager.instance().saveDungeon(dungeon, sender);

    }

}
