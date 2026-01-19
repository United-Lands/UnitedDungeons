package org.unitedlands.dungeons.classes;

import com.google.gson.annotations.Expose;

public class HighScore {

    @Expose
    private long time;
    @Expose
    private String players;

    public HighScore() {
    }

    public HighScore(long time, String players) {
        this.time = time;
        this.players = players;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }

}
