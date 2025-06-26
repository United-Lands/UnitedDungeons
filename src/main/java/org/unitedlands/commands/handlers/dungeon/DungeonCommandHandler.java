package org.unitedlands.commands.handlers.dungeon;

import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseSubcommandHandler;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonCreateCommand;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonDeleteCommand;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonInfoCommand;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonSetCommand;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonStartCommand;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonStopCommand;

public class DungeonCommandHandler extends BaseSubcommandHandler {

    public DungeonCommandHandler(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new DungeonCreateCommand(plugin));
        subHandlers.put("delete", new DungeonDeleteCommand(plugin));
        subHandlers.put("start", new DungeonStartCommand(plugin));
        subHandlers.put("stop", new DungeonStopCommand(plugin));
        subHandlers.put("set", new DungeonSetCommand(plugin));
        subHandlers.put("info", new DungeonInfoCommand(plugin));
    }

}
