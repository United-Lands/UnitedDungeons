package org.unitedlands.dungeons.commands.handlers.supplychest;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.CmdDungeonsAdmin;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedSubCommand (
    parent = CmdDungeonsAdmin.class,
    name = "supplychest",
    usage = "/uda supplychest <command>",
    playerOnly = true
)
public class CmdDungeonAdminSupplyChest implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

    }

}
