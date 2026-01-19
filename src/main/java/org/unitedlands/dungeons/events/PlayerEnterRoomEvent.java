package org.unitedlands.dungeons.events;

import org.bukkit.entity.Player;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.Room;

public class PlayerEnterRoomEvent extends PlayerRoomEvent {

    public PlayerEnterRoomEvent(Dungeon dungeon, Room room, Player player) {
        super(dungeon, room, player);
    }

}
