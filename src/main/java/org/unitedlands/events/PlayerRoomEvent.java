package org.unitedlands.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.Room;

public class PlayerRoomEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Dungeon dungeon;
    private final Room room;
    private final Player player;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PlayerRoomEvent(Dungeon dungeon, Room room, Player player) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.dungeon = dungeon;
        this.room = room;
        this.player = player;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public Room getRoom() {
        return room;
    }

    public Player getPlayer() {
        return player;
    }

}
