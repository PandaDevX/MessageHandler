package net.messagehandler.listeners.inventory.players;

import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PreferenceListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;

        String title = Utility.stripColor(e.getView().getTitle());
        if(!title.startsWith("Preference")) return;

        if(!title.endsWith(e.getWhoClicked().getName())) {
            e.setCancelled(true);
            return;
        }

        User user = new User((Player) e.getWhoClicked());

        switch (Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())) {
            case "Chat":
                user.toggleChat();
                break;
            case "Private Message":
                user.togglePrivateMessage();
                break;
            case "Email Notification":
                user.toggleEmailNotification();
                break;
            case "Ticket Notification":
                user.toggleTicketNotification();
                break;
        }
        e.setCancelled(true);
        Preference preference = new Preference(user);
        preference.setup();
        preference.openInventory();
    }
}
