package org.unitedlands.dungeons.commands.admin.dungeon.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.commands.admin.dungeon.CmdDungeonAdminDungeon;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand (
    parent = CmdDungeonAdminDungeon.class,
    name = "delete",
    usage = "/uda dungeon delete <dungeon_name>"
)
public class CmdDungeonAdminDungeonDelete implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sendUsage(sender);
            return;
        }

        Player player = (Player) sender;
        Dungeon dungeon = DungeonManager.instance().getEditSessionForPlayr(player.getUniqueId());
        if (dungeon == null) {
            United.messenger().send(sender, "error-no-edit-session");
            return;
        }
        
        String directoryPath = File.separator + "dungeons";
        File file = new File(UnitedDungeons.instance().getDataFolder(),
                directoryPath + File.separator + dungeon.getUuid().toString() + ".json");

        if (!file.exists()) {
            United.logger().error("Dungeon file " + dungeon.getUuid() + " not found.");
            return;
        }

        try {
            file.delete();
            DungeonManager.instance().removeDungeon(dungeon);
            United.messenger().send(sender, "file-delete-success");
        } catch (Exception ex) {
            United.logger().error(ex.getMessage());
            United.messenger().send(sender, "file-delete-error");
        }

    }

}
