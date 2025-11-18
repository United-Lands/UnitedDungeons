package org.unitedlands.utils.integrations;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.utils.Formatter;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;

import me.silverwolfg11.maptowny.events.WorldRenderTownEvent;
import me.silverwolfg11.maptowny.objects.MarkerOptions;

public class MapTownyIntegration implements Listener {

    private final UnitedDungeons plugin;

    public MapTownyIntegration(UnitedDungeons plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        // registerReplacements();
    }

    @EventHandler
    public void onMapTownRender(WorldRenderTownEvent event) {

        var town = TownyAPI.getInstance().getTown(event.getTownUUID());
        var meta = town.getMetadata("uniteddungeons_dungeon", StringDataField.class);
        if (meta != null && meta.getValue() != null && !meta.getValue().isEmpty()) {

            if (!plugin.getConfig().getBoolean("maptowny.show-custom-town-info"))
                return;

            var dungeon = plugin.getDungeonManager().getDungeon(UUID.fromString(meta.getValue()));
            if (dungeon == null)
                return;

            var customContent = plugin.getConfig().getString("maptowny.custom-town-info");
            customContent = customContent.replace("{dungeon-id}", dungeon.getUuid().toString());
            customContent = customContent.replace("{dungeon-name}", dungeon.getCleanName());
            customContent = customContent.replace("{dungeon-description}", dungeon.getDescription());

            String status = "<span style='color: green'>Open</span>";
            if (!dungeon.isActive())
            {
                status = "<span style='color: red'>Closed</span>";
            } else if (dungeon.isLocked())
            {
                status = "<span style='color: GoldenRod'>Locked</span> (" + Formatter.formatDuration(dungeon.getRemainingLockTime()) + ")";
            } else if (dungeon.isOnCooldown())
            {
                status = "<span style='color: GoldenRod'>On Cooldown</span> (" + Formatter.formatDuration(dungeon.getRemainingCooldown()) + ")";
            }

            customContent = customContent.replace("{dungeon-status}", status);

            MarkerOptions.Builder options = event.getMarkerOptions();
            options.strokeColor(java.awt.Color.decode(plugin.getConfig().getString("maptowny.dungeon-map-color")));
            options.fillColor(java.awt.Color.decode(plugin.getConfig().getString("maptowny.dungeon-map-fill")));
            options.name(dungeon.getCleanName()).hoverTooltip(dungeon.getCleanName()).clickTooltip(customContent)
                    .build();
        }

    }

}
