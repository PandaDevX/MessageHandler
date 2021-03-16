package net.messagehandler.listeners.inventory.ticket.listeners;

import net.messagehandler.MessageHandler;
import net.messagehandler.listeners.inventory.ticket.TicketMenu;
import net.messagehandler.utility.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class TicketsClick implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().startsWith(Utility.colorize("&6&lTickets"))) {
            return;
        }
        FileUtil fileUtil = new FileUtil(MessageHandler.getInstance(), "tickets.yml", FileUtilType.DATA);
        FileConfiguration config = fileUtil.get();
        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getItemMeta() == null) return;
        if (e.getClickedInventory() == null) return;
        int page = Integer.parseInt(Utility.stripColor(e.getClickedInventory().getItem(4).getItemMeta().getLore().get(0).split(" ")[1]));
        String name = Utility.getItemName(e.getCurrentItem());
        Player player = (Player) e.getWhoClicked();
        User user = new User(player);
        if(Utility.stripColor(name).equalsIgnoreCase("back")) {
            TicketMenu menu = new TicketMenu(user);
            String type = e.getView().getTitle().contains(Utility.colorize("&2(Admin)")) ? "admin" : "player";
            menu.setup(type);
            menu.openInventory();
            return;
        }
        String type = "";
        if(e.getView().getTitle().endsWith(Utility.colorize("&2(Admin)"))) {
            if(Utility.stripColor(e.getView().getTitle()).contains(":")) {
                type = "assign";
            }
            if(Utility.stripColor(e.getView().getTitle()).contains("=")) {
                type = Utility.stripColor(e.getView().getTitle()).endsWith("Open") ? "open" : "close";
            }
        }
        if(Utility.stripColor(name).equalsIgnoreCase("tickets")
        || Utility.stripColor(name).equalsIgnoreCase("empty")) {
            e.setCancelled(true);
            return;
        }
        if(Utility.stripColor(name).equalsIgnoreCase("next")) {
            if((TicketMenuClick.tickets.getMaxPage() -1 )== page) {
                e.getClickedInventory().setItem(e.getSlot(), null);
            }
            if(type.equals("")) {
                TicketMenuClick.tickets.setup(page + 1);
            }else {
                TicketMenuClick.tickets.setup(page + 1, type);
            }
            e.setCancelled(true);
            return;
        }
        if(Utility.stripColor(name).equalsIgnoreCase("prev")) {
            if(page == 2) {
                e.getClickedInventory().setItem(e.getSlot(), null);
            }
            if(type.equals("")) {
                TicketMenuClick.tickets.setup(page - 1);
            }else {
                TicketMenuClick.tickets.setup(page - 1, type);
            }
            e.setCancelled(true);
            return;
        }
        String finalName = Utility.stripColor(name).split(" ")[1];
        switch (e.getClick()) {
            case SHIFT_RIGHT:
                if(!user.hasPermission("messagehandler.ticket.admin")) break;
                String status = config.getString("tickets." + finalName + ".status");
                if(status.equals("Open")) {
                    user.sendMessage(Utility.getPrefix() + "Ticket has been closed");
                    config.set("tickets." + finalName + ".status", "Close");
                }else {
                    user.sendMessage(Utility.getPrefix() + "Ticket has been open");
                    config.set("tickets." + finalName + ".status", "Open");
                }
                fileUtil.save();
                TicketMenu ticketMenu = new TicketMenu(user);
                ticketMenu.setup("admin");
                ticketMenu.openInventory();
                break;
            case SHIFT_LEFT:
                if(e.getView().getTitle().contains(Utility.colorize("&2(Admin)"))
                && !(user.getUuid().toString().equals("tickets." + finalName + ".owner.uuid"))) {
                    List<String> list = config.getStringList("tickets." + finalName + ".assigned");
                    if(list.contains(user.getName())) {
                        user.getPlayer().closeInventory();
                        user.sendTitle("&2&lTicketManager:&cYou are already assigned to this ticket");
                        break;
                    }
                    if(list.isEmpty()) {
                        list = new ArrayList<>();
                        list.add(user.getName());
                        config.set("tickets." + finalName + ".assigned", list);
                    }else {
                        list.add(user.getName());
                        config.set("tickets." + finalName + ".assigned", list);
                    }
                    fileUtil.save();
                    user.sendTitle("&2&lTicketManager:&eSuccessfully assigned to ticket &6" + finalName);
                }
                user.getPlayer().closeInventory();
                user.getPlayer().teleport(config.getLocation("tickets." + finalName + ".location"));
                break;
            case RIGHT:
                user.getPlayer().closeInventory();
                if(config.getString("tickets." + finalName + ".status").equals("Close")) {
                    user.sendMessage(Utility.getPrefix() + "You cannot reply to this ticket anymore");
                    break;
                }
                user.sendTitle(Utility.getPrefix() + ":&bPut your reply or type cancel to cancel", 10, 36000, 20);
                DataManager.ticketToReply.put(user.getUuid(), Utility.stripColor(name).split(" ")[1]);
                DataManager.ticketWhoReply.put(Utility.stripColor(name).split(" ")[1], user.getName());
                break;
            default:
                user.getPlayer().closeInventory();
                String message = config.getString("tickets." + finalName + ".content");
                user.sendMessage("&2&lISSUE>> &e" + message);
                if(config.getStringList("tickets." + finalName + ".replies").isEmpty()) break;
                user.sendMessage("&2&lID>> &e" + finalName);
                user.sendMessage("&2&lReplies:\n");
                for(String msg : config.getStringList("tickets." + finalName + ".replies")) {
                    if(msg.contains("@")) {
                        user.sendMessage("&6&l    ADMIN &r" + msg.split("@")[1] + " &7Replied by: &e" + msg.split("@")[0]);
                        continue;
                    }
                    user.sendMessage("&e&l    USER &r" + msg.split(",")[1] + " &7Replied by: &e" + msg.split(",")[0]);
                }
                if(!config.getStringList("tickets." + finalName  + ".assigned").isEmpty()) {
                    user.sendMessage("&2&lAssigned Admins:");
                    for (String admin : config.getStringList("tickets." + finalName + ".assigned")) {
                        user.sendMessage(" &6&lADMIN &e" + admin);
                    }
                }

                break;
        }
        e.setCancelled(true);
    }
}
