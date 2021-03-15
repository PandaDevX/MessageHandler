package net.messagehandler.messaging;

import net.messagehandler.MessageHandler;
import org.bukkit.ChatColor;

public class MessageFormatter {

    private final MessageFile messageFile;

    public MessageFormatter() {
        this.messageFile = new MessageFile("settings/messages.yml");
    }

    public String format(String key, Object... args) {
        return format(MessageHandler.getInstance().getConfig().getBoolean("enable-prefix"), key, args);
    }


    public String format(boolean prefix, String key, Object... args) {
        String message = prefix ? messageFile.get("prefix") + messageFile.get(key) : messageFile.get(key);
        for (int i = 0; i < args.length; i++)
            message = message.replace("{" + i + "}", String.valueOf(args[i]));
        return ChatColor.translateAlternateColorCodes('&', message);
    }


    public String prefix(String msg) {
        return ChatColor.translateAlternateColorCodes('&', messageFile.get("prefix") + msg);
    }

    public MessageFile getMessageFile() {
        return messageFile;
    }

}