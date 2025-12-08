package org.unitedlands.commands.handlers.lootchest;

import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.commands.handlers.lootchest.subcommands.LootChestCreateCommand;
import org.unitedlands.commands.handlers.lootchest.subcommands.LootChestInfoCommand;
import org.unitedlands.commands.handlers.lootchest.subcommands.LootChestRemoveCommand;
import org.unitedlands.commands.handlers.lootchest.subcommands.LootChestSetCommand;
import org.unitedlands.interfaces.IMessageProvider;

public class LootChestCommandHandler extends BaseSubcommandHandler<UnitedDungeons> {

    public LootChestCommandHandler(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new LootChestCreateCommand(plugin, messageProvider));
        subHandlers.put("remove", new LootChestRemoveCommand(plugin, messageProvider));
        subHandlers.put("set", new LootChestSetCommand(plugin, messageProvider));
        subHandlers.put("info", new LootChestInfoCommand(plugin, messageProvider));
    }

}
