package org.unitedlands.commands;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.Spawner;
import org.unitedlands.utils.MessageFormatter;

import com.destroystokyo.paper.ParticleBuilder;

import net.md_5.bungee.api.ChatColor;

public class DungeonCommands implements CommandExecutor {

    private final UnitedDungeons plugin;

    public DungeonCommands(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            String[] args) {

        if (args.length == 0)
            return false;

        var player = (Player) sender;

        switch (args[0]) {
            case "create":
                handleDungeonCreate(player, args);
                break;
            case "list":
                handleDungeonList(player);
                break;
            default:
                parseDungeonSubcommands(player, args);
                break;
        }

        return true;
    }

    private void parseDungeonSubcommands(Player player, String[] args) {
        if (args.length == 1)
            return;

        var dungeon = plugin.getDungeon(args[0]);
        if (dungeon == null) {
            MessageFormatter.getWithPrefix(ChatColor.RED + "No dungeon with name " + args[0] + " found!");
            return;
        }

        switch (args[1]) {
            case "info":
                handleDungeonInfo(player, dungeon);
                break;
            case "showmarkers":
                handleDungeonShowmarkers(player, dungeon);
                break;
            case "reset":
                handleDungeonReset(player, dungeon);
                break;
            case "rename":
                handleDungeonRename(player, dungeon, args);
                break;
            case "delete":
                handleDungeonDelete(player, dungeon);
                break;
            case "toggle":
                handleDungeonToggle(player, dungeon);
                break;
            case "unlock":
                handleDungeonUnlock(player, dungeon);
                break;
            case "complete":
                handleDungeonComplete(player, dungeon);
                break;
            case "set":
                handleDungeonSetters(player, dungeon, args);
                break;
        }
    }

    private void handleDungeonSetters(Player player, Dungeon dungeon, String[] args) {
        if (args.length < 3)
            return;

        switch (args[2]) {

            case "location":
                handleDungeonSetLocation(player, dungeon);
                break;
            case "exitLocation":
                handleDungeonSetExitLocation(player, dungeon);
                break;
            case "chestPosition":
                handleDungeonSetChestPos(player, dungeon);
                break;
            case "platePosition":
                handleDungeonSetPlatePos(player, dungeon);
                break;
            case "description":
                handleDungeonSetDescription(player, dungeon, args);
                break;
            default:
                if (args.length < 4)
                    return;
                handleDungeonSetField(player, dungeon, args[2], args[3]);
                break;
        }
    }

    private void handleDungeonSetField(Player player, Dungeon dungeon, String field, String value) {
        setDungeonField(player, dungeon, field, value);
    }

    private void handleDungeonSetDescription(Player player, Dungeon dungeon, String[] args) {
        if (args.length < 4)
            return;

        String desc = "";
        for (int i = 3; i < args.length; i++)
            desc += " " + args[i];

        setDungeonField(player, dungeon, "description", desc);
    }

    private void handleDungeonRename(Player player, Dungeon dungeon, String[] args) {
        if (args.length < 3)
            return;

        plugin.Dungeons.remove(dungeon.name);
        dungeon.name = args[2];
        plugin.Dungeons.put(dungeon.name, dungeon);

        dungeon.save();

        player.sendMessage(MessageFormatter
                .getWithPrefix("Dungeon renamed to " + dungeon.name));
    }

    private void handleDungeonSetLocation(Player player, Dungeon dungeon) {
        dungeon.setLocation(player.getLocation());
        dungeon.save();

        player.sendMessage(MessageFormatter
                .getWithPrefix("Location for dungeon " + dungeon.name + " set."));
    }

    private void handleDungeonSetExitLocation(Player player, Dungeon dungeon) {

        var playerLoc = player.getLocation();
        if (Math.abs(dungeon.location.getX() - playerLoc.getX()) <= (dungeon.width / 2) &&
                Math.abs(dungeon.location.getY() - playerLoc.getY()) <= (dungeon.height / 2) &&
                Math.abs(dungeon.location.getZ() - playerLoc.getZ()) <= (dungeon.length / 2)) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix(ChatColor.RED + "Exit location must be outside of the dungeon boundaries."));
            return;
        }

        dungeon.setExitLocation(playerLoc);
        dungeon.save();

        player.sendMessage(MessageFormatter
                .getWithPrefix("Exit location for dungeon " + dungeon.name + " set."));
    }

    private void handleDungeonSetPlatePos(Player player, Dungeon dungeon) {
        dungeon.setPressurePlateLocation(player.getLocation());
        dungeon.save();
        player.sendMessage(MessageFormatter
                .getWithPrefix("Pressure plate location for dungeon " + dungeon.name + " set."));
    }

    private void handleDungeonSetChestPos(Player player, Dungeon dungeon) {
        dungeon.setRewardDropLocation(player.getLocation());
        dungeon.save();

        player.sendMessage(MessageFormatter
                .getWithPrefix("Reward drop location for dungeon " + dungeon.name + " set."));
    }

    private void handleDungeonToggle(Player player, Dungeon dungeon) {
        dungeon.isActive = !dungeon.isActive;
        dungeon.save();

        if (!dungeon.isActive) {
            if (dungeon.getSpawners() != null) {
                for (Spawner s : dungeon.getSpawners().values()) {
                    plugin.getMobManager().removeAllSpawnerMobs(s);
                }
            }
            dungeon.resetLock();
            dungeon.resetCompletion();
        }

        player.sendMessage(MessageFormatter
                .getWithPrefix("Dungeon " + dungeon.name + " " + (dungeon.isActive ? ChatColor.GREEN + "started"
                        : ChatColor.RED + "stopped and reset")));
    }

    private void handleDungeonUnlock(Player player, Dungeon dungeon) {
        dungeon.resetLock();
        player.sendMessage(MessageFormatter
                .getWithPrefix("Dungeon " + dungeon.name + " unlocked."));
    }

    private void handleDungeonComplete(Player player, Dungeon dungeon) {
        dungeon.complete();
        player.sendMessage(MessageFormatter
                .getWithPrefix("Dungeon " + dungeon.name + " completed."));
    }

    private void handleDungeonDelete(Player player, Dungeon dungeon) {

        var dungeonName = dungeon.name;
        var filePath = File.separator + "dungeons" + File.separator + dungeon.uuid + ".yml";
        File dungeonFile = new File(plugin.getDataFolder(), filePath);
        if (dungeonFile.exists()) {
            dungeonFile.delete();

            plugin.loadDungeons();

            player.sendMessage(MessageFormatter
                    .getWithPrefix(
                            ChatColor.YELLOW + "Dungeon " + dungeonName + " deleted successfully."));
        }
    }

    private void handleDungeonReset(Player player, Dungeon dungeon) {
        dungeon.resetCompletion();
        player.sendMessage(MessageFormatter.getWithPrefix("Dungeon " + dungeon.name + " reset."));
    }

    private void handleDungeonInfo(Player player, Dungeon dungeon) {

        List<String> fieldValues = new ArrayList<>();
        for (Field field : Dungeon.class.getFields()) {
            try {
                if (field.canAccess(dungeon)) {
                    if (!field.getType().equals(Location.class))
                        fieldValues.add(ChatColor.WHITE + field.getName() + ": " + ChatColor.GRAY + field.get(dungeon));
                }
            } catch (IllegalAccessException ex) {
                plugin.getLogger().severe("Illegal access to field " + field.getName());
            }
        }

        player.sendMessage(MessageFormatter.getWithPrefix(ChatColor.WHITE + "" + ChatColor.BOLD + dungeon.name));
        player.sendMessage(MessageFormatter.getWithPrefix(String.join(ChatColor.DARK_GRAY + " | ", fieldValues)));
    }

    private void handleDungeonList(Player player) {
        player.sendMessage(
                MessageFormatter.getWithPrefix(ChatColor.WHITE + "" + ChatColor.BOLD + "Registered Dungeons:"));
        for (Dungeon dungeon : plugin.getDungeons()) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix(dungeon.name + " (" + dungeon.getSpawners().size() + " spawners)"));
        }
    }

    private void handleDungeonCreate(Player player, String[] args) {

        if (args.length != 2) {
            player.sendMessage(
                    MessageFormatter.getWithPrefix(ChatColor.RED + "You must provide a name for the new dungeon."));
            return;
        }

        if (plugin.Dungeons.containsKey(args[1])) {
            player.sendMessage(
                    MessageFormatter.getWithPrefix(ChatColor.RED + "A dungeon with that name already exists."));
            return;
        }

        Dungeon dungeon = new Dungeon(player.getLocation());
        dungeon.uuid = UUID.randomUUID();
        dungeon.name = args[1];
        dungeon.isActive = false;

        if (dungeon.save()) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix("New dungeon " + dungeon.name + " created successfully."));

            plugin.addDungeon(dungeon);
            return;
        } else {
            player.sendMessage(MessageFormatter
                    .getWithPrefix(ChatColor.RED + "Error saving new dungeon " + dungeon.name));
            return;
        }
    }

    private void handleDungeonShowmarkers(Player player, Dungeon dungeon) {

        player.sendMessage(MessageFormatter.getWithPrefix("Showing dungeon markers: " +
                ChatColor.GREEN + "Bounding Box " +
                ChatColor.YELLOW + "Reward Chest " +
                ChatColor.DARK_GRAY + "Pressure Plate " +
                ChatColor.RED + "Mob Spawners " +
                ChatColor.WHITE + "Exit"));

        new BukkitRunnable() {
            int counter = 0;
            int maxExecutions = 120;

            @Override
            public void run() {
                counter++;

                new ParticleBuilder(Particle.HAPPY_VILLAGER)
                        .location(dungeon.location)
                        .count(1)
                        .receivers(player)
                        .spawn();

                if (dungeon.rewardDropLocation != null) {
                    new ParticleBuilder(Particle.ENTITY_EFFECT)
                            .data(Color.YELLOW)
                            .location(dungeon.rewardDropLocation)
                            .offset(0.15, 0.15, 0.15)
                            .count(5)
                            .receivers(player)
                            .spawn();
                }

                if (dungeon.pressurePlateLocation != null) {
                    new ParticleBuilder(Particle.ENTITY_EFFECT)
                            .data(Color.GRAY)
                            .location(dungeon.pressurePlateLocation)
                            .offset(0.15, 0.15, 0.15)
                            .count(5)
                            .receivers(player)
                            .spawn();
                }

                if (dungeon.exitLocation != null) {
                    new ParticleBuilder(Particle.ENTITY_EFFECT)
                            .data(Color.WHITE)
                            .location(dungeon.exitLocation)
                            .offset(0.15, 0.15, 0.15)
                            .count(5)
                            .receivers(player)
                            .spawn();
                }

                var spawners = dungeon.getSpawners();
                if (spawners != null && !spawners.isEmpty()) {

                    for (var spawner : spawners.values()) {
                        
                        // spawnCubeEdges(spawner.getLocation(), (int) spawner.radius, (int) spawner.radius,
                        //         (int) spawner.radius, location -> {
                        //             player.spawnParticle(Particle.RAID_OMEN, location, 0, 0, 0, 0, 0.05);
                        //         });

                        new ParticleBuilder(Particle.RAID_OMEN)
                                .location(spawner.getLocation())
                                .count(5)
                                .receivers(player)
                                .spawn();
                    }

                }

                spawnCubeEdges(dungeon.location, dungeon.width, dungeon.height, dungeon.length, location -> {
                    new ParticleBuilder(Particle.HAPPY_VILLAGER)
                            .location(location)
                            .count(1)
                            .receivers(player)
                            .spawn();
                });

                if (counter >= maxExecutions) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
        return;
    }

    private void spawnCubeEdges(Location center, int width, int height, int length,
            Consumer<Location> particleBuilder) {

        double halfWidth = Math.floor(width / 2);
        double halfHeight = Math.floor(height / 2);
        double halfLength = Math.floor(length / 2);

        double minX = center.getX() - halfWidth;
        double maxX = center.getX() + halfWidth;
        double minY = center.getY() - halfHeight;
        double maxY = center.getY() + halfHeight;
        double minZ = center.getZ() - halfLength;
        double maxZ = center.getZ() + halfLength;

        double widthParticleSpacing = Math.max(1, Math.round(width / 8));
        double heightParticleSpacing = Math.max(1, Math.round(height / 8));
        double lengthParticleSpacing = Math.max(1, Math.round(length / 8));

        for (double x = minX; x <= maxX; x += widthParticleSpacing) {
            for (double y = minY; y <= maxY; y += halfHeight * 2) {
                particleBuilder.accept(new Location(center.getWorld(), x, y, minZ));
                particleBuilder.accept(new Location(center.getWorld(), x, y, maxZ));
            }
        }

        for (double y = minY; y <= maxY; y += heightParticleSpacing) {
            for (double x = minX; x <= maxX; x += halfWidth * 2) {
                particleBuilder.accept(new Location(center.getWorld(), x, y, minZ));
                particleBuilder.accept(new Location(center.getWorld(), x, y, maxZ));
            }
        }

        for (double z = minZ; z <= maxZ; z += lengthParticleSpacing) {
            for (double y = minY; y <= maxY; y += halfHeight * 2) {
                particleBuilder.accept(new Location(center.getWorld(), minX, y, z));
                particleBuilder.accept(new Location(center.getWorld(), maxX, y, z));
            }
        }
    }

    private void setDungeonField(Player player, Dungeon dungeon, String fieldName, String arg) {
        try {
            Field field = Dungeon.class.getDeclaredField(fieldName);
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

            field.set(dungeon, value);
            dungeon.save();
            player.sendMessage(MessageFormatter.getWithPrefix(
                    "Successfully updated " + fieldName + " to " + value + " in dungeon " + dungeon.name));
        } catch (NoSuchFieldException e) {
            plugin.getLogger().severe("Field " + fieldName + " does not exist.");
        } catch (IllegalAccessException e) {
            plugin.getLogger().severe("Unable to access field " + fieldName + ".");
        } catch (NumberFormatException e) {
            player.sendMessage(MessageFormatter.getWithPrefix("Invalid value for field " + fieldName + "."));
        }
    }

}
