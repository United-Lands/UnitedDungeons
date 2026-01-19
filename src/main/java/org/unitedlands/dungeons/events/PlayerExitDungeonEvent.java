package org.unitedlands.dungeons.events;

import org.bukkit.entity.Player;
import org.unitedlands.dungeons.classes.Dungeon;

public class PlayerExitDungeonEvent extends PlayerDungeonEvent {

    public PlayerExitDungeonEvent(Dungeon dungeon, Player player) {
        super(dungeon, player);
    }

}
