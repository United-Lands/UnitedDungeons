package org.unitedlands.utils.factories.items;

import org.bukkit.inventory.ItemStack;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.RewardSet;

public abstract class BaseItemFactory implements IItemFactory {

    @SuppressWarnings("unused")
    private final UnitedDungeons plugin;

    public BaseItemFactory(UnitedDungeons plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public abstract ItemStack getItemStack(String material, int minAmount, int maxAmount);
    @Override
    public abstract ItemStack getItemStack(RewardSet rewardSet);
}
