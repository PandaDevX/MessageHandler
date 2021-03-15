package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatChannel implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onChatMessage(AsyncPlayerChatEvent e) {
        if(!new User(e.getPlayer()).isInChannel()) return;

        User sender = new User(e.getPlayer());
        if(e.getMessage().equals("LEAVE " + sender.getChannel())) {
            sender.removeChannel();
            sender.sendTitle("&2&lGroups:&eSuccessfully left the channel");
            e.setCancelled(true);
            Utility.reloadNameTag(MessageHandler.getInstance());
            return;
        }

        for(Player player : e.getRecipients()) {
            User recipient = new User(player);

            if(sender.isInChannel()) {
                if(recipient.isInChannel()) {

                    if(recipient.isSpying("chat")) {
                        continue;
                    }
                    if(!sender.getChannel().equals(recipient.getChannel())) {
                        e.getRecipients().remove(recipient.getPlayer());
                    }
                }
            }
        }
    }
}
