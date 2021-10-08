package com.ewoksithlord.minere.modules;

import com.ewoksithlord.minere.MineReUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.*;

public class VillagerBalancing implements Listener {

    private static final List<Enchantment> forbiddenEnchantments = Arrays.asList(Enchantment.MENDING, Enchantment.ARROW_INFINITE, Enchantment.SOUL_SPEED);

    @EventHandler
    public static void onAcquireTradeEvent(VillagerAcquireTradeEvent event) {
        final boolean replaceWithChainMail = MineReUtils.rollChance(0.4);
        final MerchantRecipe recipe = event.getRecipe();

        //Replace diamond tools
        if (recipe.getResult().getType().equals(Material.DIAMOND_SWORD)) {
            event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.IRON_SWORD))));
        }
        if (recipe.getResult().getType().equals(Material.DIAMOND_PICKAXE)) {
            event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.IRON_PICKAXE))));
        }
        if (recipe.getResult().getType().equals(Material.DIAMOND_SHOVEL)) {
            event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.IRON_SHOVEL))));
        }
        if (recipe.getResult().getType().equals(Material.DIAMOND_AXE)) {
            event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.IRON_AXE))));
        }
        if (recipe.getResult().getType().equals(Material.DIAMOND_HOE)) {
            event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.IRON_HOE))));
        }

        //Replace diamond armor
        if (recipe.getResult().getType().equals(Material.DIAMOND_HELMET)) {
            if (replaceWithChainMail) {
                event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.CHAINMAIL_HELMET))));
            } else {
                event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.IRON_HELMET))));
            }
        }
        if (recipe.getResult().getType().equals(Material.DIAMOND_CHESTPLATE)) {
            if (replaceWithChainMail) {
                event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.CHAINMAIL_CHESTPLATE))));
            } else {
                event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.IRON_CHESTPLATE))));
            }
        }
        if (recipe.getResult().getType().equals(Material.DIAMOND_LEGGINGS)) {
            if (replaceWithChainMail) {
                event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.CHAINMAIL_LEGGINGS))));
            } else {
                event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.IRON_LEGGINGS))));
            }
        }
        if (recipe.getResult().getType().equals(Material.DIAMOND_HELMET)) {
            if (replaceWithChainMail) {
                event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.CHAINMAIL_HELMET))));
            } else {
                event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(Material.IRON_HELMET))));
            }
        }

        //Remove Mending and Infinity
        final Set<Enchantment> enchantments = recipe.getResult().getEnchantments().keySet();
        if (enchantments.stream().anyMatch(forbiddenEnchantments::contains)) {
            event.setRecipe(createRecipeFrom(recipe, randomEnchantment(new ItemStack(recipe.getResult().getType()))));
        }
    }

    private static MerchantRecipe createRecipeFrom(MerchantRecipe original, ItemStack itemStack) {
        final MerchantRecipe newRecipe = new MerchantRecipe(itemStack, original.getMaxUses());
        newRecipe.setExperienceReward(original.hasExperienceReward());
        newRecipe.setIngredients(original.getIngredients());
        newRecipe.setUses(original.getUses());
        newRecipe.setPriceMultiplier(original.getPriceMultiplier());
        return newRecipe;
    }

    private static ItemStack randomEnchantment(ItemStack item) {
        // Store all possible enchantments for the item
        List<Enchantment> possible = new ArrayList<>();

        // Remove illegal enchantments
        final List<Enchantment> illegalEnchantments = new ArrayList<>(forbiddenEnchantments);

        //Prevent illegal combinations
        final List<Enchantment> weaponEnchantments = new ArrayList<>();
        weaponEnchantments.add(Enchantment.DAMAGE_ALL);
        weaponEnchantments.add(Enchantment.DAMAGE_ARTHROPODS);
        weaponEnchantments.add(Enchantment.DAMAGE_UNDEAD);
        weaponEnchantments.remove(MineReUtils.randomIntInRange(0, 3));
        final List<Enchantment> armorEnchantments = new ArrayList<>();
        armorEnchantments.add(Enchantment.PROTECTION_ENVIRONMENTAL);
        armorEnchantments.add(Enchantment.PROTECTION_FIRE);
        armorEnchantments.add(Enchantment.PROTECTION_EXPLOSIONS);
        armorEnchantments.add(Enchantment.PROTECTION_PROJECTILE);
        armorEnchantments.remove(MineReUtils.randomIntInRange(0, 4));
        illegalEnchantments.addAll(weaponEnchantments);
        illegalEnchantments.addAll(armorEnchantments);
        if (MineReUtils.rollChance(0.5)) {
            illegalEnchantments.add(Enchantment.PIERCING);
        } else {
            illegalEnchantments.add(Enchantment.MULTISHOT);
        }

        // Loop through all enchantemnts
        for (Enchantment ench : Enchantment.values()) {
            // Check if the enchantment can be applied to the item, save it if it can
            if (ench.canEnchantItem(item) && !illegalEnchantments.contains(ench)) {
                possible.add(ench);
            }
        }
        Collections.shuffle(possible);
        // If we have at least one possible enchantment
        if (possible.size() >= 1) {
            for (int i = 0; i < Math.min(possible.size(), 3); i++) {
                Enchantment chosen = possible.get(i);
                // Apply the enchantment with Min(1, maxLevel - belowCap)
                item.addEnchantment(chosen, Math.max(1, chosen.getMaxLevel() - 1));
            }
        }

        // Return the item even if it doesn't have any enchantments
        return item;
    }
}
