package org.unitedlands.commands;

import org.unitedlands.UnitedDungeons;
import org.unitedlands.commands.base.BaseCommandExecutor;
import org.unitedlands.commands.handlers.player.PlayerEntranceCommand;
import org.unitedlands.commands.handlers.player.PlayerHighscoresCommand;
import org.unitedlands.commands.handlers.player.PlayerInfoCommand;
import org.unitedlands.commands.handlers.player.PlayerInviteCommand;
import org.unitedlands.commands.handlers.player.PlayerListCommand;
import org.unitedlands.commands.handlers.player.PlayerStartCommand;
import org.unitedlands.commands.handlers.player.PlayerWarpCommand;

public class PlayerCommands extends BaseCommandExecutor {

    public PlayerCommands(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("entrance", new PlayerEntranceCommand(plugin));
        handlers.put("warp", new PlayerWarpCommand(plugin));
        handlers.put("start", new PlayerStartCommand(plugin));
        handlers.put("invite", new PlayerInviteCommand(plugin));
        handlers.put("list", new PlayerListCommand(plugin));
        handlers.put("highscores", new PlayerHighscoresCommand(plugin));
        handlers.put("info", new PlayerInfoCommand(plugin));
    }

}
