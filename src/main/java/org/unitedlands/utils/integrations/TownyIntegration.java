package org.unitedlands.utils.integrations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.utils.Formatter;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI.CommandType;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TownyIntegration implements Listener {

    private final UnitedDungeons plugin;
    private MiniMessage miniMessage;

    public TownyIntegration(UnitedDungeons plugin) {
        this.plugin = plugin;

        initialize();
    }

    private void initialize() {

        @SuppressWarnings("unused")
        var townyCommandHandler = new TownyCommandHandler(plugin);
        
        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.miniMessage = MiniMessage.miniMessage();

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTownStatusScreen(TownStatusScreenEvent event) {

        var meta = event.getTown().getMetadata("uniteddungeons_dungeon", StringDataField.class);
        if (meta != null && meta.getValue() != null && !meta.getValue().isEmpty()) {

            if (!plugin.getConfig().getBoolean("towny.show-custom-town-screen"))
                return;

            var dungeon = plugin.getDungeonManager().getDungeon(UUID.fromString(meta.getValue()));
            if (dungeon == null)
                return;

            var screen = event.getStatusScreen();
            var keys = new ArrayList<>(screen.getComponentKeys());

            for (var key : keys) {
                if (!key.equals("title"))
                    screen.removeStatusComponent(key);
            }
            

            String status = "<green>Open";
            if (!dungeon.isActive()) {
                status = "<red>Closed";
            } else if (dungeon.isLocked()) {
                status = "<gold>Locked <white>("
                        + Formatter.formatDuration(dungeon.getRemainingLockTime()) + ")";
            } else if (dungeon.isOnCooldown()) {
                status = "<gold>On Cooldown <white>("
                        + Formatter.formatDuration(dungeon.getRemainingCooldown()) + ")";
            }

            var lines = plugin.getConfig().getStringList("towny.custom-town-screen");
            var counter = 1;
            for (var line : lines) {
                if (line != null && !line.isEmpty()) {
                    line = line.replace("{dungeon-name}", dungeon.getCleanName());
                    line = line.replace("{dungeon-description}", dungeon.getDescription());
                    line = line.replace("{dungeon-status}", status);

                    Component component = miniMessage.deserialize(line);
                    screen.addComponentOf("dungeon-screen-" + counter, component);

                    counter++;
                }
            }

        }

    }

    // #region Towny command handler

    public class TownyCommandHandler implements CommandExecutor, TabCompleter {

        private final UnitedDungeons plugin;

        private static final String META_KEYNAME = "uniteddungeons_dungeon";

        public TownyCommandHandler(UnitedDungeons plugin) {
            this.plugin = plugin;
            TownyCommandAddonAPI.addSubCommand(new AddonCommand(CommandType.TOWNYADMIN_TOWN, "setdungeon", this));
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                @NotNull String label, @NotNull String @NotNull [] args) {

            var options = plugin.getDungeonManager().getDungeonNames();
            options.add("clear");

            return options;
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                @NotNull String @NotNull [] args) {

            if (args.length != 3) {
                sender.sendMessage("Usage: /ta town <townname> setdungeons [<dungeon_name> | clear]");
            }
            var town = TownyAPI.getInstance().getTown(args[0]);
            if (town == null)
                return false;

            if (args[2].equalsIgnoreCase("clear")) {
                town.removeMetaData(META_KEYNAME);
                town.save();
            } else {
                var dungeon = plugin.getDungeonManager().getDungeon(args[2]);
                if (dungeon == null) {
                    sender.sendMessage("<red>Unknown dungeon: " + args[2]);
                } else {
                    town.removeMetaData(META_KEYNAME);
                    town.addMetaData(new StringDataField(META_KEYNAME, dungeon.getUuid().toString()));
                    town.save();
                }
            }

            return true;
        }

    }

    // #endregion

}
