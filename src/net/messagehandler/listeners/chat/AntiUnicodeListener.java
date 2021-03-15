package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiUnicodeListener implements Listener {


    @EventHandler
    public void onListen(AsyncPlayerChatEvent e) {
        FileConfiguration config = MessageHandler.getInstance().getConfig();
        if(!config.getBoolean("Anti Unicode.Enable")) return;
        List<String> whitelist = config.getStringList("Anti Unicode.Whitelist");
        Pattern pattern = Pattern.compile("^[A-Za-z0-9-~!@#$%^&*()<>_+=-{}|';:.,\\[\"\"]|';:.,/?><_.]+$");
        Matcher matcher = pattern.matcher(e.getMessage().toLowerCase().replaceAll("\\s+", ""));

        if(!e.getPlayer().hasPermission("messagehandler.antiunicode.bypass")) {
            for(String wl : whitelist) {
                if(e.getMessage().contains(wl.toLowerCase())) return;
            }
            if(!matcher.find()) {
                e.setCancelled(true);
                User user = new User(e.getPlayer());
                user.sendTitle("&2&lAnti Unicode:&eYou cannot do that");
            }
        }
    }
}
