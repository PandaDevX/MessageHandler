package net.messagehandler.command;

import net.messagehandler.listeners.inventory.MainPage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

        Player player = (Player) sender;
        MainPage mainPage = new MainPage();
        mainPage.setup(player);
        player.openInventory(mainPage.getInventory());

        return false;
    }
}
