package org.unitedlands.dungeons.classes;

public class LootSet {
    private String item;
    private int minAmount;
    private int maxAmount;
    private double chance;

    public LootSet(String item, int minAmount, int maxAmount, double chance) {
        this.item = item;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.chance = chance;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

}