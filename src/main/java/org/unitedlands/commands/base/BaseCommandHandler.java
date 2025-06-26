package org.unitedlands.commands.base;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.UnitedDungeons;

public abstract class BaseCommandHandler implements ICommandHandler {

    protected final UnitedDungeons plugin;

    public BaseCommandHandler(UnitedDungeons plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public abstract List<String> handleTab(CommandSender sender, String[] args);
    @Override
    public abstract void handleCommand(CommandSender sender, String[] args);

}
