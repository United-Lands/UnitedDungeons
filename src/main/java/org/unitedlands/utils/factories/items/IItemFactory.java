package org.unitedlands.utils.factories.items;

import org.bukkit.inventory.ItemStack;
import org.unitedlands.classes.RewardSet;

public interface IItemFactory {
    ItemStack getItemStack(String material, int minAmount, int maxAmount);
    ItemStack getItemStack(RewardSet rewardSet);
}
