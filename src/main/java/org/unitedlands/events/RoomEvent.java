package org.unitedlands.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.Room;

public class RoomEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Dungeon dungeon;
    private final Room room;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public RoomEvent(Dungeon dungeon, Room room) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.dungeon = dungeon;
        this.room = room;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public Room getRoom() {
        return room;
    }


}
