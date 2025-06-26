package org.unitedlands.commands.handlers.spawner;

import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseSubcommandHandler;
import org.unitedlands.commands.handlers.spawner.subcommands.SpawnerCreateCommand;
import org.unitedlands.commands.handlers.spawner.subcommands.SpawnerInfoCommand;
import org.unitedlands.commands.handlers.spawner.subcommands.SpawnerRemoveCommand;
import org.unitedlands.commands.handlers.spawner.subcommands.SpawnerSetCommand;

public class SpawnerCommandHandler extends BaseSubcommandHandler {

    public SpawnerCommandHandler(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new SpawnerCreateCommand(plugin));
        subHandlers.put("remove", new SpawnerRemoveCommand(plugin));
        subHandlers.put("set", new SpawnerSetCommand(plugin));
        subHandlers.put("info", new SpawnerInfoCommand(plugin));
    }

}
