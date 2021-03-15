package net.messagehandler.utility;

import net.messagehandler.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TabManager {

    private final List<String> headers = new ArrayList<>();
    private final List<String> footers = new ArrayList<>();

    private final MessageHandler plugin;

    public TabManager(MessageHandler plugin) {
        this.plugin = plugin;
    }

    public void showTab() {
        if (headers.isEmpty() && footers.isEmpty())
            return;

        FileUtil tabFile = new FileUtil(plugin, "settings/tab.yml", FileUtilType.DEFAULT);
        new BukkitRunnable() {
            int headCount = 0;
            int footCount = 0;

            public void run() {
                if(headCount >= headers.size())
                    headCount = 0;
                if(footCount >= footers.size())
                    footCount = 0;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.setPlayerListHeaderFooter(Utility.parseMessage(plugin, player, headers.get(headCount)), Utility.parseMessage(plugin, player, footers.get(footCount)));
                    }
                headCount++;
                footCount++;
                if(isCancelled()) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.setPlayerListHeaderFooter("", "");
                    }
                }
            }
        }.runTaskTimer(plugin, tabFile.get().getInt("tab.delay"), tabFile.get().getInt("tab.period"));
    }

    public void addHeader(String text) {
        headers.add(Utility.colorize(text));
    }

    public void addFooter(String text) {
        footers.add(Utility.colorize(text));
    }
}

