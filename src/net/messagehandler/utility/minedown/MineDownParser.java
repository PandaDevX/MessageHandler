package net.messagehandler.utility.minedown;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;

public class MineDownParser {
    private static final boolean HAS_APPEND_SUPPORT = Util.hasMethod(ComponentBuilder.class, "append", new Class[] { BaseComponent[].class });

    private static final boolean HAS_RGB_SUPPORT = Util.hasMethod(ChatColor.class, "of", new Class[] { String.class });

    private static final boolean HAS_FONT_SUPPORT = Util.hasMethod(ComponentBuilder.class, "font", new Class[] { String.class });

    private static final boolean HAS_HOVER_CONTENT_SUPPORT = Util.hasMethod(HoverEvent.class, "getContents", new Class[0]);

    private char colorChar = '&';

    private Set<Option> enabledOptions = EnumSet.of(Option.LEGACY_COLORS, Option.SIMPLE_FORMATTING, Option.ADVANCED_FORMATTING);

    private Set<Option> filteredOptions = EnumSet.noneOf(Option.class);

    private boolean lenient = false;

    private boolean backwardsCompatibility = true;

    private boolean urlDetection = true;

    private String urlHoverText = "Click to open url";

    private boolean autoAddUrlPrefix = true;

    private int hoverTextWidth = 60;

    public static final Pattern URL_PATTERN = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

    public static final String FONT_PREFIX = "font=";

    public static final String COLOR_PREFIX = "color=";

    public static final String FORMAT_PREFIX = "format=";

    public static final String HOVER_PREFIX = "hover=";

    private ComponentBuilder builder;

    private StringBuilder value;

    private String font;

    private ChatColor color;

    private Set<ChatColor> format;

    private ClickEvent clickEvent;

    private HoverEvent hoverEvent;

    public MineDownParser() {
        reset();
    }

    public ComponentBuilder parse(String message) throws IllegalArgumentException {
        Matcher urlMatcher = urlDetection() ? URL_PATTERN.matcher(message) : null;
        boolean escaped = false;
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            boolean isEscape = (c == '\\' && i + 1 < message.length());
            boolean isColorCode = (isEnabled(Option.LEGACY_COLORS) && i + 1 < message.length() && (c == '§' || c == colorChar()));
            boolean isEvent = false;
            if (isEnabled(Option.ADVANCED_FORMATTING) && c == '[') {
                int nextEventClose = Util.indexOfNotEscaped(message, "](", i + 1);
                if (nextEventClose != -1 && nextEventClose + 2 < message.length()) {
                    int nextDefClose = Util.indexOfNotEscaped(message, ")", i + 2);
                    if (nextDefClose != -1) {
                        int depth = 1;
                        isEvent = true;
                        boolean innerEscaped = false;
                        for (int j = i + 1; j < nextEventClose; j++) {
                            if (innerEscaped) {
                                innerEscaped = false;
                            } else if (message.charAt(j) == '\\') {
                                innerEscaped = true;
                            } else if (message.charAt(j) == '[') {
                                depth++;
                            } else if (message.charAt(j) == ']') {
                                depth--;
                            }
                            if (depth == 0) {
                                isEvent = false;
                                break;
                            }
                        }
                    }
                }
            }
            boolean isFormatting = (isEnabled(Option.SIMPLE_FORMATTING) && (c == '_' || c == '*' || c == '~' || c == '?' || c == '#') && Util.isDouble(message, i) && message.indexOf(String.valueOf(c) + String.valueOf(c), i + 2) != -1);
            if (escaped) {
                escaped = false;
            } else {
                if (isEscape) {
                    escaped = true;
                    continue;
                }
                if (isColorCode) {
                    i++;
                    char code = message.charAt(i);
                    if (code >= 'A' && code <= 'Z')
                        code = (char)(code + 32);
                    boolean isLegacyHex = (code == 'x');
                    if (isLegacyHex)
                        i += 2;
                    ChatColor encoded = null;
                    Option filterOption = null;
                    StringBuilder colorString = new StringBuilder();
                    for (int j = i; j < message.length(); j++) {
                        char c1 = message.charAt(j);
                        if (c1 == c) {
                            if (isLegacyHex)
                                continue;
                            if (colorString.length() > 1) {
                                try {
                                    encoded = parseColor(colorString.toString(), "", lenient(), backwardsCompatibility());
                                    filterOption = Option.SIMPLE_FORMATTING;
                                    i = j;
                                } catch (IllegalArgumentException illegalArgumentException) {}
                                break;
                            }
                        }
                        if (!isLegacyHex && c1 != '_' && c1 != '#' && (c1 < 'A' || c1 > 'Z') && (c1 < 'a' || c1 > 'z') && (c1 < '0' || c1 > '9'))
                            break;
                        if (!isLegacyHex || c1 != 'x') {
                            colorString.append(c1);
                            if (isLegacyHex && colorString.length() == 6) {
                                try {
                                    encoded = parseColor("#" + colorString.toString(), "", lenient(), backwardsCompatibility());
                                    filterOption = Option.LEGACY_COLORS;
                                    i = j;
                                } catch (IllegalArgumentException illegalArgumentException) {}
                                break;
                            }
                        }
                        continue;
                    }
                    if (encoded == null) {
                        encoded = ChatColor.getByChar(code);
                        if (encoded != null)
                            filterOption = Option.LEGACY_COLORS;
                    }
                    if (encoded != null) {
                        if (!isFiltered(filterOption))
                            if (encoded == ChatColor.RESET) {
                                appendValue();
                                this.color = null;
                                Util.applyFormat(this.builder, this.format);
                                this.format = new HashSet<>();
                            } else if (!Util.isFormat(encoded)) {
                                if (this.value.length() > 0)
                                    appendValue();
                                this.color = encoded;
                                this.format = new HashSet<>();
                            } else {
                                if (this.value.length() > 0)
                                    appendValue();
                                this.format.add(encoded);
                            }
                    } else {
                        this.value.append(c).append(code);
                    }
                    continue;
                }
                if (isEvent) {
                    int index = Util.indexOfNotEscaped(message, "](", i + 1);
                    int endIndex = Util.indexOfNotEscaped(message, ")", index + 2);
                    appendValue();
                    if (!isFiltered(Option.ADVANCED_FORMATTING)) {
                        append(parseEvent(message.substring(i + 1, index), message.substring(index + 2, endIndex)));
                    } else {
                        append(copy(true).parse(message.substring(i + 1, index)));
                    }
                    i = endIndex;
                    continue;
                }
                if (isFormatting) {
                    int endIndex = message.indexOf(String.valueOf(c) + String.valueOf(c), i + 2);
                    Set<ChatColor> formats = new HashSet<>(this.format);
                    if (!isFiltered(Option.SIMPLE_FORMATTING))
                        formats.add(MineDown.getFormatFromChar(c));
                    appendValue();
                    append(copy(true).format(formats).parse(message.substring(i + 2, endIndex)));
                    i = endIndex + 1;
                    continue;
                }
            }
            if (urlDetection() && urlMatcher != null) {
                int urlEnd = message.indexOf(' ', i);
                if (urlEnd == -1)
                    urlEnd = message.length();
                if (urlMatcher.region(i, urlEnd).find()) {
                    appendValue();
                    this.value = new StringBuilder(message.substring(i, urlEnd));
                    appendValue();
                    i = urlEnd - 1;
                    continue;
                }
            }
            this.value.append(message.charAt(i));
            continue;
        }
        if (escaped)
            this.value.append('\\');
        appendValue();
        if (this.builder == null)
            this.builder = new ComponentBuilder("");
        return this.builder;
    }

    private void append(ComponentBuilder builder) {
        append(builder.create());
    }

    private void append(BaseComponent[] components) {
        if (this.builder == null) {
            if (components.length > 0) {
                this.builder = new ComponentBuilder(components[0]);
                for (int i = 1; i < components.length; i++)
                    this.builder.append(components[i]);
            } else {
                this.builder = new ComponentBuilder("");
            }
        } else if (HAS_APPEND_SUPPORT) {
            this.builder.append(components);
        } else if (components.length > 0) {
            try {
                Field fCurrent = this.builder.getClass().getDeclaredField("current");
                fCurrent.setAccessible(true);
                BaseComponent previous = (BaseComponent)fCurrent.get(this.builder);
                Field fParts = this.builder.getClass().getDeclaredField("parts");
                fParts.setAccessible(true);
                List<BaseComponent> parts = (List<BaseComponent>)fParts.get(this.builder);
                for (BaseComponent component : components) {
                    parts.add(previous);
                    fCurrent.set(this.builder, component.duplicate());
                }
            } catch (NoSuchFieldException|IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void appendValue() {
        appendValue(ComponentBuilder.FormatRetention.NONE);
    }

    private void appendValue(ComponentBuilder.FormatRetention retention) {
        if (this.builder == null) {
            this.builder = new ComponentBuilder(this.value.toString());
        } else {
            this.builder.append(this.value.toString(), retention);
        }
        if (!this.backwardsCompatibility || HAS_FONT_SUPPORT)
            this.builder.font(this.font);
        this.builder.color(this.color);
        Util.applyFormat(this.builder, this.format);
        if (urlDetection() && URL_PATTERN.matcher(this.value).matches()) {
            String v = this.value.toString();
            if (!v.startsWith("http://") && !v.startsWith("https://"))
                v = "http://" + v;
            this.builder.event(new ClickEvent(ClickEvent.Action.OPEN_URL, v));
            if (urlHoverText() != null && !urlHoverText().isEmpty())
                this.builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new MineDown(
                        urlHoverText())).replace(new String[] { "url", this.value.toString() }).toComponent()));
        }
        if (this.clickEvent != null)
            this.builder.event(this.clickEvent);
        if (this.hoverEvent != null)
            this.builder.event(this.hoverEvent);
        this.value = new StringBuilder();
    }

    public ComponentBuilder parseEvent(String text, String definitions) {
        List<String> defParts = new ArrayList<>();
        if (definitions.startsWith(" "))
            defParts.add("");
        Collections.addAll(defParts, definitions.split(" "));
        if (definitions.endsWith(" "))
            defParts.add("");
        String font = null;
        ChatColor color = null;
        Set<ChatColor> formats = new HashSet<>();
        ClickEvent clickEvent = null;
        HoverEvent hoverEvent = null;
        int formatEnd = -1;
        for (int i = 0; i < defParts.size(); i++) {
            String definition = defParts.get(i);
            ChatColor parsed = parseColor(definition, "", true, backwardsCompatibility());
            if (parsed != null) {
                if (Util.isFormat(parsed)) {
                    formats.add(parsed);
                } else {
                    color = parsed;
                }
                formatEnd = i;
            } else {
                if (definition.toLowerCase(Locale.ROOT).startsWith("font="))
                    font = definition.substring("font=".length());
                if (definition.toLowerCase(Locale.ROOT).startsWith("color=")) {
                    color = parseColor(definition, "color=", lenient(), backwardsCompatibility());
                    if (!lenient() && Util.isFormat(color))
                        throw new IllegalArgumentException(color + " is a format and not a color!");
                    formatEnd = i;
                } else if (definition.toLowerCase(Locale.ROOT).startsWith("format=")) {
                    for (String formatStr : definition.substring("format=".length()).split(",")) {
                        ChatColor format = parseColor(formatStr, "", lenient(), backwardsCompatibility());
                        if (!lenient() && !Util.isFormat(format))
                            throw new IllegalArgumentException(formats + " is a color and not a format!");
                        formats.add(format);
                    }
                    formatEnd = i;
                } else if (i == formatEnd + 1 && URL_PATTERN.matcher(definition).matches()) {
                    if (!definition.startsWith("http://") && !definition.startsWith("https://"))
                        definition = "http://" + definition;
                    clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, definition);
                } else {
                    ClickEvent.Action clickAction = definition.startsWith("/") ? ClickEvent.Action.RUN_COMMAND : null;
                    HoverEvent.Action hoverAction = null;
                    if (definition.toLowerCase(Locale.ROOT).startsWith("hover="))
                        hoverAction = HoverEvent.Action.SHOW_TEXT;
                    String[] parts = definition.split("=", 2);
                    try {
                        hoverAction = HoverEvent.Action.valueOf(parts[0].toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException illegalArgumentException) {}
                    try {
                        clickAction = ClickEvent.Action.valueOf(parts[0].toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException illegalArgumentException) {}
                    int bracketDepth = (parts.length > 1 && parts[1].startsWith("{") && (clickAction != null || hoverAction != null)) ? 1 : 0;
                    StringBuilder value = new StringBuilder();
                    if ((parts.length > 1 && clickAction != null) || hoverAction != null) {
                        if (bracketDepth > 0) {
                            value.append(parts[1].substring(1));
                        } else {
                            value.append(parts[1]);
                        }
                    } else {
                        value.append(definition);
                    }
                    for (; ++i < defParts.size(); i++) {
                        String part = defParts.get(i);
                        if (bracketDepth == 0) {
                            int equalsIndex = part.indexOf('=');
                            if (equalsIndex > 0 && !Util.isEscaped(part, equalsIndex)) {
                                i--;
                                break;
                            }
                        }
                        value.append(" ");
                        if (bracketDepth > 0) {
                            int startBracketIndex = part.indexOf("={");
                            if (startBracketIndex > 0 && !Util.isEscaped(part, startBracketIndex) && !Util.isEscaped(part, startBracketIndex + 1))
                                bracketDepth++;
                            bracketDepth--;
                            if (part.endsWith("}") && !Util.isEscaped(part, part.length() - 1) && bracketDepth == 0) {
                                value.append(part, 0, part.length() - 1);
                                break;
                            }
                        }
                        value.append(part);
                    }
                    if (clickAction != null) {
                        String v = value.toString();
                        if (autoAddUrlPrefix() && clickAction == ClickEvent.Action.OPEN_URL && !v.startsWith("http://") && !v.startsWith("https://"))
                            v = "http://" + v;
                        clickEvent = new ClickEvent(clickAction, v);
                    } else if (hoverAction == null) {
                        hoverAction = HoverEvent.Action.SHOW_TEXT;
                    }
                    if (hoverAction != null) {
                        String valueStr = value.toString();
                        if (!HAS_HOVER_CONTENT_SUPPORT) {
                            hoverEvent = new HoverEvent(hoverAction, copy(false).urlDetection(false).parse((hoverAction == HoverEvent.Action.SHOW_TEXT) ? Util.wrap(valueStr, hoverTextWidth()) : valueStr).create());
                        } else if (hoverAction == HoverEvent.Action.SHOW_TEXT) {
                            hoverEvent = new HoverEvent(hoverAction, new Content[] { (Content)new Text(copy(false).urlDetection(false).parse(Util.wrap(valueStr, hoverTextWidth())).create()) });
                        } else if (hoverAction == HoverEvent.Action.SHOW_ENTITY) {
                            String[] valueParts = valueStr.split(":", 2);
                            try {
                                String[] additionalParts = valueParts[1].split(" ", 2);
                                if (!additionalParts[0].contains(":"))
                                    additionalParts[0] = "minecraft:" + additionalParts[0];
                                hoverEvent = new HoverEvent(hoverAction, new Content[] { (Content)new Entity(additionalParts[0], valueParts[0], (additionalParts.length > 1 && additionalParts[1] != null) ? (BaseComponent)new TextComponent(copy(false).urlDetection(false).parse(additionalParts[1]).create()) : null) });
                            } catch (Exception e) {
                                if (!lenient()) {
                                    if (valueParts.length < 2)
                                        throw new IllegalArgumentException("Invalid entity definition. Needs to be of format uuid:id or uuid:namespace:id!");
                                    throw new IllegalArgumentException(e.getMessage());
                                }
                            }
                        } else if (hoverAction == HoverEvent.Action.SHOW_ITEM) {
                            String[] valueParts = valueStr.split(" ", 2);
                            String id = valueParts[0];
                            if (!id.contains(":"))
                                id = "minecraft:" + id;
                            int count = 1;
                            int countIndex = valueParts[0].indexOf('*');
                            if (countIndex > 0 && countIndex + 1 < valueParts[0].length())
                                try {
                                    count = Integer.parseInt(valueParts[0].substring(countIndex + 1));
                                    id = valueParts[0].substring(0, countIndex);
                                } catch (NumberFormatException e) {
                                    if (!lenient())
                                        throw new IllegalArgumentException(e.getMessage());
                                }
                            ItemTag tag = null;
                            if (valueParts.length > 1 && valueParts[1] != null)
                                tag = ItemTag.ofNbt(valueParts[1]);
                            hoverEvent = new HoverEvent(hoverAction, new Content[] { (Content)new Item(id, count, tag) });
                        }
                    }
                }
            }
        }
        if (clickEvent != null && hoverEvent == null)
            hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(clickEvent.getAction().toString().toLowerCase(Locale.ROOT).replace('_', ' '))).color(ChatColor.BLUE).append(" " + clickEvent.getValue()).color(ChatColor.WHITE).create());
        return copy()
                .urlDetection(false)
                .font(font)
                .color(color)
                .format(formats)
                .clickEvent(clickEvent)
                .hoverEvent(hoverEvent)
                .parse(text);
    }

    protected ComponentBuilder builder() {
        return this.builder;
    }

    protected MineDownParser builder(ComponentBuilder builder) {
        this.builder = builder;
        return this;
    }

    protected MineDownParser value(StringBuilder value) {
        this.value = value;
        return this;
    }

    protected StringBuilder value() {
        return this.value;
    }

    private MineDownParser font(String font) {
        this.font = font;
        return this;
    }

    protected String font() {
        return this.font;
    }

    protected MineDownParser color(ChatColor color) {
        this.color = color;
        return this;
    }

    protected ChatColor color() {
        return this.color;
    }

    protected MineDownParser format(Set<ChatColor> format) {
        this.format = format;
        return this;
    }

    protected Set<ChatColor> format() {
        return this.format;
    }

    protected MineDownParser clickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    protected ClickEvent clickEvent() {
        return this.clickEvent;
    }

    protected MineDownParser hoverEvent(HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
        return this;
    }

    protected HoverEvent hoverEvent() {
        return this.hoverEvent;
    }

    @Deprecated
    public static ChatColor parseColor(String colorString, String prefix, boolean lenient) {
        return parseColor(colorString, prefix, lenient, true);
    }

    private static ChatColor parseColor(String colorString, String prefix, boolean lenient, boolean backwardsCompatibility) {
        ChatColor color = null;
        if (prefix.length() + 1 == colorString.length()) {
            color = ChatColor.getByChar(colorString.charAt(prefix.length()));
            if (color == null && !lenient)
                throw new IllegalArgumentException(colorString.charAt(prefix.length()) + " is not a valid " + prefix + " char!");
        } else {
            try {
                colorString = colorString.substring(prefix.length());
                if (colorString.charAt(0) == '#') {
                    if (colorString.length() == 4) {
                        StringBuilder sb = new StringBuilder("#");
                        for (int i = 1; i < 4; i++)
                            sb.append(colorString.charAt(i)).append(colorString.charAt(i));
                        colorString = sb.toString();
                    }
                    if (!backwardsCompatibility || HAS_RGB_SUPPORT) {
                        color = ChatColor.of(colorString);
                    } else {
                        color = Util.getClosestLegacy(new Color(Integer.parseInt(colorString.substring(1), 16)));
                    }
                } else {
                    color = ChatColor.valueOf(colorString.toUpperCase(Locale.ROOT));
                }
            } catch (IllegalArgumentException e) {
                if (!lenient)
                    throw e;
            }
        }
        return color;
    }

    public MineDownParser copy() {
        return copy(false);
    }

    public MineDownParser copy(boolean formatting) {
        return (new MineDownParser()).copy(this, formatting);
    }

    public MineDownParser copy(MineDownParser from) {
        return copy(from, false);
    }

    public MineDownParser copy(MineDownParser from, boolean formatting) {
        lenient(from.lenient());
        urlDetection(from.urlDetection());
        urlHoverText(from.urlHoverText());
        autoAddUrlPrefix(from.autoAddUrlPrefix());
        hoverTextWidth(from.hoverTextWidth());
        enabledOptions(from.enabledOptions());
        filteredOptions(from.filteredOptions());
        colorChar(from.colorChar());
        if (formatting) {
            format(from.format());
            color(from.color());
            font(from.font());
            clickEvent(from.clickEvent());
            hoverEvent(from.hoverEvent());
        }
        return this;
    }

    public MineDownParser reset() {
        this.builder = null;
        this.value = new StringBuilder();
        this.font = null;
        this.color = null;
        this.format = new HashSet<>();
        this.clickEvent = null;
        this.hoverEvent = null;
        return this;
    }

    @Deprecated
    public boolean translateLegacyColors() {
        return isEnabled(Option.LEGACY_COLORS);
    }

    @Deprecated
    public MineDownParser translateLegacyColors(boolean enabled) {
        return enabled ? enable(Option.LEGACY_COLORS) : disable(Option.LEGACY_COLORS);
    }

    public boolean isEnabled(Option option) {
        return enabledOptions().contains(option);
    }

    public MineDownParser enable(Option option) {
        enabledOptions().add(option);
        return this;
    }

    public MineDownParser disable(Option option) {
        enabledOptions().remove(option);
        return this;
    }

    public boolean isFiltered(Option option) {
        return filteredOptions().contains(option);
    }

    public MineDownParser filter(Option option) {
        filteredOptions().add(option);
        enabledOptions().add(option);
        return this;
    }

    public MineDownParser unfilter(Option option) {
        filteredOptions().remove(option);
        return this;
    }

    public String escape(String string) {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            boolean isEscape = (c == '\\');
            boolean isColorCode = (isEnabled(Option.LEGACY_COLORS) && i + 1 < string.length() && (c == '§' || c == colorChar()));
            boolean isEvent = (isEnabled(Option.ADVANCED_FORMATTING) && c == '[');
            boolean isFormatting = (isEnabled(Option.SIMPLE_FORMATTING) && (c == '_' || c == '*' || c == '~' || c == '?' || c == '#') && Util.isDouble(string, i));
            if (isEscape || isColorCode || isEvent || isFormatting)
                value.append('\\');
            value.append(c);
        }
        return value.toString();
    }

    public enum Option {
        SIMPLE_FORMATTING, ADVANCED_FORMATTING, LEGACY_COLORS;
    }

    public char colorChar() {
        return this.colorChar;
    }

    public MineDownParser colorChar(char colorChar) {
        this.colorChar = colorChar;
        return this;
    }

    public Set<Option> enabledOptions() {
        return this.enabledOptions;
    }

    public MineDownParser enabledOptions(Set<Option> enabledOptions) {
        this.enabledOptions = enabledOptions;
        return this;
    }

    public Set<Option> filteredOptions() {
        return this.filteredOptions;
    }

    public MineDownParser filteredOptions(Set<Option> filteredOptions) {
        this.filteredOptions = filteredOptions;
        return this;
    }

    public boolean lenient() {
        return this.lenient;
    }

    public MineDownParser lenient(boolean lenient) {
        this.lenient = lenient;
        return this;
    }

    public boolean backwardsCompatibility() {
        return this.backwardsCompatibility;
    }

    public MineDownParser backwardsCompatibility(boolean backwardsCompatibility) {
        this.backwardsCompatibility = backwardsCompatibility;
        return this;
    }

    public boolean urlDetection() {
        return this.urlDetection;
    }

    public MineDownParser urlDetection(boolean urlDetection) {
        this.urlDetection = urlDetection;
        return this;
    }

    public String urlHoverText() {
        return this.urlHoverText;
    }

    public MineDownParser urlHoverText(String urlHoverText) {
        this.urlHoverText = urlHoverText;
        return this;
    }

    public boolean autoAddUrlPrefix() {
        return this.autoAddUrlPrefix;
    }

    public MineDownParser autoAddUrlPrefix(boolean autoAddUrlPrefix) {
        this.autoAddUrlPrefix = autoAddUrlPrefix;
        return this;
    }

    public int hoverTextWidth() {
        return this.hoverTextWidth;
    }

    public MineDownParser hoverTextWidth(int hoverTextWidth) {
        this.hoverTextWidth = hoverTextWidth;
        return this;
    }
}