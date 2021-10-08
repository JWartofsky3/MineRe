package com.ewoksithlord.minere.modules;

import com.ewoksithlord.minere.MineRe;
import com.ewoksithlord.minere.MineReUtils;
import com.ewoksithlord.minere.runnables.SetHungerRatesDelayed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;

public class HealthTweaks implements Listener {

    final static double HONEY_HEAL_AMOUNT = 1.0;
    final static double MILK_HEAL_AMOUNT = 4.0;
    final static double SOUP_HEAL_AMOUNT = 4.0;
    final static double RABBIT_STEW_HEAL_AMOUNT = 7.0;
    final static double STANDARD_POTION_HEAL_OFFSET = 2.0;
    final static JavaPlugin plugin = JavaPlugin.getPlugin(MineRe.class);

    @EventHandler
    public static void onPlayerRespawnEvent(PlayerRespawnEvent playerRespawnEvent) {
        setPlayerHungerRates(playerRespawnEvent.getPlayer());
    }

    @EventHandler
    public static void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
        setPlayerHungerRates(playerJoinEvent.getPlayer());
    }

    @EventHandler
    public static void onExhaustion(EntityExhaustionEvent event) {
        if (event.getExhaustionReason().equals(EntityExhaustionEvent.ExhaustionReason.REGEN) || event.getExhaustionReason().equals(EntityExhaustionEvent.ExhaustionReason.DAMAGED)) {
            event.setExhaustion(event.getExhaustion() * 1.5f * (float) MineReUtils.getDifficultyMultiplier(event.getEntity().getWorld()));
        }
    }

    @EventHandler
    public static void onEntityConsumeItem(PlayerItemConsumeEvent playerItemConsumeEvent) {
        final ItemStack item = playerItemConsumeEvent.getItem();
        final Player player = playerItemConsumeEvent.getPlayer();
        final double maxHealth = MineReUtils.getMaxHealth(player);
        if (item.getType().equals(Material.HONEY_BOTTLE)) {
            player.setHealth(Math.min(player.getHealth() + HONEY_HEAL_AMOUNT, maxHealth));
        }
        if (item.getType().equals(Material.MILK_BUCKET)) {
            player.setHealth(Math.min(player.getHealth() + MILK_HEAL_AMOUNT, maxHealth));
        }
        if (item.getType().equals(Material.SUSPICIOUS_STEW) || item.getType().equals(Material.MUSHROOM_STEW) || item.getType().equals(Material.BEETROOT_SOUP)) {
            player.setHealth(Math.min(player.getHealth() + SOUP_HEAL_AMOUNT, maxHealth));
        }
        if (item.getType().equals(Material.RABBIT_STEW)) {
            player.setHealth(Math.min(player.getHealth() + RABBIT_STEW_HEAL_AMOUNT, maxHealth));
        }
        if (item.getType().equals(Material.POTION)) {
            final PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            if (potionMeta != null && potionMeta.getBasePotionData().getType().equals(PotionType.INSTANT_HEAL)) {
                player.setHealth(Math.min(player.getHealth() + STANDARD_POTION_HEAL_OFFSET, maxHealth));
            }
        }
    }

    private static void setPlayerHungerRates(Player player) {
        final int healthRegenDelay = MineReUtils.difficultyIntGenerator(80, 120, 160, player.getWorld());
        new SetHungerRatesDelayed(player, healthRegenDelay).runTaskLater(plugin, 50);
    }
}
