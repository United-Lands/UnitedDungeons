package org.unitedlands.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandExecutor;
import org.unitedlands.commands.handlers.ReloadCommandHandler;
import org.unitedlands.commands.handlers.ToggleVisualisationCommandHandler;
import org.unitedlands.commands.handlers.barrier.BarrierCommandHandler;
import org.unitedlands.commands.handlers.chest.ChestCommandHandler;
import org.unitedlands.commands.handlers.dungeon.DungeonCommandHandler;
import org.unitedlands.commands.handlers.room.RoomCommandHandler;
import org.unitedlands.commands.handlers.spawner.SpawnerCommandHandler;
import org.unitedlands.utils.Messenger;

public class AdminCommands extends BaseCommandExecutor {

    public AdminCommands(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("toggledisplay", new ToggleVisualisationCommandHandler(plugin));
        handlers.put("dungeon", new DungeonCommandHandler(plugin));
        handlers.put("room", new RoomCommandHandler(plugin));
        handlers.put("spawner", new SpawnerCommandHandler(plugin));
        handlers.put("chest", new ChestCommandHandler(plugin));
        handlers.put("barrier", new BarrierCommandHandler(plugin));
        handlers.put("reload", new ReloadCommandHandler(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label,
            String @NotNull [] args) {

        if (!((Player) sender).hasPermission("united.dungeons.admin"))
        {
            Messenger.sendMessageTemplate(sender, "no-permission", null, true);
            return false;
        }

        return super.onCommand(sender, cmd, label, args);
    }

}
