package com.ewoksithlord.minere.modules;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class RestrictPlantDimension implements Listener {

    @EventHandler
    public static void onGrow(BlockGrowEvent blockGrowEvent) {
        final Block block = blockGrowEvent.getBlock();
        if ((block.getType().equals(Material.NETHER_WART) ||
                block.getType().equals(Material.CRIMSON_FUNGUS) ||
                block.getType().equals(Material.WARPED_FUNGUS) ||
                block.getType().equals(Material.TWISTING_VINES) ||
                block.getType().equals(Material.TWISTING_VINES_PLANT) ||
                block.getType().equals(Material.WEEPING_VINES) ||
                block.getType().equals(Material.WEEPING_VINES_PLANT))
                && !block.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            blockGrowEvent.setCancelled(true);
        }
        if ((block.getType().equals(Material.CHORUS_FLOWER) ||
                block.getType().equals(Material.CHORUS_PLANT) ||
                block.getType().equals(Material.CHORUS_FRUIT)) &&
                !block.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            blockGrowEvent.setCancelled(true);
        }
    }


    @EventHandler
    public static void onStructureGrow(StructureGrowEvent structureGrowEvent) {
        final TreeType species = structureGrowEvent.getSpecies();
        if ((species.equals(TreeType.CRIMSON_FUNGUS) || species.equals(TreeType.WARPED_FUNGUS)) && !structureGrowEvent.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            structureGrowEvent.setCancelled(true);
        }
        if (species.equals(TreeType.CHORUS_PLANT) && !structureGrowEvent.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            structureGrowEvent.setCancelled(true);
        }
    }
}
