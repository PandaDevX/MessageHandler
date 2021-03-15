package net.messagehandler.listeners.inventory.ticket.listeners;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TicketCreateListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        FileUtil fileUtil = new FileUtil(MessageHandler.getInstance(), "tickets.yml", FileUtilType.DATA);
        User user = new User(e.getPlayer());
        if(DataManager.ticketCategory.isEmpty()) return;
        if(!DataManager.ticketCategory.containsKey(user.getUuid())) return;
        if(e.getMessage().toLowerCase().contains("cancel")) {
            DataManager.ticketCategory.remove(user.getUuid());
            user.sendTitle(Utility.getPrefix() + ":&bSuccessfully cancelled creating a ticket");
            e.setCancelled(true);
            return;
        }
        DataManager.ticketContent.put(user.getUuid(), e.getMessage().trim());
        FileConfiguration config = fileUtil.get();
        String id = Utility.createRandomID();
        if(config.get("tickets") != null) {
                for(String ticketID : config.getConfigurationSection("tickets").getKeys(false)) {
                    if(id.equals(ticketID)) {
                        id = Utility.createRandomID();
                    }
                }
        }
        config.set("tickets." + id + ".owner.name", user.getName());
        config.set("tickets." + id + ".owner.uuid", user.getUuid().toString());
        config.set("tickets." + id + ".category", DataManager.ticketCategory.get(user.getUuid()));
        config.set("tickets." + id + ".content", DataManager.ticketContent.get(user.getUuid()));
        config.set("tickets." + id + ".status", "Open");
        config.set("tickets." + id + ".date", Utility.getMonthToday() + " " + Utility.getDateToday());
        config.set("tickets." + id + ".location", user.getPlayer().getLocation());
        user.sendTitle(Utility.getPrefix() + ":&bSuccessfully created a new ticket");
        fileUtil.save();
        DataManager.ticketCategory.remove(user.getUuid());
        DataManager.ticketContent.remove(user.getUuid());
        e.setCancelled(true);
    }
}
