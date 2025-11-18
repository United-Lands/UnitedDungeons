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
import org.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.integrations.MapTownyIntegration;
import org.unitedlands.utils.integrations.TownyIntegration;

public class UnitedDungeons extends JavaPlugin {

    private static UnitedDungeons instance;

    private DungeonManager dungeonManager;
    private RewardChestManager chestManager;

    private MobManager mobManager;
    private EffectsManager effectsManager;

    private TownyIntegration townyIntegration;
    private MapTownyIntegration mapTownyIntegration;

    private MessageProvider messageProvider;

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

        messageProvider = new MessageProvider(getConfig());

        loadManagers();

        loadIntegrations();

        saveDefaultConfig();

        registerCommands();

        getServer().getPluginManager().registerEvents(new MobDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new SelfListener(this, messageProvider), this);
        getServer().getPluginManager().registerEvents(new ServerListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListeners(this, messageProvider), this);

        dungeonManager.loadDungeons();
        dungeonManager.startChecks();


        getLogger().info("UnitedDungeons initialized.");

    }

    private void loadManagers() {
        dungeonManager = new DungeonManager(this, messageProvider);
        chestManager = new RewardChestManager(this);
        mobManager = new MobManager();
        effectsManager = new EffectsManager(this);
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
        var adminCommands = new AdminCommands(this, messageProvider);
        Objects.requireNonNull(getCommand("udadmin")).setExecutor(adminCommands);
        Objects.requireNonNull(getCommand("udadmin")).setTabCompleter(adminCommands);
        var playerCommands = new PlayerCommands(this, messageProvider);
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

    public TownyIntegration getTownyIntegration() {
        return townyIntegration;
    }

    public MapTownyIntegration getMapTownyIntegration() {
        return mapTownyIntegration;
    }

    public static UnitedDungeons getInstance() {
        return instance;
    }

    public MessageProvider getMessageProvider() {
        return messageProvider;
    }

    public void setMessageProvider(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

}
