package com.ewoksithlord.minere.modules;

import com.ewoksithlord.minere.MineRe;
import com.ewoksithlord.minere.MineReUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.stream.Collectors;

public class MobTweaks implements Listener {

    final public static int DEFAULT_FOLLOW_RANGE = 25;
    final public static int ALERT_RANGE = 22;
    final static double ALERT_CHANCE = 0.3;
    final static double ALERT_CHANCE_INDIVIDUAL = 0.5;
    final static float ITEM_DROP_CHANCE = 0.1f; // Used for items given to mobs by this plugin such as sword skeletons and wither skeleton bowmen
    final static float TIPPED_ARROW_DROP_CHANCE = 0.4f;
    final static int HORSEMAN_HEIGHT = 62;

    //--------- Hostile Mob Stats ----------

    final static float ZOMBIE_MOVE_SPEED = 0.255f;
    final static int ZOMBIE_DAMAGE = 5;
    final static float ZOMBIE_HEALTH = 20f;
    final static float HUSK_HEALTH = 22f;
    final static float ZOMBIE_KNOCKBACK_RESISTANCE = 0.25f;
    final static float ZOMBIE_REINFORCEMENT_CHANCE_INCREASE = 0.04f;
    final static double ZOMBIE_EFFECT_CHANCE = 0.2;
    final static double ZOMBIE_HORSEMAN_CHANCE = 0.02;

    final static double SKELETON_SWORD_CHANCE = 0.15;
    final static double SKELETON_TIPPED_ARROW_CHANCE = 0.2;

    final static double STRAY_POLAR_CHANCE = 0.03;

    final static float SPIDER_MOVE_SPEED = 0.39f;
    final static int SPIDER_DAMAGE = 4;
    final static double SPIDER_EFFECT_CHANCE = 0.2;

    final static int CAVE_SPIDER_DAMAGE = 3;
    final static float CAVE_SPIDER_MOVE_SPEED = 0.33f;

    final static double CREEPER_CHARGE_CHANCE = 0.15;

    final static int SILVERFISH_DAMAGE = 2;

    final static int PHANTOM_DAMAGE = 5;
    final static float PHANTOM_HEALTH = 16f;
    final static double BIG_PHANTOM_CHANCE = 0.2;
    final static int BIG_PHANTOM_SIZE = 5;
    final static double GIANT_PHANTOM_CHANCE = 0.03;
    final static int GIANT_PHANTOM_SIZE = 10;
    final static int END_PHANTOM_SIZE_INCREASE = 4;

    final static double ILLUSIONER_REPLACE_CHANCE = 0.3;


    //--------- Nether Mob Stats ------------
    final static float ZOMBIE_PIGLIN_KNOCKBACK_RESISTANCE = 0.25f;

    final static double ZOGLIN_CHANCE = 0.02;

    final static float GHAST_ARROW_RESISTANCE = 0.7f;
    final static float GHAST_DAMAGE_SCALE = 2.0f;
    final static double GHAST_FIRE_CHANCE = 0.5;
    final static float GHAST_FIRE_THRESHOLD = 6f;

    final static double MAGMA_CUBE_SIZE_INCREASE_CHANCE = 0.25;
    final static double MAGMA_CUBE_SIZE_INCREASE_CHANCE_BASALT_DELTA = 0.02;
    final static int MAGMA_CUBE_SIZE_INCREASE_MAX = 4;

    final static float WITHER_SKELETON_HEATLH = 24f;
    final static double WITHER_SKELETON_BOW_CHANCE = 0.15;
    final static float WITHER_SKELETON_KNOCKBACK_RESISTANCE = 0.25f;

    final static float PIGLIN_HEALTH = 20f;
    final static float PIGLIN_BRUTE_KNOCKBACK_RESISTANCE = 0.35f;


    //---------- Ender Mob Stats -----------

    final static int ENDERMAN_ATTACK_DAMAGE = 8;
    final static double ENDERMAN_ANGER_CHANCE = 0.3;
    final static int ENDERMAN_XP = 10;

    final static int ENDERMITE_ATTACK_DAMAGE = 3;

    final static double END_PHANTOM_SPAWN_CHANCE = 0.15;
    final static double ENDERMITE_REPLACE_CHANCE = 0.3;

    final static float SHULKER_DAMAGE = 5f;


    //---------- Animal Stats --------------

    final static float WOLF_MOVE_SPEED = 3.9f;
    final static int WOLF_HEALTH_TAME = 20;
    final static float WOLF_ATTACK_DAMAGE_TAME = 6f;
    final static int WOLF_HEALTH_WILD = 14;
    final static float WOLF_ATTACK_DAMAGE_WILD = 4f;

    final static int LLAMA_MIN_STRENGTH = 5;
    final static double HORSE_MIN_JUMP_STRENGTH = 0.6;
    final static double HORSE_MIN_SPEED = 0.19;

    final static int POLAR_BEAR_HEALTH = 40;
    final static float POLAR_BEAR_DAMAGE = 7f;
    final static float POLAR_BEAR_SPEED = 0.36f;
    final static float POLAR_BEAR_KNOCKBACK_RESISTANCE = 0.55f;

    //----------- Projectiles -------------
    final static float MOB_CROSSBOW_OFFSET = 2f;

    @EventHandler(priority = EventPriority.LOW)
    public static void onEntityTame(EntityTameEvent entityTameEvent) {
        final Entity entity = entityTameEvent.getEntity();
        if (entity instanceof final Wolf wolf) {
            setMoveSpeed(wolf, WOLF_MOVE_SPEED);
            setAttackDamage(wolf, WOLF_ATTACK_DAMAGE_TAME);
            setMaxHealth(wolf, WOLF_HEALTH_TAME);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void onCreatureSpawn(CreatureSpawnEvent creatureSpawnEvent) {
        final LivingEntity entity = creatureSpawnEvent.getEntity();
        final World world = entity.getWorld();
        final Block block = entity.getLocation().getBlock();
        final int blockY = block.getY();
        final World.Environment environment = world.getEnvironment();
        final Biome biome = block.getBiome();
        final double difficultyMultiplier = MineReUtils.getDifficultyMultiplier(world);

        if (entity instanceof final Mob mob) {
            setFollowRangeMin(mob);

            // ------------------------------------ OVER WORLD MOBS -------------------------------------

            //Zombie Variants
            if (entity instanceof final Zombie zombie && !(entity instanceof PigZombie)) {
                addReinforcementChance(zombie);
                replaceGoldArmor(zombie);
                if (zombie instanceof final Husk husk) {
                    setMaxHealth(husk, HUSK_HEALTH);
                } else {
                    setMaxHealth(zombie, ZOMBIE_HEALTH);
                }
                if (zombie.isAdult()) {
                    setAttackDamage(zombie, ZOMBIE_DAMAGE);
                    setMoveSpeed(zombie, ZOMBIE_MOVE_SPEED);
                    setKnockbackResistanceMin(zombie, ZOMBIE_KNOCKBACK_RESISTANCE);
                }
                if (blockY < getDangerDepth()) {
                    if (MineReUtils.rollChance(ZOMBIE_EFFECT_CHANCE * difficultyMultiplier)) {
                        addRandomEffect(zombie);
                    }
                }
                if (blockY > HORSEMAN_HEIGHT && MineReUtils.rollChance(ZOMBIE_HORSEMAN_CHANCE) && block.getLightFromSky() > 5) {
                    if (!(zombie instanceof Drowned) && !(zombie instanceof Husk)) {
                        final ZombieHorse zHorse = (ZombieHorse) world.spawnEntity(zombie.getLocation(), EntityType.ZOMBIE_HORSE);
                        MineReUtils.targetNearestPlayer(zHorse, ALERT_RANGE);
                        //Give sword and helmet
                        Optional.ofNullable(zombie.getEquipment()).ifPresent(equipment -> {
                            equipment.setItemInMainHand(MineReUtils.setItemDamage(new ItemStack(Material.IRON_SWORD), 70));
                            equipment.setHelmet(MineReUtils.setItemDamage(new ItemStack(Material.IRON_HELMET), 40));
                            equipment.setItemInMainHandDropChance(ITEM_DROP_CHANCE);
                            equipment.setHelmetDropChance(ITEM_DROP_CHANCE);
                        });
                        zHorse.setRemoveWhenFarAway(true);
                        zHorse.addPassenger(zombie);
                    }
                    if (zombie instanceof Husk) {
                        final TraderLlama llama = (TraderLlama) world.spawnEntity(zombie.getLocation(), EntityType.TRADER_LLAMA);
                        MineReUtils.targetNearestPlayer(llama, ALERT_RANGE);
                        //Give sword and helmet
                        Optional.ofNullable(zombie.getEquipment()).ifPresent(equipment -> {
                            equipment.setItemInMainHand(MineReUtils.setItemDamage(new ItemStack(Material.STONE_SWORD), 50));
                            equipment.setHelmet(MineReUtils.setItemDamage(new ItemStack(Material.LEATHER_HELMET), 20));
                            equipment.setItemInMainHandDropChance(ITEM_DROP_CHANCE);
                            equipment.setHelmetDropChance(ITEM_DROP_CHANCE);
                        });
                        setMoveSpeed(llama, 0.36f);
                        llama.setRemoveWhenFarAway(true);
                        llama.addPassenger(zombie);
                    }
                }

                return;
            }

            //Skeleton
            if (entity instanceof final Skeleton skeleton) {
                //If skeleton spawns in nether outside Soul Sand Valley, replace with either a Blaze or Wither Skeleton
                if (environment.equals(World.Environment.NETHER) && !biome.equals(Biome.SOUL_SAND_VALLEY)) {
                    if (MineReUtils.rollChance(0.5)) {
                        world.spawnEntity(skeleton.getLocation(), EntityType.WITHER_SKELETON);
                    } else {
                        world.spawnEntity(skeleton.getLocation(), EntityType.BLAZE);
                    }
                    creatureSpawnEvent.setCancelled(true);
                    return;
                }
                replaceGoldArmor(skeleton);
                //Skeleton Swordsman
                if (MineReUtils.rollChance(SKELETON_SWORD_CHANCE)) {
                    //Give sword and helmet
                    Optional.ofNullable(skeleton.getEquipment()).ifPresent(equipment -> {
                        equipment.setItemInMainHand(MineReUtils.setItemDamage(new ItemStack(Material.IRON_SWORD), 70));
                        equipment.setHelmet(MineReUtils.setItemDamage(new ItemStack(Material.IRON_HELMET), 40));
                        equipment.setItemInMainHandDropChance(ITEM_DROP_CHANCE);
                        equipment.setHelmetDropChance(ITEM_DROP_CHANCE);
                    });
                } else {
                    //Tipped arrows
                    if (MineReUtils.rollChance(SKELETON_TIPPED_ARROW_CHANCE * difficultyMultiplier)) {
                        Optional.ofNullable(skeleton.getEquipment()).ifPresent(equipment -> {
                            final ItemStack tippedArrows = new ItemStack(Material.TIPPED_ARROW, 2);
                            Optional.ofNullable(tippedArrows.getItemMeta()).ifPresent(itemMeta -> {
                                if (itemMeta instanceof final PotionMeta potionMeta) {
                                    if (environment.equals(World.Environment.NETHER)) {
                                        potionMeta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
                                        tippedArrows.setItemMeta(potionMeta);
                                        equipment.setItemInOffHand(tippedArrows);
                                        equipment.setItemInOffHandDropChance(TIPPED_ARROW_DROP_CHANCE);
                                    } else {
                                        if (blockY < getDangerDepth()) {
                                            if (MineReUtils.rollChance(0.5)) {
                                                potionMeta.setBasePotionData(new PotionData(PotionType.WEAKNESS));
                                            } else {
                                                potionMeta.setBasePotionData(new PotionData(PotionType.POISON));
                                            }
                                            tippedArrows.setItemMeta(potionMeta);
                                            equipment.setItemInOffHand(tippedArrows);
                                            equipment.setItemInOffHandDropChance(TIPPED_ARROW_DROP_CHANCE);
                                        }
                                    }
                                }
                            });
                        });
                    }
                }
            }

            //Stray
            if (entity instanceof final Stray stray) {
                if (blockY > HORSEMAN_HEIGHT && environment.equals(World.Environment.NORMAL) && MineReUtils.rollChance(STRAY_POLAR_CHANCE) && block.getLightFromSky() > 5) {
                    final PolarBear polarBear = (PolarBear) world.spawnEntity(stray.getLocation(), EntityType.POLAR_BEAR);
                    MineReUtils.targetNearestPlayer(polarBear, ALERT_RANGE);
                    Optional.ofNullable(stray.getEquipment()).ifPresent(equipment -> {
                        equipment.setHelmet(new ItemStack(Material.LEATHER_HELMET));
                        equipment.setHelmetDropChance(ITEM_DROP_CHANCE);
                    });
                    polarBear.setRemoveWhenFarAway(true);
                    polarBear.addPassenger(stray);
                }
            }

            //Spider
            if (entity instanceof final Spider spider) {
                if (entity instanceof CaveSpider) {
                    setAttackDamage(spider, CAVE_SPIDER_DAMAGE);
                    setMoveSpeed(spider, CAVE_SPIDER_MOVE_SPEED);
                } else {
                    setAttackDamage(spider, SPIDER_DAMAGE);
                    setMoveSpeed(spider, SPIDER_MOVE_SPEED);
                }
                if (blockY < getDangerDepth()) {
                    if (MineReUtils.rollChance(SPIDER_EFFECT_CHANCE * difficultyMultiplier)) {
                        addRandomEffect(spider);
                    }
                }
                return;
            }

            //Creeper
            if (entity instanceof final Creeper creeper) {
                if (blockY < getDangerDepth() && MineReUtils.rollChance(CREEPER_CHARGE_CHANCE * difficultyMultiplier)) {
                    creeper.setPowered(true);
                }
                return;
            }

            //Silverfish
            if (entity instanceof final Silverfish silverfish) {
                setAttackDamage(silverfish, SILVERFISH_DAMAGE);
                return;
            }

            //Phantom
            if (entity instanceof final Phantom phantom) {
                int size = 0;
                if (MineReUtils.rollChance(BIG_PHANTOM_CHANCE)) {
                    size = BIG_PHANTOM_SIZE;
                }
                if (MineReUtils.rollChance(GIANT_PHANTOM_CHANCE)) {
                    size = GIANT_PHANTOM_SIZE;
                }
                if (environment.equals(World.Environment.THE_END)) {
                    size = size + END_PHANTOM_SIZE_INCREASE;
                }
                if (size != 0) {
                    phantom.setSize(size);
                }
                setAttackDamage(phantom, PHANTOM_DAMAGE + (size * 0.5f));
                setMaxHealth(phantom, PHANTOM_HEALTH + (size * 2));
                return;
            }

            //Witch
            if (entity instanceof final Witch witch) {
                if ((biome.equals(Biome.BADLANDS)
                        || biome.equals(Biome.BADLANDS_PLATEAU)
                        || biome.equals(Biome.DESERT)
                        || biome.equals(Biome.DESERT_HILLS)
                        || biome.equals(Biome.DESERT_LAKES))
                        && MineReUtils.rollChance(ILLUSIONER_REPLACE_CHANCE)
                        && block.getLightFromSky() > 5) {
                    world.spawnEntity(witch.getLocation(), EntityType.ILLUSIONER);
                    creatureSpawnEvent.setCancelled(true);
                    return;
                }
            }

            // ---------------------------- NETHER MOBS ----------------------------------------

            //Zombie Pigman
            if (entity instanceof final PigZombie pigZombie) {
                if (MineReUtils.rollChance(ZOGLIN_CHANCE)) {
                    world.spawnEntity(pigZombie.getLocation(), EntityType.ZOGLIN);
                    creatureSpawnEvent.setCancelled(true);
                    return;
                }
                if (pigZombie.isAdult()) {
                    setKnockbackResistanceMin(pigZombie, ZOMBIE_PIGLIN_KNOCKBACK_RESISTANCE);
                } else {
                    final EntityEquipment ee = pigZombie.getEquipment();
                    if (ee != null) {
                        ee.setItemInMainHand(MineReUtils.setItemDamage(new ItemStack(Material.GOLDEN_HOE, 1), 10));
                        ee.setItemInMainHandDropChance(ITEM_DROP_CHANCE);
                    }
                }
                return;
            }

            //Magma Cube
            if (entity instanceof final MagmaCube magmaCube) {
                if (!creatureSpawnEvent.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) && !creatureSpawnEvent.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
                    if ((MineReUtils.rollChance(MAGMA_CUBE_SIZE_INCREASE_CHANCE) && !biome.equals(Biome.BASALT_DELTAS)) || (biome.equals(Biome.BASALT_DELTAS) && MineReUtils.rollChance(MAGMA_CUBE_SIZE_INCREASE_CHANCE_BASALT_DELTA))) {
                        magmaCube.setSize(magmaCube.getSize() + MineReUtils.randomIntInRange(1, MAGMA_CUBE_SIZE_INCREASE_MAX));
                    }
                }
                return;
            }

            //Wither Skeleton
            if (entity instanceof final WitherSkeleton skeleton) {
                setKnockbackResistanceMin(skeleton, WITHER_SKELETON_KNOCKBACK_RESISTANCE);
                setMaxHealth(skeleton, WITHER_SKELETON_HEATLH);
                //Wither Skeleton Bowman
                if (MineReUtils.rollChance(WITHER_SKELETON_BOW_CHANCE)) {
                    Optional.ofNullable(skeleton.getEquipment()).ifPresent(ee -> {
                        //Give bow
                        ee.setItemInMainHand(MineReUtils.setItemDamage(new ItemStack(Material.BOW), 70));
                        ee.setItemInMainHandDropChance(ITEM_DROP_CHANCE);
                        //Tipped arrows
                        if (MineReUtils.rollChance(SKELETON_TIPPED_ARROW_CHANCE * difficultyMultiplier)) {
                            final ItemStack tippedArrows = new ItemStack(Material.TIPPED_ARROW, 2);
                            Optional.ofNullable(tippedArrows.getItemMeta()).ifPresent(itemMeta -> {
                                if (itemMeta instanceof final PotionMeta potionMeta) {
                                    potionMeta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
                                    tippedArrows.setItemMeta(potionMeta);
                                    ee.setItemInOffHand(tippedArrows);
                                    ee.setItemInOffHandDropChance(TIPPED_ARROW_DROP_CHANCE);
                                }
                            });
                        }
                    });
                }
                return;
            }

            //Piglin
            if (entity instanceof final Piglin piglin) {
                if ((entity instanceof final PiglinBrute piglinBrute)) {
                    setKnockbackResistanceMin(piglinBrute, PIGLIN_BRUTE_KNOCKBACK_RESISTANCE);
                } else {
                    setMaxHealth(piglin, PIGLIN_HEALTH);
                }
                return;
            }

            // ----------------- ENDER MOBS ---------------------

            //Enderman
            if (entity instanceof final Enderman enderman) {
                setAttackDamage(enderman, ENDERMAN_ATTACK_DAMAGE);
                //End Phantom and Endermite spawning
                if (environment.equals(World.Environment.THE_END)) {
                    if (biome.equals(Biome.END_HIGHLANDS) || biome.equals(Biome.END_MIDLANDS) || biome.equals(Biome.END_BARRENS)) {
                        if (MineReUtils.rollChance(END_PHANTOM_SPAWN_CHANCE)) {
                            world.spawnEntity(enderman.getLocation().add(4, 18, 4), EntityType.PHANTOM);
                        }
                    }
                    if (biome.equals(Biome.END_HIGHLANDS)) {
                        if (MineReUtils.rollChance(ENDERMITE_REPLACE_CHANCE)) {
                            world.spawnEntity(enderman.getLocation(), EntityType.ENDERMITE);
                            creatureSpawnEvent.setCancelled(true);
                        }
                    }
                }
                return;
            }

            //Endermite
            if (entity instanceof final Endermite endermite) {
                setAttackDamage(endermite, ENDERMITE_ATTACK_DAMAGE);
                if (creatureSpawnEvent.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.ENDER_PEARL)) {
                    creatureSpawnEvent.setCancelled(true);
                }
                return;
            }

            // ------------------- ANIMALS -------------------------

            //Wolf
            if (entity instanceof final Wolf wolf) {
                setMoveSpeed(wolf, WOLF_MOVE_SPEED);
                if (wolf.isTamed()) {
                    setAttackDamage(wolf, WOLF_ATTACK_DAMAGE_TAME);
                    setMaxHealth(wolf, WOLF_HEALTH_TAME);
                } else {
                    setAttackDamage(wolf, WOLF_ATTACK_DAMAGE_WILD);
                    setMaxHealth(wolf, WOLF_HEALTH_WILD);
                }
                return;
            }

            //Polar Bear
            if (entity instanceof final PolarBear polarBear) {
                setMoveSpeed(polarBear, POLAR_BEAR_SPEED);
                setAttackDamage(polarBear, POLAR_BEAR_DAMAGE);
                setMaxHealth(polarBear, POLAR_BEAR_HEALTH);
                setKnockbackResistanceMin(polarBear, POLAR_BEAR_KNOCKBACK_RESISTANCE);
                return;
            }

            //Llama
            if (entity instanceof final Llama llama) {
                if (llama.getStrength() < LLAMA_MIN_STRENGTH) {
                    llama.setStrength(LLAMA_MIN_STRENGTH);
                }
            }

            //Horse
            if (entity instanceof final Horse horse) {
                if (horse.getJumpStrength() < HORSE_MIN_JUMP_STRENGTH) {
                    horse.setJumpStrength(HORSE_MIN_JUMP_STRENGTH);
                }
                Optional.ofNullable(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).ifPresent(movementSpeed -> {
                    if (movementSpeed.getBaseValue() < HORSE_MIN_SPEED) {
                        movementSpeed.setBaseValue(HORSE_MIN_SPEED);
                    }
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void onProjectileLaunch(ProjectileLaunchEvent projectileLaunchEvent) {
        final Projectile projectile = projectileLaunchEvent.getEntity();
        if (projectile.getShooter() instanceof Mob) {
            if (projectile instanceof final AbstractArrow abstractArrow) {
                //Crossbows do extra damage
                if (abstractArrow.isShotFromCrossbow()) {
                    abstractArrow.setDamage(abstractArrow.getDamage() + MOB_CROSSBOW_OFFSET);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public static void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        final World world = damager.getWorld();
        final Entity entity = event.getEntity();
        final double difficultyMultiplier = MineReUtils.getDifficultyMultiplier(world);

        //Ghasts have resistance to arrows
        if (entity instanceof Ghast && damager instanceof Arrow) {
            event.setDamage(event.getDamage() * (1.0f - GHAST_ARROW_RESISTANCE));
        }

        //Players take extra damage from Ghasts
        if (damager instanceof Fireball || damager instanceof Ghast) {
            event.setDamage(event.getDamage() * GHAST_DAMAGE_SCALE);
            if (event.getDamage() > GHAST_FIRE_THRESHOLD && MineReUtils.rollChance(GHAST_FIRE_CHANCE)) {
                entity.setFireTicks(80);
            }
        }

        //Scale shulker damage
        if (damager instanceof Shulker || damager instanceof ShulkerBullet) {
            event.setDamage(SHULKER_DAMAGE * difficultyMultiplier);
        }

        MineReUtils.getLivingEntityFromDamager(damager).ifPresent(damagingEntity -> {
            if (damagingEntity instanceof final Player player && player.getGameMode().equals(GameMode.CREATIVE)) {
                return;
            }
            //Anger endermen when crystals are destroyed
            if (entity instanceof EnderCrystal) {
                final List<Enderman> nearbyEndermen = entity.getNearbyEntities(64, 64, 64).stream().filter(e -> e instanceof Enderman).sorted(Comparator.comparingDouble(e -> e.getLocation().distance(entity.getLocation()))).map(e -> (Enderman) e).collect(Collectors.toList());
                for (int i = 0; i < MineReUtils.difficultyIntGenerator(1, 2, 4, world); i++) {
                    final Enderman enderman = nearbyEndermen.get(i);
                    world.playSound(enderman.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 2, 1);
                    enderman.setTarget(damagingEntity);
                }
            }
            //Anger endermen when shulkers are attacked
            if (entity instanceof final Shulker shulker) {
                if (MineReUtils.rollChance(ENDERMAN_ANGER_CHANCE * difficultyMultiplier)) {
                    shulker.getNearbyEntities(32, 32, 32).stream().filter(e -> e instanceof final Enderman end && end.getTarget() == null).sorted(Comparator.comparingDouble(e -> e.getLocation().distance(entity.getLocation()))).map(e -> (Enderman) e).findFirst().ifPresent(enderman -> {
                        world.playSound(enderman.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 2, 1);
                        enderman.setTarget(damagingEntity);
                    });
                }
            }
            //Anger endermen when dragon is attacked while perching
            if (entity instanceof final EnderDragon dragon && (
                    dragon.getPhase().equals(EnderDragon.Phase.HOVER) ||
                            dragon.getPhase().equals(EnderDragon.Phase.BREATH_ATTACK) ||
                            dragon.getPhase().equals(EnderDragon.Phase.LAND_ON_PORTAL) ||
                            dragon.getPhase().equals(EnderDragon.Phase.LEAVE_PORTAL))
            ) {
                if (MineReUtils.rollChance(ENDERMAN_ANGER_CHANCE * difficultyMultiplier)) {
                    dragon.getNearbyEntities(32, 32, 32).stream().filter(e -> e instanceof final Enderman end && end.getTarget() == null).sorted(Comparator.comparingDouble(e -> e.getLocation().distance(entity.getLocation()))).map(e -> (Enderman) e).findFirst().ifPresent(enderman -> {
                        world.playSound(enderman.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 2, 1);
                        enderman.setTarget(damagingEntity);
                    });
                }
            }

            //Alert nearby mobs when mob is damaged by player
            if (entity instanceof final LivingEntity hitCreature) {
                if (MineReUtils.isPlayerTeam(damagingEntity)) {
                    //Creature hit by something on the player team
                    if (MineReUtils.rollChance(ALERT_CHANCE * difficultyMultiplier)) {
                        final double scaledAlertChanceIndividual = MineReUtils.getDifficultyMultiplier(world) * ALERT_CHANCE_INDIVIDUAL;
                        if (hitCreature instanceof final Raider raider) {
                            raider.getNearbyEntities(ALERT_RANGE, ALERT_RANGE, ALERT_RANGE).stream().filter(e -> e instanceof Raider && MineReUtils.rollChance(scaledAlertChanceIndividual))
                                    .map(e -> (Raider) e).filter(r -> r.getTarget() == null).forEach(r -> r.setTarget(damagingEntity));
                        }
                        if (hitCreature instanceof final Piglin piglin) {
                            piglin.getNearbyEntities(ALERT_RANGE, ALERT_RANGE, ALERT_RANGE).stream().filter(e -> e instanceof Piglin && MineReUtils.rollChance(scaledAlertChanceIndividual))
                                    .map(e -> (Piglin) e).filter(p -> p.getTarget() == null).forEach(p -> p.setTarget(damagingEntity));
                        }
                        if (MineReUtils.isSkeletonTeam(hitCreature)) {
                            hitCreature.getNearbyEntities(ALERT_RANGE, ALERT_RANGE, ALERT_RANGE).stream().filter(e -> MineReUtils.isSkeletonTeam(e) && MineReUtils.rollChance(scaledAlertChanceIndividual))
                                    .map(e -> (Mob) e).filter(m -> m.getTarget() == null).forEach(m -> m.setTarget(damagingEntity));
                        }
                        if (MineReUtils.isSpiderTeam(hitCreature)) {
                            hitCreature.getNearbyEntities(ALERT_RANGE, ALERT_RANGE, ALERT_RANGE).stream().filter(e -> MineReUtils.isSpiderTeam(e) && MineReUtils.rollChance(scaledAlertChanceIndividual))
                                    .map(e -> (Mob) e).filter(m -> m.getTarget() == null).forEach(m -> {
                                        if (!(m instanceof Creeper) || (damagingEntity instanceof Player))
                                            m.setTarget(damagingEntity);
                                    });
                        }
                    }
                } else {
                    //Creature hit by something NOT on the player team
                    if (MineReUtils.isPlayerTeam(hitCreature)) {
                        hitCreature.getNearbyEntities(ALERT_RANGE, ALERT_RANGE, ALERT_RANGE).stream().filter(MineReUtils::isPlayerTeamFightingMob)
                                .map(e -> (Mob) e).filter(m -> m.getTarget() == null).forEach(r -> r.setTarget(damagingEntity));
                    }
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void onEntityDeathEvent(EntityDeathEvent deathEvent) {
        final LivingEntity entity = deathEvent.getEntity();
        Optional.ofNullable(entity.getEquipment()).ifPresent(entityEquipment -> {
            if (entity instanceof AbstractSkeleton && !entityEquipment.getItemInMainHand().getType().equals(Material.BOW)) {
                if (entityEquipment.getItemInMainHand().getType().equals(Material.IRON_SWORD)) {
                    deathEvent.getDrops().removeIf(item -> item.getType().equals(Material.ARROW));
                }
            }
            if (entity instanceof WitherSkeleton && entityEquipment.getItemInMainHand().getType().equals(Material.BOW)) {
                if (entityEquipment.getItemInMainHand().getType().equals(Material.IRON_SWORD)) {
                    deathEvent.getDrops().add(new ItemStack(Material.ARROW, MineReUtils.randomIntInRange(0, 2)));
                }
            }
        });
        if (entity instanceof Enderman) {
            deathEvent.setDroppedExp(ENDERMAN_XP);
        }
        //Giant phantoms drop extra XP and an extra membrane
        if (entity instanceof final Phantom phantom && phantom.getSize() > 1) {
            if (phantom.getSize() > 4) {
                deathEvent.getDrops().add(new ItemStack(Material.PHANTOM_MEMBRANE));
            }
            deathEvent.setDroppedExp(deathEvent.getDroppedExp() + phantom.getSize());
        }
        //Giant magma cubes drop magma blocks and obsidian
        if (entity instanceof final MagmaCube magmaCube && magmaCube.getSize() > 5) {
            deathEvent.setDroppedExp(magmaCube.getSize());
            deathEvent.getDrops().add(new ItemStack(Material.OBSIDIAN, MineReUtils.randomIntInRange(0, magmaCube.getSize() - 5)));
            deathEvent.getDrops().add(new ItemStack(Material.MAGMA_BLOCK, MineReUtils.randomIntInRange(0, magmaCube.getSize() - 4)));
        }
    }

    private static void setMaxHealth(Mob mob, float health) {
        final AttributeInstance attribute = mob.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            attribute.setBaseValue(health);
        }
        mob.setHealth(health);
    }

    private static void setAttackDamage(Mob mob, float attackDamage) {
        final AttributeInstance attribute = mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attribute != null) {
            attribute.setBaseValue(attackDamage);
        }
    }

    private static void setMoveSpeed(Mob mob, float moveSpeed) {
        final AttributeInstance attribute = mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attribute != null) {
            attribute.setBaseValue(moveSpeed);
        }
    }

    private static void setFollowRangeMin(Mob mob) {
        final AttributeInstance attribute = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        if (attribute != null && attribute.getBaseValue() < MobTweaks.DEFAULT_FOLLOW_RANGE) {
            attribute.setBaseValue(MobTweaks.DEFAULT_FOLLOW_RANGE);
        }
    }

    private static void setKnockbackResistanceMin(Mob mob, float knockbackResistance) {
        final AttributeInstance attribute = mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        if (attribute != null && attribute.getBaseValue() < knockbackResistance) {
            attribute.setBaseValue(knockbackResistance);
        }
    }

    private static void addReinforcementChance(Mob mob) {
        final AttributeInstance attributeInstance = mob.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS);
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(attributeInstance.getBaseValue() + MineReUtils.getDifficultyMultiplier(mob.getWorld()) * MobTweaks.ZOMBIE_REINFORCEMENT_CHANCE_INCREASE);
        }
    }

    private static void addRandomEffect(Mob mob) {
        switch (MineReUtils.randomIntInRange(0, 4)) {
            case 0 -> mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 250000, 1));
            case 1 -> {
                if (MineReUtils.isUndead(mob)) {
                    mob.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 250000, 2));
                } else {
                    mob.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 250000, 2));
                }
            }
            case 2 -> mob.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 250000, 1));
            case 3 -> {
                if (mob instanceof Zombie) {
                    mob.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 250000, 1));
                    break;
                }
                if (mob instanceof Spider || mob instanceof Enderman) {
                    mob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 250000, 1));
                }
            }
        }
    }

    private static int getDangerDepth() {
        return JavaPlugin.getPlugin(MineRe.class).getConfig().getInt("dangerDepth");
    }

    private static void replaceGoldArmor(Mob mob) {
        if (MineReUtils.rollChance(0.75)) {
            final Biome biome = mob.getLocation().getBlock().getBiome();
            if (!mob.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                return;
            }
            if (biome.equals(Biome.BADLANDS) || biome.equals(Biome.BADLANDS_PLATEAU) || biome.equals(Biome.ERODED_BADLANDS) || biome.equals(Biome.MODIFIED_BADLANDS_PLATEAU) || biome.equals(Biome.MODIFIED_WOODED_BADLANDS_PLATEAU)) {
                return;
            }
            final boolean useChain = MineReUtils.rollChance(0.3);
            Optional.ofNullable(mob.getEquipment()).ifPresent(entityEquipment -> {
                if (useChain) {
                    if (entityEquipment.getHelmet() != null && entityEquipment.getHelmet().getType().equals(Material.GOLDEN_HELMET)) {
                        entityEquipment.setHelmet(MineReUtils.setItemDamage(new ItemStack(Material.CHAINMAIL_HELMET), 50));
                    }
                    if (entityEquipment.getChestplate() != null && entityEquipment.getChestplate().getType().equals(Material.GOLDEN_CHESTPLATE)) {
                        entityEquipment.setChestplate(MineReUtils.setItemDamage(new ItemStack(Material.CHAINMAIL_CHESTPLATE), 80));
                    }
                    if (entityEquipment.getLeggings() != null && entityEquipment.getLeggings().getType().equals(Material.GOLDEN_LEGGINGS)) {
                        entityEquipment.setLeggings(MineReUtils.setItemDamage(new ItemStack(Material.CHAINMAIL_LEGGINGS), 70));
                    }
                    if (entityEquipment.getBoots() != null && entityEquipment.getBoots().getType().equals(Material.GOLDEN_BOOTS)) {
                        entityEquipment.setBoots(MineReUtils.setItemDamage(new ItemStack(Material.CHAINMAIL_BOOTS), 40));
                    }
                } else {
                    if (entityEquipment.getHelmet() != null && entityEquipment.getHelmet().getType().equals(Material.GOLDEN_HELMET)) {
                        entityEquipment.setHelmet(MineReUtils.setItemDamage(new ItemStack(Material.IRON_HELMET), 50));
                    }
                    if (entityEquipment.getChestplate() != null && entityEquipment.getChestplate().getType().equals(Material.GOLDEN_CHESTPLATE)) {
                        entityEquipment.setChestplate(MineReUtils.setItemDamage(new ItemStack(Material.IRON_CHESTPLATE), 80));
                    }
                    if (entityEquipment.getLeggings() != null && entityEquipment.getLeggings().getType().equals(Material.GOLDEN_LEGGINGS)) {
                        entityEquipment.setLeggings(MineReUtils.setItemDamage(new ItemStack(Material.IRON_LEGGINGS), 70));
                    }
                    if (entityEquipment.getBoots() != null && entityEquipment.getBoots().getType().equals(Material.GOLDEN_BOOTS)) {
                        entityEquipment.setBoots(MineReUtils.setItemDamage(new ItemStack(Material.IRON_BOOTS), 40));
                    }
                }
            });
        }
    }
}
