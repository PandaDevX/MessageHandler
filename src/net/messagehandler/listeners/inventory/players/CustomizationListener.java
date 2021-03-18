package net.messagehandler.listeners.inventory.players;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CustomizationListener implements Listener {


    @EventHandler
    public void onEdit(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lCustomization"))) return;
        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;
        User user = new User((Player) e.getWhoClicked());
        if(e.getClick() == ClickType.RIGHT) {
            switch(Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())) {
                case "Join Message":
                    if(!user.hasPermission("messagehandler.customize.join") || !user.hasPermission("messagehandler.customize.*")) {
                        e.setCancelled(true);
                        Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                        break;
                    }
                    user.setConfig(user.getUuid().toString() + ".joinMessage", null);
                    user.getPlayer().closeInventory();
                    user.sendActionBarMessage("&aSuccessfully cleared join message");
                    e.setCancelled(true);
                    break;
                case "Join Title":
                    if(!user.hasPermission("messagehandler.customize.join") || !user.hasPermission("messagehandler.customize.*")) {
                        e.setCancelled(true);
                        Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                        break;
                    }
                    user.setConfig(user.getUuid().toString() + ".joinTitle", null);
                    user.getPlayer().closeInventory();
                    user.sendActionBarMessage("&aSuccessfully cleared join title");
                    e.setCancelled(true);
                    break;
                case "NameTag":
                    if(!user.hasPermission("messagehandler.customize.nametag") || !user.hasPermission("messagehandler.customize.*")) {
                        e.setCancelled(true);
                        Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                        break;
                    }
                    user.setConfig(user.getUuid().toString() + ".nametag", null);
                    user.getPlayer().closeInventory();
                    user.sendActionBarMessage("&aSuccessfully cleared name tag");
                    Utility.reloadNameTag(MessageHandler.getInstance());
                    e.setCancelled(true);
                    break;
                case "Quit Message":
                    if(!user.hasPermission("messagehandler.customize.quit") || !user.hasPermission("messagehandler.customize.*")) {
                        e.setCancelled(true);
                        Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                        break;
                    }
                    user.setConfig(user.getUuid().toString() + ".leaveMessage", null);
                    user.getPlayer().closeInventory();
                    user.sendActionBarMessage("&aSuccessfully cleared quit message");
                    e.setCancelled(true);
                    break;
                case "Chat Format":
                    if(!user.hasPermission("messagehandler.customize.chat") || !user.hasPermission("messagehandler.customize.*")) {
                        e.setCancelled(true);
                        Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                        break;
                    }
                    user.getPlayer().closeInventory();
                    user.clearCustomPrefix();
                    user.clearCustomSuffix();
                    user.clearNickName();
                    user.sendActionBarMessage("&aSuccessfully cleared chat custom formats");
                    e.setCancelled(true);
                    break;
            }
        } else {
            switch(Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())) {
                case "Join Message":
                case "Join Title":
                    if(!user.hasPermission("messagehandler.customize.join") || !user.hasPermission("messagehandler.customize.*")) {
                        e.setCancelled(true);
                        Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                        break;
                    }
                case "Quit Message":
                    if(!user.hasPermission("messagehandler.customize.quit") || !user.hasPermission("messagehandler.customize.*")) {
                        e.setCancelled(true);
                        Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                        break;
                    }
                    DataManager.customization.put(e.getWhoClicked().getUniqueId(), Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                    e.setCancelled(true);
                    e.getWhoClicked().closeInventory();
                    user.sendTitle("&6&l" + Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()) + ":&fType your own customized message type &ccancel &fto cancel", 40, 36000, 40);
                    break;
                case "NameTag":
                    if(!user.hasPermission("messagehandler.customize.nametag") || !user.hasPermission("messagehandler.customize.*")) {
                        e.setCancelled(true);
                        Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                        break;
                    }
                    e.setCancelled(true);
                    NameTag nameTag = new NameTag(user);
                    nameTag.setup();
                    nameTag.open();
                    break;
                case "Chat Format":
                    if(!user.hasPermission("messagehandler.customize.chat") || !user.hasPermission("messagehandler.customize.*")) {
                        e.setCancelled(true);
                        Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                        break;
                    }
                    e.setCancelled(true);
                    ChatPrefix chatPrefix = new ChatPrefix(user);
                    chatPrefix.setup();
                    chatPrefix.open();
                    break;
                default:
                    e.setCancelled(true);
                    break;
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(!DataManager.customization.containsKey(e.getPlayer().getUniqueId())) return;
        User user = new User(e.getPlayer());
        if(e.getMessage().toLowerCase().contains("cancel")) {
            e.setCancelled(true);
            DataManager.customization.remove(e.getPlayer().getUniqueId());
            user.sendTitle("&e&lCustomization:&aSuccessfully cancelled customization");
            return;
        }
        e.setCancelled(true);
        switch (DataManager.customization.get(e.getPlayer().getUniqueId())) {
            case "Join Message":
                user.setConfig(user.getUuid().toString() + ".joinMessage", e.getMessage());
                user.sendActionBarMessage("&aSuccessfully changed join message");
                user.sendTitle("&e&l" + DataManager.customization.get(user.getUuid()) + ":" + Utility.colorize(e.getMessage()));
                break;
            case "Quit Message":
                user.setConfig(user.getUuid().toString() + ".leaveMessage", e.getMessage());
                user.sendActionBarMessage("&aSuccessfully changed join message");
                user.sendTitle("&e&l" + DataManager.customization.get(user.getUuid()) + ":" + Utility.colorize(e.getMessage()));
                break;
            case "Join Title":
                user.setConfig(user.getUuid().toString() + ".joinTitle", e.getMessage());
                user.sendActionBarMessage("&aSuccessfully changed join message");
                user.sendTitle(Utility.colorize(e.getMessage()));
                break;
        }
        DataManager.customization.remove(user.getUuid());
    }
}
