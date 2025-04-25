package org.unitedlands.listeners;

import java.time.Duration;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.events.PlayerEnterDungeonEvent;
import org.unitedlands.events.PlayerExitDungeonEvent;
import org.unitedlands.utils.MessageFormatter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.md_5.bungee.api.ChatColor;

public class SelfListener implements Listener {

    private final UnitedDungeons plugin;

    public SelfListener(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerEnterDungeon(PlayerEnterDungeonEvent event) {
        var player = event.getPlayer();
        var dungeon = event.getDungeon();

        var title = Title.title(
                Component.text(dungeon.getCleanName()).color(NamedTextColor.DARK_RED),
                Component.text(dungeon.description != null ? dungeon.description : ""),
                Times.times(Duration.ofMillis(1000), Duration.ofMillis(3000), Duration.ofMillis(2000)));

        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1, 1);

        if (dungeon.isOnCooldown) {
            player.sendMessage(MessageFormatter.getWithPrefix(
                    "This dungeon is on " + ChatColor.YELLOW + "cooldown" + ChatColor.GRAY
                            + ". It will be open again in " + ChatColor.BOLD + dungeon.getCooldownTimeString()
                            + "."));
        }

    }

    @EventHandler
    public void onPlayerExitDungeon(PlayerExitDungeonEvent event) {
        var player = event.getPlayer();
        var dungeon = event.getDungeon();

        player.sendMessage(MessageFormatter.getWithPrefix("You have left " + dungeon.getCleanName()));
    }

}