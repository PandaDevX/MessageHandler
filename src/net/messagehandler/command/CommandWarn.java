package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandWarn implements CommandExecutor {
    private MessageHandler plugin;

    public CommandWarn(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("messagehandler.warn")) {
            sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
            return true;
        }
        if(args.length <= 1) {
            sender.sendMessage(Utility.colorize("&bCorrect Argument: &f/warn <player> <reason>"));
            return true;
        }
        String admin = "";
        if(sender instanceof ConsoleCommandSender)
            admin = "Console";
        else
            admin = sender.getName();
        if(args[0].equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(Utility.colorize("&cYou cannot warn yourself"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            sender.sendMessage(Utility.colorize("&cTarget not found"));
            return true;
        }
        User user = new User(target);
        StringBuilder reason = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        user.warn(reason.toString(), admin);
        return false;
    }
}
