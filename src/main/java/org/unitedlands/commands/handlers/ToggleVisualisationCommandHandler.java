package org.unitedlands.commands.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class ToggleVisualisationCommandHandler extends BaseCommandHandler<UnitedDungeons> {

    public ToggleVisualisationCommandHandler(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
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
                plugin.getEffectsManager().addViewer((Player) sender);
                Messenger.sendMessage(sender, messageProvider.get("messages.visualisation-on"), null,
                        messageProvider.get("messages.prefix"));
                break;
            case "off":
                plugin.getEffectsManager().removeViewer((Player) sender);
                Messenger.sendMessage(sender, messageProvider.get("messages.visualvisualisation-off"), null,
                        messageProvider.get("messages.prefix"));
                break;
            default:
                return;
        }
    }

}
