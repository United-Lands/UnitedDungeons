package org.unitedlands.commands.handlers.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class PlayerLeaveCommand extends BaseCommandHandler<UnitedDungeons> {

    public PlayerLeaveCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-player-leave"), null,
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

        if (!dungeon.isPlayerLockedInDungeon(player)) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-party"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        dungeon.removeLockedPlayer(player);
        Messenger.sendMessage(sender, messageProvider.get("messages.player-left"), null,
                messageProvider.get("messages.prefix"));

    }

}
