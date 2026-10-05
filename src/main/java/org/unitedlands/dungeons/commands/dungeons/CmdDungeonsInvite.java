package org.unitedlands.dungeons.commands.dungeons;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.CmdDungeons;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeons.class,
    name = "invite",
    usage = "/ud invite <player>",
    playerOnly = true
)
public class CmdDungeonsInvite implements UnitedCommandExecutor {

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

        if (!dungeon.isPlayerLockedInDungeon(player)) {
            United.messenger().send(sender, "error-not-in-party");
            return;
        }

        var partyLeader = dungeon.getPartyLeader();
        if (!partyLeader.equals(player)) {
            United.messenger().send(sender, "error-not-party-leader");
            return;
        }

        var playerToInvite = Bukkit.getPlayer(args[0]);
        if (playerToInvite == null) {
            United.messenger().send(sender, "error-player-not-found");
            return;
        }

        dungeon.invitePlayer(playerToInvite);
        United.messenger().send(sender, "player-invited");
        United.messenger().send(playerToInvite, "player-party-start");

    }

}
