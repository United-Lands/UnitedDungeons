package org.unitedlands.commands.handlers.spawner.subcommands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Spawner;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

public class SpawnerSetCommand extends BaseCommandHandler {

    public SpawnerSetCommand(UnitedDungeons plugin) {
        super(plugin);
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
            Messenger.sendMessageTemplate(sender, "info-spawner-set", null, true);
            return;
        }

        Player player = (Player) sender;

        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found", null, true);
            return;
        }

        var room = plugin.getDungeonManager().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            Messenger.sendMessageTemplate(sender, "error-not-in-room", null, true);
            return;
        }

        Spawner spawner = null;
        for (Spawner s : room.getSpawners()) {
            if (s.getLocation().getBlock().equals(player.getLocation().getBlock())) {
                spawner = s;
            }
        }
        if (spawner == null) {
            Messenger.sendMessageTemplate(sender, "error-spawner-not-found", null, true);
            return;
        }

        setSpawnerField(player, spawner, args[0], args[1]);

        if (!plugin.getDungeonManager().saveDungeon(dungeon)) {
            Messenger.sendMessageTemplate(sender, "save-error", null, true);
        } else {
            Messenger.sendMessageTemplate(sender, "save-success", null, true);
        }
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
