package net.messagehandler.utility;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    public static Permission permission;

    public static Chat chat;

    public static Economy economy;

    public VaultHook() {
        permission = null;
        economy = null;
        chat = null;
    }

    public static boolean hook() {
        setupChat();
        setupEconomy();
        setupPermissions();
        return true;
    }

    public static boolean withdrawMoney(Player player, double amount) {
        return (economy != null && economy.withdrawPlayer(player, amount).transactionSuccess());
    }

    public static void setPrefix(Player player, String prefix) {
        if(prefix.length() > 16) {
            prefix = prefix.substring(0, 16);
        }
        chat.setPlayerPrefix(player, prefix);
    }

    public static void setSuffix(Player player, String suffix) {
        if(suffix.length() > 16) {
            suffix = suffix.substring(0,16);
        }
        chat.setPlayerSuffix(player, suffix);
    }
    public static String getPlayerPrefix(Player player) {
        if (chat.getPlayerPrefix(player) == null)
            return "";
        return chat.getPlayerPrefix(player);
    }

    public static String getPlayerSuffix(Player player) {
        if (chat.getPlayerSuffix(player) == null)
            return "";
        return chat.getPlayerSuffix(player);
    }

    public static String getPlayerGroup(Player player) {
        return permission.getPrimaryGroup(player) != null ? permission.getPrimaryGroup(player) : "";
    }

    public static boolean isInGroup(String group, Player player) {
        return getPlayerGroup(player).equalsIgnoreCase(group);
    }

    public static double getPlayerBalance(Player player) {
        if (economy == null)
            return 0.0D;
        return economy.getBalance(player);
    }

    public static boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null)
            permission = permissionProvider.getProvider();
        return (permission != null);
    }

    public static boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null)
            chat = chatProvider.getProvider();
        return (chat != null);
    }

    public static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null)
            economy = economyProvider.getProvider();
        return (economy != null);
    }
}