package org.unitedlands.dungeons.commands.handlers.supplychest;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.commands.handlers.supplychest.subcommands.SupplyChestCreateCommand;
import org.unitedlands.dungeons.commands.handlers.supplychest.subcommands.SupplyChestInfoCommand;
import org.unitedlands.dungeons.commands.handlers.supplychest.subcommands.SupplyChestRemoveCommand;
import org.unitedlands.dungeons.commands.handlers.supplychest.subcommands.SupplyChestSetCommand;
import org.unitedlands.interfaces.IMessageProvider;

public class SupplyChestCommandHandler extends BaseSubcommandHandler<UnitedDungeons> {

    public SupplyChestCommandHandler(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new SupplyChestCreateCommand(plugin, messageProvider));
        subHandlers.put("remove", new SupplyChestRemoveCommand(plugin, messageProvider));
        subHandlers.put("set", new SupplyChestSetCommand(plugin, messageProvider));
        subHandlers.put("info", new SupplyChestInfoCommand(plugin, messageProvider));
    }

}
