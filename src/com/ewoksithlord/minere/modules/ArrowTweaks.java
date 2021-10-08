package com.ewoksithlord.minere.modules;

import com.ewoksithlord.minere.MineReUtils;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class ArrowTweaks implements Listener {

    final static int CROSSBOW_DAMAGE_OFFSET = 1;

    @EventHandler(priority = EventPriority.LOW)
    public static void onProjectileLaunch(ProjectileLaunchEvent projectileLaunchEvent) {
        final Projectile projectile = projectileLaunchEvent.getEntity();
        if (projectile instanceof final Arrow arrow && arrow.isCritical() && arrow.getShooter() instanceof Player) {
            if (arrow.isShotFromCrossbow()) {
                arrow.setDamage(arrow.getDamage() + CROSSBOW_DAMAGE_OFFSET);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void onEntityDamage(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        final Entity entity = entityDamageByEntityEvent.getEntity();
        final Entity damager = entityDamageByEntityEvent.getDamager();
        if (entity instanceof final Mob mob && damager instanceof final Arrow arrow) {
            final PotionData potionData = arrow.getBasePotionData();
            final PotionType type = potionData.getType();
            if (type.getEffectType() != null) {
                if ((type.equals(PotionType.INSTANT_HEAL) && MineReUtils.isUndead(mob)) ||
                        (type.equals(PotionType.INSTANT_DAMAGE) && !MineReUtils.isUndead(mob))) {
                    entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage() + (potionData.isUpgraded() ? 12 : 6));
                } else {
                    entityDamageByEntityEvent.setCancelled(true);
                    mob.addPotionEffect(new PotionEffect(type.getEffectType(), 1, potionData.isUpgraded() ? 2 : 1));
                    mob.getWorld().spawnParticle(Particle.HEART, mob.getLocation(), 5);
                    mob.getWorld().playEffect(mob.getLocation(), Effect.MOBSPAWNER_FLAMES, 10);
                    mob.getWorld().playSound(mob.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, 1f, 0.25f);
                    arrow.remove();
                }
            }
        }
    }
}
