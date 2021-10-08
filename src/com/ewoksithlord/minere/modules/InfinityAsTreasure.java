package com.ewoksithlord.minere.modules;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

public class InfinityAsTreasure implements Listener {

    @EventHandler
    public static void onEnchantment(EnchantItemEvent event) {
        event.getEnchantsToAdd().remove(Enchantment.ARROW_INFINITE);
    }

    @EventHandler
    public static void onEnchantmentPrepare(PrepareItemEnchantEvent event) {
        for (EnchantmentOffer offer : event.getOffers()) {
            if (offer.getEnchantment().equals(Enchantment.ARROW_INFINITE)) {
                offer.setEnchantment(Enchantment.DURABILITY);
                offer.setEnchantmentLevel(3);
            }
        }
    }
}
