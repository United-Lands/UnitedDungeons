package org.unitedlands.dungeons.commands.admin.room;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.CmdDungeonsAdmin;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedSubCommand (
    parent = CmdDungeonsAdmin.class,
    name = "room",
    usage = "/uda room <command>",
    playerOnly = true
)
public class CmdDungeonAdminRoom implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

    }



}
