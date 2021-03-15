package net.messagehandler.utility;
import com.sun.istack.internal.NotNull;
import net.messagehandler.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email {

    private final String recipient;
    private final Material logo;
    private final String message;
    private final ItemStack[] attachments;
    private final String senderName;
    private final String subject;
    private final User sender;

    public Email(@NotNull User sender, String recipient, String subject, Material logo, String message, ItemStack[] attachments) {
        this.recipient = recipient;
        this.logo = logo;
        this.message = message;
        this.attachments = attachments;
        this.senderName = sender.getName();
        this.subject = subject;
        this.sender = sender;
    }

    public boolean send() {
        if(!getErrors().isEmpty()) return false;
        FileUtil fileUtil = new FileUtil(MessageHandler.getInstance(), "playerdata.yml", FileUtilType.DATA);
        FileConfiguration config = fileUtil.get();
        for(String uuid : config.getKeys(false)) {
            System.out.println(config.getString(uuid + ".name"));
            System.out.println(recipient);
            if(config.getString(uuid + ".name").equalsIgnoreCase(recipient)) {
                String mailID = Utility.createRandomID();
                if(config.get(uuid + ".mail") != null) {
                    for(String ids : config.getConfigurationSection(uuid + ".mail").getKeys(false)) {
                        if(mailID.equals(ids)) {
                            mailID = Utility.createRandomID();
                        }
                    }
                }
                config.set(uuid + ".mail." + mailID + ".sender", sender.getName());
                config.set(uuid + ".mail." + mailID + ".logo", logo.toString());
                config.set(uuid + ".mail." + mailID + ".subject", subject);
                config.set(uuid + ".mail." + mailID + ".message", message);
                if(hasAttachments()) {
                    for(int i = 0; i < attachments.length; i++) {
                        config.set(uuid + ".mail." + mailID + ".attachments." + i, attachments[i]);
                    }
                }
                fileUtil.save();

                Player player = Bukkit.getPlayer(recipient);
                if(player != null) {
                    User target = new User(player);
                    if(target.emailNotification()) {
                        target.sendTitle("&2&lEmail:&6" + senderName + " &eSent you an email");
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public String getMessage() {
        return this.message;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public List<String> getErrors() {
        List<String> raw = new ArrayList<>();
        if(message.equals("none")) {
            raw.add("Message is empty");
        }
        if(recipient.equals("messagehandler")) {
            raw.add("Recipient is empty");
        }
        if(subject.equals("subject")) {
            raw.add("Subject is empty");
        }
        FileUtil util = new FileUtil(MessageHandler.getInstance(), "playerdata.yml", FileUtilType.DATA);
        FileConfiguration config = util.get();
        Set<String> names = config.getKeys(false);
        if(names.contains(recipient)) {
            raw.add("Player not found");
        }
        if(senderName.equalsIgnoreCase(recipient)) {
            raw.add("Recipient same to the sender name");
        }
        if(Utility.sensor(Collections.singletonList(message), Utility.getWords(), sender)) {
            raw.add("Swear Words");
        }
        if(Utility.sensorAd(message, sender)) {
            raw.add("Advertisement");
        }

        return raw;
    }

    public Material getLogo() {
        return logo;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessageFirstHalf() {
        return message.substring(0, Math.min(message.length(), 16));
    }

    public String getMessageSecondHalf() {
        return message.length() > 16 ? message.substring(16) : "";
    }

    public String getSenderName() {
        return senderName;
    }

    public ItemStack[] getAttachments() {
        return attachments;
    }

    public boolean hasAttachments() {
        return attachments != null;
    }

}
