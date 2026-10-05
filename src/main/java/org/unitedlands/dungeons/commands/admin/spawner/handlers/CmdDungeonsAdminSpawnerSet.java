package org.unitedlands.dungeons.commands.admin.spawner.handlers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.Spawner;
import org.unitedlands.dungeons.commands.admin.spawner.CmdDungeonsAdminSpawner;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonsAdminSpawner.class,
    name = "set",
    usage = "/uda spawner set <property> <value>"
)
public class CmdDungeonsAdminSpawnerSet implements UnitedCommandExecutor {


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

        Spawner spawner = null;
        for (Spawner s : room.getSpawners()) {
            if (s.getLocation().getBlock().equals(player.getLocation().getBlock())) {
                spawner = s;
            }
        }
        if (spawner == null) {
            United.messenger().send(sender, "error-spawner-not-found");
            return;
        }

        setSpawnerField(player, spawner, args[0], args[1]);

        DungeonManager.instance().saveDungeon(dungeon, sender);
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
            United.logger().error("Field " + fieldName + " does not exist.");
        } catch (IllegalAccessException e) {
            United.logger().error("Unable to access field " + fieldName + ".");
        } catch (NumberFormatException e) {
            United.logger().error("Invalid value for field " + fieldName + ".");
        }
    }

}
