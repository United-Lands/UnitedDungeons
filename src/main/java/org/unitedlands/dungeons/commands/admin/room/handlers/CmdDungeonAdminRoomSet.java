package org.unitedlands.dungeons.commands.admin.room.handlers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.Room;
import org.unitedlands.dungeons.commands.admin.room.CmdDungeonAdminRoom;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonAdminRoom.class,
    name = "set",
    usage = "/uda room set <property> <value>",
    catchAll = true
)
public class CmdDungeonAdminRoomSet implements UnitedCommandExecutor {

    private List<String> propertyList = Arrays.asList("name", "enableLocking", "mustBeCompleted", "showTitle",
            "useBossMusic", "needsOtherRoomCompleted");

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return propertyList;
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sendUsage(sender);
            return;
        }

        Player player = (Player) sender;
        Dungeon dungeon = DungeonManager.instance().getEditSessionForPlayr(player.getUniqueId());
        if (dungeon == null) {
            United.messenger().send(sender, "error-no-edit-session");
            return;
        }

        var room = DungeonManager.instance().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            United.messenger().send(sender, "error-not-in-room");
            return;
        }

        setRoomFields(player, room, args[0], args[1]);

        DungeonManager.instance().saveDungeon(dungeon, sender);

    }

    private void setRoomFields(Player player, Room room, String fieldName, String arg) {
        try {
            Field field = Room.class.getDeclaredField(fieldName);
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

            field.set(room, value);

        } catch (NoSuchFieldException e) {
            United.logger().error("Field " + fieldName + " does not exist.");
        } catch (IllegalAccessException e) {
            United.logger().error("Unable to access field " + fieldName + ".");
        } catch (NumberFormatException e) {
            United.logger().error("Invalid value for field " + fieldName + ".");
        }
    }

}
