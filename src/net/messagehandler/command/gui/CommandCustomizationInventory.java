package net.messagehandler.command.gui;

import net.messagehandler.listeners.inventory.players.Customization;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCustomizationInventory implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Utility.colorize("&cYou must be a player to do that"));
            return true;
        }

        User user = new User((Player) sender);
        if(!user.hasPermission("messagehandler.customization")) {
            user.sendMessage("&cYou cannot do that");
            return true;
        }
        Customization customization = new Customization(user);
        customization.setup();
        customization.open();
        return false;
    }
}
