package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSendTitle implements CommandExecutor {
    private final MessageHandler plugin;
    public CommandSendTitle(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("messagehandler.sendtitle")) {
            Utility.colorize(Utility.getPrefix() + "You are not permitted to do that");
            return true;
        }
        Player player = (Player)  sender;
        if(args.length <= 1) {
            User user = new User(player);
            user.sendMessage("&bCorrect Argument: &f/sendtitle <player> <title>:<subtitle>");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if(args[0].equals("all") && sender.hasPermission("messagehandler.sendtitle.all")) {
            for(Player on : Bukkit.getOnlinePlayers()) {
                User online = new User(on);
                StringBuilder msg = new StringBuilder();
                for(int i = 1; i < args.length; i++) {
                    msg.append(args[i]);
                    msg.append(" ");
                }
                online.sendTitle(msg.toString());
            }
            return true;
        }
        if(target == null) {
            Utility.sendTitle(plugin, player, "&6&lError:&cPlayer not found", 10, 70, 20);
            return true;
        }
        User user = new User(target);
        StringBuilder msg = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            msg.append(args[i]);
            msg.append(" ");
        }
        user.sendTitle(msg.toString());
        return false;
    }
}
