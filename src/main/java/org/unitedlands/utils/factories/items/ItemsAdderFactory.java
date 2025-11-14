package org.unitedlands.utils.factories.items;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.RewardSet;
import org.unitedlands.utils.Logger;

import dev.lone.itemsadder.api.CustomStack;

public class ItemsAdderFactory extends BaseItemFactory {

    public ItemsAdderFactory(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public boolean isItem(ItemStack item1, ItemStack item2) {
        CustomStack customStack1 = CustomStack.byItemStack(item1);
        CustomStack customStack2 = CustomStack.byItemStack(item2);

        if (customStack1 != null) {

            Logger.log(customStack1.getNamespacedID());
            if (customStack2 == null) {
                return false;
            } else {
                Logger.log(customStack2.getNamespacedID());
                return customStack1.matchNamespacedID(customStack2);
            }
        } else {
            if (customStack2 != null) {
                Logger.log(customStack2.getNamespacedID());
                return false;
            } else {
                return item1.isSimilar(item2);
            }
        }
    }

    @Override
    public ItemStack getItemStack(String material, int minAmount, int maxAmount) {

        CustomStack customStack = CustomStack.getInstance(material);
        if (customStack != null) {
            var itemStack = customStack.getItemStack();
            itemStack.setAmount(ThreadLocalRandom.current().nextInt(minAmount, maxAmount + 1));
            return itemStack;
        } else {
            try {
                var mat = Material.getMaterial(material);
                if (mat != null) {
                    return new ItemStack(mat, ThreadLocalRandom.current().nextInt(minAmount, maxAmount + 1));
                }
            } catch (Exception ex) {
                Logger.logError("Vanilla material \"" + material + "\" not found");
            }
        }

        return null;
    }

    @Override
    public ItemStack getItemStack(RewardSet rewardSet) {
        return getItemStack(rewardSet.getItem(), rewardSet.getMinAmount(), rewardSet.getMaxAmount());
    }

}
