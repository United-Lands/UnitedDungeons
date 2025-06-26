package org.unitedlands.utils.factories.mobs;

import java.util.UUID;

import org.bukkit.Location;
import org.unitedlands.UnitedDungeons;

public abstract class BaseMobFactory implements IMobFactory {

    @SuppressWarnings("unused")
    private final UnitedDungeons plugin;

    public BaseMobFactory(UnitedDungeons plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public abstract UUID createMobAtLocation(String mobType, Location location);

}
