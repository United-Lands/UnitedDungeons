package org.unitedlands.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Spawner;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.DespawnMode;

public class MobManager {

    private final UnitedDungeons plugin;

    private HashMap<UUID, Spawner> mobList;

    public MobManager(UnitedDungeons plugin) {
        this.plugin = plugin;
        mobList = new HashMap<>();
    }

    public void createMob(Spawner spawner) {
        org.bukkit.World world = Bukkit.getWorld(spawner.getWorld());
        if (world == null)
            return;

        try {
            Entity mob = null;
            if (spawner.isMythicMob) {
                var mmob = MythicBukkit.inst().getMobManager().getMythicMob(spawner.mobType).orElse(null);
                if (mmob != null) {
                    ActiveMob ammob = MythicBukkit.inst().getMobManager().spawnMob(spawner.mobType, spawner.getLocation());
                    ammob.setDespawnMode(DespawnMode.NEVER);

                    registerMob(ammob.getUniqueId(), spawner);
                }
            } else {
                mob = world.spawnEntity(spawner.getLocation(), EntityType.valueOf(spawner.mobType));
                ((LivingEntity) mob).setPersistent(true);
                ((LivingEntity) mob).setRemoveWhenFarAway(false);

                registerMob(mob.getUniqueId(), spawner);
            }

        } catch (Exception ex) {
            plugin.getLogger().warning(ex.getMessage());
            return;
        }

    }

    public void removeAllSpawnerMobs(Spawner spawner) {
        var uuids = getSpawnerMobUUIDs(spawner);
        if (uuids == null || uuids.isEmpty())
            return;

        for (var uuid : uuids) {
            var entity = Bukkit.getEntity(uuid);
            if (entity != null)
                entity.remove();

            deregisterMob(uuid);
        }
    }

    public void checkMobKill(UUID uuid) {
        if (isMobRegistered(uuid)) {
            Spawner spawner = mobList.get(uuid);
            deregisterMob(uuid);
            spawner.registerKill();
            spawner.checkCompletion();
        }
    }

    public List<UUID> getSpawnerMobUUIDs(Spawner spawner) {
        return mobList.entrySet().stream().filter(m -> m.getValue().equals(spawner)).map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public long getMobCount(Spawner spawner) {
        return mobList.values().stream().filter(s -> s.equals(spawner)).collect(Collectors.counting());
    }

    public void registerMob(UUID uuid, Spawner spawner) {
        if (!mobList.containsKey(uuid))
            mobList.put(uuid, spawner);
    }

    public void deregisterMob(UUID uuid) {
        if (mobList.containsKey(uuid))
            mobList.remove(uuid);
    }

    public boolean isMobRegistered(UUID uuid) {
        return mobList.containsKey(uuid);
    }

    public List<UUID> getMobUUIDs() {
        return new ArrayList<UUID>(mobList.keySet());
    }

    public Spawner getMobSpawner(UUID uuid) {
        if (mobList.containsKey(uuid))
            return mobList.get(uuid);
        return null;
    }

}
