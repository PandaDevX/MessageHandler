package net.messagehandler.utility;

import net.messagehandler.utility.holo.afk.HologramAFK;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataManager {

    /*
    command files
     */
    public static List<UUID> afkPlayers = new ArrayList<>();
    public static List<UUID> vanishedPlayers = new ArrayList<>();
    public static List<UUID> commandSpy = new ArrayList<>();
    public static List<UUID> chatSpy = new ArrayList<>();
    public static HashMap<UUID, UUID> toReply = new HashMap<>();

    /*
    ticket files
     */
    public static HashMap<UUID, String> ticketCategory = new HashMap<>();
    public static HashMap<UUID, String> ticketContent = new HashMap<>();
    public static HashMap<UUID, String> ticketToReply = new HashMap<>();
    public static HashMap<String, String> ticketWhoReply = new HashMap<>();

    /*
    email files
     */
    public static HashMap<UUID, String> mailRecipient = new HashMap<>();
    public static HashMap<UUID, Material> emailBlock = new HashMap<>();
    public static HashMap<UUID, String> emailMessage = new HashMap<>();
    public static HashMap<UUID, String> emailClick = new HashMap<>();
    public static HashMap<UUID, ItemStack[]> emailAttachments = new HashMap<>();
    public static HashMap<UUID, String> emailSubject = new HashMap<>();

    /*
    chat files
     */
    public static List<UUID> globalChannel = new ArrayList<>();
    public static List<UUID> staffChannel = new ArrayList<>();
    public static List<UUID> localChannel = new ArrayList<>();


    /*
     This is for groups
     */

    public static HashMap<UUID, String> groupClicked = new HashMap<>();
    public static List<UUID> groupCreators = new ArrayList<>();


    /*
    This is for entity bars
     */
    public static HashMap<UUID, BossBar> entitySave = new HashMap<>();

    /*
    for indicator
     */
    public static HashMap<UUID, List<ArmorStand>> indicator = new HashMap<>();
    public static HashMap<UUID, HologramAFK> hologram = new HashMap<>();

    /*
     Rules
     */
    public static List<UUID> rulesPrompt = new ArrayList<>();

    /*
    this is for customization
     */
    public static HashMap<UUID, String> customization = new HashMap<>();
    public static HashMap<UUID, String> customizationNT = new HashMap<>();
}
