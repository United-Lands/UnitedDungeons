package org.unitedlands.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.unitedlands.classes.Dungeon;

public class DungeonEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Dungeon dungeon;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
		return handlers;
	}

    public DungeonEvent(Dungeon dungeon) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.dungeon = dungeon;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

}
