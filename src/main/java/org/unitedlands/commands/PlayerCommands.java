package org.unitedlands.commands;

import org.unitedlands.UnitedDungeons;
import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.commands.handlers.player.PlayerEntranceCommand;
import org.unitedlands.commands.handlers.player.PlayerHighscoresCommand;
import org.unitedlands.commands.handlers.player.PlayerInfoCommand;
import org.unitedlands.commands.handlers.player.PlayerInviteCommand;
import org.unitedlands.commands.handlers.player.PlayerListCommand;
import org.unitedlands.commands.handlers.player.PlayerStartCommand;
import org.unitedlands.commands.handlers.player.PlayerWarpCommand;
import org.unitedlands.interfaces.IMessageProvider;

public class PlayerCommands extends BaseCommandExecutor<UnitedDungeons> {

    public PlayerCommands(UnitedDungeons plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("entrance", new PlayerEntranceCommand(plugin, messageProvider));
        handlers.put("start", new PlayerStartCommand(plugin, messageProvider));
        handlers.put("invite", new PlayerInviteCommand(plugin, messageProvider));
        handlers.put("list", new PlayerListCommand(plugin, messageProvider));
        handlers.put("highscores", new PlayerHighscoresCommand(plugin, messageProvider));
        handlers.put("info", new PlayerInfoCommand(plugin, messageProvider));

        if (plugin.getConfig().getBoolean("general.allow-warp", false))
            handlers.put("warp", new PlayerWarpCommand(plugin, messageProvider));
    }

}
