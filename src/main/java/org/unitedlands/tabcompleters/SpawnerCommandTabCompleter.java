package org.unitedlands.tabcompleters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.unitedlands.UnitedDungeons;

public class SpawnerCommandTabCompleter implements TabCompleter {

    private final UnitedDungeons plugin;
    private List<String> spawnerSubcommands = Arrays.asList(
            "create",
            "delete",
            "info",
            "set");
    private List<String> spawnerSetcommands = Arrays.asList(
        "radius",
        "mobType",
        "isMythicMob",
        "maxMobs",
        "spawnFrequency",
        "isGroupSpawn",
        "killsToComplete"
    );

    public SpawnerCommandTabCompleter(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 0)
            return null;

        List<String> options = null;
        String input = args[args.length - 1];

        switch (args.length) {
            case 1:
                options = spawnerSubcommands;
                break;
            case 2:
                if (args[0].equals("create")) {
                    options = plugin.getDungeonNames();
                }
                if (args[0].equals("set")) {
                    options = spawnerSetcommands;
                }
                break;
        }

        List<String> completions = null;
        if (options != null) {
            completions = options.stream().filter(s -> s.startsWith(input)).collect(Collectors.toList());
            Collections.sort(completions);
        }
        return completions;
    }

}
