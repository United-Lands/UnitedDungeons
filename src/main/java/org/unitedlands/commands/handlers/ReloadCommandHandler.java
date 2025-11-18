package org.unitedlands.commands.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

public class ReloadCommandHandler extends BaseCommandHandler<UnitedDungeons> {

    public ReloadCommandHandler(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length > 1)
            return;

        plugin.getDungeonManager().stopChecks();
        plugin.reloadConfig();
        plugin.getMessageProvider().reload(plugin.getConfig());

        if (args != null && args.length == 1 && args[0].equals("-all")) {
            plugin.getDungeonManager().loadDungeons();
        }


        plugin.getDungeonManager().startChecks();

        Messenger.sendMessage(sender, messageProvider.get("messages.reload"), null, messageProvider.get("messages.prefix"));
        Logger.log("Config reloaded.");
    }

}
