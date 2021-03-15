package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNick implements CommandExecutor {
    private final MessageHandler plugin;

    public CommandNick(MessageHandler plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(!sender.hasPermission("messagehandler.nickname")) {
            sender.sendMessage(Utility.colorize(Utility.parseMessage(plugin, "&bYou cannot do that")));
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage(Utility.colorize(Utility.getPrefix() + "Correct argument : &6/nickname <nickname>"));
            return true;
        }
        if(args[0].equalsIgnoreCase("clear")) {
            User user = new User((Player) sender);
            user.clearNickName();
            user.sendMessage(Utility.colorize(Utility.getPrefix() + "Successfully removed your nickname"));
            return true;
        }
        String nickName = "";
        if(args[0].length() >= 16)
          nickName = args[0].substring(0, 16);
        else
            nickName = args[0];
        User user = new User((Player)sender);
        user.setNickName(nickName);
        user.sendMessage(Utility.colorize(Utility.getPrefix() + Utility.parseMessage(plugin, user.getPlayer(), "Your new nickname is {nickname}")));
        return false;
    }
}
