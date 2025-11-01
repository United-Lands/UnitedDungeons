package org.unitedlands;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.unitedlands.commands.AdminCommands;
import org.unitedlands.commands.PlayerCommands;
import org.unitedlands.listeners.MobDeathListener;
import org.unitedlands.listeners.PlayerEventListeners;
import org.unitedlands.listeners.SelfListener;
import org.unitedlands.listeners.ServerListener;
import org.unitedlands.managers.DungeonManager;
import org.unitedlands.managers.MobManager;
import org.unitedlands.managers.RewardChestManager;
import org.unitedlands.managers.EffectsManager;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.factories.items.IItemFactory;
import org.unitedlands.utils.factories.items.ItemsAdderFactory;
import org.unitedlands.utils.factories.items.VanillaItemFactory;
import org.unitedlands.utils.factories.mobs.IMobFactory;
import org.unitedlands.utils.factories.mobs.MythicMobFactory;
import org.unitedlands.utils.factories.mobs.VanillaMobFactory;
import org.unitedlands.utils.integrations.MapTownyIntegration;
import org.unitedlands.utils.integrations.TownyIntegration;

public class UnitedDungeons extends JavaPlugin {

    private static UnitedDungeons instance;

    private DungeonManager dungeonManager;
    private RewardChestManager chestManager;

    private MobManager mobManager;
    private EffectsManager effectsManager;

    private IItemFactory itemFactory;
    private IMobFactory mobFactory;

    private TownyIntegration townyIntegration;
    private MapTownyIntegration mapTownyIntegration;

    @Override
    public void onEnable() {

        instance = this;

        Logger.log("****************************");
        Logger.log("    | |__  o _|_ _  _|      ");
        Logger.log("    |_|| | |  |_(/_(_|      ");
        Logger.log("     _        _             ");
        Logger.log("    | \\   __ (_| _  _ __  _ ");
        Logger.log("    |_/|_|| |__|(/_(_)| |_> ");
        Logger.log("****************************");

        loadManagers();
        loadFactories();
        loadIntegrations();

        saveDefaultConfig();

        registerCommands();

        getServer().getPluginManager().registerEvents(new MobDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new SelfListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListeners(this), this);

        dungeonManager.loadDungeons();
        dungeonManager.startChecks();

        getLogger().info("UnitedDungeons initialized.");

    }

    private void loadManagers() {
        dungeonManager = new DungeonManager(this);
        chestManager = new RewardChestManager(this);
        mobManager = new MobManager(this);
        effectsManager = new EffectsManager(this);
    }

    private void loadFactories() {

        Plugin itemsAdder = Bukkit.getPluginManager().getPlugin("ItemsAdder");
        if (itemsAdder != null && itemsAdder.isEnabled()) {
            Logger.log("ItemsAdder found, using custom item factory.");
            itemFactory = new ItemsAdderFactory(this);
        } else {
            Logger.log("ItemsAdder not found, using vanilla item factory.");
            itemFactory = new VanillaItemFactory(this);
        }

        Plugin mythicMobs = Bukkit.getPluginManager().getPlugin("MythicMobs");
        if (mythicMobs != null && mythicMobs.isEnabled()) {
            Logger.log("MythicMobs found, using custom mob factory.");
            mobFactory = new MythicMobFactory(this);
        } else {
            Logger.log("MythicMobs not found, using vanilla mob factory.");
            mobFactory = new VanillaMobFactory(this);
        }
    }

    private void loadIntegrations() {
        Plugin towny = Bukkit.getPluginManager().getPlugin("Towny");
        if (towny != null && towny.isEnabled()) {
            Logger.log("Towny found, enabling integration.");
            townyIntegration = new TownyIntegration(this);
        }
        Plugin mapTowny = Bukkit.getPluginManager().getPlugin("MapTowny");
        if (mapTowny != null && mapTowny.isEnabled()) {
            Logger.log("MapTowny found, enabling integration.");
            mapTownyIntegration = new MapTownyIntegration(this);
        }
    }

    private void registerCommands() {
        var adminCommands = new AdminCommands(this);
        Objects.requireNonNull(getCommand("udadmin")).setExecutor(adminCommands);
        Objects.requireNonNull(getCommand("udadmin")).setTabCompleter(adminCommands);
        var playerCommands = new PlayerCommands(this);
        Objects.requireNonNull(getCommand("uniteddungeons")).setExecutor(playerCommands);
        Objects.requireNonNull(getCommand("uniteddungeons")).setTabCompleter(playerCommands);
    }

    @Override
    public void onDisable() {
        dungeonManager.stopChecks();
        super.onDisable();
    }

    public DungeonManager getDungeonManager() {
        return this.dungeonManager;
    }

    public RewardChestManager getChestManager() {
        return chestManager;
    }

    public MobManager getMobManager() {
        return this.mobManager;
    }

    public EffectsManager getEffectsManager() {
        return effectsManager;
    }

    public IItemFactory getItemFactory() {
        return itemFactory;
    }

    public IMobFactory getMobFactory() {
        return mobFactory;
    }

    public TownyIntegration getTownyIntegration() {
        return townyIntegration;
    }

    public MapTownyIntegration getMapTownyIntegration() {
        return mapTownyIntegration;
    }

    public static UnitedDungeons getInstance() {
        return instance;
    }

}
