package org.unitedlands.dungeons.commands;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedCommand (
    name = "uniteddungeons",
    aliases = { "ud", "dungeond" },
    playerOnly = true
)
public class CmdDungeons implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
    }

}
