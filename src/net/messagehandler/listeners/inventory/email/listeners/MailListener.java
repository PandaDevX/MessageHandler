package net.messagehandler.listeners.inventory.email.listeners;

import net.messagehandler.MessageHandler;
import net.messagehandler.listeners.inventory.email.EmailInbox;
import net.messagehandler.listeners.inventory.MainPage;
import net.messagehandler.listeners.inventory.email.EmailSend;
import net.messagehandler.utility.FileUtil;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MailListener implements Listener {

    public static EmailInbox inbox;

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!e.getView().getTitle().equals(Utility.colorize("&6&lEmail"))) {
            return;
        }
        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getItemMeta() == null) return;
        if(Utility.getItemName(e.getCurrentItem()) == null) return;
        if(e.getClickedInventory() == null) return;
        ItemStack itemStack = e.getCurrentItem();
        String name = Utility.getItemName(itemStack);
        Player player = (Player) e.getWhoClicked();
        User user = new User(player);

        switch (ChatColor.stripColor(name)) {

            case "Send":
                if(!user.hasPermission("messagehandler.email.send") || !user.hasPermission("messagehandler.email.*")) {
                    e.setCancelled(true);
                    Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                    break;
                }
                EmailSend emailSend = new EmailSend(user);
                emailSend.setup();
                emailSend.openEmail();
                break;
            case "Inbox":
                if(!user.hasPermission("messagehandler.email.inbox") || !user.hasPermission("messagehandler.email.*")) {
                    e.setCancelled(true);
                    Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                    break;
                }
                inbox = new EmailInbox(user);
                inbox.setup(1);
                inbox.open();
                break;
            case "Clear":
                if(!user.hasPermission("messagehandler.email.clear") || !user.hasPermission("messagehandler.email.*")) {
                    e.setCancelled(true);
                    Utility.sendNoPerm(e.getClickedInventory(), e.getSlot(), e.getCurrentItem());
                    break;
                }
                FileUtil util = new FileUtil(MessageHandler.getInstance(), "playerdata.yml", FileUtilType.DATA);
                if(!user.hasEmptyEmail()) {
                    util.get().set(user.getUuid().toString() + ".mail", null);
                    util.save();
                    user.getPlayer().closeInventory();
                    user.sendActionBarMessage("&aSuccessfully deleted all mails");
                } else {
                    user.getPlayer().closeInventory();
                    user.sendActionBarMessage("&aYou already have empty email");
                }
                break;
            case "Back":
                MainPage page = new MainPage();
                page.setup(user.getPlayer());
                user.getPlayer().openInventory(page.getInventory());
                break;
        }

        e.setCancelled(true);
    }
}
