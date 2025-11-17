package org.unitedlands.commands.handlers.lockchest.subcommands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.classes.LockChest;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

public class LockChestSetCommand extends BaseCommandHandler<UnitedDungeons> {

    public LockChestSetCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    private List<String> propertyList = Arrays.asList("requiredItems", "facing");

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return propertyList;
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-chest-set"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-dungeon-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var room = plugin.getDungeonManager().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-room"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        LockChest chest = null;
        for (LockChest c : room.getLockChests()) {
            if (c.getLocation().getBlock().equals(player.getLocation().getBlock())) {
                chest = c;
            }
        }
        if (chest == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-chest-not-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        setChestField(player, chest, args[0], args[1]);

        plugin.getDungeonManager().saveDungeon(dungeon, sender);

    }

    private void setChestField(Player player, LockChest chest, String fieldName, String arg) {
        try {
            Field field = LockChest.class.getDeclaredField(fieldName);
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

            field.set(chest, value);

        } catch (NoSuchFieldException e) {
            Logger.logError("Field " + fieldName + " does not exist.");
        } catch (IllegalAccessException e) {
            Logger.logError("Unable to access field " + fieldName + ".");
        } catch (NumberFormatException e) {
            Logger.logError("Invalid value for field " + fieldName + ".");
        }
    }

}
