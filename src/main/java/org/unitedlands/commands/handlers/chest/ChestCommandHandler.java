package org.unitedlands.commands.handlers.chest;

import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseSubcommandHandler;
import org.unitedlands.commands.handlers.chest.subcommands.ChestCreateCommand;
import org.unitedlands.commands.handlers.chest.subcommands.ChestInfoCommand;
import org.unitedlands.commands.handlers.chest.subcommands.ChestRemoveCommand;
import org.unitedlands.commands.handlers.chest.subcommands.ChestSetCommand;

public class ChestCommandHandler extends BaseSubcommandHandler {

    public ChestCommandHandler(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new ChestCreateCommand(plugin));
        subHandlers.put("remove", new ChestRemoveCommand(plugin));
        subHandlers.put("set", new ChestSetCommand(plugin));
        subHandlers.put("info", new ChestInfoCommand(plugin));
    }

}
