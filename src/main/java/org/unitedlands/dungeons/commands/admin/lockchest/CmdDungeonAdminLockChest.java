package org.unitedlands.dungeons.commands.admin.lockchest;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.CmdDungeonsAdmin;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedSubCommand (
    parent = CmdDungeonsAdmin.class,
    name = "lockchest",
    usage = "/uda lockchest <command>",
    playerOnly = true
)
public class CmdDungeonAdminLockChest implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }

}
