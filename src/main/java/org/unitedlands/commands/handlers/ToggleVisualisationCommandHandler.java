package org.unitedlands.commands.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class ToggleVisualisationCommandHandler extends BaseCommandHandler {

    public ToggleVisualisationCommandHandler(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length != 1)
            return new ArrayList<String>();

        return Arrays.asList("on", "off");
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 1)
            return;

        switch (args[0]) {
            case "on":
                plugin.getVisualisationManager().addViewer((Player) sender);
                Messenger.sendMessageTemplate(sender, "visualisation-on", null, true);
                break;
            case "off":
                plugin.getVisualisationManager().removeViewer((Player) sender);
                Messenger.sendMessageTemplate(sender, "visualisation-off", null, true);    
                break;
            default:
                return;
        }
    }

}
