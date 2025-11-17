package org.unitedlands.commands.handlers.chest;

import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.commands.handlers.chest.subcommands.ChestCreateCommand;
import org.unitedlands.commands.handlers.chest.subcommands.ChestInfoCommand;
import org.unitedlands.commands.handlers.chest.subcommands.ChestRemoveCommand;
import org.unitedlands.commands.handlers.chest.subcommands.ChestSetCommand;
import org.unitedlands.interfaces.IMessageProvider;

public class ChestCommandHandler extends BaseSubcommandHandler<UnitedDungeons> {

    public ChestCommandHandler(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new ChestCreateCommand(plugin, messageProvider));
        subHandlers.put("remove", new ChestRemoveCommand(plugin, messageProvider));
        subHandlers.put("set", new ChestSetCommand(plugin, messageProvider));
        subHandlers.put("info", new ChestInfoCommand(plugin, messageProvider));
    }

}
