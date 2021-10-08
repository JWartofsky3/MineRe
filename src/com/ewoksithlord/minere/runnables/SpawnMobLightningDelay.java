package com.ewoksithlord.minere.runnables;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnMobLightningDelay extends BukkitRunnable {

    private final World world;
    private final EntityType type;
    private final Location location;

    public SpawnMobLightningDelay(World world, EntityType type, Location location) {
        this.world = world;
        this.type = type;
        this.location = location;
    }

    @Override
    public void run() {
        world.strikeLightning(location);
        world.spawnEntity(location, type);
        world.playEffect(location, Effect.SMOKE, 8);
    }
}
