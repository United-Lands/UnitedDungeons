package org.unitedlands.dungeons;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.unitedlands.dungeons.listeners.MobDeathListener;
import org.unitedlands.dungeons.listeners.PlayerEventListeners;
import org.unitedlands.dungeons.listeners.SelfListener;
import org.unitedlands.dungeons.listeners.ServerListener;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.dungeons.managers.EffectsManager;
import org.unitedlands.dungeons.managers.LootChestManager;
import org.unitedlands.dungeons.managers.MobManager;
import org.unitedlands.dungeons.utils.integrations.UnitedLandsIntegration;
import org.unitedlands.utils.United;

public class UnitedDungeons extends JavaPlugin {

    private static UnitedDungeons instance;

    private DungeonManager dungeonManager;

    private boolean usingUnitedLands;

    private UnitedLandsIntegration unitedLandsIntegration;
    // private MapTownyIntegration mapTownyIntegration;

    @Override
    public void onEnable() {

        instance = this;

        United.logger().info("****************************");
        United.logger().info("    | |__  o _|_ _  _|      ");
        United.logger().info("    |_|| | |  |_(/_(_|      ");
        United.logger().info("     _        _             ");
        United.logger().info("    | \\   __ (_| _  _ __  _ ");
        United.logger().info("    |_/|_|| |__|(/_(_)| |_> ");
        United.logger().info("****************************");

        loadManagers();

        loadIntegrations();

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new MobDeathListener(), this);
        getServer().getPluginManager().registerEvents(new SelfListener(), this);
        getServer().getPluginManager().registerEvents(new ServerListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListeners(), this);

    }

    private void loadManagers() {
        dungeonManager = new DungeonManager();
        new LootChestManager();
        new MobManager();
        new EffectsManager();
    }

    private void loadIntegrations() {
        Plugin ul = Bukkit.getPluginManager().getPlugin("UnitedLands");
        if (ul != null && ul.isEnabled()) {
            United.logger().info("UnitedLands found, enabling integration.");
            usingUnitedLands = true;
            unitedLandsIntegration = new UnitedLandsIntegration();
        }
        // Plugin mapTowny = Bukkit.getPluginManager().getPlugin("MapTowny");
        // if (mapTowny != null && mapTowny.isEnabled()) {
        // United.logger().info("MapTowny found, enabling integration.");
        // mapTownyIntegration = new MapTownyIntegration(this);
        // }
    }

    @Override
    public void onDisable() {
        dungeonManager.stopChecks();
        super.onDisable();
    }

    public boolean isUsingUnitedLands() {
        return usingUnitedLands;
    }

    public UnitedLandsIntegration getUnitedLandsIntegration() {
        return unitedLandsIntegration;
    }

    public static UnitedDungeons instance() {
        return instance;
    }

}
