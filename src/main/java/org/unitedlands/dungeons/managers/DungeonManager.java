package org.unitedlands.dungeons.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.Room;
import org.unitedlands.dungeons.classes.Spawner;
import org.unitedlands.dungeons.utils.JsonUtils;
import org.unitedlands.dungeons.utils.MessageProvider;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

public class DungeonManager {

    private final UnitedDungeons plugin;
    private final MessageProvider messageProvider;

    private Map<UUID, Dungeon> editSessions = new HashMap<>();
    private Map<UUID, Dungeon> dungeons = new HashMap<>();

    private BukkitTask dungeonCheckerTask;

    public DungeonManager(UnitedDungeons plugin, MessageProvider messageProvider) {
        this.plugin = plugin;
        this.messageProvider = messageProvider;
    }

    // #region Public utility functions

    public Collection<Dungeon> getDungeons() {
        if (dungeons == null)
            return new ArrayList<Dungeon>();
        return dungeons.values();
    }

    public List<String> getDungeonNames() {
        if (dungeons == null)
            return new ArrayList<String>();
        return dungeons.values().stream().map(d -> d.getName()).collect(Collectors.toList());
    }

    public List<String> getPublicDungeonNames() {
        if (dungeons == null)
            return new ArrayList<String>();
        return dungeons.values().stream().filter(d -> d.isPublic()).map(d -> d.getName()).collect(Collectors.toList());
    }

    public Dungeon getDungeon(UUID uuid) {
        if (dungeons == null)
            return null;
        return dungeons.getOrDefault(uuid, null);
    }

    public Dungeon getDungeon(String name) {
        if (dungeons == null)
            return null;
        return dungeons.values().stream().filter(d -> d.getName().equals(name)).findFirst().orElse(null);
    }

    public Dungeon getClosestDungeon(Location location) {

        if (dungeons == null || dungeons.isEmpty())
            return null;

        Dungeon closestDungeon = null;
        double closestDistanceSquared = Double.MAX_VALUE;

        for (var dungeon : dungeons.values()) {
            var dungeonLocation = dungeon.getLocation();
            if (!dungeonLocation.getWorld().equals(location.getWorld()))
                continue;
            double distanceSquared = dungeonLocation.distanceSquared(location);
            if (distanceSquared < closestDistanceSquared) {
                closestDistanceSquared = distanceSquared;
                closestDungeon = dungeon;
            }
        }

        return closestDungeon;
    }

    public Room getRoomAtLocation(Dungeon dungeon, Location location) {
        for (Room room : dungeon.getRooms()) {
            if (room.getBoundingBox().contains(location.getX(), location.getY(), location.getZ()))
                return room;
        }
        return null;
    }

    public Dungeon getPlayerDungeon(Player player) {
        for (var dungeon : dungeons.values()) {
            if (dungeon.isPlayerInDungeon(player))
                return dungeon;
        }
        return null;
    }

    // #endregion

    public void addDungeon(Dungeon dungeon) {
        dungeons.put(dungeon.getUuid(), dungeon);
    }

    public void renameDungeon(Dungeon dungeon, String newName) {
        dungeon.setName(newName);
        dungeons.put(dungeon.getUuid(), dungeon);
    }

    public void removeDungeon(Dungeon dungeon) {
        var editSessionsToRemove = editSessions.entrySet().stream().filter(e -> e.getValue().equals(dungeon))
                .collect(Collectors.toList());
        for (var editSession : editSessionsToRemove)
            editSessions.remove(editSession.getKey());
        dungeons.remove(dungeon.getUuid());
    }

    public void loadDungeons() {

        dungeons = new HashMap<>();

        String directoryPath = File.separator + "dungeons";
        File directory = new File(plugin.getDataFolder(), directoryPath);

        File[] filesList = directory.listFiles();

        if (filesList != null) {
            for (File file : filesList) {
                Dungeon dungeon = loadDungeon(file);
                if (dungeon != null) {
                    for (var room : dungeon.getRooms())
                        room.setDungeon(dungeon);
                    dungeons.put(dungeon.getUuid(), dungeon);
                    dungeon.reset();
                    Logger.log("Dungeon " + dungeon.getName() + " loaded.");
                } else {
                    Logger.logError("Error loading dungeon file " + file.getName());
                }
            }
        }

        plugin.getChestManager().loadLootChests(dungeons.values());
    }

    public void startChecks() {

        Logger.log("Starting dungeon checks...");

        var frequency = plugin.getConfig().getLong("general.tick-frequency", 1L);

        dungeonCheckerTask = new BukkitRunnable() {
            @Override
            public void run() {

                for (Dungeon dungeon : dungeons.values()) {

                    if (!dungeon.isActive())
                        continue;

                    dungeon.checkPlayerProximity();
                    dungeon.checkCooldown();
                    dungeon.checkPlayerLockCooldowns();

                    if (!dungeon.isSleeping()) {
                        dungeon.checkPlayerActivity();
                        if (dungeon.isOnCooldown()) {
                            continue;
                        } else if (dungeon.requireLock() && !dungeon.isLocked()) {
                            continue;
                        } else {
                            dungeon.checkLock();
                            dungeon.checkRooms();
                        }
                    } else {
                        if (dungeon.isOnCooldown()) {
                            continue;
                        } else {
                            var autoResetTime = plugin.getConfig().getLong("general.auto-reset-time", 1800L);
                            if (autoResetTime != -1L) {
                                var sleepStart = dungeon.getSleepStartTime();
                                if (System.currentTimeMillis() - sleepStart > autoResetTime * 1000L) {
                                    dungeon.autoReset();
                                }
                            }
                        }
                    }
                }

                plugin.getMobManager().pruneMobs();
            }
        }.runTaskTimer(plugin, 0L, frequency * 20L);
    }

    public void stopChecks() {
        Logger.log("Clearing dungeon mobs...");
        for (Dungeon dungeon : dungeons.values()) {
            var spawners = dungeon.getSpawners();
            if (spawners != null) {
                for (Spawner s : spawners) {
                    plugin.getMobManager().removeAllSpawnerMobs(s);
                }
            }
        }
        Logger.log("Stopping dungeon checks...");
        if (dungeonCheckerTask != null)
            dungeonCheckerTask.cancel();
    }

    public void saveDungeon(Dungeon dungeon, CommandSender sender) {
        if (!saveDungeon(dungeon)) {
            Messenger.sendMessage(sender, messageProvider.get("messages.save-error"), null,
                    messageProvider.get("messages.prefix"));
        } else {
            Messenger.sendMessage(sender, messageProvider.get("messages.save-success"), null,
                    messageProvider.get("messages.prefix"));
        }
    }

    public boolean saveDungeon(Dungeon dungeon) {
        var uuid = dungeon.getUuid();
        var filePath = File.separator + "dungeons" + File.separator + uuid + ".json";

        File dungeonFile = new File(plugin.getDataFolder(), filePath);
        if (!dungeonFile.exists()) {
            dungeonFile.getParentFile().mkdirs();
            try {
                dungeonFile.createNewFile();
            } catch (IOException ex) {
                Logger.logError(ex.getMessage());
            }
        }

        try {
            JsonUtils.saveObjectToFile(dungeon, dungeonFile);
            return true;
        } catch (IOException ex) {
            Logger.logError(ex.getMessage());
            return false;
        }
    }

    public Dungeon loadDungeon(File file) {
        try {
            return JsonUtils.loadObjectFromFile(file, Dungeon.class);
        } catch (IOException ex) {
            Logger.logError(ex.getMessage());
            return null;
        }
    }

    public void registerEditSessionForPlayer(UUID userId, Dungeon dungen) {
        editSessions.put(userId, dungen);
    }

    public Dungeon getEditSessionForPlayr(UUID userId) {
        return editSessions.get(userId);
    }
}
