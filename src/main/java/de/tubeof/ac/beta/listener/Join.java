package de.tubeof.ac.beta.listener;

import de.tubeof.ac.beta.data.Data;
import de.tubeof.ac.beta.data.Messages;
import de.tubeof.ac.beta.enums.MessageType;
import de.tubeof.ac.beta.enums.SettingsType;
import de.tubeof.ac.beta.main.Main;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {

    private final Data data = Main.getData();
    private final Messages messages = Main.getMessages();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String world = player.getLocation().getWorld().getName();

        if (data.isWorldDisabled(world)) {
            if (data.getBooleanSettings(SettingsType.USE_LOGIN_MESSAGES)) player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.LOGIN_DISABLED));
            return;
        }

        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(data.getIntegerSettings(SettingsType.ATTACK_SPEED_VALUE));
        if (data.getBooleanSettings(SettingsType.USE_LOGIN_MESSAGES)) player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.LOGIN_ENABLED));

        if(player.hasPermission("anticooldown.update") && data.isUpdateAvailable() && data.getBooleanSettings(SettingsType.UPDATE_NOTIFY_INGAME)) {
            player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§aAn update is available! Download now: §ehttps://tubeof.de/anticooldown/lastVersion");
        }
    }
}
