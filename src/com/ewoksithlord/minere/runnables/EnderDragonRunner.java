package com.ewoksithlord.minere.runnables;

import com.ewoksithlord.minere.MineReUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.scheduler.BukkitRunnable;

@Deprecated
public class EnderDragonRunner extends BukkitRunnable {

    private final World world;

    public EnderDragonRunner(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        world.getEntitiesByClass(EnderDragon.class).stream().filter(enderDragon -> enderDragon.getPhase().equals(EnderDragon.Phase.CIRCLING)).forEach(enderDragon -> {
            teleportEntity(enderDragon, world);
            enderDragon.setPhase(EnderDragon.Phase.STRAFING);
        });
    }

    private static void teleportEntity(EnderDragon dragon, World world) {
        world.spawnParticle(Particle.PORTAL, dragon.getLocation(), 35);
        world.playSound(dragon.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 3f, 0.4f);
        final Location teleportPos = dragon.getLocation().add(MineReUtils.getRandomNumber(-64, 64), MineReUtils.getRandomNumber(0, 15), MineReUtils.getRandomNumber(-64, 64));
        if (teleportPos.getBlockY() > 120) {
            teleportPos.setY(110);
        }
        world.spawnParticle(Particle.PORTAL, dragon.getLocation(), 35);
        dragon.teleport(teleportPos);
        world.playSound(teleportPos, Sound.ENTITY_ENDERMAN_TELEPORT, 3f, 0.4f);
        world.spawnParticle(Particle.PORTAL, dragon.getLocation(), 35);
    }
}
