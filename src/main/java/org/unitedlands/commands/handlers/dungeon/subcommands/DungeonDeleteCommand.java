package org.unitedlands.commands.handlers.dungeon.subcommands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

public class DungeonDeleteCommand extends BaseCommandHandler {

    public DungeonDeleteCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 0) {
            Messenger.sendMessageTemplate(sender, "info-dungeon-delete", null, true);
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found", null, true);
            return;
        }

        String directoryPath = File.separator + "dungeons";
        File file = new File(plugin.getDataFolder(),
                directoryPath + File.separator + dungeon.getUuid().toString() + ".json");

        if (!file.exists()) {
            Logger.logError("Dungeon file " + dungeon.getUuid() + " not found.");
            return;
        }

        try {
            file.delete();
            plugin.getDungeonManager().removeDungeon(dungeon);
            Messenger.sendMessageTemplate(sender, "file-delete-success", null, true);
        } catch (Exception ex) {
            Logger.logError(ex.getMessage());
            Messenger.sendMessageTemplate(sender, "file-delete-error", null, true);
        }

    }

}
