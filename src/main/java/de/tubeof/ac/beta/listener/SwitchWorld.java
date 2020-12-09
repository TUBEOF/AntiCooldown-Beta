package de.tubeof.ac.beta.listener;

import de.tubeof.ac.beta.data.Data;
import de.tubeof.ac.beta.data.Messages;
import de.tubeof.ac.beta.enums.MessageType;
import de.tubeof.ac.beta.enums.SettingsType;
import de.tubeof.ac.beta.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SwitchWorld implements Listener {

    private final Data data = Main.getData();
    private final Messages messages = Main.getMessages();


    @EventHandler
    public void onWorldTeleport(PlayerTeleportEvent event) {
        if(event.getFrom().getWorld() == event.getTo().getWorld()) return;
        Player player = event.getPlayer();
        String world = event.getTo().getWorld().getName();

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getMain(), () -> {
            if (data.isWorldDisabled(world)) {
                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
                if (data.getBooleanSettings(SettingsType.USE_SWITCH_WORLD_MESSAGES))  player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.SWITCH_WORLD_DISABLED));
            } else {
                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(data.getIntegerSettings(SettingsType.ATTACK_SPEED_VALUE));
                if (data.getBooleanSettings(SettingsType.USE_SWITCH_WORLD_MESSAGES)) player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.SWITCH_WORLD_ENABLED));
            }
        }, 2);
    }
}