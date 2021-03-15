package net.messagehandler.listeners.inventory.words;

import net.messagehandler.listeners.inventory.MainPage;
import net.messagehandler.listeners.inventory.MenuClick;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BannedWordsListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lBanned Words"))) return;
        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;
        int page = 0;
        if(e.getSlot() == 4) {
            page = Integer.parseInt(Utility.stripColor(e.getClickedInventory().getItem(4).getItemMeta().getLore().get(0)));
        }

        String finalName = Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        switch (finalName) {
            case "Next":
                if((MenuClick.bannedWords.getMaxPage() - 1) == page) {
                    e.getClickedInventory().setItem(e.getSlot(), null);
                }
                MenuClick.bannedWords.setup(page + 1);
                break;
            case "Prev":
                if(page == 2) {
                    e.getClickedInventory().setItem(e.getSlot(), null);
                }
                MenuClick.bannedWords.setup(page - 1);
                break;
            case "Back":
                MainPage mainPage = new MainPage();
                mainPage.setup((Player) e.getWhoClicked());
                e.getWhoClicked().openInventory(mainPage.getInventory());
                break;
        }

        e.setCancelled(true);
    }
}
