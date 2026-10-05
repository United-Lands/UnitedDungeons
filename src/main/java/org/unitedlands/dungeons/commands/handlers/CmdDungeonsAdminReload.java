package org.unitedlands.dungeons.commands.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.commands.CmdDungeonsAdmin;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.registrars.messages.UnitedMessagesRegistrar;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonsAdmin.class,
    name = "reload",
    usage = "/uda reload",
    playerOnly = true
)
public class CmdDungeonsAdminReload implements UnitedCommandExecutor{

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        DungeonManager.instance().stopChecks();
        UnitedDungeons.instance().reloadConfig();
        UnitedMessagesRegistrar.reload(UnitedDungeons.instance());

        if (args != null && args.length == 1 && args[0].equals("-all")) {
            DungeonManager.instance().loadDungeons();
        }

        DungeonManager.instance().startChecks();

        United.messenger().send(sender, "reload");
    }

}
