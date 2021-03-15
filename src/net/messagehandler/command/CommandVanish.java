package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandVanish implements CommandExecutor {
    private MessageHandler plugin;
    public CommandVanish(MessageHandler plugin) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("messagehandler.vanish")) {
            Utility.colorize(Utility.getPrefix() + "You are not permitted to do that");
            return true;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(Utility.colorize(Utility.getPrefix() + "You must be a player to do that"));
            return true;
        }
        Player player = (Player) sender;
        User user = new User(player);
        if(user.isHidden()) {
            player.sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&bYou are no longer vanished"), 30, 20, 30);
            for(Player online : Bukkit.getOnlinePlayers()) {
                if(online.hasPermission("messagehandler.vanish.admin")) {
                    continue;
                }
                online.showPlayer(plugin, player);
            }
            user.setVanish(false);
            return true;
        }
        user.setVanish(true);
        player.sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&bYou have been vanished"), 30, 20, 30);
        for(Player online : Bukkit.getOnlinePlayers()) {
            if(online.hasPermission("messagehandler.vanish.admin")) {
                continue;
            }
            online.hidePlayer(plugin, player);
        }
        return false;
    }
}
