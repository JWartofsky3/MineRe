package com.ewoksithlord.minere.runnables;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SetHungerRatesDelayed extends BukkitRunnable {

    final Player player;
    final int delay;

    public SetHungerRatesDelayed(Player player, int delay) {
        this.player = player;
        this.delay = delay;
    }

    @Override
    public void run() {
        player.setSaturatedRegenRate(delay);
        player.setUnsaturatedRegenRate(delay);
    }
}
