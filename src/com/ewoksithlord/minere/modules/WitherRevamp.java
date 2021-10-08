package com.ewoksithlord.minere.modules;

import com.ewoksithlord.minere.MineRe;
import com.ewoksithlord.minere.MineReUtils;
import com.ewoksithlord.minere.runnables.SpawnMobLightningDelay;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class WitherRevamp implements Listener {

    final static private JavaPlugin plugin = JavaPlugin.getPlugin(MineRe.class);
    final static double WITHER_TELEPORT_DAMAGE = 30.0;
    final static double WITHER_TELEPORT_CHANCE = 0.3;
    final static double WITHER_BACKUP_CHANCE = 0.5;

    @EventHandler
    public static void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof final Wither wither) {
            final World world = wither.getWorld();
            if (event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) {
                witherTeleport(wither, world);
                return;
            }

            final double damageTaken = wither.getMetadata("DamageTaken").stream()
                    .filter(metadataValue -> Objects.equals(metadataValue.getOwningPlugin(), plugin))
                    .findFirst().map(MetadataValue::asDouble).orElse(0.0) + event.getDamage();
            wither.setMetadata("DamageTaken", new FixedMetadataValue(plugin, damageTaken));

            if (wither.getHealth() > MineReUtils.getMaxHealth(wither)/2.0) {
                //PHASE 1
                if (damageTaken > WITHER_TELEPORT_DAMAGE && MineReUtils.rollChance(WITHER_TELEPORT_CHANCE)) {
                    if (MineReUtils.rollChance(WITHER_BACKUP_CHANCE)) {
                        world.spawnEntity(wither.getLocation(), EntityType.WITHER_SKELETON);
                    }
                    witherTeleport(wither, world);
                    wither.setMetadata("DamageTaken", new FixedMetadataValue(plugin, 0));
                }
            } else {
                //PHASE 2
                if (!MineReUtils.getBooleanTag(wither, "Reinforcements", plugin)) {
                    world.playSound(wither.getLocation(), Sound.ENTITY_WITHER_DEATH, 2f, 1.3f);
                    wither.setMetadata("Reinforcements", new FixedMetadataValue(plugin, true));
                    for (int i = 0; i < MineReUtils.difficultyIntGenerator(2, 4, 6, world); i++) {
                        new SpawnMobLightningDelay(world, EntityType.WITHER_SKELETON, wither.getLocation().add(MineReUtils.getRandomNumber(-10, 10), MineReUtils.getRandomNumber(0, 0), MineReUtils.getRandomNumber(-10, 10))).runTaskLater(plugin, 5 + i * 5L);
                    }
                }
            }
        }
    }

    private static void witherTeleport(Wither wither, World world) {
        world.playSound(wither.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.8f, 0.3f);
        world.playEffect(wither.getLocation(), Effect.SMOKE, 10);
        final Location teleportPos = wither.getLocation().add(MineReUtils.getRandomNumber(-18, 18), MineReUtils.getRandomNumber(0, 15), MineReUtils.getRandomNumber(-18, 18));
        if (teleportPos.getBlockY() > 120) {
            teleportPos.setY(110);
        }
        wither.teleport(teleportPos);
        world.playSound(teleportPos, Sound.ENTITY_WITHER_SHOOT, 0.8f, 0.4f);
        world.playEffect(teleportPos, Effect.SMOKE, 10);
    }
}