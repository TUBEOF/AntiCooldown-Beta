package de.tubeof.ac.beta.listener;

import de.tubeof.ac.beta.data.Data;
import de.tubeof.ac.beta.enums.SettingsType;
import de.tubeof.ac.beta.main.Main;
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
