package org.unitedlands.utils.factories.mobs;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.utils.Logger;

public class VanillaMobFactory extends BaseMobFactory {

    public VanillaMobFactory(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public UUID createMobAtLocation(String mobType, Location location) {
        try {
            var entityType = EntityType.valueOf(mobType);
            var entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);

            entity.setPersistent(true);
            entity.setRemoveWhenFarAway(false);

            return entity.getUniqueId();
        } catch (Exception ex) {
            Logger.logError("Error creating entity: " + ex.getMessage());
        }
        return null;
    }

}
