package org.unitedlands.dungeons.commands.handlers.dungeon.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.commands.handlers.dungeon.CmdDungeonAdminDungeon;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonAdminDungeon.class,
    name = "start",
    usage = "/uda dungeon start <dungeon_name>"
)
public class CmdDungeonAdminDungeonStart implements UnitedCommandExecutor {

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
        Player player = (Player) sender; 

        if (args.length == 0) {
            dungeon = DungeonManager.instance().getEditSessionForPlayr(player.getUniqueId());
            if (dungeon == null) {
                United.messenger().send(sender, "error-no-edit-session");
                return;
            }
        } else if (args.length == 1) {
            dungeon = DungeonManager.instance().getDungeon(args[0]);
            if (dungeon == null) {
                United.messenger().send(sender, "error-no-dungeon-found-by-name");
                return;
            }
        } else {
            United.messenger().send(sender, "info-dungeon-start");
            return;
        }

        if (dungeon.getWarpLocation() == null) {
            United.messenger().send(sender, "error-start-no-warp");
            return;
        }

        if (dungeon.getRooms() == null || dungeon.getRooms().size() == 0) {
            United.messenger().send(sender, "error-start-no-rooms");
            return;
        }

        dungeon.setActive(true);
        dungeon.reset();

        United.messenger().send(sender, "dungeon-started");

        DungeonManager.instance().saveDungeon(dungeon, sender);

    }
}
