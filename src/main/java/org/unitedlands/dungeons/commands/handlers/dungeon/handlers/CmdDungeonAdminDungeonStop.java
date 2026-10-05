package org.unitedlands.dungeons.commands.handlers.dungeon.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.Spawner;
import org.unitedlands.dungeons.commands.handlers.dungeon.CmdDungeonAdminDungeon;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.dungeons.managers.MobManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonAdminDungeon.class,
    name = "stop",
    usage = "/uda dungeon stop <dungeon_name>"
)
public class CmdDungeonAdminDungeonStop implements UnitedCommandExecutor {

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
            United.messenger().send(sender, "info-dungeon-stop");
            return;
        }

        if (dungeon.getSpawners() != null) {
            for (Spawner s : dungeon.getSpawners()) {
                MobManager.instance().removeAllSpawnerMobs(s);
            }
        }

        dungeon.setActive(false);
        dungeon.reset();

        United.messenger().send(sender, "dungeon-stopped");

        DungeonManager.instance().saveDungeon(dungeon, sender);

    }

}
