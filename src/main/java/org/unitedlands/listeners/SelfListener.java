package org.unitedlands.listeners;

import java.time.Duration;
import java.util.Map;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.events.DungeonCompleteEvent;
import org.unitedlands.events.DungeonOpenEvent;
import org.unitedlands.events.PlayerEnterRoomEvent;
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

        var title = Title.title(
                Component.text(dungeon.getCleanName()).color(NamedTextColor.DARK_RED),
                Component.text(room.getCleanName()).color(NamedTextColor.WHITE),
                Times.times(Duration.ofMillis(1000), Duration.ofMillis(3000), Duration.ofMillis(2000)));

        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1, 1);

        if (dungeon.isOnCooldown()) {
            Messenger.sendMessageTemplate(player, "dungeon-status-cooldown",
                    Map.of("cooldown-time", Formatter.formatDuration(dungeon.getRemainingCooldown())), true);
        }
    }

    @EventHandler
    public void onDungeonComplete(DungeonCompleteEvent event) {
        var dungeon = event.getDungeon();
        Messenger.broadcastMessageListTemplate("dungeon-event-completed",
                Map.of("dungeon-name", dungeon.getCleanName(), "cooldown-time",
                        Formatter.formatDuration(dungeon.getRemainingCooldown())),
                false);
    }

    @EventHandler
    public void onDungeonOpen(DungeonOpenEvent event) {
        var dungeon = event.getDungeon();
        Messenger.broadcastMessageListTemplate("dungeon-event-open",
                Map.of("dungeon-name", dungeon.getCleanName(),
                        "dungeon-description", dungeon.getDescription()),
                false);
    }

}