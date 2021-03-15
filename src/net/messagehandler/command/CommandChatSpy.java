package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChatSpy implements CommandExecutor {
    private final MessageHandler plugin;
    public CommandChatSpy(MessageHandler plugin) {
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("messagehandler.chatspy")) {
            sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
            return true;
        }
        if(sender instanceof Player) {
            User user = new User((Player) sender);
            if(user.isSpying("chat")) {
                user.setSpy("chat", false);
                user.sendTitle("&6CommandSpy:&bYou are no longer spying chats", 40, 20, 40);
                return true;
            }
            user.setSpy("chat", true);
            user.sendTitle("&6CommandSpy:&bYou are now spying chats", 40, 20, 40);
            return true;
        }
        return false;
    }
}
