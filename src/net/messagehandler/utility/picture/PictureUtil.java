package net.messagehandler.utility.picture;

import net.messagehandler.MessageHandler;
import net.messagehandler.utility.FileUtilType;
import net.messagehandler.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PictureUtil {
    private FileConfiguration motd = Utility.getConfigByFile("settings/motd.yml", FileUtilType.DEFAULT);

    private URL newURL(String player_uuid, String player_name) {
        String url = motd.getString("Picture.URL").replace("%uuid%", player_uuid).replace("%player%", player_name);
        try {
            return new URL(url);
        } catch (Exception e) {
            MessageHandler.getInstance().getLogger().warning("Could not read url from file.");
            return null;
        }
    }

    private BufferedImage getImage(Player player) {
        URL head_image = newURL(player.getUniqueId().toString(), player.getName());
        if (head_image != null)
            try {
                HttpURLConnection connection = (HttpURLConnection)head_image.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                return ImageIO.read(connection.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                MessageHandler.getInstance().getLogger().warning("Error retrieving avatar");
            }
        try {
            return ImageIO.read((new FallbackPicture()).get());
        } catch (Exception e) {
            MessageHandler.getInstance().getLogger().warning("Error Fallback image");
            return null;
        }
    }

    public ImageMessage createPictureMessage(Player player, List<String> messages) {
        BufferedImage image = getImage(player);
        if (image == null)
            return null;
        messages.replaceAll(message -> addPlaceholders(message, player));
        return getMessage(messages, image);
    }

    public ImageMessage getMessage(List<String> messages, BufferedImage image) {
        int imageDimensions = 8, count = 0;
        ImageMessage imageMessage = new ImageMessage(image, imageDimensions, getChar());
        String[] msg = new String[imageDimensions];

        for (String message : messages) {
            if (count > msg.length) break;
            msg[count++] = message;
        }

        while (count < imageDimensions) {
            msg[count++] = "";
        }

        if (motd.getBoolean("Picture.Center Text", false))
            return imageMessage.appendCenteredText(msg);

        return imageMessage.appendText(msg);
    }

    private char getChar() {
        try {
            return ImageChar.valueOf(motd.getString("Picture.Character").toUpperCase()).getChar();
        } catch (IllegalArgumentException e) {
            return ImageChar.BLOCK.getChar();
        }
    }

    public void sendOutPictureMessage(ImageMessage picture_message) {
        Bukkit.getOnlinePlayers().forEach(online_player -> {
            if (motd.getBoolean("Picture.Clear Chat"))
                clearChat(online_player);
            picture_message.sendToPlayer(online_player);
        });
    }

    private String addPlaceholders(String msg, Player player) {
        return Utility.parseMessage(MessageHandler.getInstance(), player, msg);
    }

    public void clearChat(Player player) {
        for (int i = 0; i < 20; i++)
            player.sendMessage("");
    }
}
