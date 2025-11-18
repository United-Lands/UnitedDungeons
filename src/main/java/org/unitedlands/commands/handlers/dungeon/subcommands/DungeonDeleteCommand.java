package org.unitedlands.commands.handlers.dungeon.subcommands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

public class DungeonDeleteCommand extends BaseCommandHandler<UnitedDungeons> {

    public DungeonDeleteCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-dungeon-delete"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-dungeon-found"), null,
                    messageProvider.get("messages.prefix"));
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
            Messenger.sendMessage(sender, messageProvider.get("messages.file-delete-success"), null,
                    messageProvider.get("messages.prefix"));
        } catch (Exception ex) {
            Logger.logError(ex.getMessage());
            Messenger.sendMessage(sender, messageProvider.get("messages.file-delete-error"), null,
                    messageProvider.get("messages.prefix"));
        }

    }

}
