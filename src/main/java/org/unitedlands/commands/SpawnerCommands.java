package org.unitedlands.commands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.Spawner;
import org.unitedlands.utils.MessageFormatter;

import net.md_5.bungee.api.ChatColor;

public class SpawnerCommands implements CommandExecutor {

    private final UnitedDungeons plugin;

    public SpawnerCommands(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            String[] args) {

        if (args.length == 0)
            return false;

        var player = (Player) sender;

        Spawner spawner = null;
        if (!args[0].equals("create")) {
            spawner = findSpawner(player);
            if (spawner == null) {
                player.sendMessage(MessageFormatter.getWithPrefix("There is no spawner in this location."));
                return false;
            }
        }

        switch (args[0]) {
            case "create":
                handleSpawnerCreate(player, args);
                break;
            case "info":
                handleSpawnerInfo(player, spawner);
                break;
            case "delete":
                handleSpawnerDelete(player, spawner);
                break;
            case "set":
                handleSpawnerSetters(player, spawner, args);
            default:
                break;
        }

        return true;
    }

    private void handleSpawnerSetters(Player player, Spawner spawner, String[] args) {
        if (args.length < 3)
            return;

        setSpawnerField(player, spawner, args[1], args[2]);
    }

    private void handleSpawnerDelete(Player player, Spawner spawner) {
        plugin.getMobManager().removeAllSpawnerMobs(spawner);

        var dungeon = spawner.getDungeon();
        dungeon.removeSpawner(spawner.uuid);
        dungeon.save();

        player.sendMessage(MessageFormatter.getWithPrefix("Spawner removed."));
    }

    private void handleSpawnerCreate(Player player, String[] args) {
        if (args.length < 2)
            return;

        var dungeon = plugin.getDungeonManager().getDungeon(args[1]);
        if (dungeon == null) {
            MessageFormatter.getWithPrefix(ChatColor.RED + "No dungeon with name " + args[0] + " found!");
            return;
        }

        Spawner spawer = new Spawner(player.getLocation(), dungeon);
        dungeon.addSpawner(spawer);
        dungeon.save();

        player.sendMessage(
                MessageFormatter.getWithPrefix("Spawner added to dungeon " + dungeon.name + "."));
    }

    private void handleSpawnerInfo(Player player, Spawner spawner) {

        List<String> fieldValues = new ArrayList<>();
        for (Field field : Spawner.class.getFields()) {
            try {
                if (field.canAccess(spawner)) {
                    if (!field.getType().equals(Location.class))
                        fieldValues.add(ChatColor.WHITE + field.getName() + ": " + ChatColor.GRAY + field.get(spawner));
                }
            } catch (IllegalAccessException ex) {
                plugin.getLogger().severe("Illegal access to field " + field.getName());
            }
        }

        player.sendMessage(
                MessageFormatter.getWithPrefix(ChatColor.WHITE + "" + ChatColor.BOLD + "Spawner Info"));
        player.sendMessage(MessageFormatter.getWithPrefix(String.join(ChatColor.DARK_GRAY + " | ", fieldValues)));
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
            spawner.getDungeon().save();

            player.sendMessage(MessageFormatter.getWithPrefix(
                    "Successfully updated " + fieldName + " to " + value + "."));
        } catch (NoSuchFieldException e) {
            plugin.getLogger().severe("Field " + fieldName + " does not exist.");
        } catch (IllegalAccessException e) {
            plugin.getLogger().severe("Unable to access field " + fieldName + ".");
        } catch (NumberFormatException e) {
            player.sendMessage(MessageFormatter.getWithPrefix("Invalid value for field " + fieldName + "."));
        }
    }

    private Spawner findSpawner(Player player) {
        Block block = player.getLocation().getBlock();
        for (Dungeon dungeon : plugin.getDungeonManager().getDungeons()) {
            var spawners = dungeon.getSpawners();
            if (spawners != null)
                for (Spawner spawner : spawners.values()) {
                    if (spawner.getBlock().equals(block))
                        return spawner;
                }
        }
        return null;
    }

}
