package org.unitedlands.dungeons.commands.handlers.lockchest;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.commands.handlers.lockchest.subcommands.LockChestCreateCommand;
import org.unitedlands.dungeons.commands.handlers.lockchest.subcommands.LockChestInfoCommand;
import org.unitedlands.dungeons.commands.handlers.lockchest.subcommands.LockChestRemoveCommand;
import org.unitedlands.dungeons.commands.handlers.lockchest.subcommands.LockChestSetCommand;
import org.unitedlands.interfaces.IMessageProvider;

public class LockChestCommandHandler extends BaseSubcommandHandler<UnitedDungeons> {

    public LockChestCommandHandler(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new LockChestCreateCommand(plugin, messageProvider));
        subHandlers.put("remove", new LockChestRemoveCommand(plugin, messageProvider));
        subHandlers.put("set", new LockChestSetCommand(plugin, messageProvider));
        subHandlers.put("info", new LockChestInfoCommand(plugin, messageProvider));
    }

}
