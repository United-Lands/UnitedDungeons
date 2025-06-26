package org.unitedlands.commands.handlers.room;

import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseSubcommandHandler;
import org.unitedlands.commands.handlers.room.subcommands.RoomCreateCommand;
import org.unitedlands.commands.handlers.room.subcommands.RoomDeleteCommand;
import org.unitedlands.commands.handlers.room.subcommands.RoomExpandCommand;
import org.unitedlands.commands.handlers.room.subcommands.RoomInfoCommand;
import org.unitedlands.commands.handlers.room.subcommands.RoomMoveCommand;
import org.unitedlands.commands.handlers.room.subcommands.RoomSetCommand;
import org.unitedlands.commands.handlers.room.subcommands.RoomStateCommand;

public class RoomCommandHandler extends BaseSubcommandHandler {

    public RoomCommandHandler(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    protected void registerSubHandlers() {
        subHandlers.put("create", new RoomCreateCommand(plugin));
        subHandlers.put("delete", new RoomDeleteCommand(plugin));
        subHandlers.put("move", new RoomMoveCommand(plugin));
        subHandlers.put("expand", new RoomExpandCommand(plugin));
        subHandlers.put("state", new RoomStateCommand(plugin));
        subHandlers.put("set", new RoomSetCommand(plugin));
        subHandlers.put("info", new RoomInfoCommand(plugin));
    }

}
