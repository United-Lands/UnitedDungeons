package org.unitedlands.utils.factories.items;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.RewardSet;
import org.unitedlands.utils.Logger;

public class VanillaItemFactory extends BaseItemFactory {

    public VanillaItemFactory(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public boolean isItem(ItemStack item1, ItemStack item2) {
        return item1.isSimilar(item2);
    }

    @Override
    public ItemStack getItemStack(String material, int minAmount, int maxAmount) {
        try {
            var mat = Material.getMaterial(material);
            if (mat != null) {
                return new ItemStack(mat, ThreadLocalRandom.current().nextInt(minAmount, maxAmount + 1));
            }
        } catch (Exception ex) {
            Logger.logError("Error creating item stack: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public ItemStack getItemStack(RewardSet rewardSet) {
        return getItemStack(rewardSet.getItem(), rewardSet.getMinAmount(), rewardSet.getMaxAmount());
    }

}
