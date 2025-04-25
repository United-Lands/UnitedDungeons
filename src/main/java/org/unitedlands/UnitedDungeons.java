package org.unitedlands;

import org.bukkit.plugin.java.JavaPlugin;
import org.unitedlands.commands.DungeonCommands;
import org.unitedlands.commands.GlobalCommands;
import org.unitedlands.commands.SpawnerCommands;
import org.unitedlands.listeners.MobDeathListener;
import org.unitedlands.managers.DungeonManager;
import org.unitedlands.managers.MobManager;
import org.unitedlands.tabcompleters.DungeonCommandTabCompleter;
import org.unitedlands.tabcompleters.GlobalCommandsTabCompleter;
import org.unitedlands.tabcompleters.SpawnerCommandTabCompleter;

public class UnitedDungeons extends JavaPlugin {

    private DungeonManager dungeonManager;
    private MobManager mobManager;


    @Override
    public void onEnable() {

        dungeonManager = new DungeonManager(this);
        mobManager = new MobManager(this);

        getCommand("ud").setExecutor(new GlobalCommands(this));
        getCommand("ud").setTabCompleter(new GlobalCommandsTabCompleter(this));

        getCommand("uddungeon").setExecutor(new DungeonCommands(this));
        getCommand("uddungeon").setTabCompleter(new DungeonCommandTabCompleter(this));

        getCommand("udspawner").setExecutor(new SpawnerCommands(this));
        getCommand("udspawner").setTabCompleter(new SpawnerCommandTabCompleter(this));

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new MobDeathListener(this), this);

        dungeonManager.loadDungeons();
        dungeonManager.startChecks();

        getLogger().info("UnitedDungeons initialized.");
    }

    @Override
    public void onDisable() {
        dungeonManager.stopChecks();
        super.onDisable();
    }

    public DungeonManager getDungeonManager() {
        return this.dungeonManager;
    }

    public MobManager getMobManager() {
        return this.mobManager;
    }

}
