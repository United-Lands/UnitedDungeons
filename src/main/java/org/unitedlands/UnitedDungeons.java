package org.unitedlands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.Spawner;
import org.unitedlands.commands.DungeonCommands;
import org.unitedlands.commands.GlobalCommands;
import org.unitedlands.commands.SpawnerCommands;
import org.unitedlands.listeners.MobDeathListener;
import org.unitedlands.managers.MobManager;
import org.unitedlands.tabcompleters.DungeonCommandTabCompleter;
import org.unitedlands.tabcompleters.GlobalCommandsTabCompleter;
import org.unitedlands.tabcompleters.SpawnerCommandTabCompleter;

public class UnitedDungeons extends JavaPlugin {

    public Map<String, Dungeon> Dungeons;

    private MobManager mobManager;

    private final long _checkFrequency = 1L;
    private BukkitTask _checker;

    @Override
    public void onEnable() {

        mobManager = new MobManager(this);

        getCommand("ud").setExecutor(new GlobalCommands(this));
        getCommand("ud").setTabCompleter(new GlobalCommandsTabCompleter(this));

        getCommand("uddungeon").setExecutor(new DungeonCommands(this));
        getCommand("uddungeon").setTabCompleter(new DungeonCommandTabCompleter(this));

        getCommand("udspawner").setExecutor(new SpawnerCommands(this));
        getCommand("udspawner").setTabCompleter(new SpawnerCommandTabCompleter(this));

        saveDefaultConfig();
        loadDungeons();
        startChecks();

        getServer().getPluginManager().registerEvents(new MobDeathListener(this), this);

        getLogger().info("UnitedDungeons initialized.");
    }

    @Override
    public void onDisable() {
        stopChecks();
        super.onDisable();
    }

    public MobManager getMobManager() {
        return this.mobManager;
    }

    public List<String> getDungeonNames() {
        return new ArrayList<String>(Dungeons.keySet());
    }

    public List<String> getPublicDungeonNames() {
        return Dungeons.values().stream().filter(d -> d.isPublicWarp).map(d -> d.name).collect(Collectors.toList());
    }

    public Collection<Dungeon> getDungeons() {
        return Dungeons.values();
    }

    public Dungeon getDungeon(String name) {
        if (this.Dungeons.containsKey(name))
            return this.Dungeons.get(name);
        return null;
    }

    public void addDungeon(Dungeon dungeon) {
        Dungeons.put(dungeon.name, dungeon);
    }

    public void loadDungeons() {

        Dungeons = new HashMap<>();

        String directoryPath = File.separator + "dungeons";
        File directory = new File(getDataFolder(), directoryPath);

        File[] filesList = directory.listFiles();

        if (filesList != null) {
            for (File file : filesList) {
                Dungeon dungeon = new Dungeon(file);
                Dungeons.put(dungeon.name, dungeon);

                getLogger().info("Dungeon " + dungeon.name + " loaded...");
            }
        } else {

        }
    }

    public void startChecks() {
        getLogger().info("Starting dungeon checks...");
        _checker = new BukkitRunnable() {
            @Override
            public void run() {
                for (Dungeon dungeon : Dungeons.values()) {
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
                                getLogger().info("Dungeon " + dungeon.name + " completed.");
                                dungeon.complete();
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, _checkFrequency * 20L);
    }

    public void stopChecks() {
        getLogger().info("Clearing dungeon mobs...");
        for (Dungeon dungeon : Dungeons.values()) {
            var spawners = dungeon.getSpawners();
            if (spawners != null) {
                for (Spawner s : spawners.values()) {
                    mobManager.removeAllSpawnerMobs(s);
                }
            }
            dungeon.resetCompletion();
        }
        getLogger().info("Stopping dungeon checks...");
        _checker.cancel();
    }

}
