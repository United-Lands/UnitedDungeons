package org.unitedlands.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.unitedlands.classes.Dungeon;

public class PlayerDungeonEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Dungeon dungeon;
    private final Player player;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public PlayerDungeonEvent(Dungeon dungeon, Player player) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.dungeon = dungeon;
        this.player = player;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public Player getPlayer() {
        return player;
    }

}
