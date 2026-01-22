package org.unitedlands.dungeons.events;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.unitedlands.dungeons.classes.Dungeon;

public class DungeonCompleteEvent extends DungeonEvent {

    private final Collection<Player> players;

    public DungeonCompleteEvent(Dungeon dungeon, Collection<Player> players) {
        super(dungeon);
        this.players = players;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

}
