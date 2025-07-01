package org.unitedlands.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.classes.HighScore;

public class HighscoreEvent extends Event {

    
    private static final HandlerList handlers = new HandlerList();

    private final Dungeon dungeon;
    private final HighScore highscore;
    private final Integer placement;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
		return handlers;
	}

    public HighscoreEvent(Dungeon dungeon, HighScore highscore, Integer placement) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.dungeon = dungeon;
        this.highscore = highscore;
        this.placement = placement;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public HighScore getHighscore() {
        return highscore;
    }

    public Integer getPlacement() {
        return placement;
    }

    
}
