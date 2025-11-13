package org.unitedlands.commands.handlers.lockchest.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.LockChest;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;

public class LockChestInfoCommand extends BaseCommandHandler {

    public LockChestInfoCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessageTemplate(sender, "info-chest-info", null, true);
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found", null, true);
            return;
        }

        var room = plugin.getDungeonManager().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            Messenger.sendMessageTemplate(sender, "error-not-in-room", null, true);
            return;
        }

        LockChest chest = null;
        for (LockChest c : room.getLockChests()) {
            if (c.getLocation().getBlock().equals(player.getLocation().getBlock())) {
                chest = c;
            }
        }
        if (chest == null) {
            Messenger.sendMessageTemplate(sender, "error-chest-not-found", null, true);
            return;
        }

        player.sendMessage(Formatter.getFieldValuesString(LockChest.class, chest));

    }

}
