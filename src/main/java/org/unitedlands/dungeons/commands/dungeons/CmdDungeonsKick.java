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
    name = "kick",
    usage = "/ud kick <player>",
    playerOnly = true
)
public class CmdDungeonsKick implements UnitedCommandExecutor {

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

        var player = (Player) sender;

        var dungeon = DungeonManager.instance().getPlayerDungeon(player);
        if (dungeon == null) {
            United.messenger().send(sender, "error-not-dungeon");
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

        var playerToKick = Bukkit.getPlayer(args[0]);
        if (playerToKick == null) {
            United.messenger().send(sender, "error-player-not-found");
            return;
        }

        if (playerToKick.getUniqueId().equals(player.getUniqueId())) {
            United.messenger().send(sender, "error-cant-kick-yourself");
            return;
        }            

        var lockedPlayers = dungeon.getLockedPlayersInDungeon();
        if (!lockedPlayers.contains(playerToKick)) {
            United.messenger().send(sender, "error-player-not-in-party");
            return;
        }

        dungeon.removeLockedPlayer(playerToKick);
        United.messenger().send(player, "player-kicked");
        United.messenger().send(playerToKick, "player-party-kicked");

    }

}
