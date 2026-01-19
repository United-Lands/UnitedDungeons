package org.unitedlands.dungeons.classes;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Location;
import org.unitedlands.dungeons.utils.annotations.Info;

import com.google.gson.annotations.Expose;

public class SupplyChest {
    @Expose
    public UUID uuid;
    @Expose
    private Location location;

    @Expose
    @Info
    private String items;
    @Expose
    @Info
    private String facing;

    public SupplyChest() {

    }

    public SupplyChest(Location location) {
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

        public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getFacing() {
        return facing;
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LootChest c = (LootChest) o;
        return Objects.equals(uuid, c.getUuid());
    }



}
