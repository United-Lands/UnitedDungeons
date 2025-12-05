package org.unitedlands.commands.handlers.spawner.subcommands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.Spawner;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Logger;

import org.unitedlands.utils.Messenger;

public class SpawnerSetCommand extends BaseCommandHandler<UnitedDungeons> {

    public SpawnerSetCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    private List<String> propertyList = Arrays.asList("mobType", "maxMobs", "spawnFrequency", "radius", "isGroupSpawn",
            "killsToComplete");

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return propertyList;
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-spawner-set"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Player player = (Player) sender;
        Dungeon dungeon = plugin.getDungeonManager().getEditSessionForPlayr(player.getUniqueId());
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-edit-session"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var room = plugin.getDungeonManager().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-room"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Spawner spawner = null;
        for (Spawner s : room.getSpawners()) {
            if (s.getLocation().getBlock().equals(player.getLocation().getBlock())) {
                spawner = s;
            }
        }
        if (spawner == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-spawner-not-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        setSpawnerField(player, spawner, args[0], args[1]);

        plugin.getDungeonManager().saveDungeon(dungeon, sender);
    }

    private void setSpawnerField(Player player, Spawner spawner, String fieldName, String arg) {
        try {
            Field field = Spawner.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            Class<?> fieldType = field.getType();

            Object value;
            if (fieldType == int.class) {
                value = Integer.parseInt(arg);
            } else if (fieldType == double.class) {
                value = Double.parseDouble(arg);
            } else if (fieldType == long.class) {
                value = Long.parseLong(arg);
            } else if (fieldType == boolean.class) {
                value = Boolean.parseBoolean(arg);
            } else {
                value = arg;
            }

            field.set(spawner, value);

        } catch (NoSuchFieldException e) {
            Logger.logError("Field " + fieldName + " does not exist.");
        } catch (IllegalAccessException e) {
            Logger.logError("Unable to access field " + fieldName + ".");
        } catch (NumberFormatException e) {
            Logger.logError("Invalid value for field " + fieldName + ".");
        }
    }

}
