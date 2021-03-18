package net.messagehandler.command;

import net.messagehandler.MessageHandler;
import net.messagehandler.listeners.inventory.MainPage;
import net.messagehandler.utility.PaginatedList;
import net.messagehandler.utility.User;
import net.messagehandler.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

        if(!sender.hasPermission("messagehandler.command")) {
            sender.sendMessage(Utility.colorize(Utility.parseMessage(MessageHandler.getInstance(), "&bYou cannot do that")));
            return true;
        }

        Player player = (Player) sender;
        if(args.length == 0) {
            MainPage mainPage = new MainPage();
            mainPage.setup(player);
            player.openInventory(mainPage.getInventory());
            return true;
        }
        User user = new User(player);
        if(args[0].equalsIgnoreCase("help")) {
            if(!sender.hasPermission("messagehandler.help")) {
                sender.sendMessage(Utility.colorize(Utility.parseMessage(MessageHandler.getInstance(), "&bYou cannot do that")));
                return true;
            }
            int page = 1;
            if(args.length > 1) {
                if(Utility.isInt(args[1])) {
                    page = Integer.parseInt(args[1]);
                }
            }
            List<String> help = new ArrayList<>();
            HashMap<String, String> helpMap = new HashMap<>();
            helpMap.put("afk", "Away from keyboard");
            helpMap.put("broadcast", "Send message to the players");
            helpMap.put("clearchat", "Clear the chatbox");
            helpMap.put("help", "To know how to play");
            helpMap.put("ignore", "To ignore player");
            helpMap.put("ignoreall", "To ignore all players");
            helpMap.put("motd", "Check the server's motd");
            helpMap.put("nickname", "Change your displayname");
            helpMap.put("realname", "Check the realname of a player behind the nickname");
            helpMap.put("reply", "Reply to someone who sent you a message");
            helpMap.put("rules", "Rules of the server");
            helpMap.put("sendtitle", "Send a titled message to a player");
            helpMap.put("vanish", "Hide to the server");
            helpMap.put("warn", "Warn a player");
            helpMap.put("whisper", "Use to send a private message to player");
            helpMap.put("messagehandler", "Check all messagehandler menus");
            helpMap.put("messagehandler reload", "Reload config");
            helpMap.put("online", "Show all online players");
            helpMap.put("staffs", "Show all staffs");
            helpMap.put("chat", "Toggle your chat box");
            helpMap.put("groupchat", "Create and join chat channels");
            helpMap.put("email", "To send an email");
            helpMap.put("ticket", "Report unwanted things");
            if(args[0].equalsIgnoreCase("help")) {
                for(String key : helpMap.keySet()) {
                    if(player.hasPermission("messagehandler.help." + key)) {
                        help.add(key + ">"  + helpMap.get(key));
                    }
                }
                helpMap.clear();
            }
            PaginatedList paginatedList = new PaginatedList(help, 6);
            if (page > paginatedList.getMaxPage()) {
                page = paginatedList.getMaxPage();
            }
            List<String> finalList = paginatedList.getListOfPage(page);
            user.sendMessage("&7>> &bMessageHandler &7<<");
            user.sendMessage(" ");
            for(String helps : finalList) {
                String[] raw = helps.split(">");
                user.sendMessage("&a/" + raw[0] + ": &f" + raw[1]);
            }
            user.sendMessage(" ");
            user.sendMessage("&7Page ( &a" + page + " / " + paginatedList.getMaxPage() + " &7)");
            return true;
        }
        if(args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("messagehandler.reload")) {
                sender.sendMessage(Utility.colorize(Utility.parseMessage(MessageHandler.getInstance(), "&bYou cannot do that")));
                return true;
            }
            MessageHandler.getInstance().reloadConfig();
            user.sendMessage("&aConfig reloaded");
        }
        return false;
    }
}
