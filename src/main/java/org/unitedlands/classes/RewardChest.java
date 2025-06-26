package org.unitedlands.classes;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Location;
import org.unitedlands.utils.annotations.Info;

import com.google.gson.annotations.Expose;

public class RewardChest {
    @Expose
    public UUID uuid;
    @Expose
    private Location location;

    @Expose
    @Info
    private String rewards;
    @Expose
    @Info
    private String randomRewards;
    @Expose
    @Info
    private int randomRewardCount = 0;

    public RewardChest() {

    }

    public RewardChest(Location location) {
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

    public String getRewards() {
        return rewards;
    }

    public void setReward(String rewards) {
        this.rewards = rewards;
    }

    public String getRandomRewards() {
        return randomRewards;
    }

    public void setRandomRewards(String randomRewards) {
        this.randomRewards = randomRewards;
    }

    public int getRandomRewardCount() {
        return randomRewardCount;
    }

    public void setRandomRewardCount(int randomRewardCount) {
        this.randomRewardCount = randomRewardCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RewardChest c = (RewardChest) o;
        return Objects.equals(uuid, c.getUuid());
    }

}
