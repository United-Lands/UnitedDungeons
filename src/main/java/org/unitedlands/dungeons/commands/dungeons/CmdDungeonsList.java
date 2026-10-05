package org.unitedlands.dungeons.commands.dungeons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.CmdDungeons;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeons.class,
    name = "invite",
    usage = "/ud list",
    playerOnly = true
)
public class CmdDungeonsList implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        List<String> dungeonList = new ArrayList<>();
        if (player.hasPermission("united.dungeons.admin")) {
            dungeonList = DungeonManager.instance().getDungeonNames();
        } else {
            dungeonList = DungeonManager.instance().getPublicDungeonNames();
        }


        var msg = String.join(", ", dungeonList);
        United.messenger().sendRaw(sender, msg);
    }

}
