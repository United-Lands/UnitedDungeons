package org.unitedlands.dungeons.classes;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.utils.annotations.Info;

import com.google.gson.annotations.Expose;

public class Spawner {

    @Expose
    private UUID uuid;
    @Expose
    private Location location;

    @Expose
    @Info
    private double radius = 16;
    @Expose
    @Info
    private String mobType = "ZOMBIE";
    @Expose
    @Info
    private int maxMobs = 1;
    @Expose
    @Info
    private double spawnFrequency = 1;
    @Expose
    @Info
    private boolean isGroupSpawn = false;

    @Expose
    @Info
    private int killsToComplete = Integer.MAX_VALUE;
    @Expose
    private boolean isComplete = false;

    @Expose
    private double lastSpawnTime;
    @Expose
    private int currentKillCount;

    private final UnitedDungeons plugin = getPlugin();

    public Spawner() {

    }

    public Spawner(Location location) {
        this.uuid = UUID.randomUUID();
        this.location = location.getBlock().getLocation().add(0.5, 0.5, 0.5);
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
        Collection<Entity> nearbyEntities = location.getNearbyEntities(this.radius, this.radius, this.radius);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                return true;
            }
        }
        return false;
    }

    // #region Getters & Setters

    private UnitedDungeons getPlugin() {
        return (UnitedDungeons) Bukkit.getPluginManager().getPlugin("UnitedDungeons");
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getMobType() {
        return mobType;
    }

    public void setMobType(String mobType) {
        this.mobType = mobType;
    }

    public int getMaxMobs() {
        return maxMobs;
    }

    public void setMaxMobs(int maxMobs) {
        this.maxMobs = maxMobs;
    }

    public double getSpawnFrequency() {
        return spawnFrequency;
    }

    public void setSpawnFrequency(double spawnFrequency) {
        this.spawnFrequency = spawnFrequency;
    }

    public boolean isGroupSpawn() {
        return isGroupSpawn;
    }

    public void setGroupSpawn(boolean isGroupSpawn) {
        this.isGroupSpawn = isGroupSpawn;
    }

    public int getKillsToComplete() {
        return killsToComplete;
    }

    public void setKillsToComplete(int killsToComplete) {
        this.killsToComplete = killsToComplete;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public double getLastSpawnTime() {
        return lastSpawnTime;
    }

    public void setLastSpawnTime(double lastSpawnTime) {
        this.lastSpawnTime = lastSpawnTime;
    }

    public int getCurrentKillCount() {
        return currentKillCount;
    }

    public void setCurrentKillCount(int currentKillCount) {
        this.currentKillCount = currentKillCount;
    }

    // #endregion

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Spawner s = (Spawner) o;
        return Objects.equals(uuid, s.getUuid());
    }

}
