package org.unitedlands.dungeons.commands.handlers.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class PlayerListCommand extends BaseCommandHandler<UnitedDungeons> {

    public PlayerListCommand(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessage(sender, messageProvider.get("messages.info-player-dungeon-list"), null,
                    messageProvider.get("messages.prefix"));
            return;
        }

        Player player = (Player) sender;

        List<String> dungeonList = new ArrayList<>();
        if (player.hasPermission("united.dungeons.admin")) {
            dungeonList = plugin.getDungeonManager().getDungeonNames();
        } else {
            dungeonList = plugin.getDungeonManager().getPublicDungeonNames();
        }

        var msg = String.join(", ", dungeonList);
        Messenger.sendMessage(sender, msg, null, messageProvider.get("messages.prefix"));
    }

}
