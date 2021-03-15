package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatListener implements Listener {

    private final MessageHandler plugin;
    public ChatListener(MessageHandler plugin) {
        this.plugin = plugin;
    }

    // Word Replacement
    @EventHandler (priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if(e.getPlayer().hasPermission("messagehandler.antiswear.bypass")) return;
        if(plugin.getConfig().getString("Anti Swear.Option").equalsIgnoreCase("disable")) {
            return;
        }
        if(!plugin.getConfig().getString("Anti Swear.Option").equals("replace")
        && (!plugin.getConfig().getString("Anti Swear.Option").equals("block"))) {
            plugin.getConfig().set("Anti Swear.Option", "block");
            return;
        }
        boolean replaceEnable = plugin.getConfig().getString("Anti Swear.Option").equals("replace");
        boolean blockEnable = plugin.getConfig().getString("Anti Swear.Option").equals("block");
        List<String> wordsToReplace = Utility.getWords();

        if(replaceEnable) {

            List<String> words = Arrays.asList(e.getMessage().split(" "));
            for(int x = 0; x < words.size(); x++) {
                for(int y = 0; y < wordsToReplace.size(); y++) {
                    String findX = words.get(x).toLowerCase();
                    String findY = wordsToReplace.get(y).toLowerCase();
                    if(findX.contains(findY)) {
                        StringBuilder builder = new StringBuilder();
                        for(int z = 0; z < findY.length(); z++) {
                            builder.append(plugin.getConfig().getString("Anti Swear.Replacement Char"));
                        }
                        builder.append("&r");
                        System.out.println(builder.toString());
                        words.set(x, findX.replaceAll(findY, builder.toString()));
                        StringBuilder message = new StringBuilder();
                        for(String s : words)
                            message.append(s).append(" ");
                        e.setMessage(Utility.colorize(message.toString()));
                    }
                }
            }

        } else if(blockEnable) {
            List<String> words = Arrays.asList(e.getMessage().split(" "));

            if(Utility.sensor(words, wordsToReplace, new User(e.getPlayer()))) {
                e.setCancelled(true);
                e.getPlayer().sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&bYou cannot do that"), 30, 20, 30);
            }
        }
    }

}
