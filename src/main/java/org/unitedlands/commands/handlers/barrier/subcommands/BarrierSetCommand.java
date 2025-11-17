package org.unitedlands.commands.handlers.barrier.subcommands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Barrier;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

public class BarrierSetCommand extends BaseCommandHandler<UnitedDungeons> {

    public BarrierSetCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    private List<String> propertyList = Arrays.asList("material", "height", "inverse", "triggerChance", "facing");

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return propertyList;
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-barrier-set"), null,
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

        Barrier barrier = null;
        for (Barrier b : room.getBarriers()) {
            if (b.getLocation().getBlock().equals(player.getLocation().getBlock())) {
                barrier = b;
            }
        }
        if (barrier == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-barrier-not-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        setBarrierField(player, barrier, args[0], args[1]);

        plugin.getDungeonManager().saveDungeon(dungeon, sender);
    }

    private void setBarrierField(Player player, Barrier barrier, String fieldName, String arg) {
        try {
            Field field = Barrier.class.getDeclaredField(fieldName);
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

            field.set(barrier, value);

        } catch (NoSuchFieldException e) {
            Logger.logError("Field " + fieldName + " does not exist.");
        } catch (IllegalAccessException e) {
            Logger.logError("Unable to access field " + fieldName + ".");
        } catch (NumberFormatException e) {
            Logger.logError("Invalid value for field " + fieldName + ".");
        }
    }

}
