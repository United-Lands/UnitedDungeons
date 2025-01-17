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

    public String world;
    public Dungeon dungeon;
    public Location location;
    public Block block;
   
    public double radius = 16;
    public String mobType = "ZOMBIE";
    public boolean isMythicMob = false;
    public int maxMobs = 1;
    public double spawnFrequency = 1;
    public boolean isGroupSpawn = false;
    
    public int killsToComplete = Integer.MAX_VALUE;
    public boolean complete = false;

    private double lastSpawnTime;
    private int currentKillCount;

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
            this.complete = true;
        }
    }

    public void resetCompletion() {
        currentKillCount = 0;
        this.complete = false;
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
        Collection<Entity> nearbyEntities = Bukkit.getWorld(this.world).getNearbyEntities(this.location, this.radius, this.radius, this.radius);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                return true;
            }
        }
        return false;
    }

    private UnitedDungeons getPlugin() {
        return (UnitedDungeons) Bukkit.getPluginManager().getPlugin("UnitedDungeons");
    }

}
