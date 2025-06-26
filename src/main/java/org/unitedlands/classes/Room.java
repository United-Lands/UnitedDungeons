package org.unitedlands.classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Wall;
import org.bukkit.block.data.type.Wall.Height;
import org.bukkit.util.BoundingBox;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.annotations.Info;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.gson.annotations.Expose;

public class Room {

    @Expose
    private UUID uuid;
    @Expose
    private BoundingBox boundingBox;
    @Expose
    @Info
    private String name;
    @Expose
    private String description;
    @Expose
    @Info
    private boolean enableLocking = false;
    @Expose
    @Info
    private boolean mustBeCompleted = true;

    private boolean isComplete;

    @Expose
    private Set<Spawner> spawners = new HashSet<>();
    @Expose
    private Set<RewardChest> chests = new HashSet<>();
    @Expose
    private Set<Barrier> barriers = new HashSet<>();

    private Set<Player> playersInRoom = new HashSet<>();

    public Room() {

    }

    public Room(Location center, int width, int length, int height) {

        var maxLength = UnitedDungeons.getInstance().getConfig().getInt("general.max-room-edge-lenth", 0);
        width = Math.min(width, maxLength);
        length = Math.min(length, maxLength);
        height = Math.min(height, maxLength);

        width = Math.max(width, 2);
        length = Math.max(length, 2);
        height = Math.max(height, 2);

        this.uuid = UUID.randomUUID();

        double halfWidth = (double) width / 2;
        double halfLength = (double) length / 2;
        double halfHeight = (double) height / 2;

        var centerX = Math.floor(center.getX());
        var centerY = Math.floor(center.getY());
        var centerZ = Math.floor(center.getZ());

        if (width % 2 == 1)
            centerX += 0.5;
        if (height % 2 == 1)
            centerY += 0.5;
        if (length % 2 == 1)
            centerZ += 0.5;

        var x1 = centerX - halfWidth;
        var y1 = centerY - halfHeight;
        var z1 = centerZ - halfLength;
        var x2 = centerX + halfWidth;
        var y2 = centerY + halfHeight;
        var z2 = centerZ + halfLength;

        var box = new BoundingBox(x1, y1, z1, x2, y2, z2);

        setBoundingBox(box);
    }

    // #region State changes

    public void complete() {

        spawnRewardChests();
        despawnBarriers(false, false);

        this.isComplete = true;
    }

    public void reset() {

        despawnRewardChests();
        despawnBarriers(true, true);
        spawnBarriers();

        for (Spawner spawner : spawners)
            spawner.resetCompletion();

        this.isComplete = false;
    }

    public void edit() {

        despawnRewardChests();
        despawnBarriers(true, true);

        this.isComplete = false;
    }

    // #endregion

    // #region Object spawning and despawning

    private void spawnRewardChests() {
        if (chests != null && !chests.isEmpty()) {

            for (RewardChest chest : chests) {

                Block chestBlock = chest.getLocation().getBlock();
                chestBlock.setType(Material.YELLOW_SHULKER_BOX);

                Bukkit.getScheduler().runTaskLater(UnitedDungeons.getInstance(), () -> {
                    BlockState state = chestBlock.getState();
                    if (state instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) state;

                        if (chest.getRewards() != null) {
                            var rewards = parseRewards(chest.getRewards());
                            if (rewards != null && !rewards.isEmpty()) {
                                for (var reward : rewards) {
                                    addRewardToShulker(shulker, reward);
                                }
                            }
                        }

                        if (chest.getRandomRewards() != null && chest.getRandomRewardCount() > 0) {
                            var randomRewards = parseRewards(chest.getRandomRewards());
                            for (int i = 0; i < chest.getRandomRewardCount(); i++) {
                                var reward = getRandomReward(randomRewards);
                                if (reward != null) {
                                    addRewardToShulker(shulker, reward);
                                }
                            }

                        }

                        chest.getLocation().getWorld().playSound(chest.getLocation(), Sound.BLOCK_GRAVEL_HIT, 1, 1);

                    }

                    new ParticleBuilder(Particle.WAX_OFF)
                            .location(chest.getLocation())
                            .offset(0.6, 0.6, 0.6)
                            .receivers(64)
                            .count(24)
                            .spawn();

                }, 1L);
            }
        }
    }

    private void despawnRewardChests() {
        if (chests != null && !chests.isEmpty()) {

            for (RewardChest chest : chests) {

                Block chestBlock = chest.getLocation().getBlock();
                chestBlock.setType(Material.AIR);
            }
        }
    }

    private void spawnBarriers() {
        if (barriers != null && !barriers.isEmpty()) {

            for (Barrier barrier : barriers) {

                var rnd = new Random();
                Double triggerChance = barrier.getTriggerChance() / 100;
                if (rnd.nextDouble() > triggerChance)
                    continue;

                Material barrierMaterial = null;
                try {
                    barrierMaterial = Material.valueOf(barrier.getMaterial());
                } catch (Exception ex) {
                    continue;
                }
                for (int i = 0; i < barrier.getHeight(); i++) {
                    var loc = barrier.getLocation().clone().add(0, i, 0);
                    var block = loc.getBlock();
                    if (!barrier.isInverse()) {
                        if (block.getType() == Material.AIR) {
                            block.setType(barrierMaterial, true);
                            // Update the connections one tick later
                            Bukkit.getScheduler().runTaskLater(UnitedDungeons.getInstance(), () -> {
                                updateConnections(block);
                            }, 1);
                        }
                    } else {
                        if (block.getType() == barrierMaterial) {
                            block.setType(Material.AIR);
                        }
                    }
                }

                barrier.getLocation().getWorld().playSound(barrier.getLocation(), Sound.BLOCK_GRAVEL_HIT, 1, 1);

            }
        }
    }

    private void despawnBarriers(boolean ignoreInverse, boolean ignoreTrigger) {
        if (barriers != null && !barriers.isEmpty()) {

            for (Barrier barrier : barriers) {

                if (!ignoreTrigger) {
                    var rnd = new Random();
                    Double triggerChance = barrier.getTriggerChance() / 100;
                    if (rnd.nextDouble() > triggerChance)
                        continue;
                }

                Material barrierMaterial = null;
                try {
                    barrierMaterial = Material.valueOf(barrier.getMaterial());
                } catch (Exception ex) {
                    continue;
                }
                for (int i = 0; i < barrier.getHeight(); i++) {
                    var loc = barrier.getLocation().clone().add(0, i, 0);
                    var block = loc.getBlock();
                    if (!barrier.isInverse() || ignoreInverse) {
                        if (block.getType() == barrierMaterial) {
                            block.setType(Material.AIR);
                        }
                    } else {
                        if (block.getType() == Material.AIR) {
                            block.setType(barrierMaterial, true);
                            // Update the connections one tick later
                            Bukkit.getScheduler().runTaskLater(UnitedDungeons.getInstance(), () -> {
                                updateConnections(block);
                            }, 1);
                        }
                    }
                }

                barrier.getLocation().getWorld().playSound(barrier.getLocation(), Sound.BLOCK_GRAVEL_HIT, 1, 1);

            }

        }
    }

    private void updateConnections(Block block) {
        var data = block.getBlockData();
        if (data instanceof Wall) {
            var wall = (Wall) data;
            if (block.getRelative(BlockFace.EAST).getType() != Material.AIR)
                wall.setHeight(BlockFace.EAST, Height.TALL);
            if (block.getRelative(BlockFace.WEST).getType() != Material.AIR)
                wall.setHeight(BlockFace.WEST, Height.TALL);
            if (block.getRelative(BlockFace.NORTH).getType() != Material.AIR)
                wall.setHeight(BlockFace.NORTH, Height.TALL);
            if (block.getRelative(BlockFace.SOUTH).getType() != Material.AIR)
                wall.setHeight(BlockFace.SOUTH, Height.TALL);
            if (block.getRelative(BlockFace.UP).getType() != Material.AIR)
                wall.setUp(true);
            block.setBlockData(wall, true);
        } else if (data instanceof Fence) {
            Logger.log("Fence");
            var fence = (Fence) data;
            if (block.getRelative(BlockFace.EAST).getType() != Material.AIR)
                fence.setFace(BlockFace.EAST, true);
            if (block.getRelative(BlockFace.WEST).getType() != Material.AIR)
                fence.setFace(BlockFace.WEST, true);
            if (block.getRelative(BlockFace.NORTH).getType() != Material.AIR)
                fence.setFace(BlockFace.NORTH, true);
            if (block.getRelative(BlockFace.SOUTH).getType() != Material.AIR)
                fence.setFace(BlockFace.SOUTH, true);
            block.setBlockData(fence, true);
        }

    }

    // #endregion

    // #region Room Objects

    public void addSpawner(Spawner spawner) {
        if (spawners == null)
            spawners = new HashSet<>();
        if (!spawners.contains(spawner))
            spawners.add(spawner);
    }

    public void removeSpawner(Spawner spawner) {
        if (spawners == null)
            return;
        if (spawners.contains(spawner))
            spawners.remove(spawner);
    }

    public void addChest(RewardChest chest) {
        if (chests == null)
            chests = new HashSet<>();
        if (!chests.contains(chest))
            chests.add(chest);
    }

    public void removeChest(RewardChest chest) {
        if (chests == null)
            return;
        if (chests.contains(chest))
            chests.remove(chest);
    }

    public void addBarrier(Barrier barrier) {
        if (barriers == null)
            barriers = new HashSet<>();
        if (!barriers.contains(barrier))
            barriers.add(barrier);
    }

    public void removeBarrier(Barrier barrier) {
        if (barriers == null)
            return;
        if (barriers.contains(barrier))
            barriers.remove(barrier);
    }

    // #endregion

    // #region Getters and Setters

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public String getName() {
        return name;
    }

    public String getCleanName() {
        if (name != null)
            return name.replace("_", " ");
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Spawner> getSpawners() {
        return spawners;
    }

    public Set<RewardChest> getChests() {
        return chests;
    }

    public Set<Barrier> getBarriers() {
        return barriers;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public Set<Player> getPlayersInRoom() {
        if (playersInRoom == null)
            playersInRoom = new HashSet<>();
        return playersInRoom;
    }

    public void setPlayersInRoom(Set<Player> playersInRoom) {
        this.playersInRoom = playersInRoom;
    }

    public boolean enableLocking() {
        return enableLocking;
    }

    public void setEnableLocking(boolean enableLocking) {
        this.enableLocking = enableLocking;
    }

    public boolean mustBeCompleted() {
        return mustBeCompleted;
    }

    public void setMustBeCompleted(boolean mustBeCompleted) {
        this.mustBeCompleted = mustBeCompleted;
    }

    // #endregion

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Room r = (Room) o;
        return Objects.equals(uuid, r.getUuid());
    }

    // #region Helper methods

    private void addRewardToShulker(ShulkerBox shulker, RewardSet rewardSet) {
        var itemStack = UnitedDungeons.getInstance().getItemFactory().getItemStack(rewardSet);
        if (itemStack != null) {
            int maxSplits = 3;
            int amount = itemStack.getAmount();
            int splits = 1 + new Random().nextInt(maxSplits);

            int[] splitAmounts = new int[splits];
            int remaining = amount;
            for (int i = 0; i < splits - 1; i++) {
                int max = remaining - (splits - i - 1);
                int split = 1 + (max > 1 ? new Random().nextInt(max - 1) : 0);
                splitAmounts[i] = split;
                remaining -= split;
            }
            splitAmounts[splits - 1] = remaining;

            var inv = shulker.getInventory();
            List<Integer> emptySlots = new ArrayList<>();
            for (int i = 0; i < inv.getSize(); i++) {
                if (inv.getItem(i) == null)
                    emptySlots.add(i);
            }
            Random rand = new Random();

            for (int splitAmount : splitAmounts) {
                if (emptySlots.isEmpty())
                    break;
                int slotIndex = emptySlots.remove(rand.nextInt(emptySlots.size()));
                var subStack = itemStack.clone();
                subStack.setAmount(splitAmount);
                inv.setItem(slotIndex, subStack);
            }
        } else {
            Logger.logError("Could not generate ItemStack " + rewardSet.getItem() + ":" + rewardSet.getAmount()
                    + " for chest in room " + this.uuid);
        }
    }

    private List<RewardSet> parseRewards(String rewards) {
        var result = new ArrayList<RewardSet>();
        if (rewards != null && !rewards.isEmpty()) {
            var rewardSets = rewards.split(";");
            for (var rewardSet : rewardSets) {
                var rewardAmountSplits = rewardSet.split("#");
                if (rewardAmountSplits.length == 2) {
                    var rewardPercentSplits = rewardAmountSplits[0].split("%");
                    if (rewardPercentSplits.length == 1) {
                        result.add(new RewardSet(rewardAmountSplits[0],
                                Integer.parseInt(rewardAmountSplits[1]), 1.0));
                    } else if (rewardPercentSplits.length == 2) {
                        result.add(new RewardSet(rewardPercentSplits[1],
                                Integer.parseInt(rewardAmountSplits[1]),
                                Double.parseDouble(rewardPercentSplits[0]) / 100));
                    }
                }
            }
        }

        return result;
    }

    private RewardSet getRandomReward(List<RewardSet> rewards) {
        double totalChance = 0.0;
        for (RewardSet reward : rewards) {
            totalChance += reward.getChance();
        }

        // Normalize if totalChance > 1.0
        if (totalChance > 1.0) {
            for (RewardSet reward : rewards) {
                reward.setChance(reward.getChance() / totalChance);
            }
            totalChance = 1.0;
        }

        double random = Math.random(); // [0.0, 1.0)
        double cumulative = 0.0;

        for (RewardSet reward : rewards) {
            cumulative += reward.getChance();
            if (random < cumulative) {
                return reward;
            }
        }

        // Remaining chance goes to "no reward"
        return null;
    }
    // #endregion

}
