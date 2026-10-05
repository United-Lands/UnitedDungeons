package org.unitedlands.dungeons.commands.admin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.CmdDungeonsAdmin;
import org.unitedlands.dungeons.managers.EffectsManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonsAdmin.class,
    name = "togglevisualisation",
    usage = "/uda togglevisualisation <on|off>",
    playerOnly = true
)
public class CmdDungeonsAdminToggleVisualisation implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
           return Arrays.asList("on", "off");
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 1) 
        {
            sendUsage(sender);
            return;
        }

        switch (args[0]) {
            case "on":
                EffectsManager.instance().addViewer((Player) sender);
                United.messenger().send(sender, "visualisation-on");
                break;
            case "off":
                EffectsManager.instance().removeViewer((Player) sender);
                United.messenger().send(sender, "visualvisualisation-off");
                break;
            default:
                return;
        }
    }

}
