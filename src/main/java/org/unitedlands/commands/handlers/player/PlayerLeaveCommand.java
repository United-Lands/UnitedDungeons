package org.unitedlands.commands.handlers.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class PlayerLeaveCommand extends BaseCommandHandler {

    public PlayerLeaveCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessageTemplate(sender, "info-player-leave", null, true);
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found", null, true);
            return;
        }

        if (!dungeon.isLocked()) {
            Messenger.sendMessageTemplate(sender, "error-dungeon-not-locked", null, true);
            return;
        }

        if (!dungeon.isPlayerLockedInDungeon(player)) {
            Messenger.sendMessageTemplate(sender, "error-not-in-party", null, true);
            return;
        }

        dungeon.removeLockedPlayer(player);
        Messenger.sendMessageTemplate(sender, "player-left", null, true);

    }

}
