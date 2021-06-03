package com.yourgamespace.anticooldown.beta.listener;

import com.yourgamespace.anticooldown.beta.data.Data;
import com.yourgamespace.anticooldown.beta.data.Messages;
import com.yourgamespace.anticooldown.beta.main.Main;
import com.yourgamespace.anticooldown.beta.enums.MessageType;
import com.yourgamespace.anticooldown.beta.enums.SettingsType;
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