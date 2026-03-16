package org.unitedlands.dungeons.commands.handlers.player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class PlayerKickCommand extends BaseCommandHandler<UnitedDungeons> {

    public PlayerKickCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

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
            Messenger.sendMessage(sender, messageProvider.get("messages.info-player-kick"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var player = (Player) sender;

        var dungeon = plugin.getDungeonManager().getPlayerDungeon(player);
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-dungeon"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (!dungeon.isPlayerLockedInDungeon(player)) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-party"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var partyLeader = dungeon.getPartyLeader();
        if (!partyLeader.equals(player)) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-party-leader"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var playerToKick = Bukkit.getPlayer(args[0]);
        if (playerToKick == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-player-not-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (playerToKick.equals(player)) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-cant-kick-yourself"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }            

        var lockedPlayers = dungeon.getLockedPlayersInDungeon();
        if (!lockedPlayers.contains(playerToKick)) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-player-not-in-party"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        dungeon.removeLockedPlayer(playerToKick);
        Messenger.sendMessage(player, messageProvider.get("messages.player-kicked"), null,
                messageProvider.get("messages.prefix"));
        Messenger.sendMessage(playerToKick, messageProvider.get("messages.player-party-kicked"), null,
                messageProvider.get("messages.prefix"));

    }

}
