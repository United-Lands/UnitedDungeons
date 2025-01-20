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

public class DungeonCommandTabCompleter implements TabCompleter {

    private final UnitedDungeons plugin;
    private List<String> dungeonSubcommands = Arrays.asList(
            "delete",
            "info",
            "rename",
            "reset",
            "toggle",
            "unlock",
            "complete",
            "set",
            "showmarkers");
    private List<String> dungeonSetcommands = Arrays.asList(
            "description",
            "location",
            "exitLocation",
            "chestPosition",
            "platePosition",
            "isPublicWarp",
            "isLockable",
            "width",
            "length",
            "height",
            "doRewardDrop",
            "staticRewards",
            "randomRewards",
            "randomRewardsCount",
            "doPressurePlate",
            "cooldownTime",
            "lockTime");

    public DungeonCommandTabCompleter(UnitedDungeons plugin) {
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
                options = new ArrayList<>(Arrays.asList("create", "list"));
                options.addAll(plugin.getDungeonNames());
                break;
            case 2:
                if (!args[0].equals("create"))
                    options = dungeonSubcommands;
                break;
            case 3:
                if (args[1].equals("set"))
                    options = dungeonSetcommands;
                break;
            case 4:
                if (args[2].startsWith("do"))
                    options = Arrays.asList("true", "false");
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
