package net.messagehandler.utility;

import net.messagehandler.MessageHandler;
import org.bukkit.Bukkit;

public class Hook {

    protected static PAPIHook placeholderApi;
    protected static VaultHook vaultHook;

    public void hookManager() {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceHolderAPI"))
            placeholderApi = new PAPIHook();
    }

    public static void loadDependencies() {
        if(Utility.searchPlugin("PlaceHolderAPI")) {
            placeholderApi = new PAPIHook();
            (new MessageHandlerPlaceHolder(MessageHandler.getInstance())).register();
        }
        if(Utility.searchPlugin("Vault")) {
            vaultHook = new VaultHook();
        }
    }

    public static boolean isPlaceholderAPILoaded() {
        return (placeholderApi != null);
    }

    public static boolean isVaultLoaded() {
        return (vaultHook != null);
    }

}
