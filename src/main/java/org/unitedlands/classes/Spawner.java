package org.unitedlands.classes;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;

public class Spawner {

    public UUID uuid;

    private String world;
    private Dungeon dungeon;
    private Location location;
    private Block block;

    public double radius = 16;
    public String mobType = "ZOMBIE";
    public boolean isMythicMob = false;
    public int maxMobs = 1;
    public double spawnFrequency = 1;
    public boolean isGroupSpawn = false;

    public int killsToComplete = Integer.MAX_VALUE;
    public boolean isComplete = false;

    private double lastSpawnTime;
    public int currentKillCount;

    private final UnitedDungeons plugin = getPlugin();

    public Spawner() {

    }

    public Spawner(Location location, Dungeon dungeon) {
        this.uuid = UUID.randomUUID();

        this.location = location.getBlock().getLocation().add(0.5, 0.5, 0.5);
        this.world = location.getWorld().getName();
        this.block = location.getBlock();

        this.dungeon = dungeon;
    }

    public void registerKill() {
        currentKillCount++;
    }

    public void checkCompletion() {
        if (currentKillCount >= this.killsToComplete) {
            this.isComplete = true;
        }
    }

    public void resetCompletion() {
        currentKillCount = 0;
        this.isComplete = false;
    }

    public void prepareSpawn() {
        if (System.currentTimeMillis() - lastSpawnTime >= this.spawnFrequency) {
            lastSpawnTime = System.currentTimeMillis();
            var currentMobCount = plugin.getMobManager().getMobCount(this);
            if (currentMobCount < this.maxMobs) {
                if (!this.isGroupSpawn) {
                    plugin.getMobManager().createMob(this);
                } else {
                    var num = this.maxMobs - currentMobCount;
                    for (int i = 0; i < num; i++) {
                        plugin.getMobManager().createMob(this);
                    }
                }
            }
        }
    }

    public boolean isPlayerNearby() {
        Collection<Entity> nearbyEntities = Bukkit.getWorld(this.world).getNearbyEntities(this.location, this.radius,
                this.radius, this.radius);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                return true;
            }
        }
        return false;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
        this.block = location.getBlock();
    }


    public Dungeon getDungeon() {
        return this.dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public String getWorld() {
        return this.world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Block getBlock() {
        return this.block;
    }

    private UnitedDungeons getPlugin() {
        return (UnitedDungeons) Bukkit.getPluginManager().getPlugin("UnitedDungeons");
    }

}
