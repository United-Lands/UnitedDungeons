package org.unitedlands.commands.handlers.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;

public class PlayerInfoCommand extends BaseCommandHandler {

    public PlayerInfoCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (((Player) sender).hasPermission("united.dungeon.admin"))
                return plugin.getDungeonManager().getDungeonNames();
            return plugin.getDungeonManager().getPublicDungeonNames();
        }
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Messenger.sendMessageTemplate(sender, "info-player-dungeon-info", null, true);
            return;
        }
        
        var dungeon = plugin.getDungeonManager().getDungeon(args[0]);
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found-by-name", null, true);
            return;
        }

        String status = "";
        if (dungeon.isOnCooldown()) {
            status = "§eOn Cooldown §7(" + Formatter.formatDuration(dungeon.getRemainingCooldown()) + " remaining)";
        } else if (dungeon.isLocked()) {
            status = "§4Locked by dungeon party §7(" + Formatter.formatDuration(dungeon.getRemainingLockTime()) + " remaining)";
        } else if (!dungeon.isActive()) {
            status = "§cClosed";
        } else {
            status = "§2Open";
        }

        String players = "-";
        if (!dungeon.getPlayersInDungeon().isEmpty())
        {
            players = String.join(", ", dungeon.getPlayersInDungeon().stream().map(p -> p.getName()).collect(Collectors.toSet()));
        }

        Messenger.sendMessageListTemplate(sender, "dungeon-info", Map.of("dungeon-name", dungeon.getCleanName(),
                                                                                       "dungeon-description", dungeon.getDescription(),
                                                                                       "status", status,
                                                                                       "players", players), false);
    }

}
