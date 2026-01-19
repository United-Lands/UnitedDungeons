package org.unitedlands.dungeons.events;

import org.bukkit.entity.Player;
import org.unitedlands.dungeons.classes.Dungeon;

public class PlayerEnterDungeonEvent extends PlayerDungeonEvent {

    public PlayerEnterDungeonEvent(Dungeon dungeon, Player player) {
        super(dungeon, player);
    }

}
