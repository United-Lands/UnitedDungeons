package org.unitedlands.commands.handlers.dungeon.subcommands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.Room;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

public class DungeonSetCommand extends BaseCommandHandler {

    public DungeonSetCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    private List<String> propertyList = Arrays.asList("location", "warp", "isPublic", "isLockable", "name", "pulloutTime",
            "description", "cooldownTime", "lockTime", "ticksBeforeSleep");

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return propertyList;
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Messenger.sendMessageTemplate(sender, "info-dungeon-set", null, true);
            return;
        } else if (args.length == 1 && (!args[0].equals("location") && !args[0].equals("warp"))) {
            Messenger.sendMessageTemplate(sender, "info-dungeon-set", null, true);
            return;
        } else if (args.length >= 2 && !propertyList.contains(args[0])) {
            Messenger.sendMessageTemplate(sender, "info-dungeon-set", null, true);
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found", null, true);
            return;
        }

        if (args[0].equals("location")) {
            handleSetLocation(player, dungeon);
        } else if (args[0].equals("warp")) {
            handleSetWarp(player, dungeon);
        } else if (args[0].equals("description")) {
            handleSetDescription(player, dungeon, args);
        } else {
            handleSetField(player, dungeon, args);

        }
    }

    private void handleSetDescription(Player player, Dungeon dungeon, String[] args) {
        var description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        dungeon.setDescription(description);
        saveDungeon(player, dungeon);
    }

    private void handleSetField(Player player, Dungeon dungeon, String[] args) {
        setDungeonField(player, dungeon, args[0], args[1]);
        saveDungeon(player, dungeon);
    }

    private void handleSetWarp(Player player, Dungeon dungeon) {

        var room = plugin.getDungeonManager().getRoomAtLocation(dungeon, player.getLocation());
        if (room != null) {
            Messenger.sendMessageTemplate(player, "error-in-room", null, true);
            return;
        }

        dungeon.setWarpLocation(player.getLocation());

        saveDungeon(player, dungeon);
    }

    private void handleSetLocation(Player player, Dungeon dungeon) {

        var oldLocation = dungeon.getLocation().clone();
        var newLocation = player.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5);

        var minDistance = plugin.getConfig().getDouble("general.min-dungeon-distance", 0);
        for (var otherDungeon : plugin.getDungeonManager().getDungeons()) {
            if (dungeon.equals(otherDungeon))
                continue;
            var distance = newLocation.distance(otherDungeon.getLocation());
            if (distance < minDistance) {
                Messenger.sendMessageTemplate(player, "error-dungeon-too-close", null, true);
                return;
            }
        }

        var delta = newLocation.subtract(oldLocation);
        for (Room room : dungeon.getRooms()) {
            dungeon.shiftRoom(room, delta);
        }

        dungeon.setLocation(player.getLocation());

        saveDungeon(player, dungeon);
    }

    private void saveDungeon(Player player, Dungeon dungeon) {
        if (!plugin.getDungeonManager().saveDungeon(dungeon)) {
            Messenger.sendMessageTemplate(player, "save-error", null, true);
        } else {
            Messenger.sendMessageTemplate(player, "save-success", null, true);
        }
    }

    private void setDungeonField(Player player, Dungeon dungeon, String fieldName, String arg) {
        try {
            Field field = Dungeon.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            Class<?> fieldType = field.getType();

            Object value;
            if (fieldType == int.class || fieldType == Integer.class) {
                value = Integer.parseInt(arg);
            } else if (fieldType == double.class || fieldType == Double.class) {
                value = Double.parseDouble(arg);
            } else if (fieldType == long.class || fieldType == Long.class) {
                value = Long.parseLong(arg);
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                value = Boolean.parseBoolean(arg);
            } else {
                value = arg;
            }

            field.set(dungeon, value);

        } catch (NoSuchFieldException e) {
            Logger.logError("Field " + fieldName + " does not exist.");
        } catch (IllegalAccessException e) {
            Logger.logError("Unable to access field " + fieldName + ".");
        } catch (NumberFormatException e) {
            Logger.logError("Invalid value for field " + fieldName + ".");
        }
    }

}
