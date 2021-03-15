package net.messagehandler.listeners.anvil;

import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilColorListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onEdit(InventoryClickEvent e) {
        if(e.getInventory().getType() == InventoryType.ANVIL && e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            AnvilInventory anvilInventory = (AnvilInventory) e.getInventory();
            if(e.getRawSlot() == 2) {
                String text = anvilInventory.getRenameText();
                text = Utility.colorize(text);
                if(player.hasPermission("messagehandler.anvil.color") || player.hasPermission("messagehandler.anvil.*")) {
                    Utility.formatColor(text);
                }
                if(player.hasPermission("messagehandler.anvil.format") || player.hasPermission("messagehandler.anvil.*")) {
                    Utility.formatString(text);
                }
                ItemStack itemStack = anvilInventory.getItem(2);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(text);
                itemStack.setItemMeta(itemMeta);
            }
        }
    }
}
