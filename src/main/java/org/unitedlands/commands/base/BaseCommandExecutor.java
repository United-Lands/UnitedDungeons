package org.unitedlands.commands.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.utils.Formatter;

public abstract class BaseCommandExecutor implements CommandExecutor, TabCompleter {

    protected final UnitedDungeons plugin;
    protected final Map<String, ICommandHandler> handlers = new HashMap<>();

    public BaseCommandExecutor(UnitedDungeons plugin) {
        this.plugin = plugin;
        registerHandlers();
    }

    protected abstract void registerHandlers();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, String alias,
            String[] args) {

        if (args.length == 0)
            return null;

        List<String> options = null;
        String input = args[args.length - 1];

        if (args.length == 1) {
            options = new ArrayList<>(handlers.keySet());
        } else {
            String subcommand = args[0].toLowerCase();
            ICommandHandler handler = handlers.get(subcommand);

            if (handler != null) {
                options = handler.handleTab(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        return Formatter.getSortedCompletions(input, options);
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label,
            String @NotNull [] args) {

        if (args.length == 0)
            return false;

        String subcommand = args[0].toLowerCase();
        ICommandHandler handler = handlers.get(subcommand);

        if (handler == null) {
            return false;
        }

        handler.handleCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }
    
}
