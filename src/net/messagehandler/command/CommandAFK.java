package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAFK implements CommandExecutor {

    private final MessageHandler plugin;
    public CommandAFK(MessageHandler plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("messagehandler.afk")) {
            Utility.colorize(Utility.getPrefix() + "You are not permitted to do that");
            return true;
        }
        if(!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player)sender;
        User user = new User(player);
        if(args.length == 0) {
            if(user.isAFK()) {
                if(plugin.getConfig().getBoolean("AFK.Vanish")) {
                    user.setVanish(false);
                    for(Player online : Bukkit.getOnlinePlayers()) {
                        online.showPlayer(plugin, player);
                    }
                }
                user.setAFK(false);
                player.sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&aYou are no longer afk"), 30, 20, 30);
                for(Player online : Bukkit.getOnlinePlayers()) {
                    online.sendMessage(Utility.colorize(Utility.getPrefix() + "&6" + player.getName() + " &bis no longer afk"));
                }
                return true;
            }
            if(plugin.getConfig().getBoolean("AFK.Vanish")) {
                user.setVanish(true);
                for(Player online : Bukkit.getOnlinePlayers()) {
                    online.hidePlayer(plugin, player);
                }
            }
            user.setAFK(true);
            player.sendTitle(Utility.colorize(Utility.getPrefix()), Utility.colorize("&aYou are now afk"), 30, 20, 30);
            for(Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(Utility.colorize(Utility.getPrefix() + "&6" + player.getName() + " &bis now afk"));
            }
        }
        return false;
    }
}
