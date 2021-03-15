package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReply implements CommandExecutor {
    private final MessageHandler plugin;
    public CommandReply(MessageHandler plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("messagehandler.whisper")) {
            sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
            return true;
        }
        User user = new User((Player)sender);
        if(args.length == 0) {
            user.sendMessage("&bCorrect Argument: &6/reply <message>");
            return true;
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            builder.append(args[i] + " ");
        }
        user.reply(builder.toString());
        return false;
    }
}
