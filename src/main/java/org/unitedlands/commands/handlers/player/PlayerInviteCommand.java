package org.unitedlands.commands.handlers.player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandHandler;
import org.unitedlands.utils.Messenger;

public class PlayerInviteCommand extends BaseCommandHandler {

    public PlayerInviteCommand(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            Messenger.sendMessageTemplate(sender, "info-player-invite", null, true);
            return;
        }

        Player player = (Player) sender;
        var dungeon = plugin.getDungeonManager().getClosestDungeon(player.getLocation());
        if (dungeon == null) {
            Messenger.sendMessageTemplate(sender, "error-no-dungeon-found", null, true);
            return;
        }

        if (!dungeon.isLocked()) {
            Messenger.sendMessageTemplate(sender, "error-dungeon-not-locked", null, true);
            return;
        }

        if (!dungeon.isPlayerLockedInDungeon(player) && !player.hasPermission("united.dungeons.admin")) {
            Messenger.sendMessageTemplate(sender, "error-not-in-party", null, true);
            return;
        }

        var playerToInvite = Bukkit.getPlayer(args[0]);
        if (playerToInvite == null) {
            Messenger.sendMessageTemplate(sender, "error-player-not-found", null, true);
            return;
        }

        dungeon.invitePlayer(playerToInvite);
        Messenger.sendMessageTemplate(sender, "player-invited", null, true);
        Messenger.sendMessageTemplate(playerToInvite, "player-party-start", null, true);

    }

}
