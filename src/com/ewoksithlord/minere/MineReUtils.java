package com.ewoksithlord.minere;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class MineReUtils {
    private static final Random random = new Random();
    public static int randomIntInRange(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static boolean oneInN(int n) {
        return random.nextInt(n) == n - 1;
    }

    public static ItemStack setItemDamage(ItemStack item, int damage) {
        final ItemMeta meta = item.getItemMeta();
        if (meta instanceof final Damageable damageable) {
            damageable.setDamage(damage);
        }
        return item;
    }

    public static boolean rollChance(double chance) {
        return Math.random() <= chance;
    }

    public static boolean isUndead(Entity entity) {
        if (entity instanceof final Mob mob) {
            return mob instanceof Zombie ||
                    mob instanceof AbstractSkeleton ||
                    mob instanceof Wither ||
                    mob instanceof Zoglin ||
                    mob instanceof Phantom ||
                    mob instanceof ZombieHorse ||
                    mob instanceof SkeletonHorse;
        }
        return false;
    }

    public static double getDifficultyMultiplier(World world) {
        return world.getDifficulty().equals(Difficulty.HARD) ? 1.5 : world.getDifficulty().equals(Difficulty.NORMAL) ? 1.0 : 0.5;
    }

    public static void targetNearestPlayer(Mob mob, int range) {
        mob.getNearbyEntities(range, range, range).stream().filter(entity ->
                entity instanceof final Player player && (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)))
                .map(entity -> (LivingEntity) entity).min(Comparator.comparing(player -> player.getLocation().distance(mob.getLocation()))).ifPresent(mob::setTarget);
    }

    public static void targetNearestPlayerOrGolem(Mob mob, int range) {
        mob.getNearbyEntities(range, range, range).stream().filter(entity ->
                (entity instanceof final Player player && (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)))
                        || entity instanceof IronGolem).map(entity -> (LivingEntity) entity).min(Comparator.comparing(player -> player.getLocation().distance(mob.getLocation()))).ifPresent(mob::setTarget);
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static double getMaxHealth(LivingEntity entity) {
        final AttributeInstance att = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (att != null) {
            return att.getBaseValue();
        } else {
            return 20.0;
        }
    }

    public static boolean getBooleanTag(Entity entity, String tagName, JavaPlugin plugin) {
        return entity.getMetadata(tagName).stream().filter(metadataValue -> Objects.equals(metadataValue.getOwningPlugin(), plugin))
                .findFirst().map(MetadataValue::asBoolean).orElse(false);
    }

    public static int getIntTag(Entity entity, String tagName, JavaPlugin plugin) {
        return entity.getMetadata(tagName).stream().filter(metadataValue -> Objects.equals(metadataValue.getOwningPlugin(), plugin))
                .findFirst().map(MetadataValue::asInt).orElse(0);
    }

    public static int difficultyIntGenerator(int easy, int normal, int hard, World world) {
        return world.getDifficulty().equals(Difficulty.EASY) ? easy : world.getDifficulty().equals(Difficulty.NORMAL) ? normal : world.getDifficulty().equals(Difficulty.HARD) ? hard : 0;
    }

    public static double difficultyDoubleGenerator(double easy, double normal, double hard, World world) {
        return world.getDifficulty().equals(Difficulty.EASY) ? easy : world.getDifficulty().equals(Difficulty.NORMAL) ? normal : world.getDifficulty().equals(Difficulty.HARD) ? hard : 0.0;
    }

    public static boolean isSkeletonTeam(Entity entity) {
        if (entity instanceof final Mob mob) {
            return (mob instanceof Zombie && !(mob instanceof PigZombie)) ||
                    mob instanceof AbstractSkeleton ||
                    mob instanceof Blaze;
        }
        return false;
    }

    public static boolean isSpiderTeam(Entity entity) {
        if (entity instanceof final Mob mob) {
            return (mob instanceof Spider) ||
                    mob instanceof Creeper ||
                    mob instanceof Witch ||
                    mob instanceof Phantom;
        }
        return false;
    }

    public static boolean isPlayerTeam(Entity entity) {
        if (entity instanceof final LivingEntity livingEntity) {
            return livingEntity instanceof Player ||
                    (livingEntity instanceof final Tameable tameable && tameable.isTamed()) ||
                    livingEntity instanceof IronGolem ||
                    livingEntity instanceof AbstractVillager ||
                    livingEntity instanceof Snowman;
        }
        return false;
    }

    public static boolean isPlayerTeamFightingMob(Entity entity) {
        if (entity instanceof final Mob mob) {
            return mob instanceof Wolf ||
                    mob instanceof IronGolem ||
                    mob instanceof Snowman;
        }
        return false;
    }

    public static Optional<LivingEntity> getLivingEntityFromDamager(Entity damager) {
        if (damager instanceof final LivingEntity livingEntity) {
            return Optional.of(livingEntity);
        }
        if (damager instanceof final Projectile projectile) {
            if (projectile.getShooter() instanceof final LivingEntity livingEntity) {
                return Optional.of(livingEntity);
            }
        }
        return Optional.empty();
    }

    public static Optional<Player> getPlayerFromDamager(Entity damager) {
        return (damager instanceof final Player player) ? Optional.of(player)
                : (damager instanceof final Projectile projectile) ? (projectile.getShooter() instanceof final Player p) ? Optional.of(p)
                : Optional.empty() : Optional.empty();
    }

    public static boolean isDay(World world) {
        final long time = world.getTime() % 24000;
        return time < 12300 || time > 23850;
    }
}
