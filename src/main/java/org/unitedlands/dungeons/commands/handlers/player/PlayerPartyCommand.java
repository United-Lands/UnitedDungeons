package org.unitedlands.dungeons.commands.handlers.player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class PlayerPartyCommand extends BaseCommandHandler<UnitedDungeons> {

    public PlayerPartyCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-player-party"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var player = (Player) sender;

        var dungeon = plugin.getDungeonManager().getPlayerDungeon(player);
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-party"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (!dungeon.isPlayerLockedInDungeon(player)) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-party"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var partyLeader = dungeon.getPartyLeader();
        var lockedPlayers = dungeon.getLockedPlayersInDungeon();

        lockedPlayers.remove(partyLeader);

        var leaderName = partyLeader.getName();
        var memberNames = String.join(", ", lockedPlayers.stream().map(Player::getName).collect(Collectors.toList()));

        Messenger.sendMessage(player, messageProvider.get("player-party-info"),
                Map.of("leader", leaderName, "members", memberNames), messageProvider.get("messages.prefix"));

    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}
