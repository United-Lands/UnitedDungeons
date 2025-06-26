package org.unitedlands.commands.handlers.barrier;

import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseSubcommandHandler;
import org.unitedlands.commands.handlers.barrier.subcommands.BarrierCreateCommand;
import org.unitedlands.commands.handlers.barrier.subcommands.BarrierInfoCommand;
import org.unitedlands.commands.handlers.barrier.subcommands.BarrierRemoveCommand;
import org.unitedlands.commands.handlers.barrier.subcommands.BarrierSetCommand;

public class BarrierCommandHandler extends BaseSubcommandHandler {

    public BarrierCommandHandler(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new BarrierCreateCommand(plugin));
        subHandlers.put("remove", new BarrierRemoveCommand(plugin));
        subHandlers.put("set", new BarrierSetCommand(plugin));
        subHandlers.put("info", new BarrierInfoCommand(plugin));
    }

}
