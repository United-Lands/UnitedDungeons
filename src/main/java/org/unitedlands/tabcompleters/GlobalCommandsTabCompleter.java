package org.unitedlands.tabcompleters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.unitedlands.UnitedDungeons;

public class GlobalCommandsTabCompleter implements TabCompleter {

    private final UnitedDungeons plugin;
    private List<String> globalCommands = Arrays.asList(
            "start",
            "leave",
            "entrance",
            "info");

    public GlobalCommandsTabCompleter(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 0)
            return null;

        List<String> options = globalCommands;
        String input = args[args.length - 1];

        List<String> completions = null;
        if (options != null) {
            completions = options.stream().filter(s -> s.startsWith(input)).collect(Collectors.toList());
            Collections.sort(completions);
        }
        return completions;
    }

}
