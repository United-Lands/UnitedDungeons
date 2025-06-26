package org.unitedlands.classes;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Location;
import org.unitedlands.utils.annotations.Info;

import com.google.gson.annotations.Expose;

public class Barrier {
    @Expose
    public UUID uuid;
    @Expose
    private Location location;

    @Expose
    @Info
    private String material;
    @Expose
    @Info
    private Integer height;
    @Expose
    @Info
    private Integer maxHeight;
    @Expose
    @Info
    private Boolean inverse = false;
    @Expose
    @Info
    private Double triggerChance = 100d;

    public Barrier() {

    }

    public Barrier(Location location, Integer maxHeight) {
        this.uuid = UUID.randomUUID();
        this.height = maxHeight;
        this.maxHeight = maxHeight;
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

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        if (height < 1)
            height = 1;
        if (height > maxHeight)
            height = maxHeight;
        this.height = height;
    }

    public Integer getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Boolean isInverse() {
        return inverse;
    }

    public void setInverse(Boolean inverse) {
        this.inverse = inverse;
    }

    public Double getTriggerChance() {
        return triggerChance;
    }

    public void setTriggerChance(Double triggerChance) {
        this.triggerChance = triggerChance;
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
