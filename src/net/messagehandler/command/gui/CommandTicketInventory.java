package net.messagehandler.command.gui;

import net.messagehandler.listeners.inventory.MenuClick;
import net.messagehandler.listeners.inventory.players.Online;
import net.messagehandler.listeners.inventory.ticket.TicketMenu;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTicketInventory implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Utility.colorize("&cYou must be a player to do that"));
            return true;
        }

        User user = new User((Player) sender);
        if(!user.hasPermission("messagehandler.gui.ticket")) {
            user.sendMessage("&cYou cannot do that");
            return true;
        }
        TicketMenu ticketMenu = new TicketMenu(user);
        ticketMenu.setup("player");
        ticketMenu.openInventory();
        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("admin")) {
                if(!user.hasPermission("messagehandler.ticketadmin")) {
                    user.sendMessage("&cYou cannot do that");
                    return true;
                }
                ticketMenu.setup("admin");
                ticketMenu.openInventory();
                return true;
            }
            return true;
        }
        return false;
    }
}
