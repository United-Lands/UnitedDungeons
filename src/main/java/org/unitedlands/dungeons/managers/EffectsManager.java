package org.unitedlands.dungeons.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.classes.Barrier;
import org.unitedlands.dungeons.classes.LockChest;
import org.unitedlands.dungeons.classes.LootChest;
import org.unitedlands.dungeons.classes.Spawner;
import org.unitedlands.dungeons.classes.SupplyChest;
import org.unitedlands.utils.Logger;

public class EffectsManager {

    private final UnitedDungeons plugin;

    private Set<Player> viewers = new HashSet<>();

    private BukkitRunnable displayTask = null;
    private Map<UUID, BukkitRunnable> soundLoopTasks = new HashMap<>();

    public EffectsManager(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    // #region Visualisation

    public void addViewer(Player player) {
        if (!viewers.contains(player))
            viewers.add(player);

        if (displayTask == null)
            startVisualisation();
    }

    public void removeViewer(Player player) {
        if (viewers.contains(player)) {
            viewers.remove(player);
            if (viewers.isEmpty())
                stopVisualisation();
        }
    }

    private void startVisualisation() {

        // POI visualisations
        String dungeonCenterParticleName = plugin.getConfig().getString("visualisation.dungeon-center.particle");
        Particle dungeonCenterParticle = Particle.valueOf(dungeonCenterParticleName);

        String spawnerParticleName = plugin.getConfig().getString("visualisation.spawner.particle");
        Particle spawnerParticle = Particle.valueOf(spawnerParticleName);

        String lootChestParticleName = plugin.getConfig().getString("visualisation.loot-chest.particle");
        Particle lootChestParticle = Particle.valueOf(lootChestParticleName);

        String lockChestParticleName = plugin.getConfig().getString("visualisation.lock-chest.particle");
        Particle lockChestParticle = Particle.valueOf(lockChestParticleName);

        String supplyChestParticleName = plugin.getConfig().getString("visualisation.supply-chest.particle");
        Particle supplyChestParticle = Particle.valueOf(supplyChestParticleName);

        // Barrier visualisations
        String barrierParticleName = plugin.getConfig().getString("visualisation.barrier.particle");
        Particle barrierParticle = Particle.valueOf(barrierParticleName);

        // Warp visualisations
        String warpParticleName = plugin.getConfig().getString("visualisation.warp.particle");
        Particle warpParticle = Particle.valueOf(warpParticleName);

        // Room visualisations
        String roomEdgeParticleName = plugin.getConfig().getString("visualisation.room-edge.particle");
        Particle roomEdgeParticle = Particle.valueOf(roomEdgeParticleName);
        Double roomEdgeParticleDensity = plugin.getConfig().getDouble("visualisation.room-edge.density");

        displayTask = new BukkitRunnable() {
            @Override
            public void run() {

                var dungeons = plugin.getDungeonManager().getDungeons();

                // Room visualisations

                for (Player player : viewers) {

                    for (var dungeon : dungeons) {

                        player.spawnParticle(dungeonCenterParticle, dungeon.getLocation(), 1, 0, 0, 0, 0);

                        if (dungeon.getWarpLocation() != null)
                            player.spawnParticle(warpParticle, dungeon.getWarpLocation(), 1, 0, 0, 0, 0);

                        for (var room : dungeon.getRooms()) {
                            var world = dungeon.getLocation().getWorld();
                            var boundingBox = room.getBoundingBox();
                            var boxEdges = getBoundingBoxEdges(world, boundingBox);

                            for (Edge edge : boxEdges) {
                                Location start = edge.start;
                                Location end = edge.end;

                                double dx = end.getX() - start.getX();
                                double dy = end.getY() - start.getY();
                                double dz = end.getZ() - start.getZ();

                                var edgeLength = Math.abs(dx + dy + dz);
                                var particleCount = edgeLength * roomEdgeParticleDensity;

                                for (int i = 0; i <= particleCount; i++) {
                                    double t = (double) i / particleCount;
                                    double x = start.getX() + dx * t;
                                    double y = start.getY() + dy * t;
                                    double z = start.getZ() + dz * t;
                                    player.spawnParticle(roomEdgeParticle, new Location(world, x, y, z), 1, 0, 0, 0, 0);
                                }
                            }

                            for (Spawner spawner : room.getSpawners()) {
                                player.spawnParticle(spawnerParticle, spawner.getLocation(), 1, 0, 0, 0, 0);
                            }

                            for (LootChest chest : room.getLootChests()) {
                                player.spawnParticle(lootChestParticle, chest.getLocation(), 1, 0, 0, 0, 0);
                            }

                            for (LockChest lockChest : room.getLockChests()) {
                                player.spawnParticle(lockChestParticle, lockChest.getLocation(), 1, 0, 0, 0, 0);
                            }

                            for (SupplyChest supplyChest : room.getSupplyChests()) {
                                player.spawnParticle(supplyChestParticle, supplyChest.getLocation(), 1, 0, 0, 0, 0);
                            }

                            for (Barrier barrier : room.getBarriers()) {
                                for (int i = 0; i < barrier.getHeight(); i++) {
                                    var loc = barrier.getLocation().clone();
                                    player.spawnParticle(barrierParticle, loc.add(0, i, 0), 1, 0, 0, 0, 0);
                                }
                            }
                        }
                    }
                }

            }
        };
        displayTask.runTaskTimer(plugin, 0, 10L);
    }

    private void stopVisualisation() {
        if (displayTask != null) {
            displayTask.cancel();
            displayTask = null;
        }
        Logger.log("Dungeon visualisation task stopped.");
    }

    private Set<Edge> getBoundingBoxEdges(World world, BoundingBox boundingBox) {

        Set<Edge> edges = new HashSet<>();

        double minX = boundingBox.getMinX();
        double minY = boundingBox.getMinY();
        double minZ = boundingBox.getMinZ();
        double maxX = boundingBox.getMaxX();
        double maxY = boundingBox.getMaxY();
        double maxZ = boundingBox.getMaxZ();

        // Top
        edges.add(new Edge(new Location(world, minX, minY, minZ), new Location(world, minX, minY, maxZ)));
        edges.add(new Edge(new Location(world, minX, minY, maxZ), new Location(world, maxX, minY, maxZ)));
        edges.add(new Edge(new Location(world, maxX, minY, maxZ), new Location(world, maxX, minY, minZ)));
        edges.add(new Edge(new Location(world, maxX, minY, minZ), new Location(world, minX, minY, minZ)));
        // Bottom
        edges.add(new Edge(new Location(world, minX, maxY, minZ), new Location(world, minX, maxY, maxZ)));
        edges.add(new Edge(new Location(world, minX, maxY, maxZ), new Location(world, maxX, maxY, maxZ)));
        edges.add(new Edge(new Location(world, maxX, maxY, maxZ), new Location(world, maxX, maxY, minZ)));
        edges.add(new Edge(new Location(world, maxX, maxY, minZ), new Location(world, minX, maxY, minZ)));
        // Verticals
        edges.add(new Edge(new Location(world, minX, minY, minZ), new Location(world, minX, maxY, minZ)));
        edges.add(new Edge(new Location(world, minX, minY, maxZ), new Location(world, minX, maxY, maxZ)));
        edges.add(new Edge(new Location(world, maxX, minY, maxZ), new Location(world, maxX, maxY, maxZ)));
        edges.add(new Edge(new Location(world, maxX, minY, minZ), new Location(world, maxX, maxY, minZ)));

        return edges;
    }

    private static class Edge {
        private Location start, end;

        Edge(Location start, Location end) {
            this.start = start;
            this.end = end;
        }
    }

    // #endregion

    // #region Sound effects

    public void playBossMusicForPlayer(Player player, Location location) {

        var sound = plugin.getConfig().getString("general.boss-music");
        var loopticks = plugin.getConfig().getLong("general.boss-music-loop-length");

        var task = new BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(location, sound, 10, 1);
            }
        };
        task.runTaskTimer(plugin, 0, loopticks);
        soundLoopTasks.put(player.getUniqueId(), task);
    }

    public void stopBossMusicForPlayer(Player player) {
        var sound = plugin.getConfig().getString("general.boss-music");
        var task = soundLoopTasks.get(player.getUniqueId());
        if (task != null) {
            player.stopSound(sound);
            task.cancel();
            soundLoopTasks.remove(player.getUniqueId());
        }
    }

    public void stopBossMusicForPlayers(Set<Player> players) {
        for (Player player : players) {
            stopBossMusicForPlayer(player);
        }
    }

    // #endregion
}
