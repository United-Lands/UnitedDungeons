package org.unitedlands.commands.handlers.room;

import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseSubcommandHandler;
import org.unitedlands.commands.handlers.room.subcommands.RoomCreateCommand;
import org.unitedlands.commands.handlers.room.subcommands.RoomDeleteCommand;
import org.unitedlands.commands.handlers.room.subcommands.RoomExpandCommand;
import org.unitedlands.commands.handlers.room.subcommands.RoomInfoCommand;
import org.unitedlands.commands.handlers.room.subcommands.RoomMoveCommand;
import org.unitedlands.commands.handlers.room.subcommands.RoomSetCommand;
import org.unitedlands.commands.handlers.room.subcommands.RoomStateCommand;
import org.unitedlands.interfaces.IMessageProvider;

public class RoomCommandHandler extends BaseSubcommandHandler<UnitedDungeons> {

    public RoomCommandHandler(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new RoomCreateCommand(plugin, messageProvider));
        subHandlers.put("delete", new RoomDeleteCommand(plugin, messageProvider));
        subHandlers.put("move", new RoomMoveCommand(plugin, messageProvider));
        subHandlers.put("expand", new RoomExpandCommand(plugin, messageProvider));
        subHandlers.put("state", new RoomStateCommand(plugin, messageProvider));
        subHandlers.put("set", new RoomSetCommand(plugin, messageProvider));
        subHandlers.put("info", new RoomInfoCommand(plugin, messageProvider));
    }

}
