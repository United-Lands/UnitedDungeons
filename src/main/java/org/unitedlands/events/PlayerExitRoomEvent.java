package org.unitedlands.events;

import org.bukkit.entity.Player;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.Room;

public class PlayerExitRoomEvent extends PlayerRoomEvent {

    public PlayerExitRoomEvent(Dungeon dungeon, Room room, Player player) {
        super(dungeon, room, player);
    }

}
