package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRealName implements CommandExecutor {
    private final MessageHandler plugin;
    public CommandRealName(MessageHandler plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("messagehandler.realname")) {
            sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
            return true;
        }
        if(args.length == 0) {
            Player player = (Player) sender;
            User user = new User(player);
            user.sendMessage(Utility.colorize("&bCorrect Argument: &f/realname <nickname>"));
            return true;
        }
        String query = args[0];
        for(Player online : Bukkit.getOnlinePlayers()) {
            String nickName = ChatColor.stripColor(online.getDisplayName());
            if(query.equalsIgnoreCase(nickName)) {
                sender.sendMessage(Utility.colorize(Utility.getPrefix() + "His identity is " + online.getName()));
                return true;
            }
        }
        return false;
    }
}
