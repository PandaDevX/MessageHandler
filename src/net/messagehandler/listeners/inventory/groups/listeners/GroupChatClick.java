package net.messagehandler.listeners.inventory.groups.listeners;

import net.messagehandler.listeners.inventory.MainPage;
import net.messagehandler.listeners.inventory.groups.Groups;
import net.messagehandler.listeners.inventory.groups.PersonalGroups;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class GroupChatClick implements Listener {
    public static Groups groups;
    public static PersonalGroups personalGroups;

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lGroup Chat"))) return;

        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;

        String name = e.getCurrentItem().getItemMeta().getDisplayName();
        String finalName = Utility.stripColor(name);
        User user = new User((Player)e.getWhoClicked() );
        switch (finalName) {
            case "Groups":
                if(!user.hasPermission("messagehandler.groups.groups") || !user.hasPermission("messagehandler.groups.*")) {
                    e.setCancelled(true);
                    Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                    break;
                }
                groups = new Groups(new User((Player) e.getWhoClicked()));
                groups.setup(1);
                groups.open();
                break;
            case "Your Groups":
                if(!user.hasPermission("messagehandler.groups.personal") || !user.hasPermission("messagehandler.groups.*")) {
                    e.setCancelled(true);
                    Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                    break;
                }
                personalGroups = new PersonalGroups(new User((Player) e.getWhoClicked()));
                personalGroups.setup(1);
                personalGroups.open();
                break;
            case "Create":
                if(!user.hasPermission("messagehandler.groups.create") || !user.hasPermission("messagehandler.groups.*")) {
                    e.setCancelled(true);
                    Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                    break;
                }
                DataManager.groupCreators.add(e.getWhoClicked().getUniqueId());
                e.getWhoClicked().closeInventory();
                if(e.getClick() == ClickType.SHIFT_LEFT) {
                    user.sendTitle("&2&lGroup Chat:&eType the name of the group and the password or &ccancel &eto cancel", 10, 36000, 20);
                    break;
                }
                user.sendTitle("&2&lGroup Chat:&eType the name of the group or &ccancel &eto cancel", 10, 36000, 20);
                break;
            case "Back":
                MainPage mainPage = new MainPage();
                mainPage.setup((Player) e.getWhoClicked());
                e.getWhoClicked().openInventory(mainPage.getInventory());
                break;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(!DataManager.groupCreators.contains(e.getPlayer().getUniqueId())) return;
        User user = new User(e.getPlayer());
        DataManager.groupCreators.remove(e.getPlayer().getUniqueId());
        if(e.getMessage().equals("cancel")) {
            user.sendTitle("&2&lGroup Chat:&eYou successfully cancelled the creation");
            e.setCancelled(true);
            return;
        }
        String type = e.getMessage().contains(" ") ? "Private" : "Public";
        if(type.equals("Public")) {
            if(user.createPublicGroup(e.getMessage())) {
                user.sendTitle("&2&lGroup Chat:&eGroup &c" + e.getMessage() + " &esuccessfully created");
            }else {
                user.sendTitle("&2&lGroup Chat:&eGroup &c" + e.getMessage() + " &ealready exist");
            }
            e.setCancelled(true);
            return;
        }
        if(user.createPrivate(e.getMessage().split(" ")[0], e.getMessage().split(" ")[1])) {
            user.sendTitle("&2&lGroup Chat:&eGroup &c" + e.getMessage().split(" ")[0] + " &esuccessfully created");
        }else {
            user.sendTitle("&2&lGroup Chat:&eGroup &c" + e.getMessage().split(" ")[0] + " &ealready exist");
        }
        e.setCancelled(true);
    }
}
