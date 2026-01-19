package org.unitedlands.dungeons.commands.handlers.barrier.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.classes.Barrier;
import org.unitedlands.dungeons.classes.Dungeon;
import org.unitedlands.dungeons.utils.FieldHelper;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class BarrierInfoCommand extends BaseCommandHandler<UnitedDungeons> {

    public BarrierInfoCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-barrier-info"), null,
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

        Barrier barrier = null;
        for (Barrier b : room.getBarriers()) {
            if (b.getLocation().getBlock().equals(player.getLocation().getBlock())) {
                barrier = b;
            }
        }
        if (barrier == null) {
            Messenger.sendMessage(sender, messageProvider.get("messages.error-barrier-not-found"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Messenger.sendMessage(player, FieldHelper.getFieldValuesString(Barrier.class, barrier));
    }

}
