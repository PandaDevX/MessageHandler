package net.messagehandler.listeners.inventory.email.listeners;

import net.messagehandler.MessageHandler;
import net.messagehandler.listeners.inventory.email.EmailAttachment;
import net.messagehandler.listeners.inventory.email.EmailLogo;
import net.messagehandler.listeners.inventory.email.EmailSend;
import net.messagehandler.listeners.inventory.email.EmailViewer;
import net.messagehandler.utility.DataManager;
import net.messagehandler.utility.Email;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class EmailSendListener implements Listener {
    public static EmailLogo logo;

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(Utility.colorize("&6&lEmail Send"))) {
            return;
        }
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getItemMeta() == null) return;
        if (Utility.getItemName(e.getCurrentItem()) == null) return;
        ItemStack itemStack = e.getCurrentItem();
        String name = Utility.getItemName(itemStack);
        Player player = (Player) e.getWhoClicked();
        User user = new User(player);

        switch (ChatColor.stripColor(name)) {
            case "Recipient":
                user.getPlayer().closeInventory();
                user.sendTitle(Utility.getPrefix() + ":&bInput recipient or type &6cancel &bto cancel", 10, 36000, 20);
                DataManager.emailClick.put(user.getUuid(), "Recipient");
                break;
            case "Logo":
                logo = new EmailLogo(user);
                logo.setup(1);
                logo.openInventory();
                break;
            case "Message":
                if(e.getClick() == ClickType.SHIFT_LEFT) {
                    DataManager.emailClick.put(user.getUuid(), "Subject");
                    user.getPlayer().closeInventory();
                    user.sendTitle(Utility.getPrefix() + ":&bInput subject or type &6cancel &bto cancel");
                    break;
                }
                user.getPlayer().closeInventory();
                user.sendTitle(Utility.getPrefix() + ":&bInput message or type &6cancel &bto cancel", 10, 36000, 20);
                DataManager.emailClick.put(user.getUuid(), "Message");
                break;
            case "Attachment":
                EmailAttachment emailAttachment = new EmailAttachment(user);
                emailAttachment.setup();
                emailAttachment.open();
                break;
            case "Submit":
                if(e.getClick() == ClickType.SHIFT_LEFT) {
                    DataManager.emailAttachments.remove(user.getUuid());
                    DataManager.emailMessage.remove(user.getUuid());
                    DataManager.emailBlock.remove(user.getUuid());
                    DataManager.emailClick.remove(user.getUuid());
                    DataManager.mailRecipient.remove(user.getUuid());
                    DataManager.emailSubject.remove(user.getUuid());
                    user.getPlayer().closeInventory();
                    user.sendTitle("&2&lEmail>>:&eSuccessfully cancel the creation of email");
                }else {
                    if(DataManager.emailSubject.get(user.getUuid()) == null || DataManager.emailMessage.get(user.getUuid())== null
                    || DataManager.mailRecipient.get(user.getUuid()) == null) {
                        e.getClickedInventory().setItem(e.getSlot(), Utility.createGUIItem("&cIncomplete", Material.RED_CONCRETE));
                        e.setCancelled(true);
                        final int slot = e.getSlot();
                        final Inventory clickedInv = e.getClickedInventory();
                        new BukkitRunnable() {
                            public void run() {
                                clickedInv.setItem(slot, Utility.createGUIItem("&b&lSubmit",
                                        Utility.colorizeList(Arrays.asList("", "&7DESCRIPTION: &fClick to submit email", "", "&5SHIFT CLICK &7To cancel"), user.getPlayer()),
                                        Material.BOOK));
                            }
                        }.runTaskLater(MessageHandler.getInstance(), 60L);
                        return;
                    }
                    Email email = user.composeEmail(DataManager.mailRecipient.get(user.getUuid()), DataManager.emailSubject.get(user.getUuid()),
                            DataManager.emailBlock.get(user.getUuid()),
                            DataManager.emailMessage.get(user.getUuid()),
                            DataManager.emailAttachments.get(user.getUuid()));
                    DataManager.emailSubject.remove(user.getUuid());
                    DataManager.emailAttachments.remove(user.getUuid());
                    DataManager.emailMessage.remove(user.getUuid());
                    DataManager.emailBlock.remove(user.getUuid());
                    DataManager.emailClick.remove(user.getUuid());
                    DataManager.mailRecipient.remove(user.getUuid());
                    if(email.send()) {
                        user.getPlayer().closeInventory();
                        user.sendTitle("&2&lEmail:&eSuccessfuly sent to &6" + email.getRecipient());
                        new BukkitRunnable() {
                            public void run() {
                                EmailViewer viewer = new EmailViewer(user);
                                viewer.setup(email);
                                viewer.open();
                            }
                        }.runTaskLater(MessageHandler.getInstance(), 60L);
                    } else {
                        user.getPlayer().closeInventory();
                        user.sendTitle("&2&lEmail:&eUnsuccessful please wait a while for the reasons");
                        user.sendMessage("&2&lEmail>> &2Collecting errors.");
                        new BukkitRunnable() {
                            public void run() {
                                user.sendMessage("&2&lEmail>> &2Errors");
                                for(String reason : email.getErrors()) {
                                    user.sendMessage("  &b&l( * ) &6" + reason);
                                }
                            }
                        }.runTaskLater(MessageHandler.getInstance(), 60L);
                        if(email.hasAttachments()) {
                            user.getPlayer().getInventory().addItem(email.getAttachments());
                        }
                    }
                }
        }

        e.setCancelled(true);
        ((Player) e.getWhoClicked()).updateInventory();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(!DataManager.emailClick.containsKey(e.getPlayer().getUniqueId())) return;
        if(!DataManager.emailClick.get(e.getPlayer().getUniqueId()).equals("Recipient")) return;
        User user = new User(e.getPlayer());
        DataManager.emailClick.remove(user.getUuid());
        DataManager.mailRecipient.remove(user.getUuid());
        if(e.getMessage().equalsIgnoreCase("cancel")) {
            e.setCancelled(true);
            user.sendTitle(Utility.getPrefix() + ":&bSuccessfully cancelled creating email");
            return;
        }
        user.sendTitle(Utility.getPrefix() + ":&bSuccessfully chosen &6" + e.getMessage() + " &bas recipient");
        EmailSend emailSend = new EmailSend(user);
        DataManager.mailRecipient.put(user.getUuid(), e.getMessage());
        emailSend.setup();
        emailSend.openEmail(20L);
        e.setCancelled(true);

    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e) {
        if(!DataManager.emailClick.containsKey(e.getPlayer().getUniqueId())) return;
        User user = new User(e.getPlayer());
        if(!DataManager.emailClick.get(e.getPlayer().getUniqueId()).equals("Message")) return;
        DataManager.emailMessage.remove(user.getUuid());
        DataManager.emailClick.remove(user.getUuid());
        if(e.getMessage().equalsIgnoreCase("cancel")) {
            e.setCancelled(true);
            user.sendTitle(Utility.getPrefix() + ":&bSuccessfully cancelled creating email");
            return;
        }
        user.sendTitle(Utility.getPrefix() + ":&bSuccessfully chosen a message");
        EmailSend send = new EmailSend(user);
        DataManager.emailMessage.put(user.getUuid(), e.getMessage());
        send.setup();
        send.openEmail(20L);
        e.setCancelled(true);
    }


    @EventHandler
    public void onSubject(AsyncPlayerChatEvent e) {
        if(!DataManager.emailClick.containsKey(e.getPlayer().getUniqueId())) return;
        User user = new User(e.getPlayer());
        if(!DataManager.emailClick.get(e.getPlayer().getUniqueId()).equals("Subject")) return;
        DataManager.emailSubject.remove(user.getUuid());
        DataManager.emailClick.remove(user.getUuid());
        if(e.getMessage().equalsIgnoreCase("cancel")) {
            e.setCancelled(true);
            user.sendTitle(Utility.getPrefix() + ":&bSuccessfully cancelled creating email");
            return;
        }
        if(e.getMessage().contains(" ")) {
            DataManager.emailSubject.put(user.getUuid(), e.getMessage().split(" ")[0]);
            user.sendTitle(Utility.getPrefix() + ":&bSuccessfully chosen a subject");
            EmailSend send = new EmailSend(user);
            send.setup();
            send.openEmail(20L);
            e.setCancelled(true);
            return;
        }
        user.sendTitle(Utility.getPrefix() + ":&bSuccessfully chosen a subject");
        EmailSend send = new EmailSend(user);
        DataManager.emailSubject.put(user.getUuid(), e.getMessage());
        send.setup();
        send.openEmail(20L);
        e.setCancelled(true);
    }


}
