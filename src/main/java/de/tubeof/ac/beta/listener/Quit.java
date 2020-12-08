package de.tubeof.ac.beta.listener;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getBaseValue() != 4)
            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
    }
}
