package org.unitedlands.dungeons.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.unitedlands.UnitedLib;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.Spawner;
import org.unitedlands.utils.Logger;

public class MobManager {

    private HashMap<UUID, Spawner> mobList = new HashMap<>();

    public MobManager() {

    }

    public void createMob(Dungeon dungeon, Spawner spawner) {

        double level = 1;
        if (dungeon.scaleMobLevels())
        {
            if (dungeon.isLocked() && dungeon.getLockedPlayersInDungeon().size() > 1)
                level = dungeon.getLockedPlayersInDungeon().size();
            else if (dungeon.getPlayersInDungeon().size() > 1)
                level = dungeon.getPlayersInDungeon().size();
        }

        var newMobUuid = UnitedLib.getInstance().getMobFactory().createMobAtLocation(spawner.getMobType(), spawner.getLocation(), level);
        if (newMobUuid == null) {
            Logger.logError("Error creating new mob for spawner " + spawner.getUuid());
            return;
        }
        registerMob(newMobUuid, spawner);
    }

    // Remove mobs that have been unloaded for external reasons (e.g. chunk unloads,
    // kill commands)
    public void pruneMobs() {
        List<UUID> mobsToPrune = new ArrayList<>();
        for (var mobId : mobList.keySet()) {
            var entity = Bukkit.getEntity(mobId);
            if (entity == null)
                mobsToPrune.add(mobId);
        }
        for (var mobId : mobsToPrune) {
            Logger.log("Entity with id " + mobId + " no longer trackable, removing.");
            deregisterMob(mobId);
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
