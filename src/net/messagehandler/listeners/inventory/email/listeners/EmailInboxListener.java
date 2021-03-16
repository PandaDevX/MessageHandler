package net.messagehandler.listeners.inventory.email.listeners;

import net.messagehandler.MessageHandler;
import net.messagehandler.listeners.inventory.email.Mail;
import net.messagehandler.utility.FileUtil;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EmailInboxListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if(e.getCurrentItem() == null) return;
        if(!e.getCurrentItem().hasItemMeta()) return;
        if(!e.getView().getTitle().equalsIgnoreCase(Utility.colorize("&6&lInbox"))) return;
        int page = Integer.parseInt(Utility.stripColor(e.getClickedInventory().getItem(4).getItemMeta().getLore().get(0).split(" ")[1]));

        switch (Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())) {
            case "Back":
                e.setCancelled(true);
                Mail mail = new Mail(new User((Player)e.getWhoClicked()));
                mail.setup();
                mail.openEmail();
                break;
            case "Inbox":
                e.setCancelled(true);
                break;
            case "Next":

                if((MailListener.inbox.getMaxPage()-1) == page) {
                    e.getClickedInventory().setItem(e.getSlot(), null);
                }
                MailListener.inbox.setup(page+1);
                e.setCancelled(true);
                break;
            case "Prev":
                if(page == 2) {
                    e.getClickedInventory().setItem(e.getSlot(), null);
                }
                MailListener.inbox.setup(page-1);
                e.setCancelled(true);
                break;

            default:
                e.setCancelled(true);
                FileUtil util = new FileUtil(MessageHandler.getInstance(), "playerdata.yml", FileUtilType.DATA);
                FileConfiguration playerData = util.get();
                String subject = Utility.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                String id = Utility.stripColor(e.getCurrentItem().getItemMeta().getLore().get(2).split(" ")[1]);
                String from = "";
                StringBuilder builder = new StringBuilder();
                List<ItemStack> itemStackList = new ArrayList<>();
                Set<String> uuids = playerData.getKeys(false);
                for(String uuid : uuids) {
                    if(subject.equals(playerData.getString(uuid + ".mail." + id + ".subject"))) {
                        builder.append(playerData.getString(uuid + ".mail." + id + ".message"));
                        from = playerData.getString(uuid + ".mail." + id + ".sender");
                        if(playerData.get(uuid + ".mail." + id + ".attachments") != null) {
                            for(String key : playerData.getConfigurationSection(uuid + ".mail." + id + ".attachments").getKeys(false)) {
                                itemStackList.add(playerData.getItemStack(uuid + ".mail." + id + ".attachments." + key));
                                playerData.set(uuid + ".mail." + id + ".attachments." + key, null);
                                util.save();
                            }
                        }
                        break;
                    }
                }
                String message = builder.toString();
                String[] array = new String[2];
                if(message.length() > 16) {
                    array[0] = message.substring(0, 16);
                    array[1] = message.substring(16);
                }
                User user = new User((Player) e.getWhoClicked());
                user.sendMessage("&6&lEmail >> &fMessage Viewer");
                user.getPlayer().closeInventory();
                if(array[0] != null || array[1] != null) {
                    user.sendMessage(array[0] + "\n" + array[1]);
                }
                user.sendMessage(message);
                user.sendMessage("&6&lFrom >> &f" + from);
                for(ItemStack itemStack : itemStackList) {
                    user.getPlayer().getInventory().addItem(itemStack);
                }
                break;
        }
    }
}
