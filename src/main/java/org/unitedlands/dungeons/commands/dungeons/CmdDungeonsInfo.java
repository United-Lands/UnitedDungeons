package org.unitedlands.dungeons.commands.dungeons;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.CmdDungeons;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeons.class,
    name = "info",
    usage = "/ud info <dungeon_name>",
    playerOnly = true
)
public class CmdDungeonsInfo implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (((Player) sender).hasPermission("united.dungeon.admin"))
                return DungeonManager.instance().getDungeonNames();
            return DungeonManager.instance().getPublicDungeonNames();
        }
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }
        
        var dungeon = DungeonManager.instance().getDungeon(args[0]);
        if (dungeon == null) {
            United.messenger().send(sender, "error-no-dungeon-found-by-name");
            return;
        }

        String status = "";
        if (dungeon.isOnCooldown()) {
            status = "<yellow>On Cooldown</yellow> <gray>(" + United.formatter().formatDuration(dungeon.getRemainingCooldown()) + " remaining)</gray>";
        } else if (dungeon.isLocked()) {
            status = "<dark_red>Locked by dungeon party</dark_red> <gray>(" + Formatter.formatDuration(dungeon.getRemainingLockTime()) + " remaining)</gray>";
        } else if (!dungeon.isActive()) {
            status = "<red>Closed</red>";
        } else {
            status = "<dark_green>Open</dark_green>";
        }

        String players = "-";
        if (!dungeon.getPlayersInDungeon().isEmpty())
        {
            players = String.join(", ", dungeon.getPlayersInDungeon().stream().map(p -> p.getName()).collect(Collectors.toSet()));
        }

        // TODO: replace with Info screen?

        // United.messenger().send(sender, "dungeon-info", Map.of("dungeon-name", dungeon.getCleanName(),
        //                                                                                "dungeon-description", dungeon.getDescription(),
        //                                                                                "status", status,
        //                                                                                "players", players));
       
    }

}
