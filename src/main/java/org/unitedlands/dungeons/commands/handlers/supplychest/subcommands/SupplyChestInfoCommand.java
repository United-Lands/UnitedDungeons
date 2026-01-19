package org.unitedlands.dungeons.commands.handlers.supplychest.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.classes.SupplyChest;
import org.unitedlands.dungeons.utils.FieldHelper;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class SupplyChestInfoCommand extends BaseCommandHandler<UnitedDungeons> {

    public SupplyChestInfoCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-chest-info"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Player player = (Player) sender;
        Dungeon dungeon = plugin.getDungeonManager().getEditSessionForPlayr(player.getUniqueId());
        if (dungeon == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-no-edit-session"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        var room = plugin.getDungeonManager().getRoomAtLocation(dungeon, player.getLocation());
        if (room == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-not-in-room"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        SupplyChest chest = null;
        for (SupplyChest c : room.getSupplyChests()) {
            if (c.getLocation().getBlock().equals(player.getLocation().getBlock())) {
                chest = c;
            }
        }
        if (chest == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-chest-not-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Messenger.sendMessage(player, FieldHelper.getFieldValuesString(SupplyChest.class, chest));

    }

}
