package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandIgnore implements CommandExecutor {
    private final MessageHandler plugin;
    public CommandIgnore(MessageHandler plugin) {
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("messagehandler.ignore")) {
            sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
            return true;
        }
        User user = new User((Player) sender);
        if(args.length == 0) {
            user.sendMessage("&bCorrect Argument: &f/ignore <player>");
            return true;
        }
        User target = new User(Bukkit.getPlayer(args[0]));
        if(target == null) {
            user.sendMessage("&cTarget not found");
            return true;
        }
        if(user.getIgnorePlayers().contains(target.getUuid())) {
            user.ignore(target, false);
            user.sendMessage("&bYou are no longer ignoring > &6" + target.getName());
            return true;
        }
        user.ignore(target, true);
        user.sendMessage("&bYou ignored player > &6" + target.getName());
        return false;
    }
}
