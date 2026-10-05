package org.unitedlands.dungeons.commands.admin.barrier;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.CmdDungeonsAdmin;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedSubCommand (
    parent = CmdDungeonsAdmin.class,
    name = "barrier",
    usage = "/uda barrier <command>",
    playerOnly = true
)
public class CmdDungeonAdminBarrier implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

    }

}
