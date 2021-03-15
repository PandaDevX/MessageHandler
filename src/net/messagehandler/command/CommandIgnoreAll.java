package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandIgnoreAll implements CommandExecutor {
    private final MessageHandler plugin;
    public CommandIgnoreAll(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("messagehandler.ignoreall")) {
            sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
            return true;
        }
        User user = new User((Player) sender);
        if(user.isIgnoringAll()) {
            user.ignoreAll(false);
            user.sendMessage("&bYou are no longer ignoring > &6all");
            return true;
        }
        user.ignoreAll(true);
        user.sendMessage("&bYou ignored &6All Players");
        return false;
    }
}
