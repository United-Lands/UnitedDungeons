package org.unitedlands.managers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.LootChest;

public class LootChestManager {


    private final UnitedDungeons plugin;

    private Set<LootChest> lootChests;

    public LootChestManager(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    public void loadLootChests(Collection<Dungeon> dungeons) {
        lootChests = new HashSet<>();
        for (var dungeon : dungeons) {
            for (var room : dungeon.getRooms()) {
                lootChests.addAll(room.getLootChests());
            }
        }
        plugin.getLogger().info("Loaded " + lootChests.size() + " loot chests.");
    }

    public void registerLootChest(LootChest chest) {
        if (lootChests == null)
            return;
        lootChests.add(chest);
    }

    public void unregisterLootChest(LootChest chest) {
        if (lootChests == null)
            return;
        lootChests.remove(chest);
    }

    public LootChest getLootChestAtLocation(Location location) {
        for (var chest : lootChests) {
            if (chest.getLocation().getBlock().getLocation().equals(location))
                return chest;
        }
        return null;
    }

}
