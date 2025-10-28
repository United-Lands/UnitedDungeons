package org.unitedlands.managers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.RewardChest;

public class RewardChestManager {

    @SuppressWarnings("unused")
    private final UnitedDungeons plugin;

    private Set<RewardChest> chests;

    public RewardChestManager(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    public void loadChests(Collection<Dungeon> dungeons) {
        chests = new HashSet<>();
        for (var dungeon : dungeons) {
            for (var room : dungeon.getRooms()) {
                chests.addAll(room.getChests());
            }
        }
        plugin.getLogger().info("Loaded " + chests.size() + " reward chests.");
    }

    public void registerChest(RewardChest chest) {
        if (chests == null)
            return;
        chests.add(chest);
    }

    public void unregisterChest(RewardChest chest) {
        if (chests == null)
            return;
        chests.remove(chest);
    }

    public RewardChest getChestAtLocation(Location location) {
        for (var chest : chests) {
            plugin.getLogger().info(chest.getLocation().getBlock().getLocation() + " == " + location);
            if (chest.getLocation().getBlock().getLocation().equals(location))
                return chest;
        }
        return null;
    }

}
