package net.messagehandler.utility.picture;

import net.messagehandler.utility.minedown.MineDown;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageMessage {
    private static final char TRANSPARENT_CHAR = '░';

    private String[] lines;

    public ImageMessage(BufferedImage image, int height, char imgChar) {
        Color[][] chatColors = toChatColorArray(image, height);
        this.lines = toImgMessage(chatColors, imgChar);
    }

    private Color[][] toChatColorArray(BufferedImage image, int height) {
        double ratio = image.getHeight() / image.getWidth();
        int width = (int)(height / ratio);
        if (width > 10)
            width = 10;
        BufferedImage resized = resizeImage(image, width, height);
        Color[][] chatImg = new Color[resized.getWidth()][resized.getHeight()];
        for (int x = 0; x < resized.getWidth(); x++) {
            for (int y = 0; y < resized.getHeight(); y++) {
                int rgb = resized.getRGB(x, y);
                chatImg[x][y] = new Color(rgb, true);
            }
        }
        return chatImg;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        AffineTransform af = new AffineTransform();
        af.scale(width / originalImage
                .getWidth(), height / originalImage
                .getHeight());
        AffineTransformOp operation = new AffineTransformOp(af, 1);
        return operation.filter(originalImage, (BufferedImage)null);
    }

    private String[] toImgMessage(Color[][] colors, char imgchar) {
        this.lines = new String[(colors[0]).length];
        for (int y = 0; y < (colors[0]).length; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < colors.length; x++) {
                Color color = colors[x][y];
                if (color != null) {
                    line.append("&")
                            .append(colorToHex(colors[x][y]))
                            .append("&")
                            .append(imgchar);
                } else {
                    line.append(' ');
                }
            }
            this.lines[y] = line.toString() + ChatColor.RESET;
        }
        return this.lines;
    }

    private String colorToHex(Color c) {
        return String.format("#%02x%02x%02x", new Object[] { Integer.valueOf(c.getRed()), Integer.valueOf(c.getGreen()), Integer.valueOf(c.getBlue()) });
    }

    public ImageMessage appendText(String... text) {
        for (int y = 0; y < this.lines.length; y++) {
            if (text.length > y)
                this.lines[y] = this.lines[y] + " " + text[y];
        }
        return this;
    }

    public ImageMessage appendCenteredText(String... text) {
        for (int y = 0; y < this.lines.length; y++) {
            if (text.length > y) {
                int len = 65 - this.lines[y].length();
                this.lines[y] = this.lines[y] + center(text[y], len);
            } else {
                return this;
            }
        }
        return this;
    }

    private String center(String s, int length) {
        if (s.length() > length)
            return s.substring(0, length);
        if (s.length() == length)
            return s;
        int leftPadding = (length - s.length()) / 2;
        StringBuilder leftBuilder = new StringBuilder();
        for (int i = 0; i < leftPadding; i++)
            leftBuilder.append(" ");
        return leftBuilder.toString() + s;
    }

    public void sendToPlayer(Player player) {
        for (String line : this.lines)
            player.spigot().sendMessage(MineDown.parse(line, new String[0]));
    }
}