package net.messagehandler.listeners.inventory.email.listeners;

import net.messagehandler.listeners.inventory.email.EmailSend;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EmailAttachmentListener implements Listener {

    @EventHandler
    public void onAttachment(InventoryClickEvent e) {
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lEmail Attachment"))) return;

        if(e.getSlot() != e.getInventory().getSize() - 1) return;
        User user = new User((Player) e.getWhoClicked());
        switch (e.getClick()) {
            case LEFT:
                ItemStack[] items = new ItemStack[e.getInventory().getSize() - 1];
                for(int i = 0; i < items.length; i++) {
                    items[i] = e.getInventory().getItem(i);
                }
                DataManager.emailAttachments.put(user.getUuid(), items);
                user.sendTitle("&2&lEmail:&eYou successfully added item, wait a moment to setup");
                e.setCancelled(true);
                user.getPlayer().closeInventory();
                EmailSend send = new EmailSend(user);
                send.setup();
                send.openEmail();
                break;
            case SHIFT_LEFT:
                user.sendTitle("&2&lEmail:&eYou successfully cancel item adding attachments, wait a moment to get you back");
                e.setCancelled(true);
                user.getPlayer().closeInventory();
                for(int i = 0; i < e.getInventory().getSize()-1;i++) {
                    if(e.getInventory().getItem(i) == null) continue;
                    user.getPlayer().getInventory().addItem(e.getInventory().getItem(i));
                }
                DataManager.emailAttachments.remove(user.getUuid());
                EmailSend emailSend = new EmailSend(user);
                emailSend.setup();
                emailSend.openEmail();
                break;
        }
    }
}
