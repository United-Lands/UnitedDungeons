package org.unitedlands.dungeons.commands.admin.lootchest;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.CmdDungeonsAdmin;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedSubCommand (
    parent = CmdDungeonsAdmin.class,
    name = "lootchest",
    usage = "/uda lootchest <command>",
    playerOnly = true
)
public class CmdDungeonAdminLootChest implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

    }

}
