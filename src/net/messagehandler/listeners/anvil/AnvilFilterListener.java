package net.messagehandler.listeners.anvil;

import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;

import java.util.Arrays;

public class AnvilFilterListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChangeName(InventoryClickEvent e) {
        if(e.getInventory().getType() == InventoryType.ANVIL && e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if(player.hasPermission("messagehandler.anvil.filtermsg") || player.hasPermission("messagehandler.anvil.*")) {
                return;
            }
            AnvilInventory anvilInventory = (AnvilInventory) e.getInventory();
            if(e.getRawSlot() == 2) {
                String text = anvilInventory.getRenameText();
                if(Utility.sensor(Arrays.asList(text.split(" ")), Utility.getWords(), new User(player))) {
                    e.setCancelled(true);
                    User user = new User(player);
                    user.getPlayer().closeInventory();
                    user.sendTitle("&2&lAnti Swear:&eYou cannot do that");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChangeNameAD(InventoryClickEvent e) {
        if(e.getInventory().getType() == InventoryType.ANVIL && e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if(player.hasPermission("messagehandler.anvil.filterad") || player.hasPermission("messagehandler.anvil.*")) {
                return;
            }
            AnvilInventory anvilInventory = (AnvilInventory) e.getInventory();
            if(e.getRawSlot() == 2) {
                String text = anvilInventory.getRenameText();
                if(Utility.sensorAd(text, new User(player))) {
                    e.setCancelled(true);
                    User user = new User(player);
                    user.getPlayer().closeInventory();
                    user.sendTitle("&2&lAnti Advertise:&eYou cannot do that");
                }
            }
        }
    }

}
