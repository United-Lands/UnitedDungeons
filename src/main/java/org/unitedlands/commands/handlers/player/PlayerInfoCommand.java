package org.unitedlands.commands.handlers.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;

public class PlayerInfoCommand extends BaseCommandHandler<UnitedDungeons> {

    public PlayerInfoCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
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
            Messenger.sendMessage(sender, messageProvider.get("messages.info-player-dungeon-info"), null, messageProvider.get("messages.prefix"));
            return;
        }
        
        var dungeon = plugin.getDungeonManager().getDungeon(args[0]);
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-dungeon-found-by-name"), null, messageProvider.get("messages.prefix"));
            return;
        }

        String status = "";
        if (dungeon.isOnCooldown()) {
            status = "<yellow>On Cooldown <gray>(" + Formatter.formatDuration(dungeon.getRemainingCooldown()) + " remaining)";
        } else if (dungeon.isLocked()) {
            status = "<dark_red>Locked by dungeon party <gray>(" + Formatter.formatDuration(dungeon.getRemainingLockTime()) + " remaining)";
        } else if (!dungeon.isActive()) {
            status = "<red>Closed";
        } else {
            status = "<dark_green>Open";
        }

        String players = "-";
        if (!dungeon.getPlayersInDungeon().isEmpty())
        {
            players = String.join(", ", dungeon.getPlayersInDungeon().stream().map(p -> p.getName()).collect(Collectors.toSet()));
        }

        Messenger.sendMessage(sender, messageProvider.getList("messages.dungeon-info"), Map.of("dungeon-name", dungeon.getCleanName(),
                                                                                       "dungeon-description", dungeon.getDescription(),
                                                                                       "status", status,
                                                                                       "players", players));
       
    }

}
