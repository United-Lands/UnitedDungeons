package org.unitedlands.dungeons.commands.admin.dungeon.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.admin.dungeon.CmdDungeonAdminDungeon;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonAdminDungeon.class,
    name = "kick",
    usage = "/uda dungeon kick <player>"
)
public class CmdDungeonAdminDungeonKick implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

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

        var playerToKick = Bukkit.getPlayer(args[0]);
        if (playerToKick == null) {
            United.messenger().send(sender, "error-player-not-found");
            return;
        }

        if (!dungeon.isPlayerLockedInDungeon(player)) {
            United.messenger().send(sender, "error-player-not-in-party");
            return;
        }

        dungeon.removeLockedPlayer(playerToKick);
        United.messenger().send(sender, "player-kicked");
    }
}
