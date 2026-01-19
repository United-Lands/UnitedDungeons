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

public class PlayerInviteCommand extends BaseCommandHandler<UnitedDungeons> {

    public PlayerInviteCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
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
            Messenger.sendMessage(sender, messageProvider.get("messages.info-player-invite"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-dungeon-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (!dungeon.isLocked()) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-dungeon-not-locked"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (!dungeon.isPlayerLockedInDungeon(player) && !player.hasPermission("united.dungeons.admin")) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-party"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var playerToInvite = Bukkit.getPlayer(args[0]);
        if (playerToInvite == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-player-not-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        dungeon.invitePlayer(playerToInvite);
        Messenger.sendMessage(sender, messageProvider.get("messages.player-invited"), null,
                messageProvider.get("messages.prefix"));
        Messenger.sendMessage(playerToInvite, messageProvider.get("messages.player-party-start"), null,
                messageProvider.get("messages.prefix"));

    }

}
