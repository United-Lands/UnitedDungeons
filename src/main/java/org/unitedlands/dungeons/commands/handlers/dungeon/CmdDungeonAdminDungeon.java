package org.unitedlands.dungeons.commands.handlers.dungeon;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.CmdDungeonsAdmin;
import org.unitedlands.registrars.command.UnitedCommandExecutor;


@UnitedSubCommand (
    parent = CmdDungeonsAdmin.class,
    name = "dungeon",
    usage = "/uda dungeon <command>",
    playerOnly = true
)
public class CmdDungeonAdminDungeon implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }

}
