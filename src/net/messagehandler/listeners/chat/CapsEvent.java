package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.Utility;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

public class CapsEvent implements Listener {
    private final MessageHandler plugin;
    HashMap<UUID, Integer> counter = new HashMap<>();

    public CapsEvent(MessageHandler plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onCaps(AsyncPlayerChatEvent e) {
        if(!plugin.getConfig().getBoolean("Anti Caps.Enable")) {
            return;
        }
        int upper = 0;
        int lower = 0;
        if(e.getMessage().toLowerCase().length() >= plugin.getConfig().getInt("Anti Caps.Min Word")) {
            for(int i = 0; i < e.getMessage().length(); i++) {
                if(Character.isLetter(e.getMessage().charAt(i))) {
                    if(Character.isUpperCase(e.getMessage().charAt(i))) {
                        upper++;
                    }else {
                        lower++;
                    }
                }
            }

            if(upper + lower != 0 && upper + lower !=0 && 1.0D * upper / (upper + lower) * 100.0D >= plugin.getConfig().getInt("Anti Caps.Percentage")) {
                e.getPlayer().sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&bYou cannot do that"), 30, 20, 30);
                switch(plugin.getConfig().getString("Anti Caps.Action")) {
                    case "block":
                        e.setCancelled(true);
                        break;
                    default:
                        e.setMessage(e.getMessage().toLowerCase());
                        break;
                }
            }
        }
    }
}
