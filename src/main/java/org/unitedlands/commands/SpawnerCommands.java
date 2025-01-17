package org.unitedlands.commands;

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

        String value = args[2];
        switch (args[1]) {
            case "mob":
                handleSpawnerSetMob(player, spawner, value);
                break;
            case "radius":
                handleSpawnerSetRadius(player, spawner, value);
                break;
            case "maxmobs":
                handleSpawnerSetMaxMobs(player, spawner, value);
                break;
            case "mythicmob":
                handleSpawnerSetMythicMobs(player, spawner, value);
                break;
            case "groupspawn":
                handleSpawnerSetGroupspawn(player, spawner, value);
                break;
            case "frequency":
                handleSpawnerSetFrequency(player, spawner, value);
                break;
            case "killstocomplete":
                handleSpawnerSetKillsToComplete(player, spawner, value);
                break;
        }
    }

    private void handleSpawnerSetKillsToComplete(Player player, Spawner spawner, String value) {
        int killstocomplete = 0;
        try {
            killstocomplete = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            player.sendMessage(MessageFormatter.getWithPrefix("Kills to complete must be a valid number"));
            return;
        }
        spawner.killsToComplete = killstocomplete;
        spawner.dungeon.save();
        player.sendMessage(MessageFormatter.getWithPrefix("Spawner kills to complete set to " + value));
        return;
    }

    private void handleSpawnerSetFrequency(Player player, Spawner spawner, String value) {
        double frequency = 0;
        try {
            frequency = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            player.sendMessage(MessageFormatter.getWithPrefix("Frequency must be a valid number"));
            return;
        }
        spawner.spawnFrequency = frequency;
        spawner.dungeon.save();
        player.sendMessage(MessageFormatter.getWithPrefix("Spawner spawn frequency set to " + value));
        return;
    }

    private void handleSpawnerSetGroupspawn(Player player, Spawner spawner, String value) {
        boolean groupspawn = false;
        try {
            groupspawn = Boolean.parseBoolean(value);
        } catch (NumberFormatException ex) {
            player.sendMessage(MessageFormatter.getWithPrefix("Groupspawn must be true or false"));
            return;
        }
        spawner.isGroupSpawn = groupspawn;
        player.sendMessage(MessageFormatter.getWithPrefix("Spawner groupspawn set to " + value));
        spawner.dungeon.save();
        return;
    }

    private void handleSpawnerSetMythicMobs(Player player, Spawner spawner, String value) {
        boolean mythicmob = false;
        try {
            mythicmob = Boolean.parseBoolean(value);
        } catch (NumberFormatException ex) {
            player.sendMessage(MessageFormatter.getWithPrefix("Mythicmob must be true or false"));
            return;
        }
        spawner.isMythicMob = mythicmob;
        spawner.dungeon.save();
        player.sendMessage(MessageFormatter.getWithPrefix("Spawner mythic mobs set to " + value));
        return;
    }

    private void handleSpawnerSetMaxMobs(Player player, Spawner spawner, String value) {
        int maxmobs = 0;
        try {
            maxmobs = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            player.sendMessage(MessageFormatter.getWithPrefix("Max mobs must be a valid number"));
            return;
        }
        spawner.maxMobs = maxmobs;
        spawner.dungeon.save();
        player.sendMessage(MessageFormatter.getWithPrefix("Spawner maximum mobs set to " + value));
        return;
    }

    private void handleSpawnerSetRadius(Player player, Spawner spawner, String value) {
        int radius = 0;
        try {
            radius = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            player.sendMessage(MessageFormatter.getWithPrefix("Radius must be a valid number"));
            return;
        }
        spawner.radius = radius;
        spawner.dungeon.save();
        player.sendMessage(MessageFormatter.getWithPrefix("Spawner activation radius set to " + value));
        return;
    }

    private void handleSpawnerSetMob(Player player, Spawner spawner, String value) {
        spawner.mobType = value;
        spawner.dungeon.save();
        player.sendMessage(MessageFormatter.getWithPrefix("Spawner mob type set to " + value));
    }

    private void handleSpawnerDelete(Player player, Spawner spawner) {
        plugin.getMobManager().removeAllSpawnerMobs(spawner);

        var dungeon = spawner.dungeon;
        dungeon.Spawners.remove(spawner.uuid);
        dungeon.save();

        player.sendMessage(MessageFormatter.getWithPrefix("Spawner removed."));
    }

    private void handleSpawnerCreate(Player player, String[] args) {
        if (args.length < 2)
            return;

        var dungeon = plugin.getDungeon(args[1]);
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
        player.sendMessage(
                MessageFormatter.getWithPrefix(ChatColor.WHITE + "" + ChatColor.BOLD + "Spawner Info"));
        player.sendMessage(
                MessageFormatter.getWithPrefix("Dungeon: " + spawner.dungeon.name + " | Mob type: "
                        + spawner.mobType + " | Mythic Mob: " + spawner.isMythicMob + " | Maximum mobs: "
                        + spawner.maxMobs + " | Group spawn: " + spawner.isGroupSpawn
                        + " | Spawn frequency (milliseconds): " + spawner.spawnFrequency
                        + " | Activation radius (blocks): " + spawner.radius
                        + " | Kills to complete: " + spawner.killsToComplete));
    }

    private Spawner findSpawner(Player player) {
        Block block = player.getLocation().getBlock();
        if (plugin.Dungeons != null) {
            for (Dungeon dungeon : plugin.Dungeons.values()) {
                if (dungeon.Spawners != null)
                    for (Spawner spawner : dungeon.Spawners.values()) {
                        if (spawner.block.equals(block))
                            return spawner;
                    }
            }
        }
        return null;
    }

}
