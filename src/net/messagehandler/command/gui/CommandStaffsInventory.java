package net.messagehandler.command.gui;

import net.messagehandler.listeners.inventory.MenuClick;
import net.messagehandler.listeners.inventory.players.Staffs;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStaffsInventory implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Utility.colorize("&cYou must be a player to do that"));
            return true;
        }

        User user = new User((Player) sender);
        if(!user.hasPermission("messagehandler.gui.staffs")) {
            user.sendMessage("&cYou cannot do that");
            return true;
        }
        MenuClick.staff = new Staffs(user);
        MenuClick.staff.setup(1);
        MenuClick.staff.open();
        return false;
    }
}
