package org.unitedlands.utils.factories.mobs;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.utils.Logger;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.DespawnMode;

public class MythicMobFactory extends BaseMobFactory {

    public MythicMobFactory(UnitedDungeons plugin) {
        super(plugin);
    }

    @Override
    public UUID createMobAtLocation(String mobType, Location location) {
        var mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(mobType).orElse(null);
        if (mythicMob != null) {
            ActiveMob activeMythicMob = MythicBukkit.inst().getMobManager().spawnMob(mobType, location);
            activeMythicMob.setDespawnMode(DespawnMode.NEVER);

            return activeMythicMob.getUniqueId();
        } else {
            try {
                var entityType = EntityType.valueOf(mobType);
                var entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);

                entity.setPersistent(true);
                entity.setRemoveWhenFarAway(false);

                return entity.getUniqueId();
            } catch (Exception ex) {
                Logger.logError("Error creating entity: " + ex.getMessage());
            }
        }
        return null;
    }

}
