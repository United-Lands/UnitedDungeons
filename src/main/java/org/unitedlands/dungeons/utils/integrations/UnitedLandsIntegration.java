package org.unitedlands.dungeons.utils.integrations;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.unitedlands.dungeons.UnitedDungeons;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.unitedlands.classes.events.infoscreen.SettlementInfoScreenEvent;
import org.unitedlands.unitedlands.classes.events.settlement.SettlementMapRenderEvent;
import org.unitedlands.unitedlands.classes.infoscreen.InfoScreenComponent;
import org.unitedlands.unitedlands.classes.metadata.StringMetaDataField;
import org.unitedlands.utils.United;

public class UnitedLandsIntegration implements Listener {

    private final String META_KEYNAME = "uniteddungeons_dungeon";

    public UnitedLandsIntegration() {

        Bukkit.getPluginManager().registerEvents(this, UnitedDungeons.instance());

    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onSettlementScreen(SettlementInfoScreenEvent event) {

        if (!event.getSettlement().hasMetadata(META_KEYNAME))
            return;

        if (!UnitedDungeons.instance().getConfig().getBoolean("town-screen.enabled", false))
            return;

        var dungeonId = UUID
                .fromString(((StringMetaDataField) event.getSettlement().getMetadata(META_KEYNAME)).getValue());
        var dungeon = DungeonManager.instance().getDungeon(dungeonId);
        if (dungeon == null)
            return;

        var screen = event.getInfoScreen();
        var keys = new ArrayList<>(screen.getComponents().stream().map(InfoScreenComponent::getId).toList());
        for (var key : keys) {
            if (!key.equals("header") && !key.equals("footer"))
                screen.removeComponent(key);
        }

        screen.addComponent("header", "town-screen.header", UnitedDungeons.instance());
        screen.addComponent("name", "town-screen.name", UnitedDungeons.instance(), dungeon.getCleanName());
        screen.addComponent("description", "town-screen.description", UnitedDungeons.instance(), dungeon.getDescription());

        String status = "<green>Open";
        if (!dungeon.isActive()) {
            status = "<red>Closed";
        } else if (dungeon.isLocked()) {
            status = "<gold>Locked <white>("
                    + United.formatter().formatDuration(dungeon.getRemainingLockTime()) + ")";
        } else if (dungeon.isOnCooldown()) {
            status = "<gold>On Cooldown <white>("
                    + United.formatter().formatDuration(dungeon.getRemainingCooldown()) + ")";
        }

        screen.addComponent("status", "town-screen.status", UnitedDungeons.instance(), status);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onSettlementMapRender(SettlementMapRenderEvent event) {

    }

}
