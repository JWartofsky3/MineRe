package com.ewoksithlord.minere.runnables;

import com.ewoksithlord.minere.MineReUtils;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class MobBehaviorRunner extends BukkitRunnable {

    private static final int SPIDER_SEARCH_RANGE = 20;
    private static final float ZOMBIE_HORSE_SPEED = 0.2f;
    private static final float ZOMBIE_HORSE_SPEED_RIDDEN = 0.4f;
    private final World world;

    public MobBehaviorRunner(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        world.getEntitiesByClass(Spider.class).stream()
                .filter(spider -> spider.getWorld().getBlockAt(spider.getLocation()).getLightFromSky() < 11 || !MineReUtils.isDay(world))
                .forEach(spider  -> MineReUtils.targetNearestPlayer(spider, SPIDER_SEARCH_RANGE));
        world.getEntitiesByClass(ZombieHorse.class).forEach(zombieHorse -> {
            Optional.ofNullable(zombieHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).ifPresent(movementSpeed -> {
                if (zombieHorse.getPassengers().stream().anyMatch(e -> e instanceof Zombie)) {
                    movementSpeed.setBaseValue(ZOMBIE_HORSE_SPEED_RIDDEN);
                } else {
                    movementSpeed.setBaseValue(ZOMBIE_HORSE_SPEED);
                }
            });
            if (zombieHorse.getLocation().getBlock().getLightFromSky() > 11 && MineReUtils.isDay(world)) {
                zombieHorse.setFireTicks(80);
            }
        });
    }
}
