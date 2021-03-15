package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWhisper implements CommandExecutor {
    private final MessageHandler plugin;
    public CommandWhisper(MessageHandler plugin) {
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("messagehandler.whisper")) {
            sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
            return true;
        }
        if(!(sender instanceof Player)) {
            return true;
        }
        User user = new User((Player)sender);
        if(args.length <= 1) {
            user.sendMessage("&bCorrect Argument: &f/whisper <player> <message>");
            return true;
        }
        if(args[0].equals(user.getName())) {
            user.sendMessage("&bYou cannot message yourself");
            return true;
        }
        User target = new User(Bukkit.getPlayer(args[0]));
        if(!target.chat()) {
            user.sendMessage("&cYou cannot message that player");
            return true;
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            builder.append(args[i] + " ");
        }
        user.sendMessage(target, builder.toString());
        return false;
    }
}
