package org.unitedlands.commands.handlers.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class PlayerListCommand extends BaseCommandHandler {

    public PlayerListCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            Messenger.sendMessageTemplate(sender, "info-player-dungeon-list", null, true);
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
        Messenger.sendMessage(player, msg, true);
    }

}
