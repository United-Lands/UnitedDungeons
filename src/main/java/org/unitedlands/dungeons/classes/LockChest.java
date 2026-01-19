package org.unitedlands.dungeons.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.unitedlands.UnitedLib;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.utils.annotations.Info;
import org.unitedlands.utils.Logger;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.gson.annotations.Expose;

public class LockChest {

    @Expose
    public UUID uuid;
    @Expose
    private Location location;

    @Expose
    @Info
    private String requiredItems;

    @Expose
    @Info
    private String facing;

    @Expose
    @Info
    private boolean complete;

    public LockChest() {

    }

    public LockChest(Location location) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LootChest c = (LootChest) o;
        return Objects.equals(uuid, c.getUuid());
    }

    public String getRequiredItems() {
        return requiredItems;
    }

    public List<ItemStack> getRequiredItemsParsed() {
        var result = new ArrayList<ItemStack>();
        if (requiredItems != null && !requiredItems.isEmpty()) {
            var itemSets = requiredItems.split(";");
            for (var itemSet : itemSets) {
                var itemAmountSplit = itemSet.split("#");
                if (itemAmountSplit.length == 2) {
                    try {
                        var amount = Integer.parseInt(itemAmountSplit[1]);
                        var itemStack = UnitedLib.getInstance().getItemFactory().getItemStack(itemAmountSplit[0], amount, amount);
                        result.add(itemStack);
                    } catch (Exception ex) {
                        Logger.logError(
                                "Could not parse required item " + itemAmountSplit[0] + " in lock chest " + location);
                    }
                }
            }
        }
        return result;
    }

    public void setRequiredItems(String requiredItems) {
        this.requiredItems = requiredItems;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getFacing() {
        return facing;
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    public void checkCompletion() {
        if (complete)
            return;

        var block = location.getBlock();
        if (block.getType() != Material.CHEST)
            return;

        if (block.getState() instanceof Chest chest) {
            Inventory inventory = chest.getBlockInventory();
            List<ItemStack> chestItems = Arrays.stream(inventory.getContents())
                    .filter(item -> item != null && item.getType().isItem())
                    .toList();

            var expected = new ArrayList<>(getRequiredItemsParsed());

            if (chestItems.size() != expected.size()) {
                return;
            }

            List<ItemStack> remaining = new ArrayList<>(chestItems);
            for (ItemStack expectedItem : expected) {
                boolean matched = remaining.removeIf(
                        chestItem -> UnitedLib.getInstance().getItemFactory().isItem(chestItem, expectedItem)
                                && chestItem.getAmount() == expectedItem.getAmount());
                if (!matched)
                    return;
            }

            complete = true;
            location.getBlock().setType(Material.AIR);
            Bukkit.getScheduler().runTaskLater(UnitedDungeons.getInstance(), () -> {
                location.getWorld().playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
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
