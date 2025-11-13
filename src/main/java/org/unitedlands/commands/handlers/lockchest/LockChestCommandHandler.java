package org.unitedlands.commands.handlers.lockchest;

import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseSubcommandHandler;
import org.unitedlands.commands.handlers.lockchest.subcommands.LockChestCreateCommand;
import org.unitedlands.commands.handlers.lockchest.subcommands.LockChestInfoCommand;
import org.unitedlands.commands.handlers.lockchest.subcommands.LockChestRemoveCommand;
import org.unitedlands.commands.handlers.lockchest.subcommands.LockChestSetCommand;

public class LockChestCommandHandler extends BaseSubcommandHandler {

    public LockChestCommandHandler(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new LockChestCreateCommand(plugin));
        subHandlers.put("remove", new LockChestRemoveCommand(plugin));
        subHandlers.put("set", new LockChestSetCommand(plugin));
        subHandlers.put("info", new LockChestInfoCommand(plugin));
    }

}
