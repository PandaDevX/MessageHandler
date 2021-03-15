package net.messagehandler.listeners.chat;

import net.messagehandler.MessageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GrammarEvent implements Listener {
    private final MessageHandler plugin;

    public GrammarEvent(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCapitalize(AsyncPlayerChatEvent e) {
        if (!plugin.getConfig().getBoolean("Grammar.Auto Capitalization")) {
            return;
        }
        String message = e.getMessage();
        String s1 = message.substring(0, 1).toUpperCase();
        String capitalized = s1 + message.substring(1);
        message = capitalized;

        if (!plugin.getConfig().getBoolean("Grammar.Grammar Fix")) {
            return;
        }


        String[] word = message.split(" ");
        StringBuilder builder = new StringBuilder();
        byte b;
        int i;
        String[] array;
        for (i = (array = word).length, b = 0; b < i;) {
            String splitWord = array[b];
            if (splitWord.equals("i")) {
                builder.append("I").append(" ");
            } else if (splitWord.equalsIgnoreCase("i'm") || splitWord.equalsIgnoreCase("im")) {
                builder.append("I'm").append(" ");
            } else if (splitWord.equalsIgnoreCase("i'd") || splitWord.equalsIgnoreCase("id")) {
                builder.append("I'd").append(" ");
            } else if (splitWord.equalsIgnoreCase("i've") || splitWord.equalsIgnoreCase("ive")) {
                builder.append("I've").append(" ");
            }else if (splitWord.equalsIgnoreCase("i'll") || splitWord.equalsIgnoreCase("ill")) {
                builder.append("I'll").append(" ");
            }else if (splitWord.equals("cant")) {
                builder.append("can't").append(" ");
            }else if (splitWord.equalsIgnoreCase("couldve")) {
                builder.append("could've").append(" ");
            }else if (splitWord.equalsIgnoreCase("couldnt")) {
                builder.append("couldn't").append(" ");
            }else if (splitWord.equalsIgnoreCase("youre")) {
                builder.append("you're").append(" ");
            }else if (splitWord.equalsIgnoreCase("youve")) {
                builder.append("you've").append(" ");
            }else if (splitWord.equalsIgnoreCase("dont")) {
                builder.append("don't").append(" ");
            }else if (splitWord.equalsIgnoreCase("whos")) {
                builder.append("who's").append(" ");
            }else if (splitWord.equalsIgnoreCase("alot")) {
                builder.append("a lot").append(" ");
            }else if (splitWord.equalsIgnoreCase("theyve")) {
                builder.append("they've").append(" ");
            }else if (splitWord.equalsIgnoreCase("theyre")) {
                builder.append("they're").append(" ");
            }else if (splitWord.equalsIgnoreCase("thats")) {
                builder.append("that's").append(" ");
            }else {
                builder.append(splitWord).append(" ");
            }
            b++;
        }

        char[] chars = e.getMessage().toCharArray();
        String str = String.valueOf(message.charAt(chars.length - 1));
        if (!(new StringBuilder(str)).toString().equals("!") &&
                !(new StringBuilder(str)).toString().equals(".") &&
                !(new StringBuilder(str)).toString().equals(",") &&
                !(new StringBuilder(str)).toString().equals("?"))
            message = message + ".";
        e.setMessage(builder.toString());
    }
}
