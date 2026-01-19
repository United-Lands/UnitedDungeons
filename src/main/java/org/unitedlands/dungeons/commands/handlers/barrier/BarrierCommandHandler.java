package org.unitedlands.dungeons.commands.handlers.barrier;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.commands.handlers.barrier.subcommands.BarrierCreateCommand;
import org.unitedlands.dungeons.commands.handlers.barrier.subcommands.BarrierInfoCommand;
import org.unitedlands.dungeons.commands.handlers.barrier.subcommands.BarrierRemoveCommand;
import org.unitedlands.dungeons.commands.handlers.barrier.subcommands.BarrierSetCommand;
import org.unitedlands.interfaces.IMessageProvider;


public class BarrierCommandHandler extends BaseSubcommandHandler<UnitedDungeons> {

    public BarrierCommandHandler(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new BarrierCreateCommand(plugin, messageProvider));
        subHandlers.put("remove", new BarrierRemoveCommand(plugin, messageProvider));
        subHandlers.put("set", new BarrierSetCommand(plugin, messageProvider));
        subHandlers.put("info", new BarrierInfoCommand(plugin, messageProvider));
    }

}
