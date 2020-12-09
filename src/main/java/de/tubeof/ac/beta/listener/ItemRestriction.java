package de.tubeof.ac.beta.listener;

import de.tubeof.ac.beta.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemRestriction implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemHold(PlayerItemHeldEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if(item == null) return;

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item == null) return;

    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemPickup(PlayerPickupItemEvent event) {
        if(event.isCancelled()) return;

        Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if(item == null) return;

        }, 1);
    }
}
