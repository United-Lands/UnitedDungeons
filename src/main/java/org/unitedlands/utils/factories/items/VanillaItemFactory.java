package org.unitedlands.utils.factories.items;

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
    public ItemStack getItemStack(String material, int amount) {
        try {
            var mat = Material.getMaterial(material);
            if (mat != null) {
                return new ItemStack(mat, amount);
            }
        } catch (Exception ex) {
            Logger.logError("Error creating item stack: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public ItemStack getItemStack(RewardSet rewardSet) {
        return getItemStack(rewardSet.getItem(), rewardSet.getAmount());
    }

}
