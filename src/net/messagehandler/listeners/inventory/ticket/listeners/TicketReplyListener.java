package net.messagehandler.listeners.inventory.ticket.listeners;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class TicketReplyListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        User user = new User(e.getPlayer());
        if(!DataManager.ticketToReply.containsKey(user.getUuid())) return;
        if(Utility.stripColor(e.getMessage().toLowerCase()).contains("cancel")) {
            DataManager.ticketToReply.remove(user.getUuid());
            user.sendTitle(Utility.getPrefix() + ":&bSuccessfully cancel reply to the ticket");
            e.setCancelled(true);
            return;
        }
        FileUtil ticketFile = new FileUtil(MessageHandler.getInstance(), "tickets.yml", FileUtilType.DATA);
        FileConfiguration config = ticketFile.get();
        List<String> replies = config.getStringList("tickets." +
                DataManager.ticketToReply.get(user.getUuid()) + ".replies").isEmpty() ? new ArrayList<>() :
                config.getStringList("tickets." + DataManager.ticketToReply.get(user.getUuid()) + ".replies");
        User replier = new User(Bukkit.getPlayer(DataManager.ticketWhoReply.get(DataManager.ticketToReply.get(user.getUuid()))));
        if(replier.hasPermission("messagehandler.ticket.admin") && !replier.getName().equals(DataManager.ticketWhoReply.get(DataManager.ticketToReply.get(user.getUuid())))) {
            replies.add(DataManager.ticketWhoReply.get(DataManager.ticketToReply.get(user.getUuid())) + "@" + e.getMessage().trim());
            user.sendTitle(Utility.getPrefix() + ":&bSuccessfully added reply");
            e.setCancelled(true);
            config.set("tickets." + DataManager.ticketToReply.get(user.getUuid()) + ".replies", replies);
            ticketFile.save();
            Player target = Bukkit.getPlayerExact(config.getString("tickets." + DataManager.ticketToReply.get(user.getUuid()) + ".owner.name"));
            if(target == null) return;
            User targ = new User(target);
            if(targ.ticket()) {
                targ.sendActionBarMessage("&6You got a new reply to your ticket &e/ticket &6to check");
            }
            DataManager.ticketWhoReply.remove(DataManager.ticketToReply.get(user.getUuid()));
            DataManager.ticketToReply.remove(user.getUuid());
            return;
        }
        replies.add((DataManager.ticketWhoReply.get(DataManager.ticketToReply.get(user.getUuid())) + "," + e.getMessage()).trim());
        user.sendTitle(Utility.getPrefix() + ":&bSuccessfully added reply");
        e.setCancelled(true);
        List<String> admins = config.getStringList("tickets." + DataManager.ticketToReply.get(user.getUuid()) + ".owner.name");
        if(!admins.isEmpty()) {
            for(String admin : admins) {
                Player adm = Bukkit.getPlayerExact(admin);
                if(adm == null) continue;
                User ad = new User(adm);
                if(user.getName().equals(ad.getName())) continue;
                if(!ad.ticket()) continue;
                ad.sendTitle(Utility.getPrefix() + ":&bYou got a reply from ticket assigned to you");
            }
        }
        Player target = Bukkit.getPlayerExact(config.getString("tickets." + DataManager.ticketToReply.get(user.getUuid()) + ".owner.name"));
        if(target == null) return;
        User targ = new User(target);
        if(targ.ticket()) {
            targ.sendActionBarMessage("&6You got a new reply to your ticket &e/ticket &6to check");
        }
        config.set("tickets." + DataManager.ticketToReply.get(user.getUuid()) + ".replies", replies);
        ticketFile.save();
        DataManager.ticketWhoReply.remove(DataManager.ticketToReply.get(user.getUuid()));
        DataManager.ticketToReply.remove(user.getUuid());
    }
}
