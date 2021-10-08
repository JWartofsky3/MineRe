package com.ewoksithlord.minere;

import com.ewoksithlord.minere.modules.*;
import com.ewoksithlord.minere.runnables.MobBehaviorRunner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MineRe extends JavaPlugin {

    @Override
    public void onEnable() {
        final FileConfiguration config = this.getConfig();
        final PluginManager manager = getServer().getPluginManager();

        config.addDefault("mobTweaks", true);
        config.addDefault("dangerDepth", 16);
        config.addDefault("healthTweaks", true);
        config.addDefault("arrowTweaks", true);
        config.addDefault("witherRevamp", true);
        config.addDefault("enderDragonRevamp", false);
        config.addDefault("breedingNerf", true);
        config.addDefault("restrictPlantDimensions", true);
        config.addDefault("villagerBalancing", true);
        config.options().copyDefaults(true);
        saveConfig();

        if (config.getBoolean("mobTweaks")) {
            manager.registerEvents(new MobTweaks(), this);
            getServer().getWorlds().forEach(world -> {
                new MobBehaviorRunner(world).runTaskTimer(this,50, 50);
            });
        }

        if (config.getBoolean("witherRevamp")) {
            manager.registerEvents(new WitherRevamp(), this);
        }

        if (config.getBoolean("healthTweaks")) {
            manager.registerEvents(new HealthTweaks(), this);
        }

        if (config.getBoolean("arrowTweaks")) {
            manager.registerEvents(new ArrowTweaks(), this);
        }

        if (config.getBoolean("breedingNerf")) {
            manager.registerEvents(new BreedingNerf(), this);
        }

        if (config.getBoolean("restrictPlantDimensions")) {
            manager.registerEvents(new RestrictPlantDimension(), this);
        }

        if (config.getBoolean("villagerBalancing")) {
            manager.registerEvents(new VillagerBalancing(), this);
        }

        getServer().getConsoleSender().sendMessage("MineRe enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("MineRe disabled!");
    }
}
