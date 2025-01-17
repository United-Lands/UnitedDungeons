package org.unitedlands.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.utils.MessageFormatter;

import net.md_5.bungee.api.ChatColor;

public class GlobalCommands implements CommandExecutor {

    private final UnitedDungeons plugin;

    public GlobalCommands(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            String[] args) {

        if (args.length == 0)
            return false;

        Player player = (Player) sender;

        switch (args[0]) {
            case "start":
                handleDungeonStart(player);
                break;
            case "teststart":
                handleDungeonTestStart(player);
                break;
            case "leave":
                handleDungeonLeave(player);
                break;
            case "entrance":
                handleDungeonEntrance(player, args);
                break;
            case "info":
                handleDungeonInfo(player, args);
                break;
        }

        return true;
    }

    private void handleDungeonInfo(Player player, String[] args) {
        Dungeon playerDungeon = getPlayerDungeon(player);
        if (playerDungeon == null) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix(ChatColor.RED + "You're not in a dungeon!"));
        }

        if (!playerDungeon.isActive)
            return;

        player.sendMessage(MessageFormatter.getWithPrefix(
                ChatColor.WHITE + "" + ChatColor.BOLD + playerDungeon.getCleanName()));
        if (playerDungeon.isOnCooldown) {
            player.sendMessage(MessageFormatter.getWithPrefix(
                    "This dungeon is on " + ChatColor.YELLOW + "cooldown" + ChatColor.GRAY
                            + ". It will be open again in " + ChatColor.BOLD + playerDungeon.getCooldownTimeString()
                            + "."));
        } else if (playerDungeon.isLocked) {
            player.sendMessage(MessageFormatter.getWithPrefix(
                    "This dungeon is " + ChatColor.RED + "locked" + ChatColor.GRAY
                            + " by a party. It will be open again in " + ChatColor.BOLD
                            + playerDungeon.getLockTimeString()
                            + "."));
        } else {

            if (!playerDungeon.isLockable) {
                player.sendMessage(MessageFormatter.getWithPrefix(
                        ChatColor.RED + "This dungeon cannot be locked for dungeon parties."));
            } else {
                player.sendMessage(MessageFormatter.getWithPrefix(
                        "This dungeon is " + ChatColor.GREEN + "open" + ChatColor.GRAY
                                + ". Use \"/ud start\" to lock it when your party is gathered in the dungeon."));
            }
        }

    }

    private void handleDungeonEntrance(Player player, String[] args) {
        Dungeon playerDungeon = getPlayerDungeon(player);
        if (playerDungeon == null) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix(ChatColor.RED + "You're not in a dungeon!"));
            return;
        }

        player.teleport(playerDungeon.exitLocation);
    }

    private void handleDungeonLeave(Player player) {
        Dungeon playerDungeon = getPlayerLockedDungeon(player);
        if (playerDungeon == null) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix(ChatColor.RED + "You're not in a dungeon party!"));
            return;
        }

        playerDungeon.removeLockedPlayer(player);
    }

    private void handleDungeonStart(Player player) {
        Dungeon playerDungeon = getPlayerDungeon(player);
        if (playerDungeon == null) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix(ChatColor.RED + "You're not in a dungeon!"));
            return;
        }

        if (playerDungeon.isLocked) {
            player.sendMessage(MessageFormatter.getWithPrefix(
                    "This dungeon is " + ChatColor.RED + "locked" + ChatColor.GRAY
                            + " by a party. It will be open again in " + ChatColor.BOLD
                            + playerDungeon.getLockTimeString()
                            + "."));
            return;
        }

        if (!playerDungeon.isLockable) {
            player.sendMessage(MessageFormatter.getWithPrefix(
                    ChatColor.RED + "This dungeon cannot be locked for dungeon parties."));
            return;
        }
        playerDungeon.lockDungeon();
    }

    private void handleDungeonTestStart(Player player) {

        if (!player.hasPermission("united.dungeon.admin"))
            return;

        Dungeon playerDungeon = getPlayerDungeon(player);
        if (playerDungeon == null) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix(ChatColor.RED + "You're not in a dungeon!"));
            return;
        }

        playerDungeon.lockDungeonTest();
    }

    private Dungeon getPlayerDungeon(Player player) {
        Dungeon playerDungeon = null;
        for (Dungeon dungeon : plugin.Dungeons.values()) {
            if (dungeon.isPlayerInDungeon(player))
                playerDungeon = dungeon;
        }
        return playerDungeon;
    }

    private Dungeon getPlayerLockedDungeon(Player player) {
        Dungeon playerDungeon = null;
        for (Dungeon dungeon : plugin.Dungeons.values()) {
            if (dungeon.isPlayerLockedInDungeon(player))
                playerDungeon = dungeon;
        }
        return playerDungeon;
    }
}