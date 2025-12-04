package org.unitedlands.commands.handlers.dungeon;

import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonClearHighscores;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonCreateCommand;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonDeleteCommand;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonEditCommand;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonInfoCommand;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonSetCommand;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonStartCommand;
import org.unitedlands.commands.handlers.dungeon.subcommands.DungeonStopCommand;
import org.unitedlands.interfaces.IMessageProvider;

public class DungeonCommandHandler extends BaseSubcommandHandler<UnitedDungeons> {

    public DungeonCommandHandler(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new DungeonCreateCommand(plugin, messageProvider));
        subHandlers.put("edit", new DungeonEditCommand(plugin, messageProvider));
        subHandlers.put("delete", new DungeonDeleteCommand(plugin, messageProvider));
        subHandlers.put("start", new DungeonStartCommand(plugin, messageProvider));
        subHandlers.put("stop", new DungeonStopCommand(plugin, messageProvider));
        subHandlers.put("set", new DungeonSetCommand(plugin, messageProvider));
        subHandlers.put("info", new DungeonInfoCommand(plugin, messageProvider));
        subHandlers.put("clearhighscores", new DungeonClearHighscores(plugin, messageProvider));
    }

}
