package net.messagehandler.listeners.inventory.ticket;

import net.messagehandler.MessageHandler;
import net.messagehandler.listeners.inventory.MainMenu;
import net.messagehandler.utility.*;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tickets {

    private final User user;
    public int maxPage;
    private String name;
    private Inventory inventory = null;
    public Tickets(User user) {
        this.user = user;
    }

    public void setup(int page) {
        this.name = "&6&lTickets";
        this.inventory = MainMenu.createInventory(6, this.name);
        FileUtil ticketFile = new FileUtil(MessageHandler.getInstance(), "tickets.yml", FileUtilType.DATA);
        FileConfiguration categoryConfig = Utility.getConfigByFile("settings/ticket.yml", FileUtilType.DEFAULT);

        FileConfiguration config = ticketFile.get();
        if(config.get("tickets") == null) {
            inventory.setItem(4, Utility.createGUIItem("&3&lTickets", Material.LAPIS_LAZULI));
            inventory.setItem(22, Utility.createGUIItem("&3&lEmpty", Material.BEACON));
            inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Collections.singletonList("Return to the main menu"), Material.BARRIER));
            return;
        }
        List<ItemStack> tickets = new ArrayList<>();
        for(String ticketID : config.getConfigurationSection("tickets").getKeys(false)) {
            if(user.getUuid().toString().equals(config.getString("tickets." + ticketID + ".owner.uuid"))) {
                List<String> lore = new ArrayList<>();
                String category = "";
                for(String categ : categoryConfig.getConfigurationSection("categories").getKeys(false)) {
                    if(config.getString("tickets." + ticketID + ".category").equals(
                            categoryConfig.getString("categories." + categ + ".name").substring(2)
                    )|| config.getString("tickets." + ticketID + ".category").equals(
                            categoryConfig.getString("categories." + categ + ".name").substring(4)
                    ) || config.getString("tickets." + ticketID + ".category").equals(
                            categoryConfig.getString("categories." + categ + ".name")
                    )) {
                        category = categoryConfig.getString("categories." + categ + ".type");
                        break;
                    }
                }
                String status = config.getString("tickets." + ticketID + ".status");
                String replies = config.getStringList("tickets." + ticketID + ".replies").isEmpty() ? "&c0"
                        : "&a" + config.getStringList("tickets." + ticketID + ".replies").size();
                lore.add(Utility.colorize("&7Category: &f" + config.getString("tickets." + ticketID + ".category")));
                lore.add(Utility.colorize("&7Status:" + (status.equals("Open") ? " &aOpen" : " &cClose")));
                lore.add(Utility.colorize("&7Replies: " + replies));
                lore.add("");
                lore.add(Utility.colorize("&5LEFT CLICK &7Check ticket info and replies"));
                lore.add(Utility.colorize("&5RIGHT CLICK &7Add a new reply"));
                lore.add(Utility.colorize("&5SHIFT LEFT CLICK &7Teleport to ticket location"));
                if(user.hasPermission("messagehandler.ticket.admin")) lore.add(Utility.colorize("&5SHIFT RIGHT CLICK &7Toggle the status"));
                lore.add("");
                lore.add(Utility.colorize("&7Date created: &f" + config.getString("tickets." + ticketID + ".date")));
                Material material = Material.valueOf(category);
                tickets.add(Utility.createGUIItem("&3&lTicket " + ticketID, lore, material));
            }
        }
        if(tickets.isEmpty()) {
            inventory.setItem(4, Utility.createGUIItem("&3&lTickets", Material.LAPIS_LAZULI));
            inventory.setItem(22, Utility.createGUIItem("&3&lEmpty", Material.BEACON));
            inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Collections.singletonList("Return to the main menu"), Material.BARRIER));
            return;
        }
        PaginatedList paginatedList = new PaginatedList(tickets, 36);
        List<ItemStack> ticketList = paginatedList.getListOfPage(page);
        this.maxPage = paginatedList.getMaxPage();
        inventory.setItem(4, Utility.createGUIItem("&3&lTickets", Collections.singletonList(Utility.colorize("&7Page(s): &a" + page + " / " + maxPage)), Material.LAPIS_LAZULI));

        for(int i = 9; i < ticketList.size() + 9; i++) {
            inventory.setItem(i, ticketList.get(i-9));
        }

        if(maxPage > 1 && maxPage != page) {
            inventory.setItem(51, Utility.createGUIItem("&3&lNext", Material.ENDER_PEARL));
        }
        if(page > 1) {
            inventory.setItem(50, Utility.createGUIItem("&3&lPrev", Material.ENDER_PEARL));
        }
        inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Collections.singletonList("Return to the main menu"), Material.BARRIER));
        return;
    }

    public void setup(int page, String type) {
        if(type.equals("open")) {
            this.name = "&6&lTickets &2(Admin) &6Status = &aOpen";
            this.inventory = MainMenu.createInventory(6, this.name);
                FileUtil ticketFile = new FileUtil(MessageHandler.getInstance(), "tickets.yml", FileUtilType.DATA);
                FileConfiguration categoryConfig = Utility.getConfigByFile("settings/ticket.yml", FileUtilType.DEFAULT);

                FileConfiguration config = ticketFile.get();

                List<ItemStack> tickets = new ArrayList<>();
                for(String ticketID : config.getConfigurationSection("tickets").getKeys(false)) {
                    if(user.getUuid().toString().equals(config.getString("tickets." + ticketID + ".owner.uuid"))) {
                        List<String> lore = new ArrayList<>();
                        String category = "";
                        for(String categ : categoryConfig.getConfigurationSection("categories").getKeys(false)) {
                            if(config.getString("tickets." + ticketID + ".category").equals(
                                    categoryConfig.getString("categories." + categ + ".name").substring(2)
                            )|| config.getString("tickets." + ticketID + ".category").equals(
                                    categoryConfig.getString("categories." + categ + ".name").substring(4)
                            ) || config.getString("tickets." + ticketID + ".category").equals(
                                    categoryConfig.getString("categories." + categ + ".name"))) {
                                category = categoryConfig.getString("categories." + categ + ".type");
                                break;
                            }
                        }
                        String status = config.getString("tickets." + ticketID + ".status");
                        if(status.equals("Close")) {
                            continue;
                        }
                        String replies = config.getStringList("tickets." + ticketID + ".replies").isEmpty() ? "&c0"
                                : "&a" + config.getStringList("tickets." + ticketID + ".replies").size();
                        lore.add(Utility.colorize("&7Category: &f" + config.getString("tickets." + ticketID + ".category")));
                        lore.add(Utility.colorize("&7Status:" + (status.equals("Open") ? " &aOpen" : " &cClose")));
                        lore.add(Utility.colorize("&7Overview:"));
                        lore.add(Utility.colorize("&f" + config.getString("tickets." + ticketID + ".content")));
                        lore.add(Utility.colorize("&7Replies: " + replies));
                        if(!config.getStringList("tickets." + ticketID + ".assigned").isEmpty()) {
                            lore.add(Utility.colorize("&7Assigned Admins:"));
                            for (String admin : config.getStringList("tickets." + ticketID + ".assigned")) {
                                lore.add(Utility.colorize(" &6&lADMIN &2" + admin));
                            }
                        }
                        lore.add(Utility.colorize("&7Owner Name: &f" + config.getString("tickets." + ticketID + ".owner.name")));
                        lore.add("");
                        lore.add(Utility.colorize("&5LEFT CLICK &7Check ticket info and replies"));
                        lore.add(Utility.colorize("&5RIGHT CLICK &7Add a new reply"));
                        lore.add(Utility.colorize("&5SHIFT LEFT CLICK &7To take responsibility"));
                        if(user.hasPermission("messagehandler.ticket.admin")) lore.add(Utility.colorize("&5SHIFT RIGHT CLICK &7Toggle the status"));
                        lore.add("");
                        lore.add(Utility.colorize("&7Date created: &f" + config.getString("tickets." + ticketID + ".date")));
                        Material material = Material.valueOf(category);
                        tickets.add(Utility.createGUIItem("&3&lTicket " + ticketID, lore, material));
                    }
                }
                if(tickets.isEmpty()) {
                    inventory.setItem(4, Utility.createGUIItem("&3&lTickets", Material.LAPIS_LAZULI));
                    inventory.setItem(22, Utility.createGUIItem("&3&lEmpty", Material.BEACON));
                    inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Collections.singletonList("Return to the main menu"), Material.BARRIER));
                    return;
                }
                PaginatedList paginatedList = new PaginatedList(tickets, 36);
                List<ItemStack> ticketList = paginatedList.getListOfPage(page);
                this.maxPage = paginatedList.getMaxPage();
                inventory.setItem(4, Utility.createGUIItem("&3&lTickets", Collections.singletonList(Utility.colorize("&7Page(s): &a" + page + " / " + maxPage)), Material.LAPIS_LAZULI));

                for(int i = 9; i < ticketList.size() + 9; i++) {
                    inventory.setItem(i, ticketList.get(i-9));
                }

                if(maxPage > 1 && maxPage != page) {
                    inventory.setItem(51, Utility.createGUIItem("&3&lNext", Material.ENDER_PEARL));
                }
                if(page > 1) {
                    inventory.setItem(51, Utility.createGUIItem("&3&lNext", Material.ENDER_PEARL));
                }
                List<String> ticketLore = new ArrayList<>();
                if(user.ticket()) {
                    ticketLore.add(Utility.colorize("&7Toggle: &aEnable"));
                }else {
                    ticketLore.add(Utility.colorize("&7Toggle: &cDisable"));
                }
                inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Collections.singletonList("Return to the main menu"), Material.BARRIER));
                return;
        }
        if(type.equals("close")) {
            this.name = "&6&lTickets &2(Admin) &6Status = &cClose";
            this.inventory = MainMenu.createInventory(6, this.name);
            FileUtil ticketFile = new FileUtil(MessageHandler.getInstance(), "tickets.yml", FileUtilType.DATA);
            FileConfiguration categoryConfig = Utility.getConfigByFile("settings/ticket.yml", FileUtilType.DEFAULT);

            FileConfiguration config = ticketFile.get();
            List<ItemStack> tickets = new ArrayList<>();
            for(String ticketID : config.getConfigurationSection("tickets").getKeys(false)) {
                if(user.getUuid().toString().equals(config.getString("tickets." + ticketID + ".owner.uuid"))) {
                    List<String> lore = new ArrayList<>();
                    String category = "";
                    for(String categ : categoryConfig.getConfigurationSection("categories").getKeys(false)) {
                        if(config.getString("tickets." + ticketID + ".category").equals(
                                categoryConfig.getString("categories." + categ + ".name").substring(2)
                        )|| config.getString("tickets." + ticketID + ".category").equals(
                                categoryConfig.getString("categories." + categ + ".name").substring(4)
                        ) || config.getString("tickets." + ticketID + ".category").equals(
                                categoryConfig.getString("categories." + categ + ".name")
                        )) {
                            category = categoryConfig.getString("categories." + categ + ".type");
                            break;
                        }
                    }
                    String status = config.getString("tickets." + ticketID + ".status");
                    if(status.equals("Open")) {
                        continue;
                    }
                    String replies = config.getStringList("tickets." + ticketID + ".replies").isEmpty() ? "&c0"
                            : "&a" + config.getStringList("tickets." + ticketID + ".replies").size();
                    lore.add(Utility.colorize("&7Category: &f" + config.getString("tickets." + ticketID + ".category")));
                    lore.add(Utility.colorize("&7Status: &cClose"));
                    lore.add(Utility.colorize("&7Overview:"));
                    lore.add(Utility.colorize("&f" + config.getString("tickets." + ticketID + ".content")));
                    lore.add(Utility.colorize("&7Replies: " + replies));
                    if(!config.getStringList("tickets." + ticketID + ".assigned").isEmpty()) {
                        lore.add(Utility.colorize("&7Assigned Admins:"));
                        for (String admin : config.getStringList("tickets." + ticketID + ".assigned")) {
                            lore.add(Utility.colorize(" &6&lADMIN &2" + admin));
                        }
                    }
                    lore.add(Utility.colorize("&7Owner Name: &f" + config.getString("tickets." + ticketID + ".owner.name")));
                    lore.add("");
                    lore.add(Utility.colorize("&5LEFT CLICK &7Check ticket info and replies"));
                    lore.add(Utility.colorize("&5RIGHT CLICK &7Add a new reply"));
                    lore.add(Utility.colorize("&5SHIFT LEFT CLICK &7To take responsibility"));
                    if(user.hasPermission("messagehandler.ticket.admin")) lore.add(Utility.colorize("&5SHIFT RIGHT CLICK &7Toggle the status"));
                    lore.add("");
                    lore.add(Utility.colorize("&7Date created: &f" + config.getString("tickets." + ticketID + ".date")));
                    Material material = Material.valueOf(category);
                    tickets.add(Utility.createGUIItem("&3&lTicket " + ticketID, lore, material));
                }
            }
            if(tickets.isEmpty()) {
                inventory.setItem(4, Utility.createGUIItem("&3&lTickets", Material.LAPIS_LAZULI));
                inventory.setItem(22, Utility.createGUIItem("&3&lEmpty", Material.BEACON));
                inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Collections.singletonList("Return to the main menu"), Material.BARRIER));
                return;
            }
            PaginatedList paginatedList = new PaginatedList(tickets, 36);
            List<ItemStack> ticketList = paginatedList.getListOfPage(page);
            this.maxPage = paginatedList.getMaxPage();
            inventory.setItem(4, Utility.createGUIItem("&3&lTickets", Collections.singletonList(Utility.colorize("&7Page(s): &a" + page + " / " + maxPage)), Material.LAPIS_LAZULI));

            for(int i = 9; i < ticketList.size() + 9; i++) {
                inventory.setItem(i, ticketList.get(i-9));
            }

            if(maxPage > 1 && maxPage != page) {
                inventory.setItem(51, Utility.createGUIItem("&3&lNext", Material.ENDER_PEARL));
            }
            if(page > 1) {
                inventory.setItem(50, Utility.createGUIItem("&3&lPrev", Material.ENDER_PEARL));
            }
            List<String> ticketLore = new ArrayList<>();
            if(user.ticket()) {
                ticketLore.add(Utility.colorize("&7Toggle: &aEnable"));
            }else {
                ticketLore.add(Utility.colorize("&7Toggle: &cDisable"));
            }
            inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Collections.singletonList("Return to the main menu"), Material.BARRIER));
            return;
        }
        if(type.equals("assign")) {
            this.name = "&6&lTickets &2(Admin:" + user.getName() + ")";
            this.inventory = MainMenu.createInventory(6, this.name);
            FileUtil ticketFile = new FileUtil(MessageHandler.getInstance(), "tickets.yml", FileUtilType.DATA);
            FileConfiguration categoryConfig = Utility.getConfigByFile("settings/ticket.yml", FileUtilType.DEFAULT);

            FileConfiguration config = ticketFile.get();
            List<ItemStack> tickets = new ArrayList<>();
            for(String ticketID : config.getConfigurationSection("tickets").getKeys(false)) {
                if(user.getUuid().toString().equals(config.getString("tickets." + ticketID + ".owner.uuid"))) {
                    List<String> lore = new ArrayList<>();
                    String category = "";
                    for(String categ : categoryConfig.getConfigurationSection("categories").getKeys(false)) {
                        if(config.getString("tickets." + ticketID + ".category").equals(
                                categoryConfig.getString("categories." + categ + ".name").substring(2)
                        )|| config.getString("tickets." + ticketID + ".category").equals(
                                categoryConfig.getString("categories." + categ + ".name").substring(4)
                        ) || config.getString("tickets." + ticketID + ".category").equals(
                                categoryConfig.getString("categories." + categ + ".name")
                        )) {
                            category = categoryConfig.getString("categories." + categ + ".type");
                            break;
                        }
                    }
                    String status = config.getString("tickets." + ticketID + ".status");
                    if(status.equals("Close")) continue;
                    if(config.getStringList("tickets." + ticketID + ".assigned").isEmpty()
                            || !config.getStringList("tickets." + ticketID + ".assigned").contains(user.getName())) {
                        continue;
                    }
                    String replies = config.getStringList("tickets." + ticketID + ".replies").isEmpty() ? "&c0"
                            : "&a" + config.getStringList("tickets." + ticketID + ".replies").size();
                    lore.add(Utility.colorize("&7Category: &f" + config.getString("tickets." + ticketID + ".category")));
                    lore.add(Utility.colorize("&7Status:" + (status.equals("Open") ? " &aOpen" : " &cClose")));
                    lore.add(Utility.colorize("&7Overview:"));
                    lore.add(Utility.colorize("&f" + config.getString("tickets." + ticketID + ".content")));
                    lore.add(Utility.colorize("&7Replies: " + replies));
                    lore.add(Utility.colorize("&7Owner Name: &f" + config.getString("tickets." + ticketID + ".owner.name")));
                    lore.add("");
                    lore.add(Utility.colorize("&5LEFT CLICK &7Check ticket info and replies"));
                    lore.add(Utility.colorize("&5RIGHT CLICK &7Add a new reply"));
                    lore.add(Utility.colorize("&5SHIFT LEFT CLICK &7To teleport to ticket location"));
                    if(user.hasPermission("messagehandler.ticket.admin")) lore.add(Utility.colorize("&5SHIFT RIGHT CLICK &7Toggle the status"));
                    lore.add("");
                    lore.add(Utility.colorize("&7Date created: &f" + config.getString("tickets." + ticketID + ".date")));
                    Material material = Material.valueOf(category);
                    tickets.add(Utility.createGUIItem("&3&lTicket " + ticketID, lore, material));
                }
            }
            if(tickets.isEmpty()) {
                inventory.setItem(4, Utility.createGUIItem("&3&lTickets", Material.LAPIS_LAZULI));
                inventory.setItem(22, Utility.createGUIItem("&3&lEmpty", Material.BEACON));
                inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Collections.singletonList("Return to the main menu"), Material.BARRIER));
                return;
            }
            PaginatedList paginatedList = new PaginatedList(tickets, 36);
            List<ItemStack> ticketList = paginatedList.getListOfPage(page);
            this.maxPage = paginatedList.getMaxPage();
            inventory.setItem(4, Utility.createGUIItem("&3&lTickets", Collections.singletonList(Utility.colorize("&7Page(s): &a" + page + " / " + maxPage)), Material.LAPIS_LAZULI));

            for(int i = 9; i < ticketList.size() + 9; i++) {
                inventory.setItem(i, ticketList.get(i-9));
            }

            if(maxPage > 1 && maxPage != page) {
                inventory.setItem(51, Utility.createGUIItem("&3&lNext", Material.ENDER_PEARL));
            }
            if(page > 1) {
                inventory.setItem(50, Utility.createGUIItem("&3&lPrev", Material.ENDER_PEARL));
            }
            inventory.setItem(inventory.getSize() - 1, Utility.createGUIItem("&c&lBack", Collections.singletonList("Return to the main menu"), Material.BARRIER));
        }
    }

    public void openInventory() {
        new BukkitRunnable() {
            public void run() {
                user.getPlayer().openInventory(inventory);
            }
        }.runTaskLater(MessageHandler.getInstance(), 1);
    }

    public int getMaxPage() {
        return maxPage;
    }
}
