package org.unitedlands.dungeons.commands.admin.dungeon.handlers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.Room;
import org.unitedlands.dungeons.commands.admin.dungeon.CmdDungeonAdminDungeon;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonAdminDungeon.class,
    name = "set",
    usage = "/uda dungeon set <property> <value>",
    catchAll = true
)
public class CmdDungeonAdminDungeonSet implements UnitedCommandExecutor {

    private List<String> propertyList = Arrays.asList("location", "warp", "isPublic", "isLockable", "name",
            "pulloutTime", "description", "cooldownTime", "lockTime", "ticksBeforeSleep", "disableElytra",
            "disableEnderpearls", "disableWindcharge", "playerDetectionRange", "requireLock", "scaleMobLevels");

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return propertyList;
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        Player player = (Player) sender;
        Dungeon dungeon = DungeonManager.instance().getEditSessionForPlayr(player.getUniqueId());
        if (dungeon == null) {
            United.messenger().send(sender, "error-no-edit-session");
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

        var room = DungeonManager.instance().getRoomAtLocation(dungeon, player.getLocation());
        if (room != null) {
            United.messenger().send(player, "error-in-room");
            return;
        }

        dungeon.setWarpLocation(player.getLocation());

        saveDungeon(player, dungeon);
    }

    private void handleSetLocation(Player player, Dungeon dungeon) {

        var oldLocation = dungeon.getLocation().clone();
        var newLocation = player.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5);
        
        var delta = newLocation.subtract(oldLocation);
        for (Room room : dungeon.getRooms()) {
            dungeon.shiftRoom(room, delta);
        }

        dungeon.setLocation(player.getLocation());

        saveDungeon(player, dungeon);
    }

    private void saveDungeon(Player player, Dungeon dungeon) {
        DungeonManager.instance().saveDungeon(dungeon, player);
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
            United.logger().error("Field " + fieldName + " does not exist.");
        } catch (IllegalAccessException e) {
            United.logger().error("Unable to access field " + fieldName + ".");
        } catch (NumberFormatException e) {
            United.logger().error("Invalid value for field " + fieldName + ".");
        }
    }

}
