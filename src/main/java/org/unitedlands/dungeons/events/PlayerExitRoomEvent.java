package org.unitedlands.dungeons.events;

import org.bukkit.entity.Player;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.Room;

public class PlayerExitRoomEvent extends PlayerRoomEvent {

    public PlayerExitRoomEvent(Dungeon dungeon, Room room, Player player) {
        super(dungeon, room, player);
    }

}
