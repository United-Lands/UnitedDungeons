package org.unitedlands.dungeons.commands.dungeons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.commands.CmdDungeons;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeons.class,
    name = "entrance",
    playerOnly = true
)
public class CmdDungeonsEntrance implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            United.messenger().send(sender, "info-chest-remove");
            return;
        }

        Player player = (Player) sender;
        var dungeon = DungeonManager.instance().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            United.messenger().send(sender, "error-no-dungeon-found");
            return;
        }

        var room = DungeonManager.instance().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            United.messenger().send(sender, "error-not-in-room");
            return;
        }

        United.messenger().send(sender, "teleport");

        new BukkitRunnable() {
            int counter = 0;
            int maxExecutions = 3;

            Location startBlock = player.getLocation().getBlock().getLocation();

            @Override
            public void run() {
                counter++;

                if (counter <= maxExecutions) {
                    United.messenger().send(sender, (maxExecutions - counter + 1) + "...");
                }

                if (!player.getLocation().getBlock().getLocation().equals(startBlock)) {
                    United.messenger().send(sender, "teleport-cancel");
                    this.cancel();
                }

                if (counter > maxExecutions) {
                    player.teleport(dungeon.getWarpLocation());
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    this.cancel();
                }
            }
        }.runTaskTimer(UnitedDungeons.instance(), 0L, 20L);
        return;

    }

}
