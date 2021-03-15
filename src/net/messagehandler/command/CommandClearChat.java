package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandClearChat implements CommandExecutor {
    private final MessageHandler plugin;

    public CommandClearChat(MessageHandler plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("messagehandler.clearchat")) {
            sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
            return true;
        }
        if(args.length == 0) {
            User user = new User((Player) sender);
            for(int i = 0; i < 500; i++) {
                sender.sendMessage(" ");
            }
            user.sendMessage(Utility.getPrefix() + "Chat cleared successfully");
            return true;
        }
        if(args.length == 1) {
            User user = new User((Player) sender);
            if(args[0].equalsIgnoreCase("silent")) {
                for(int i = 0; i < 500; i++) {
                    sender.sendMessage(" ");
                }
                return true;
            }
            if(args[0].equalsIgnoreCase("@a")) {
                if(!sender.hasPermission("messagehandler.clearchat.all")) {
                    sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
                    return true;
                }
                for(Player online : Bukkit.getOnlinePlayers()) {
                    for(int i = 0; i < 500; i++) {
                        online.sendMessage(" ");
                    }
                    online.sendMessage(Utility.colorize(Utility.getPrefix() + "Chat cleared by &6" + sender.getName()));
                }
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                for(int i = 0; i < 500; i++) {
                    sender.sendMessage(" ");
                }
                user.sendMessage(Utility.getPrefix() + "Chat cleared successfully");
                return true;
            }
            if(!sender.hasPermission("messagehandler.clearchat.others")) {
                sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
                return true;
            }
            User targ = new User(target);
            for(int i = 0; i < 500; i++) {
                targ.sendMessage(" ");
            }
            targ.sendMessage(Utility.getPrefix() + "Chat cleared by &6" + user.getName());
            return true;
        }
        return false;
    }
}
