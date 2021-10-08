package com.ewoksithlord.minere.modules;

import com.ewoksithlord.minere.MineRe;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class BreedingNerf implements Listener {

    final static private Plugin plugin = MineRe.getProvidingPlugin(MineRe.class);

    @EventHandler
    public static void onEnterLove(EntityEnterLoveModeEvent loveModeEvent) {
        final Animals animals = loveModeEvent.getEntity();
        final int foodEaten = animals.getMetadata("FoodEaten").stream()
                .filter(metadataValue -> Objects.equals(metadataValue.getOwningPlugin(), plugin))
                .findFirst().map(MetadataValue::asInt).orElse(0) + 1;
        if (foodEaten < getFoodNeededForAnimal(animals)) {
            final World world = animals.getWorld();
            world.playSound(animals.getLocation(), Sound.BLOCK_ROOTED_DIRT_PLACE, 0.5f, 1f);
            animals.getWorld().playEffect(animals.getLocation(), Effect.SMOKE, 5);
            animals.setMetadata("FoodEaten", new FixedMetadataValue(plugin, foodEaten));
            loveModeEvent.setCancelled(true);
        } else {
            animals.setMetadata("FoodEaten", new FixedMetadataValue(plugin, 0));
        }
    }

    private static int getFoodNeededForAnimal(Animals animal) {
        if (animal instanceof Cow || animal instanceof Sheep || animal instanceof Chicken || animal instanceof Pig || animal instanceof Goat) {
            return 3;
        }
        if (animal instanceof Hoglin || animal instanceof Strider || animal instanceof Wolf) {
            return 2;
        }
        return 1;
    }
}