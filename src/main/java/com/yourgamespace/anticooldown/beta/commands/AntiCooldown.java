package com.yourgamespace.anticooldown.beta.commands;

import com.yourgamespace.anticooldown.beta.data.Data;
import com.yourgamespace.anticooldown.beta.data.Messages;
import com.yourgamespace.anticooldown.beta.enums.MessageType;
import com.yourgamespace.anticooldown.beta.enums.SettingsType;
import com.yourgamespace.anticooldown.beta.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AntiCooldown implements CommandExecutor {

    private final Messages messages = Main.getMessages();
    private final Data data = Main.getData();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§cYou have to be a player!");
            return true;
        }
        Player player = (Player) commandSender;
        if(args.length > 2) {
            player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown");
            player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown help");
            player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown listDisabledWorlds");
            player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown enableWorld [<World>]");
            player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown disableWorld [<World>]");
            return true;
        }

        //Default Plugin Message
        if(args.length == 0) {
            player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§3AntiCooldown §afrom §eTUBEOF §ais running on this server.");
            player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§eDownload §anow for §efree:§6 https://www.spigotmc.org/resources/51321/");
            return true;
        }

        //Admin Area
        if(!(player.hasPermission("anticooldown.settings"))) {
            player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.ERROR_NO_PERMISSIONS));
            return true;
        }

        String arg = args[0];

        if(args.length == 1) {
            if(arg.equalsIgnoreCase("help")) {
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown help");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown listDisabledWorlds");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown enableWorld [<World>]");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown disableWorld [<World>]");
                return true;
            }
            else if(arg.equalsIgnoreCase("listDisabledWorlds")) {
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§aDisabled Worlds:");
                for(String world : data.getDisabledWorlds()) {
                    player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e" + world);
                }
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7##### §cEND OF LIST §7#####");
                return true;
            }
            else if(arg.equalsIgnoreCase("enableWorld")) {
                String world = player.getLocation().getWorld().getName();
                if(!(data.isWorldDisabled(world))) {
                    player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.ERROR_WORLD_NOT_LISTED));
                    return true;
                }

                data.enableWorld(world);
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.SETTING_ADD_DISABLED_WORLD).replace("%world%", world));

                World bukkitWorld = Bukkit.getWorld(world);
                if(bukkitWorld == null) return true;
                for(Player worldPlayer : bukkitWorld.getPlayers()) {
                    worldPlayer.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(data.getIntegerSettings(SettingsType.ATTACK_SPEED_VALUE));
                }
                return true;
            }
            else if(arg.equalsIgnoreCase("disableWorld")) {
                String world = player.getLocation().getWorld().getName();
                if(data.isWorldDisabled(world)) {
                    player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.ERROR_WORLD_ALRADY_LISTED));
                    return true;
                }

                data.disabledWorld(world);
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.SETTING_REMOVE_DISABLED_WORLD).replace("%world%", world));

                World bukkitWorld = Bukkit.getWorld(world);
                if(bukkitWorld == null) return true;
                for(Player worldPlayer : bukkitWorld.getPlayers()) {
                    worldPlayer.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
                }
                return true;
            }
            else {
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§cWrong usage!");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown help");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown listDisabledWorlds");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown enableWorld [<World>]");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown disableWorld [<World>]");
                return true;
            }
        }

        String world = args[1];

        if(args.length == 2) {
            if(arg.equalsIgnoreCase("enableWorld")) {
                if(!(data.isWorldDisabled(world))) {
                    player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.ERROR_WORLD_NOT_LISTED));
                    return true;
                }

                data.enableWorld(world);
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.SETTING_REMOVE_DISABLED_WORLD).replace("%world%", world));

                World bukkitWorld = Bukkit.getWorld(world);
                if(bukkitWorld == null) return true;
                for(Player worldPlayer : bukkitWorld.getPlayers()) {
                    worldPlayer.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(data.getIntegerSettings(SettingsType.ATTACK_SPEED_VALUE));
                }
                return true;
            }
            else if(arg.equalsIgnoreCase("disableWorld")) {
                if(data.isWorldDisabled(world)) {
                    player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.ERROR_WORLD_ALRADY_LISTED));
                    return true;
                }

                data.disabledWorld(world);
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + messages.getTextMessage(MessageType.SETTING_ADD_DISABLED_WORLD).replace("%world%", world));

                World bukkitWorld = Bukkit.getWorld(world);
                if(bukkitWorld == null) return true;
                for(Player worldPlayer : bukkitWorld.getPlayers()) {
                    worldPlayer.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
                }
                return true;
            }
            else {
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§cWrong usage!");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown help");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown listDisabledWorlds");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown enableWorld [<World>]");
                player.sendMessage(messages.getTextMessage(MessageType.PREFIX) + "§7> §e/anticooldown disableWorld [<World>]");
                return true;
            }
        }
        return true;
    }
}
