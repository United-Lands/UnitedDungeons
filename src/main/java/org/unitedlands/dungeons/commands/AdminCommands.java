package org.unitedlands.dungeons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.commands.handlers.ReloadCommandHandler;
import org.unitedlands.dungeons.commands.handlers.ToggleVisualisationCommandHandler;
import org.unitedlands.dungeons.commands.handlers.barrier.BarrierCommandHandler;
import org.unitedlands.dungeons.commands.handlers.dungeon.DungeonCommandHandler;
import org.unitedlands.dungeons.commands.handlers.lockchest.LockChestCommandHandler;
import org.unitedlands.dungeons.commands.handlers.lootchest.LootChestCommandHandler;
import org.unitedlands.dungeons.commands.handlers.room.RoomCommandHandler;
import org.unitedlands.dungeons.commands.handlers.spawner.SpawnerCommandHandler;
import org.unitedlands.dungeons.commands.handlers.supplychest.SupplyChestCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class AdminCommands extends BaseCommandExecutor<UnitedDungeons> {

    public AdminCommands(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("toggledisplay", new ToggleVisualisationCommandHandler(plugin, messageProvider));
        handlers.put("dungeon", new DungeonCommandHandler(plugin, messageProvider));
        handlers.put("room", new RoomCommandHandler(plugin, messageProvider));
        handlers.put("spawner", new SpawnerCommandHandler(plugin, messageProvider));
        handlers.put("supplychest", new SupplyChestCommandHandler(plugin, messageProvider));
        handlers.put("lootchest", new LootChestCommandHandler(plugin, messageProvider));
        handlers.put("lockchest", new LockChestCommandHandler(plugin, messageProvider));
        handlers.put("barrier", new BarrierCommandHandler(plugin, messageProvider));
        handlers.put("reload", new ReloadCommandHandler(plugin, messageProvider));
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label,
            String @NotNull [] args) {

        if (!((Player) sender).hasPermission("united.dungeons.admin")) {
            Messenger.sendMessage(sender, messageProvider.get("messages.no-permission"), null,
                    messageProvider.get("messages.prefix"));
            return false;
        }

        return super.onCommand(sender, cmd, label, args);
    }

}
