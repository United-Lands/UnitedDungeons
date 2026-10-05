package org.unitedlands.dungeons.commands.dungeons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.classes.Room;
import org.unitedlands.dungeons.commands.CmdDungeons;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeons.class,
    name = "start",
    usage = "/ud start",
    playerOnly = true
)
public class CmdDungeonsStart implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

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

        if (dungeon.isOnCooldown()) {
            United.messenger().send(sender, "dungeon-status-lock-cooldown",
                    Map.of("cooldown-time", United.formatter().formatDuration(dungeon.getRemainingCooldown())));
            return;
        }

        if (dungeon.isLocked()) {
            United.messenger().send(sender, "dungeon-status-locked",
                    Map.of("lock-time", United.formatter().formatDuration(dungeon.getRemainingLockTime())));
            return;
        }

        if (!dungeon.isLockable()) {
            United.messenger().send(sender, "dungeon-status-not-lockable");
            return;
        }

        if (!room.enableLocking()) {
            United.messenger().send(sender, "dungeon-room-not-lockable");
            return;
        }

        if (dungeon.isPlayerOnLockCooldown(player.getUniqueId())) {

            United.messenger().send(sender, "error-leader-still-on-lock-cooldown",
                            United.formatter().formatDuration(dungeon.getPlayerRemainingLockCooldown(player.getUniqueId())));
            return;
        }

        for (Room r : dungeon.getRooms()) {
            if (r.mustBeCompleted()) {
                if (r.isComplete()) {
                    United.messenger().send(sender, "error-dungeon-partly-solved");
                    return;
                }
            }
        }

        dungeon.lockDungeon(player);
    }

}
