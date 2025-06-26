package org.unitedlands.utils.factories.items;

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
    public ItemStack getItemStack(String material, int amount) {

        CustomStack customStack = CustomStack.getInstance(material);
        if (customStack != null) {
            var itemStack = customStack.getItemStack();
            itemStack.setAmount(amount);
            return itemStack;
        } else {
            try {
                var mat = Material.getMaterial(material);
                if (mat != null) {
                    return new ItemStack(mat, amount);
                }
            } catch (Exception ex) {
                Logger.logError("Vanilla material \"" + material + "\" not found");
            }
        }

        return null;
    }

    @Override
    public ItemStack getItemStack(RewardSet rewardSet) {
        return getItemStack(rewardSet.getItem(), rewardSet.getAmount());
    }

}
