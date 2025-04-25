package org.unitedlands.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.Spawner;

public class DungeonManager {

    private final UnitedDungeons plugin;
    private Map<String, Dungeon> dungeons;

    private final long _checkFrequency = 1L;
    private BukkitTask _checker;

    public DungeonManager(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    public List<String> getDungeonNames() {
        if (dungeons == null)
            return new ArrayList<String>();
        return new ArrayList<String>(dungeons.keySet());
    }

    public List<String> getPublicDungeonNames() {
        if (dungeons == null)
            return new ArrayList<String>();
        return dungeons.values().stream().filter(d -> d.isPublicWarp).map(d -> d.name).collect(Collectors.toList());
    }

    public Collection<Dungeon> getDungeons() {
        if (dungeons == null)
            return new ArrayList<Dungeon>();
        return dungeons.values();
    }

    public Dungeon getDungeon(String name) {
        if (this.dungeons.containsKey(name))
            return this.dungeons.get(name);
        return null;
    }

    public void addDungeon(Dungeon dungeon) {
        dungeons.put(dungeon.name, dungeon);
    }

    public void renameDungeon(Dungeon dungeon, String newName) {
        dungeons.remove(dungeon.name);
        dungeon.name = newName;
        dungeons.put(dungeon.name, dungeon);
    }

    public void removeDungeon(Dungeon dungeon) {
        dungeons.remove(dungeon.name);
    }

    public void loadDungeons() {

        dungeons = new HashMap<>();

        String directoryPath = File.separator + "dungeons";
        File directory = new File(plugin.getDataFolder(), directoryPath);

        File[] filesList = directory.listFiles();

        if (filesList != null) {
            for (File file : filesList) {
                Dungeon dungeon = new Dungeon(file);
                dungeons.put(dungeon.name, dungeon);

                plugin.getLogger().info("Dungeon " + dungeon.name + " loaded.");
            }
        }
    }

    public void startChecks() {
        plugin.getLogger().info("Starting dungeon checks...");
        _checker = new BukkitRunnable() {
            @Override
            public void run() {
                for (Dungeon dungeon : dungeons.values()) {
                    if (!dungeon.isActive)
                        continue;

                    dungeon.checkPlayerActivity();
                    dungeon.checkLock();
                    dungeon.checkCooldown();

                    if (dungeon.isSleeping || dungeon.isOnCooldown) {
                        continue;
                    } else {
                        var spawners = dungeon.getSpawners();
                        if (spawners != null && !spawners.isEmpty()) {
                            boolean allComplete = true;
                            for (Spawner s : spawners.values()) {
                                s.checkCompletion();
                                allComplete = allComplete && s.isComplete;
                                if (!s.isComplete) {
                                    if (s.isPlayerNearby()) {
                                        s.prepareSpawn();
                                    }
                                }
                            }
                            if (allComplete) {
                                plugin.getLogger().info("Dungeon " + dungeon.name + " completed.");
                                dungeon.complete();
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, _checkFrequency * 20L);
    }

    public void stopChecks() {
        plugin.getLogger().info("Clearing dungeon mobs...");
        for (Dungeon dungeon : dungeons.values()) {
            var spawners = dungeon.getSpawners();
            if (spawners != null) {
                for (Spawner s : spawners.values()) {
                    plugin.getMobManager().removeAllSpawnerMobs(s);
                }
            }
            dungeon.resetCompletion();
        }
        plugin.getLogger().info("Stopping dungeon checks...");
        _checker.cancel();
    }
}
