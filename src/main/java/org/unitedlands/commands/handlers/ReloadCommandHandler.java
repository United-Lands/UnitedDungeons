package org.unitedlands.commands.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

public class ReloadCommandHandler extends BaseCommandHandler {

    public ReloadCommandHandler(UnitedDungeons plugin) {
        super(plugin);
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

        if (args != null && args.length == 1 && args[0].equals("-all")) {
            plugin.getDungeonManager().loadDungeons();
        }

        plugin.getDungeonManager().startChecks();

        Messenger.sendMessageTemplate(sender, "reload", null, true);
        Logger.log("Config reloaded.");
    }

}
