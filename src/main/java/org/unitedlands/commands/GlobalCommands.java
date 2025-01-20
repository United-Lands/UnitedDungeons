package org.unitedlands.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
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
            case "invite":
                handleDungeonInvite(player, args);
                break;
            case "warp":
                handleDungeonWarp(player, args);
        }

        return true;
    }

    private void handleDungeonInvite(Player player, String[] args) {
        if (args.length != 2)
            return;

        Dungeon playerDungeon = getPlayerDungeon(player);
        if (playerDungeon == null) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix(ChatColor.RED + "You're not in a dungeon!"));
            return;
        }

        if (!playerDungeon.isPlayerLockedInDungeon(player) && !player.hasPermission("united.dungeons.admin")) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix(ChatColor.RED + "You're not in the dungeon party!"));
            return;
        }

        var playerToInvite = Bukkit.getPlayer(args[1]);
        if (playerToInvite == null) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix(ChatColor.RED + "Player could not be found!"));
            return;
        }

        playerDungeon.invitePlayer(playerToInvite);
        player.sendMessage(MessageFormatter
                .getWithPrefix("Player " + args[1] + " has been added to the dungeon party."));
    }

    private void handleDungeonWarp(Player player, String[] args) {
        if (args.length != 2)
            return;

        var dungeon = plugin.getDungeon(args[1]);
        if (dungeon == null) {
            player.sendMessage(
                    MessageFormatter.getWithPrefix(ChatColor.RED + "No dungeon with name " + args[0] + " found!"));
            return;
        }

        if (!dungeon.isPublicWarp && !player.hasPermission("united.dungeons.admin")) {
            player.sendMessage(MessageFormatter.getWithPrefix(ChatColor.RED + "You cannot warp to this dungeon!"));
            return;
        }

        player.sendMessage(MessageFormatter.getWithPrefix("Teleporting in 3 seconds. Move to cancel."));
        new BukkitRunnable() {
            int counter = 0;
            int maxExecutions = 3;

            Location startBlock = player.getLocation().getBlock().getLocation();

            @Override
            public void run() {
                counter++;

                if (counter <= maxExecutions) {
                    player.sendMessage(MessageFormatter.getWithPrefix((maxExecutions - counter + 1) + "..."));
                }

                if (!player.getLocation().getBlock().getLocation().equals(startBlock)) {
                    player.sendMessage(MessageFormatter.getWithPrefix("Teleportation cancelled."));
                    this.cancel();
                }

                if (counter > maxExecutions) {
                    player.teleport(dungeon.exitLocation);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
        return;

    }

    private void handleDungeonInfo(Player player, String[] args) {

        Dungeon dungeon = null;

        if (args.length == 1) {
            dungeon = getPlayerDungeon(player);
            if (dungeon == null) {
                player.sendMessage(MessageFormatter
                        .getWithPrefix(ChatColor.RED + "You're not in a dungeon!"));
                return;
            }
        } else {
            dungeon = plugin.getDungeon(args[1]);
            if (dungeon == null) {
                MessageFormatter.getWithPrefix(ChatColor.RED + "No dungeon with name " + args[1] + " found!");
                return;
            }
        }

        player.sendMessage(MessageFormatter.getWithPrefix(
                ChatColor.WHITE + "" + ChatColor.BOLD + dungeon.getCleanName()));
        if (!dungeon.isActive) {
            player.sendMessage(MessageFormatter.getWithPrefix(
                    "This dungeon is currently " + ChatColor.RED + "deactivated" + ChatColor.GRAY + "."));
        } else if (dungeon.isOnCooldown) {
            player.sendMessage(MessageFormatter.getWithPrefix(
                    "This dungeon is on " + ChatColor.YELLOW + "cooldown" + ChatColor.GRAY
                            + ". It will be open again in " + ChatColor.BOLD + dungeon.getCooldownTimeString()
                            + "."));
        } else if (dungeon.isLocked) {
            player.sendMessage(MessageFormatter.getWithPrefix(
                    "This dungeon is " + ChatColor.RED + "locked" + ChatColor.GRAY
                            + " by a party. It will be open again in " + ChatColor.BOLD
                            + dungeon.getLockTimeString()
                            + "."));
        } else {
            if (!dungeon.isLockable) {
                player.sendMessage(MessageFormatter.getWithPrefix(
                        "This dungeon is " + ChatColor.GREEN + "open" + ChatColor.GRAY + " for all players."));
            } else {
                player.sendMessage(MessageFormatter.getWithPrefix(
                        "This dungeon is " + ChatColor.GREEN + "open" + ChatColor.GRAY + " and " + ChatColor.GREEN
                                + "lockable" + ChatColor.GRAY
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