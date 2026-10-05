package org.unitedlands.dungeons.commands.admin.supplychest.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.SupplyChest;
import org.unitedlands.dungeons.commands.admin.supplychest.CmdDungeonAdminSupplyChest;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.dungeons.utils.FieldHelper;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonAdminSupplyChest.class,
    name = "info",
    usage = "/uda supplychest info"
)
public class CmdDungeonAdminSupplyChestInfo implements UnitedCommandExecutor {

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

        SupplyChest chest = null;
        for (SupplyChest c : room.getSupplyChests()) {
            if (c.getLocation().getBlock().equals(player.getLocation().getBlock())) {
                chest = c;
            }
        }
        if (chest == null) {
            United.messenger().send(sender, "error-chest-not-found");
            return;
        }

        United.messenger().sendRaw(player, FieldHelper.getFieldValuesString(SupplyChest.class, chest));

    }

}
