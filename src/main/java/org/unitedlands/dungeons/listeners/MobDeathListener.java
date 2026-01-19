package org.unitedlands.dungeons.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.dungeons.UnitedDungeons;

public class MobDeathListener implements Listener {

    private final UnitedDungeons plugin;

    public MobDeathListener(UnitedDungeons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        handleMobDeath(event.getEntity());
    }

    @EventHandler
    public void onMobExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof LivingEntity)
            handleMobDeath((LivingEntity) event.getEntity());
    }

    @EventHandler
    public void onMobExplode2(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof LivingEntity)
            handleMobDeath((LivingEntity) event.getEntity());
    }

    private void handleMobDeath(@NotNull LivingEntity entity) {
        if (!(entity.getEntitySpawnReason() == SpawnReason.CUSTOM || entity.getEntitySpawnReason() == SpawnReason.PATROL))
            return;
        plugin.getMobManager().checkMobKill(entity.getUniqueId());
    }
}