package org.unitedlands.listeners;

import java.time.Duration;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.events.DungeonCompleteEvent;
import org.unitedlands.events.DungeonOpenEvent;
import org.unitedlands.events.HighscoreEvent;
import org.unitedlands.events.PlayerEnterRoomEvent;
import org.unitedlands.events.PlayerExitRoomEvent;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class SelfListener implements Listener {

    @SuppressWarnings("unused")
    private final UnitedDungeons plugin;

    public SelfListener(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerEnterRoom(PlayerEnterRoomEvent event) {
        var player = event.getPlayer();
        var dungeon = event.getDungeon();
        var room = event.getRoom();

        if (dungeon.isOnCooldown() || dungeon.isLocked())
            return;

        if (room.showTitle()) {

            var title = Title.title(
                    Component.text(dungeon.getCleanName()).color(NamedTextColor.DARK_RED),
                    Component.text(room.getCleanName()).color(NamedTextColor.WHITE),
                    Times.times(Duration.ofMillis(1000), Duration.ofMillis(3000), Duration.ofMillis(2000)));

            player.showTitle(title);

            if (!room.useBossMusic())
                player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1, 1);
        }

        if (room.useBossMusic()) {

            var location = new Location(player.getWorld(), room.getBoundingBox().getCenterX(),
                    room.getBoundingBox().getCenterY(), room.getBoundingBox().getCenterZ());

            plugin.getEffectsManager().playBossMusicForPlayer(player, location);
        }

        if (dungeon.isActive() && !dungeon.isOnCooldown() && !dungeon.isLocked() && room.enableLocking()) {
            Messenger.sendMessageTemplate(player, "dungeon-room-lockable", null, true);
        }
    }

    @EventHandler
    public void onPlayerExitRoom(PlayerExitRoomEvent event) {
        var player = event.getPlayer();
        var room = event.getRoom();

        if (room.useBossMusic()) {
            plugin.getEffectsManager().stopBossMusicForPlayer(player);
        }
    }

    @EventHandler
    public void onDungeonComplete(DungeonCompleteEvent event) {
        var dungeon = event.getDungeon();

        if (dungeon.isPublic()) {
            Messenger.broadcastMessageListTemplate("dungeon-event-completed",
                    Map.of("dungeon-name", dungeon.getCleanName(), "cooldown-time",
                            Formatter.formatDuration(dungeon.getRemainingCooldown())),
                    false);
        }

        for (var player : dungeon.getPlayersInPullout()) {
            Messenger.sendMessageTemplate(player, "dungeon-complete-countdown",
                    Map.of("countdown", Formatter.formatDuration(dungeon.getPulloutTime() * 1000)), true);
        }
    }

    @EventHandler
    public void onDungeonOpen(DungeonOpenEvent event) {
        var dungeon = event.getDungeon();
        if (dungeon.isPublic()) {
            Messenger.broadcastMessageListTemplate("dungeon-event-open",
                    Map.of("dungeon-name", dungeon.getCleanName(),
                            "dungeon-description", dungeon.getDescription()),
                    false);
        }
    }

    @EventHandler
    public void onHighscore(HighscoreEvent event) {
        var dungeon = event.getDungeon();
        var highscore = event.getHighscore();
        var placement = event.getPlacement();

        var players = dungeon.getPlayersInDungeon();
        for (var player : players) {
            Messenger.sendMessageTemplate(player, "new-highscore",
                    Map.of("time", Formatter.formatDuration(highscore.getTime()), "placement", placement.toString()),
                    false);
        }

    }

}