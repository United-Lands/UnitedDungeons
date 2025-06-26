package org.unitedlands.commands.handlers.dungeon.subcommands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class DungeonKickCommand extends BaseCommandHandler {

    public DungeonKickCommand(UnitedDungeons plugin) {
        super(plugin);
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
            Messenger.sendMessageTemplate(sender, "info-dungeon-kick", null, true);
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
        
        var playerToKick = Bukkit.getPlayer(args[0]);
        if (playerToKick == null) {
            Messenger.sendMessageTemplate(sender, "error-player-not-found", null, true);
            return;
        }

        if (!dungeon.isPlayerLockedInDungeon(player)) {
            Messenger.sendMessageTemplate(sender, "error-player-not-in-party", null, true);
            return;
        }

        dungeon.removeLockedPlayer(playerToKick);
        Messenger.sendMessageTemplate(sender, "player-kicked", null, true);
    }
}
