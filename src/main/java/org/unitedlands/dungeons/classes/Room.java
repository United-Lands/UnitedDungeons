package org.unitedlands.dungeons.classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Wall;
import org.bukkit.block.data.type.Wall.Height;
import org.bukkit.util.BoundingBox;
import org.unitedlands.UnitedLib;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.utils.annotations.Info;
import org.unitedlands.utils.Logger;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.gson.annotations.Expose;

import net.kyori.adventure.text.Component;

public class Room {

    @Expose
    private UUID uuid;

    private Dungeon dungeon;

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
    @Expose
    @Info
    private boolean showTitle = false;
    @Expose
    @Info
    private boolean useBossMusic = false;

    @Expose
    @Info
    private String needsOtherRoomCompleted;

    private boolean isComplete;

    @Expose
    private Set<Spawner> spawners = new HashSet<>();
    @Expose
    private Set<LootChest> lootChests = new HashSet<>();
    @Expose
    private Set<LockChest> lockChests = new HashSet<>();
    @Expose
    private Set<SupplyChest> supplyChests = new HashSet<>();
    @Expose
    private Set<Barrier> barriers = new HashSet<>();

    private Set<Player> playersInRoom = new HashSet<>();

    public Room() {

    }

    public Room(Location center, int width, int length, int height) {

        width = Math.max(width, 2);
        length = Math.max(length, 2);
        height = Math.max(height, 2);

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

        spawnLootChests();
        despawnLockChests();
        despawnBarriers(false, false);

        if (this.useBossMusic) {
            UnitedDungeons.getInstance().getEffectsManager().stopBossMusicForPlayers(playersInRoom);
        }

        this.isComplete = true;
    }

    public void reset() {

        spawnSupplyChests();
        despawnLootChests();
        despawnBarriers(true, true);

        for (LockChest lockChest : lockChests)
            lockChest.setComplete(false);
        spawnLockChests();

        spawnBarriers();

        for (Spawner spawner : spawners)
            spawner.resetCompletion();

        if (this.useBossMusic) {
            UnitedDungeons.getInstance().getEffectsManager().stopBossMusicForPlayers(playersInRoom);
        }

        this.isComplete = false;
    }

    public void edit() {

        despawnSupplyChests();
        despawnLootChests();
        despawnLockChests();
        despawnBarriers(true, true);

        if (this.useBossMusic) {
            UnitedDungeons.getInstance().getEffectsManager().stopBossMusicForPlayers(playersInRoom);
        }

        this.isComplete = false;
    }

    // #endregion

    // #region Object spawning and despawning

    private void spawnSupplyChests() {
        if (supplyChests != null && !supplyChests.isEmpty()) {

            for (SupplyChest supplyChest : supplyChests) {

                String facing = supplyChest.getFacing();
                if (facing == null || facing.isEmpty())
                    facing = "NORTH";

                var block = supplyChest.getLocation().getBlock();
                block.setType(Material.CHEST);

                try {
                    BlockFace face = BlockFace.valueOf(facing);
                    var directional = (Directional) block.getBlockData();
                    directional.setFacing(face);
                    block.setBlockData(directional);
                } catch (Exception ex) {
                    Logger.logError("Wrong facing data on loot chest: " + facing);
                }

                if (supplyChest.getItems() != null) {
                    Logger.log(supplyChest.getItems());
                    var itemsTable = parseLoot(supplyChest.getItems());
                    if (itemsTable != null && !itemsTable.isEmpty()) {
                        BlockState state = block.getState();
                        if (state instanceof Chest chest) {
                            Inventory chestInventory = chest.getInventory();
                            chestInventory.clear();
                            Logger.log("Adding items...");
                            for (var item : itemsTable) {
                                addLootToInventory(chestInventory, item);
                            }
                        } else {
                            Logger.log("No chest found.");
                        }
                    }
                }
            }
        }
    }

    private void spawnLootChests() {
        if (lootChests != null && !lootChests.isEmpty()) {

            for (LootChest lootChest : lootChests) {

                String materialName = lootChest.getMaterial();
                if (materialName == null || materialName.isEmpty())
                    materialName = "YELLOW_SHULKER_BOX";
                String facing = lootChest.getFacing();
                if (facing == null || facing.isEmpty())
                    facing = "NORTH";

                Material material = Material.CHEST;
                try {
                    material = Material.valueOf(materialName);
                } catch (Exception ignore) {
                    Logger.logError("Loot chest material not found: " + materialName, "UnitedDungeons");
                }

                var block = lootChest.getLocation().getBlock();
                block.setType(material);

                try {
                    BlockFace face = BlockFace.valueOf(facing);
                    var directional = (Directional) block.getBlockData();
                    directional.setFacing(face);
                    block.setBlockData(directional);
                } catch (Exception ex) {
                    Logger.logError("Wrong facing data on loot chest: " + facing);
                }

                List<UUID> playerUUIDs = new ArrayList<>();

                if (dungeon != null && dungeon.isLocked() && !dungeon.getLockedPlayersInDungeon().isEmpty()) {
                    playerUUIDs = dungeon.getLockedPlayersInDungeon().stream().map(Player::getUniqueId)
                            .collect(Collectors.toList());
                } else {
                    playerUUIDs = playersInRoom.stream().map(Player::getUniqueId).collect(Collectors.toList());
                }

                if (playerUUIDs.size() == 0)
                    continue;

                for (var uuid : playerUUIDs) {
                    Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST,
                            Component.text("Dungeon Loot"));
                    lootChest.addInventory(uuid, inv);
                }

                if (lootChest.getLoot() != null) {
                    var lootTable = parseLoot(lootChest.getLoot());
                    if (lootTable != null && !lootTable.isEmpty()) {
                        for (var uuid : playerUUIDs) {
                            for (var loot : lootTable) {
                                addLootToInventory(lootChest.getInventory(uuid), loot);
                            }
                        }
                    }
                }

                if (lootChest.getRandomLoot() != null && lootChest.getRandomLootCount() > 0) {
                    var randomLootTable = parseLoot(lootChest.getRandomLoot());
                    if (lootChest.randomLootPerPlayer()) {
                        for (var uuid : playerUUIDs) {
                            for (int i = 0; i < lootChest.getRandomLootCount(); i++) {
                                var loot = getRandomLoot(randomLootTable);
                                if (loot != null) {
                                    addLootToInventory(lootChest.getInventory(uuid), loot);
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i < lootChest.getRandomLootCount(); i++) {
                            var loot = getRandomLoot(randomLootTable);
                            if (loot != null) {
                                int rnd = ThreadLocalRandom.current().nextInt(playerUUIDs.size());
                                UUID rndUUID = playerUUIDs.get(rnd);
                                addLootToInventory(lootChest.getInventory(rndUUID), loot);
                            }
                        }
                    }
                }

                Bukkit.getScheduler().runTaskLater(UnitedDungeons.getInstance(), () -> {

                    lootChest.getLocation().getWorld().playSound(lootChest.getLocation(), Sound.BLOCK_GRAVEL_HIT, 1, 1);
                    new ParticleBuilder(Particle.WAX_OFF)
                            .location(lootChest.getLocation())
                            .offset(0.6, 0.6, 0.6)
                            .receivers(64)
                            .count(24)
                            .spawn();

                }, 1L);
            }
        }
    }

    private void despawnSupplyChests() {
        if (supplyChests != null && !supplyChests.isEmpty()) {
            for (SupplyChest chest : supplyChests) {
                Block chestBlock = chest.getLocation().getBlock();
                chestBlock.setType(Material.AIR);
            }
        }
    }

    private void despawnLootChests() {
        if (lootChests != null && !lootChests.isEmpty()) {

            for (LootChest chest : lootChests) {
                chest.clearInventories();
                Block chestBlock = chest.getLocation().getBlock();
                chestBlock.setType(Material.AIR);
            }
        }
    }

    private void spawnLockChests() {
        for (var lockChest : lockChests) {
            var block = lockChest.getLocation().getBlock();
            block.setType(Material.CHEST, false);
            if (lockChest.getFacing() != null && !lockChest.getFacing().isEmpty()) {
                try {
                    BlockFace face = BlockFace.valueOf(lockChest.getFacing());
                    var directional = (Directional) block.getBlockData();
                    directional.setFacing(face);
                    block.setBlockData(directional);
                } catch (Exception ex) {
                    Logger.logError("Wrong facing data on lock chest " + lockChest.getUuid());
                }
            }
        }
    }

    private void despawnLockChests() {
        for (var lockChest : lockChests) {
            lockChest.getLocation().getBlock().setType(Material.AIR, false);
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
                            if (barrier.getFacing() != null && !barrier.getFacing().isEmpty()) {
                                try {
                                    BlockFace face = BlockFace.valueOf(barrier.getFacing());
                                    var directional = (Directional) block.getBlockData();
                                    directional.setFacing(face);
                                    block.setBlockData(directional);
                                } catch (Exception ex) {
                                    Logger.logError("Wrong facing data on barrier " + barrier.getUuid());
                                }
                            }

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
                        block.setType(Material.AIR);
                    } else {
                        block.setType(barrierMaterial, true);
                        if (barrier.getFacing() != null && !barrier.getFacing().isEmpty()) {
                            try {
                                BlockFace face = BlockFace.valueOf(barrier.getFacing());
                                var directional = (Directional) block.getBlockData();
                                directional.setFacing(face);
                                block.setBlockData(directional);
                            } catch (Exception ex) {
                                Logger.logError("Wrong facing data on barrier " + barrier.getUuid());
                            }
                        }
                        // Update the connections one tick later
                        Bukkit.getScheduler().runTaskLater(UnitedDungeons.getInstance(), () -> {
                            updateConnections(block);
                        }, 1);
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

    public void addLootChest(LootChest chest) {
        if (lootChests == null)
            lootChests = new HashSet<>();
        if (!lootChests.contains(chest))
            lootChests.add(chest);
    }

    public void removeLootChest(LootChest chest) {
        if (lootChests == null)
            return;
        if (lootChests.contains(chest))
            lootChests.remove(chest);
    }

    public void addLockChest(LockChest chest) {
        if (lockChests == null)
            lockChests = new HashSet<>();
        if (!lockChests.contains(chest))
            lockChests.add(chest);
    }

    public void removeLockChest(LockChest chest) {
        if (lockChests == null)
            return;
        if (lockChests.contains(chest))
            lockChests.remove(chest);
    }

    public void addSupplyChest(SupplyChest chest) {
        if (supplyChests == null)
            supplyChests = new HashSet<>();
        if (!supplyChests.contains(chest))
            supplyChests.add(chest);
    }

    public void removeSupplyChest(SupplyChest chest) {
        if (supplyChests == null)
            return;
        if (supplyChests.contains(chest))
            supplyChests.remove(chest);
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

    public Dungeon getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
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

    public Set<LootChest> getLootChests() {
        return lootChests;
    }

    public Set<LockChest> getLockChests() {
        return lockChests;
    }

    public Set<Barrier> getBarriers() {
        return barriers;
    }

    public Set<SupplyChest> getSupplyChests() {
        return supplyChests;
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

    public boolean showTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public boolean useBossMusic() {
        return useBossMusic;
    }

    public void setUseBossMusic(boolean useBossMusic) {
        this.useBossMusic = useBossMusic;
    }

    public String getNeedsOtherRoomCompleted() {
        return needsOtherRoomCompleted;
    }

    public void setNeedsOtherRoomCompleted(String needsOtherRoomCompleted) {
        this.needsOtherRoomCompleted = needsOtherRoomCompleted;
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

    private void addLootToInventory(Inventory inv, LootSet lootSet) {
        var itemStack = UnitedLib.getInstance().getItemFactory().getItemStack(lootSet.getItem(), lootSet.getMinAmount(),
                lootSet.getMaxAmount());
        if (itemStack != null) {

            int splits = 1;
            int amount = itemStack.getAmount();
            if (itemStack.getMaxStackSize() == 1) {
                splits = itemStack.getAmount();
            } else {
                int maxSplits = Math.min(itemStack.getAmount(), 3);
                splits = 1 + new Random().nextInt(maxSplits);
            }

            int[] splitAmounts = new int[splits];
            int remaining = amount;
            for (int i = 0; i < splits - 1; i++) {
                int max = remaining - (splits - i - 1);
                int split = 1 + (max > 1 ? new Random().nextInt(max - 1) : 0);
                splitAmounts[i] = split;
                remaining -= split;
            }
            splitAmounts[splits - 1] = remaining;

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
            Logger.logError("Could not generate ItemStack " + lootSet.getItem() + ":" + lootSet.getMinAmount() + "-"
                    + lootSet.getMaxAmount()
                    + " for chest in room " + this.uuid);
        }
    }

    @SuppressWarnings("unused")
    private void addLootToShulker(ShulkerBox shulker, LootSet lootSet) {
        var itemStack = UnitedLib.getInstance().getItemFactory().getItemStack(lootSet.getItem(), lootSet.getMinAmount(),
                lootSet.getMaxAmount());
        if (itemStack != null) {

            int splits = 1;
            int amount = itemStack.getAmount();
            if (itemStack.getMaxStackSize() == 1) {
                splits = itemStack.getAmount();
            } else {
                int maxSplits = Math.min(itemStack.getAmount(), 3);
                splits = 1 + new Random().nextInt(maxSplits);
            }

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
            Logger.logError("Could not generate ItemStack " + lootSet.getItem() + ":" + lootSet.getMinAmount() + "-"
                    + lootSet.getMaxAmount()
                    + " for chest in room " + this.uuid);
        }
    }

    private List<LootSet> parseLoot(String lootString) {
        var result = new ArrayList<LootSet>();
        if (lootString != null && !lootString.isEmpty()) {
            var lootSets = lootString.split(";");
            for (var lootSet : lootSets) {
                var lootAmountSplits = lootSet.split("#");
                if (lootAmountSplits.length == 2) {

                    String amountStr = lootAmountSplits[1];
                    int minAmount, maxAmount;

                    var amountSplit = amountStr.split("-");
                    if (amountSplit.length == 2) {
                        minAmount = Integer.parseInt(amountSplit[0]);
                        maxAmount = Integer.parseInt(amountSplit[1]);
                    } else {
                        minAmount = Integer.parseInt(amountSplit[0]);
                        maxAmount = minAmount;
                    }

                    var lootPercentSplits = lootAmountSplits[0].split("%");
                    if (lootPercentSplits.length == 1) {
                        result.add(new LootSet(lootAmountSplits[0],
                                minAmount, maxAmount, 1.0));
                    } else if (lootPercentSplits.length == 2) {
                        result.add(new LootSet(lootPercentSplits[1], minAmount, maxAmount,
                                Double.parseDouble(lootPercentSplits[0]) / 100));
                    }
                }
            }
        }

        return result;
    }

    private LootSet getRandomLoot(List<LootSet> lootTable) {
        double totalChance = 0.0;
        for (LootSet lootSet : lootTable) {
            totalChance += lootSet.getChance();
        }

        // Normalize if totalChance > 1.0
        if (totalChance > 1.0) {
            for (LootSet lootSet : lootTable) {
                lootSet.setChance(lootSet.getChance() / totalChance);
            }
            totalChance = 1.0;
        }

        double random = Math.random(); // [0.0, 1.0)
        double cumulative = 0.0;

        for (LootSet lootSet : lootTable) {
            cumulative += lootSet.getChance();
            if (random < cumulative) {
                return lootSet;
            }
        }

        // Remaining chance goes to "no loot"
        return null;
    }
    // #endregion

}
