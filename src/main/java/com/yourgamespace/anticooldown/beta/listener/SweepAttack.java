package com.yourgamespace.anticooldown.beta.listener;

import com.yourgamespace.anticooldown.beta.data.Data;
import com.yourgamespace.anticooldown.beta.main.Main;
import com.yourgamespace.anticooldown.beta.enums.SettingsType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class SweepAttack implements Listener {

    private final Data data = Main.getData();

    @EventHandler
    public void onSweep(EntityDamageByEntityEvent event) {
        if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return;;
        if(!data.getBooleanSettings(SettingsType.DISABLE_SWEEP_ATTACK)) return;
        event.setCancelled(true);
    }
}
