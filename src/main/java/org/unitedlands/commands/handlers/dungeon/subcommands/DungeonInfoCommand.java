package org.unitedlands.commands.handlers.dungeon.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.Dungeon;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;

public class DungeonInfoCommand extends BaseCommandHandler {

    public DungeonInfoCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessageTemplate(sender, "info-dungeon-info", null, true);
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found", null, true);
            return;
        }

        player.sendMessage(Formatter.getFieldValuesString(Dungeon.class, dungeon));
    }

}
