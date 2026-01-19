package org.unitedlands.dungeons.commands.handlers.dungeon.subcommands;

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

public class DungeonKickCommand extends BaseCommandHandler<UnitedDungeons> {

    public DungeonKickCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
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
            Messenger.sendMessage(sender, messageProvider.get("messages.info-dungeon-kick"), null,
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

        var playerToKick = Bukkit.getPlayer(args[0]);
        if (playerToKick == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-player-not-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        if (!dungeon.isPlayerLockedInDungeon(player)) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-player-not-in-party"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        dungeon.removeLockedPlayer(playerToKick);
        Messenger.sendMessage(sender, messageProvider.get("messages.player-kicked"), null,
                messageProvider.get("messages.prefix"));
    }
}
