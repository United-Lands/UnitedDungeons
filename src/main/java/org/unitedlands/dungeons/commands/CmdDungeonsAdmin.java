package org.unitedlands.dungeons.commands;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedCommand (
    name = "uniteddungeonsadmin",
    aliases = { "uda", "dungeonsadmin" },
    permission = "united.dungeons.admin"
)
public class CmdDungeonsAdmin implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }


}
