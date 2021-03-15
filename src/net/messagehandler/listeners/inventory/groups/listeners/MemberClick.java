package net.messagehandler.listeners.inventory.groups.listeners;

import net.messagehandler.listeners.inventory.groups.GroupClick;
import net.messagehandler.listeners.inventory.groups.Groups;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.Group;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MemberClick implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lMembers"))) return;
        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;
        if(e.getClickedInventory() == null) return;
        int page = Integer.parseInt(Utility.stripColor(e.getClickedInventory().getItem(4).getItemMeta().getLore().get(0).split(" ")[1]));

        String name = e.getCurrentItem().getItemMeta().getDisplayName();
        String finalName = Utility.stripColor(name);
        User user = new User((Player) e.getWhoClicked());
        String group = DataManager.groupClicked.get(e.getWhoClicked().getUniqueId());

        if(finalName.equals(group)) {
            e.setCancelled(true);
            return;
        }

        if(finalName.equals("Back")) {
            e.setCancelled(true);
            Groups groups = new Groups(user);
            groups.setup(1);
            groups.open();
            DataManager.groupClicked.remove(user.getUuid());
            return;
        }
        if(finalName.equals("Next")) {
            e.setCancelled(true);
            if((GroupClickListener.members.getMaxPage()-1) == page) {
                e.getClickedInventory().setItem(e.getSlot(), null);
            }
            GroupClickListener.members.setup(page+1);
            return;
        }
        if(finalName.equals("Prev")) {
            e.setCancelled(true);
            if(page == 2) {
                e.getClickedInventory().setItem(e.getSlot(), null);
            }
            GroupClickListener.members.setup(page-1);
            return;
        }

        e.setCancelled(true);
        if(e.getClick() == ClickType.SHIFT_LEFT) {
            e.setCancelled(true);
            if(finalName.equals(user.getName())) {
                return;
            }
            Group gp = new Group(group);
            if(!gp.getOwner().equals(user.getName())) {
                return;
            }
            gp.remove(finalName);
            user.getPlayer().closeInventory();
            user.sendTitle("&2&lGroups:&eSuccessfully kicked player &6" + finalName + " &eto the group &6" + group);
            DataManager.groupClicked.remove(user.getUuid());
        }
    }
}
