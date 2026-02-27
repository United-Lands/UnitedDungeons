package org.unitedlands.dungeons.classes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.unitedlands.dungeons.utils.annotations.Info;

import com.google.gson.annotations.Expose;

public class LootChest {
    @Expose
    public UUID uuid;
    @Expose
    private Location location;

    @Expose
    @Info
    private String loot;
    @Expose
    @Info
    private String randomLoot;
    @Expose
    @Info
    private int randomLootCount = 0;
    @Expose
    @Info
    private boolean randomLootPerPlayer = false;
    @Expose
    @Info
    private String material;
    @Expose
    @Info
    private String facing;

    private Map<UUID, Inventory> inventories;

    public LootChest() {

    }

    public LootChest(Location location) {
        this.uuid = UUID.randomUUID();
        setLocation(location);
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
        this.location = location.getBlock().getLocation().add(0.5, 0.5, 0.5);
    }

    public String getLoot() {
        return loot;
    }

    public void setLoot(String loot) {
        this.loot = loot;
    }

    public String getRandomLoot() {
        return randomLoot;
    }

    public void setRandomLoot(String randomLoot) {
        this.randomLoot = randomLoot;
    }

    public int getRandomLootCount() {
        return randomLootCount;
    }

    public void setRandomLootCount(int randomLootCount) {
        this.randomLootCount = randomLootCount;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String block) {
        this.material = block;
    }

    public String getFacing() {
        return facing;
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    public boolean randomLootPerPlayer() {
        return randomLootPerPlayer;
    }

    public void setRandomLootPerPlayer(boolean randomLootPerPlayer) {
        this.randomLootPerPlayer = randomLootPerPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LootChest c = (LootChest) o;
        return Objects.equals(uuid, c.getUuid());
    }

    public Inventory getInventory(UUID uuid) {
        if (inventories == null)
            return null;
        return inventories.get(uuid);
    }

    public void addInventory(UUID uuid, Inventory inventory) {
        if (inventories == null)
            inventories = new HashMap<>();
        inventories.put(uuid, inventory);
    }

    public void removeInventory(UUID uuid) {
        if (inventories == null)
            return;
        inventories.remove(uuid);
    }

    public void clearInventories() {
        inventories = new HashMap<>();
    }

}
