package org.unitedlands.dungeons.commands.admin.barrier.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.classes.Barrier;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.commands.admin.barrier.CmdDungeonAdminBarrier;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.dungeons.utils.FieldHelper;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonAdminBarrier.class,
    name = "info",
    usage = "/uda barrier info"
)
public class CmdDungeonAdminBarrierInfo implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        Player player = (Player) sender;
        Dungeon dungeon = DungeonManager.instance().getEditSessionForPlayr(player.getUniqueId());
        if (dungeon == null) {
            United.messenger().send(sender, "error-no-edit-session");
            return;
        }

        var room = DungeonManager.instance().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            United.messenger().send(sender, "error-not-in-room");
            return;
        }

        Barrier barrier = null;
        for (Barrier b : room.getBarriers()) {
            if (b.getLocation().getBlock().equals(player.getLocation().getBlock())) {
                barrier = b;
            }
        }
        if (barrier == null) {
            United.messenger().send(sender, "error-barrier-not-found");
            return;
        }

        United.messenger().sendRaw(player, FieldHelper.getFieldValuesString(Barrier.class, barrier));
    }

}
