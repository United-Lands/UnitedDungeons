package org.unitedlands.dungeons.commands.dungeons;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.CmdDungeons;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeons.class,
    name = "party",
    usage = "/ud party",
    playerOnly = true
)
public class CmdDungeonsParty implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        var dungeon = DungeonManager.instance().getPlayerDungeon(player);
        if (dungeon == null) {
            United.messenger().send(sender, "error-not-in-party");
            return;
        }

        if (!dungeon.isPlayerLockedInDungeon(player)) {
            United.messenger().send(sender, "error-not-in-party");
            return;
        }

        var partyLeader = dungeon.getPartyLeader();
        var lockedPlayers = dungeon.getLockedPlayersInDungeon();

        var leaderName = partyLeader.getName();
        var memberNames = String.join(", ", lockedPlayers.stream().map(Player::getName).collect(Collectors.toList()));

        United.messenger().send(player, "player-party-info",
                leaderName, memberNames);

    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}
