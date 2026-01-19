package org.unitedlands.dungeons.commands.handlers.spawner;

import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.commands.handlers.spawner.subcommands.SpawnerCreateCommand;
import org.unitedlands.dungeons.commands.handlers.spawner.subcommands.SpawnerInfoCommand;
import org.unitedlands.dungeons.commands.handlers.spawner.subcommands.SpawnerRemoveCommand;
import org.unitedlands.dungeons.commands.handlers.spawner.subcommands.SpawnerSetCommand;
import org.unitedlands.interfaces.IMessageProvider;

public class SpawnerCommandHandler extends BaseSubcommandHandler<UnitedDungeons> {

    public SpawnerCommandHandler(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new SpawnerCreateCommand(plugin, messageProvider));
        subHandlers.put("remove", new SpawnerRemoveCommand(plugin, messageProvider));
        subHandlers.put("set", new SpawnerSetCommand(plugin, messageProvider));
        subHandlers.put("info", new SpawnerInfoCommand(plugin, messageProvider));
    }

}
