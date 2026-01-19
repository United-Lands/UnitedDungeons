package org.unitedlands.dungeons.classes;

import java.util.Objects;
import java.util.UUID;

import com.google.gson.annotations.Expose;

public class PlayerLockCooldown {
    @Expose
    private Long time;
    @Expose
    private UUID player;

    public PlayerLockCooldown() {
    }

    public PlayerLockCooldown(Long time, UUID player) {
        this.time = time;
        this.player = player;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PlayerLockCooldown c = (PlayerLockCooldown) o;
        return Objects.equals(this.player, c.getPlayer()) && Objects.equals(this.time, c.getTime());
    }
}
