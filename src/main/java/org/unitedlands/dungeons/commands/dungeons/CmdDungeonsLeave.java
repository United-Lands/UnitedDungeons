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
    name = "leave",
    usage = "/ud leave",
    playerOnly = true
)
public class CmdDungeonsLeave implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        Player player = (Player) sender;
        var dungeon = DungeonManager.instance().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            United.messenger().send(sender, "error-no-dungeon-found");
            return;
        }

        if (!dungeon.isLocked()) {
            United.messenger().send(sender, "error-dungeon-not-locked");
            return;
        }

        if (!dungeon.isPlayerLockedInDungeon(player)) {
            United.messenger().send(sender, "error-not-in-party");
            return;
        }

        dungeon.removeLockedPlayer(player);
        United.messenger().send(sender, "player-left");

    }

}
