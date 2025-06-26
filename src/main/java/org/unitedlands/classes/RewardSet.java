package org.unitedlands.classes;

public class RewardSet {
    private String item;
    private int amount;
    private double chance;

    public RewardSet(String item, int amount, double chance) {
        this.item = item;
        this.amount = amount;
        this.chance = chance;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

}