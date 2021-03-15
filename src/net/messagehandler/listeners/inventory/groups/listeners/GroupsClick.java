package net.messagehandler.listeners.inventory.groups.listeners;

import net.messagehandler.listeners.inventory.MenuClick;
import net.messagehandler.listeners.inventory.groups.GroupChat;
import net.messagehandler.listeners.inventory.groups.GroupClick;
import net.messagehandler.listeners.inventory.groups.Groups;
import net.messagehandler.listeners.inventory.groups.PersonalGroups;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GroupsClick implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lGroups"))) return;

        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;
        if(e.getClickedInventory() == null) return;
        int page = Integer.parseInt(Utility.stripColor(e.getClickedInventory().getItem(4).getItemMeta().getLore().get(0).split(" ")[1]));

        String name = e.getCurrentItem().getItemMeta().getDisplayName();
        String finalName = Utility.stripColor(name);
        User user = new User((Player) e.getWhoClicked());
        if(finalName.equals("Groups")) {
            e.setCancelled(true);
            return;
        }
        if(finalName.equals("Back")) {
            e.setCancelled(true);
            GroupChat groupChat = new GroupChat(user);
            groupChat.setup();
            groupChat.open();
            return;
        }
        if(finalName.equals("Next")) {
            if((GroupChatClick.groups.getMaxPage()-1) == page) {
                e.getClickedInventory().setItem(e.getSlot(), null);
            }
            GroupChatClick.groups.setup(page+1);
            return;
        }
        if(finalName.equals("Prev")) {
            if(page == 2) {
                e.getClickedInventory().setItem(e.getSlot(), null);
            }
            GroupChatClick.groups.setup(page-1);
            return;
        }
        GroupClick groupClick = new GroupClick(user);
        DataManager.groupClicked.put(user.getUuid(), finalName);
        groupClick.setup();
        groupClick.open();
        e.setCancelled(true);
    }

    @EventHandler
    public void onClickP(InventoryClickEvent e) {
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lYour Groups"))) return;

        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;
        if(e.getClickedInventory() == null) return;
        int page = Integer.parseInt(Utility.stripColor(e.getClickedInventory().getItem(4).getItemMeta().getLore().get(0).split(" ")[1]));
        String name = e.getCurrentItem().getItemMeta().getDisplayName();
        String finalName = Utility.stripColor(name);
        User user = new User((Player) e.getWhoClicked());
        if(finalName.equals("Groups")) {
            e.setCancelled(true);
            return;
        }
        if(finalName.equals("Back")) {
            e.setCancelled(true);
            GroupChat groupChat = new GroupChat(user);
            groupChat.setup();
            groupChat.open();
            return;
        }
        if(finalName.equals("Next")) {
            if((GroupChatClick.personalGroups.getMaxPage()-1) == page) {
                e.getClickedInventory().setItem(e.getSlot(), null);
            }
            GroupChatClick.personalGroups.setup(page+1);
            return;
        }
        if(finalName.equals("Prev")) {
            if(page == 2) {
                e.getClickedInventory().setItem(e.getSlot(), null);
            }
            GroupChatClick.groups.setup(page-1);
            return;
        }
        GroupClick groupClick = new GroupClick(user);
        DataManager.groupClicked.put(user.getUuid(), finalName);
        groupClick.setup();
        groupClick.open();
        e.setCancelled(true);
    }
}
