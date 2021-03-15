package net.messagehandler.utility.minedown;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.ChatColor;

import java.util.Map;

public class MineDown {
    private String message;

    private final Replacer replacer = new Replacer();

    private final MineDownParser parser = new MineDownParser();

    private BaseComponent[] baseComponents = null;

    private boolean replaceFirst = false;

    public MineDown(String message) {
        this.message = message;
    }

    public static BaseComponent[] parse(String message, String... replacements) {
        return (new MineDown(message)).replace(replacements).toComponent();
    }

    public static String stringify(BaseComponent[] components) {
        return (new MineDownStringifier()).stringify(components);
    }

    public BaseComponent[] toComponent() {
        if (baseComponents() == null)
            if (replaceFirst()) {
                this.baseComponents = parser().parse(replacer().replaceIn(message())).create();
            } else {
                this.baseComponents = replacer().replaceIn(parser().parse(message()).create());
            }
        return baseComponents();
    }

    private void reset() {
        this.baseComponents = null;
    }

    public MineDown replaceFirst(boolean replaceFirst) {
        reset();
        this.replaceFirst = replaceFirst;
        return this;
    }

    public boolean replaceFirst() {
        return this.replaceFirst;
    }

    public MineDown replace(String... replacements) {
        reset();
        replacer().replace(replacements);
        return this;
    }

    public MineDown replace(Map<String, ?> replacements) {
        reset();
        replacer().replace(replacements);
        return this;
    }

    public MineDown replace(String placeholder, BaseComponent... replacement) {
        reset();
        replacer().replace(placeholder, replacement);
        return this;
    }

    public MineDown placeholderIndicator(String placeholderIndicator) {
        placeholderPrefix(placeholderIndicator);
        placeholderSuffix(placeholderIndicator);
        return this;
    }

    public MineDown placeholderPrefix(String placeholderPrefix) {
        reset();
        replacer().placeholderPrefix(placeholderPrefix);
        return this;
    }

    public String placeholderPrefix() {
        return replacer().placeholderPrefix();
    }

    public MineDown placeholderSuffix(String placeholderSuffix) {
        reset();
        replacer().placeholderSuffix(placeholderSuffix);
        return this;
    }

    public String placeholderSuffix() {
        return replacer().placeholderSuffix();
    }

    public MineDown ignorePlaceholderCase(boolean ignorePlaceholderCase) {
        reset();
        replacer().ignorePlaceholderCase(ignorePlaceholderCase);
        return this;
    }

    public boolean ignorePlaceholderCase() {
        return replacer().ignorePlaceholderCase();
    }

    @Deprecated
    public MineDown translateLegacyColors(boolean translateLegacyColors) {
        reset();
        parser().translateLegacyColors(translateLegacyColors);
        return this;
    }

    public MineDown urlDetection(boolean enabled) {
        reset();
        parser().urlDetection(enabled);
        return this;
    }

    public MineDown autoAddUrlPrefix(boolean enabled) {
        reset();
        parser().autoAddUrlPrefix(enabled);
        return this;
    }

    public MineDown urlHoverText(String text) {
        reset();
        parser().urlHoverText(text);
        return this;
    }

    public MineDown hoverTextWidth(int hoverTextWidth) {
        reset();
        parser().hoverTextWidth(hoverTextWidth);
        return this;
    }

    public MineDown enable(MineDownParser.Option option) {
        reset();
        parser().enable(option);
        return this;
    }

    public MineDown disable(MineDownParser.Option option) {
        reset();
        parser().disable(option);
        return this;
    }

    public MineDown filter(MineDownParser.Option option) {
        reset();
        parser().filter(option);
        return this;
    }

    public MineDown unfilter(MineDownParser.Option option) {
        reset();
        parser().unfilter(option);
        return this;
    }

    public MineDown colorChar(char colorChar) {
        reset();
        parser().colorChar(colorChar);
        return this;
    }

    public String message() {
        return this.message;
    }

    public MineDown message(String message) {
        this.message = message;
        reset();
        return this;
    }

    public Replacer replacer() {
        return this.replacer;
    }

    public MineDownParser parser() {
        return this.parser;
    }

    protected BaseComponent[] baseComponents() {
        return this.baseComponents;
    }

    public MineDown copy() {
        return (new MineDown(message())).copy(this);
    }

    public MineDown copy(MineDown from) {
        replacer().copy(from.replacer());
        parser().copy(from.parser());
        return this;
    }

    public static String getFormatString(ChatColor format) {
        if (format == ChatColor.BOLD)
            return "**";
        if (format == ChatColor.ITALIC)
            return "##";
        if (format == ChatColor.UNDERLINE)
            return "__";
        if (format == ChatColor.STRIKETHROUGH)
            return "~~";
        if (format == ChatColor.MAGIC)
            return "??";
        return "";
    }

    public static ChatColor getFormatFromChar(char c) {
        switch (c) {
            case '~':
                return ChatColor.STRIKETHROUGH;
            case '_':
                return ChatColor.UNDERLINE;
            case '*':
                return ChatColor.BOLD;
            case '#':
                return ChatColor.ITALIC;
            case '?':
                return ChatColor.MAGIC;
        }
        return null;
    }

    public static String escape(String string) {
        return (new MineDown(string)).parser().escape(string);
    }
}