package net.messagehandler.utility.minedown;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Util {
    private static final Pattern WRAP_PATTERN = Pattern.compile(" ", 16);

    public static void validate(boolean value, String message) throws IllegalArgumentException {
        if (!value)
            throw new IllegalArgumentException(message);
    }

    public static BaseComponent applyFormat(BaseComponent component, Collection<ChatColor> formats) {
        for (ChatColor format : formats) {
            if (format == ChatColor.BOLD) {
                component.setBold(Boolean.valueOf(true));
                continue;
            }
            if (format == ChatColor.ITALIC) {
                component.setItalic(Boolean.valueOf(true));
                continue;
            }
            if (format == ChatColor.UNDERLINE) {
                component.setUnderlined(Boolean.valueOf(true));
                continue;
            }
            if (format == ChatColor.STRIKETHROUGH) {
                component.setStrikethrough(Boolean.valueOf(true));
                continue;
            }
            if (format == ChatColor.MAGIC) {
                component.setObfuscated(Boolean.valueOf(true));
                continue;
            }
            if (format == ChatColor.RESET) {
                component.setBold(Boolean.valueOf(false));
                component.setItalic(Boolean.valueOf(false));
                component.setUnderlined(Boolean.valueOf(false));
                component.setStrikethrough(Boolean.valueOf(false));
                component.setObfuscated(Boolean.valueOf(false));
                component.setColor(ChatColor.WHITE);
                continue;
            }
            component.setColor(format);
        }
        if (component.getExtra() != null)
            for (BaseComponent extra : component.getExtra())
                applyFormat(extra, formats);
        return component;
    }

    public static ComponentBuilder applyFormat(ComponentBuilder builder, Collection<ChatColor> formats) {
        for (ChatColor format : formats) {
            if (format == ChatColor.BOLD) {
                builder.bold(true);
                continue;
            }
            if (format == ChatColor.ITALIC) {
                builder.italic(true);
                continue;
            }
            if (format == ChatColor.UNDERLINE) {
                builder.underlined(true);
                continue;
            }
            if (format == ChatColor.STRIKETHROUGH) {
                builder.strikethrough(true);
                continue;
            }
            if (format == ChatColor.MAGIC) {
                builder.obfuscated(true);
                continue;
            }
            if (format == ChatColor.RESET) {
                builder.bold(false);
                builder.italic(false);
                builder.underlined(false);
                builder.strikethrough(false);
                builder.obfuscated(false);
                builder.color(ChatColor.WHITE);
                continue;
            }
            builder.color(format);
        }
        return builder;
    }

    public static boolean isDouble(String string, int index) {
        return (index + 1 < string.length() && string.charAt(index) == string.charAt(index + 1));
    }

    public static boolean isFormat(ChatColor format) {
        return !MineDown.getFormatString(format).isEmpty();
    }

    public static Set<ChatColor> getFormats(BaseComponent component, boolean ignoreParent) {
        Set<ChatColor> formats = new LinkedHashSet<>();
        if ((!ignoreParent && component.isBold()) || (component.isBoldRaw() != null && component.isBoldRaw().booleanValue()))
            formats.add(ChatColor.BOLD);
        if ((!ignoreParent && component.isItalic()) || (component.isItalicRaw() != null && component.isItalicRaw().booleanValue()))
            formats.add(ChatColor.ITALIC);
        if ((!ignoreParent && component.isUnderlined()) || (component.isUnderlinedRaw() != null && component.isUnderlinedRaw().booleanValue()))
            formats.add(ChatColor.UNDERLINE);
        if ((!ignoreParent && component.isStrikethrough()) || (component.isStrikethroughRaw() != null && component.isStrikethroughRaw().booleanValue()))
            formats.add(ChatColor.STRIKETHROUGH);
        if ((!ignoreParent && component.isObfuscated()) || (component.isObfuscatedRaw() != null && component.isObfuscatedRaw().booleanValue()))
            formats.add(ChatColor.MAGIC);
        return formats;
    }

    public static int indexOfNotEscaped(String string, String chars) {
        return indexOfNotEscaped(string, chars, 0);
    }

    public static int indexOfNotEscaped(String string, String chars, int fromIndex) {
        for (int i = fromIndex; i < string.length(); i++) {
            int index = string.indexOf(chars, i);
            if (index == -1)
                return -1;
            if (!isEscaped(string, index))
                return index;
        }
        return -1;
    }

    public static boolean isEscaped(String string, int index) {
        if (index - 1 > string.length())
            return false;
        int e = 0;
        while (index > e && string.charAt(index - e - 1) == '\\')
            e++;
        return (e % 2 != 0);
    }

    public static String wrap(String string, int lineLength) {
        if (string.length() <= lineLength || string.contains("\n"))
            return string;
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        for (String s : WRAP_PATTERN.split(string)) {
            if (currentLine.length() + s.length() + 1 > lineLength) {
                int rest = lineLength - currentLine.length() - 1;
                if (rest > lineLength / 4 && s.length() > Math.min(rest * 2, lineLength / 4)) {
                    currentLine.append(" ").append(s, 0, rest);
                } else {
                    rest = 0;
                }
                lines.add(currentLine.toString());
                String restString = s.substring(rest);
                while (restString.length() >= lineLength) {
                    lines.add(restString.substring(0, lineLength));
                    restString = restString.substring(lineLength);
                }
                currentLine = new StringBuilder(restString);
            } else {
                if (currentLine.length() > 0)
                    currentLine.append(" ");
                currentLine.append(s);
            }
        }
        if (currentLine.length() > 0)
            lines.add(currentLine.toString());
        return String.join("\n", (Iterable)lines);
    }

    private static Map<ChatColor, Color> legacyColors = new LinkedHashMap<>();

    static {
        legacyColors.put(ChatColor.BLACK, new Color(0));
        legacyColors.put(ChatColor.DARK_BLUE, new Color(170));
        legacyColors.put(ChatColor.DARK_GREEN, new Color(43520));
        legacyColors.put(ChatColor.DARK_AQUA, new Color(43690));
        legacyColors.put(ChatColor.DARK_RED, new Color(11141120));
        legacyColors.put(ChatColor.DARK_PURPLE, new Color(11141290));
        legacyColors.put(ChatColor.GOLD, new Color(16755200));
        legacyColors.put(ChatColor.GRAY, new Color(11184810));
        legacyColors.put(ChatColor.DARK_GRAY, new Color(5592405));
        legacyColors.put(ChatColor.BLUE, new Color(5592575));
        legacyColors.put(ChatColor.GREEN, new Color(5635925));
        legacyColors.put(ChatColor.AQUA, new Color(5636095));
        legacyColors.put(ChatColor.RED, new Color(16733525));
        legacyColors.put(ChatColor.LIGHT_PURPLE, new Color(16733695));
        legacyColors.put(ChatColor.YELLOW, new Color(16777045));
        legacyColors.put(ChatColor.WHITE, new Color(16777215));
    }

    public static BaseComponent[] rgbColorsToLegacy(BaseComponent[] components) {
        for (BaseComponent component : components) {
            if (component.getColorRaw() != null && component.getColorRaw().getName().startsWith("#"))
                component.setColor(getClosestLegacy(new Color(Integer.parseInt(component.getColorRaw().getName().substring(1), 16))));
            if (component.getExtra() != null)
                rgbColorsToLegacy((BaseComponent[])component.getExtra().toArray((Object[])new BaseComponent[0]));
        }
        return components;
    }

    public static ChatColor getClosestLegacy(Color color) {
        ChatColor closest = null;
        double smallestDistance = Double.MAX_VALUE;
        for (Map.Entry<ChatColor, Color> legacy : legacyColors.entrySet()) {
            double distance = distance(color, legacy.getValue());
            if (distance < smallestDistance) {
                smallestDistance = distance;
                closest = legacy.getKey();
            }
        }
        return closest;
    }

    public static double distance(Color c1, Color c2) {
        if (c1.getRGB() == c2.getRGB())
            return 0.0D;
        return Math.sqrt(Math.pow((c1.getRed() - c2.getRed()), 2.0D) + Math.pow((c1.getGreen() - c2.getGreen()), 2.0D) + Math.pow((c1.getBlue() - c2.getBlue()), 2.0D));
    }

    public static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException classDoesntExist) {
            return false;
        }
    }

    public static boolean hasMethod(Class<?> clazz, String method, Class<?>... parameter) {
        try {
            clazz.getMethod(method, parameter);
            return true;
        } catch (NoSuchMethodException methodDoesntExist) {
            return false;
        }
    }

    public static Method getMethod(Class<?> clazz, String method, Class<?>... parameter) {
        try {
            return clazz.getMethod(method, parameter);
        } catch (NoSuchMethodException methodDoesntExist) {
            return null;
        }
    }
}
