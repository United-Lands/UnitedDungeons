package org.unitedlands.dungeons.listeners;

import java.time.Duration;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.events.DungeonCompleteEvent;
import org.unitedlands.dungeons.events.DungeonOpenEvent;
import org.unitedlands.dungeons.events.HighscoreEvent;
import org.unitedlands.dungeons.events.PlayerEnterRoomEvent;
import org.unitedlands.dungeons.events.PlayerExitRoomEvent;
import org.unitedlands.dungeons.utils.MessageProvider;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class SelfListener implements Listener {

    private final UnitedDungeons plugin;
    private final MessageProvider messageProvider;

    public SelfListener(UnitedDungeons plugin, MessageProvider messageProvider) {
        this.plugin = plugin;
        this.messageProvider = messageProvider;
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
            Messenger.sendMessage(player, messageProvider.get("messages.dungeon-room-lockable"), null,
                    messageProvider.get("messages.prefix"));

        } else if (dungeon.requireLock() && !dungeon.isLocked()) {
            Messenger.sendMessage(player, messageProvider.get("messages.dungeon-requires-locking"), null,
                    messageProvider.get("messages.prefix"));
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

            Messenger.sendMessage(Bukkit.getServer(),
                    messageProvider.getList("messages.dungeon-event-completed"),
                    Map.of("dungeon-name", dungeon.getCleanName(),
                            "cooldown-time", Formatter.formatDuration(dungeon.getRemainingCooldown())),
                    null);
        }

        Messenger.sendMessage(dungeon.getPlayersInPullout(),
                messageProvider.get("messages.dungeon-complete-countdown"),
                Map.of("countdown", Formatter.formatDuration(dungeon.getPulloutTime() * 1000)),
                messageProvider.get("messages.prefix"));
    }

    @EventHandler
    public void onDungeonOpen(DungeonOpenEvent event) {
        var dungeon = event.getDungeon();
        if (dungeon.isPublic()) {
            Messenger.sendMessage(Bukkit.getServer(),
                    messageProvider.getList("messages.dungeon-event-open"),
                    Map.of("dungeon-name", dungeon.getCleanName(),
                            "dungeon-description", dungeon.getDescription()),
                    null);
        }
    }

    @EventHandler
    public void onHighscore(HighscoreEvent event) {

        var dungeon = event.getDungeon();
        var highscore = event.getHighscore();
        var placement = event.getPlacement();

        Messenger.sendMessage(dungeon.getPlayersInDungeon(),
                messageProvider.get("messages.new-highscore"),
                Map.of("time", Formatter.formatDuration(highscore.getTime()), "placement", placement.toString()),
                messageProvider.get("messages.prefix"));

    }

}