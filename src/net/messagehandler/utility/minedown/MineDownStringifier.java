package net.messagehandler.utility.minedown;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;

public class MineDownStringifier {
    private static final boolean HAS_FONT_SUPPORT = Util.hasMethod(BaseComponent.class, "getFontRaw", new Class[0]);

    private static final boolean HAS_HOVER_CONTENT_SUPPORT = Util.hasMethod(HoverEvent.class, "getContents", new Class[0]);

    private static final Method HOVER_GET_VALUE = Util.getMethod(HoverEvent.class, "getValue", new Class[0]);

    private boolean useLegacyColors = false;

    private boolean useLegacyFormatting = false;

    private boolean preferSimpleEvents = true;

    private boolean formattingInEventDefinition = false;

    private boolean colorInEventDefinition = true;

    private char colorChar = '&';

    public static final String FONT_PREFIX = "font=";

    public static final String COLOR_PREFIX = "color=";

    public static final String FORMAT_PREFIX = "format=";

    public static final String HOVER_PREFIX = "hover=";

    private StringBuilder value = new StringBuilder();

    private ChatColor color = null;

    private ClickEvent clickEvent = null;

    private HoverEvent hoverEvent = null;

    private Set<ChatColor> formats = new LinkedHashSet<>();

    public String stringify(BaseComponent... components) {
        StringBuilder sb = new StringBuilder();
        for (BaseComponent component : components) {
            if (!component.hasFormatting()) {
                appendText(sb, component);
            } else {
                if (component.getClickEvent() != null || component.getHoverEvent() != null) {
                    sb.append('[');
                    if (!formattingInEventDefinition())
                        appendFormat(sb, component);
                    if (!colorInEventDefinition())
                        appendColor(sb, component.getColor());
                } else if (component.getColorRaw() != null) {
                    appendFormat(sb, component);
                    appendColor(sb, component.getColor());
                } else {
                    appendFormat(sb, component);
                }
                appendText(sb, component);
                if (component.getExtra() != null && !component.getExtra().isEmpty())
                    sb.append(copy().stringify((BaseComponent[])component.getExtra().toArray((Object[])new BaseComponent[0])));
                if (component.getClickEvent() != this.clickEvent || component.getHoverEvent() != this.hoverEvent) {
                    this.clickEvent = component.getClickEvent();
                    this.hoverEvent = component.getHoverEvent();
                    if (!formattingInEventDefinition())
                        appendFormatSuffix(sb, component);
                    sb.append("](");
                    List<String> definitions = new ArrayList<>();
                    if (colorInEventDefinition()) {
                        StringBuilder sbi = new StringBuilder();
                        if (!preferSimpleEvents())
                            sbi.append("color=");
                        sbi.append(component.getColor().getName().toLowerCase(Locale.ROOT));
                        definitions.add(sbi.toString());
                    }
                    if (formattingInEventDefinition()) {
                        StringBuilder sbi = new StringBuilder();
                        if (!this.preferSimpleEvents)
                            sbi.append("format=");
                        sbi.append(Util.getFormats(component, true).stream().map(c -> c.getName().toLowerCase(Locale.ROOT)).collect(Collectors.joining(" ")));
                        definitions.add(sbi.toString());
                    }
                    if (HAS_FONT_SUPPORT && component.getFontRaw() != null)
                        definitions.add("font=" + component.getFontRaw());
                    if (component.getClickEvent() != null)
                        if (preferSimpleEvents() && component.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                            definitions.add(component.getClickEvent().getValue());
                        } else {
                            definitions.add(component.getClickEvent().getAction().toString().toLowerCase(Locale.ROOT) + "=" + component.getClickEvent().getValue());
                        }
                    if (component.getHoverEvent() != null) {
                        StringBuilder sbi = new StringBuilder();
                        if (preferSimpleEvents() && component.getHoverEvent().getAction() == HoverEvent.Action.SHOW_TEXT && (component
                                .getClickEvent() == null || component.getClickEvent().getAction() != ClickEvent.Action.OPEN_URL)) {
                            sbi.append("hover=");
                        } else {
                            sbi.append(component.getHoverEvent().getAction().toString().toLowerCase(Locale.ROOT)).append('=');
                        }
                        if (HAS_HOVER_CONTENT_SUPPORT) {
                            sbi.append(copy().stringify(component.getHoverEvent().getContents()));
                        } else if (HOVER_GET_VALUE != null) {
                            try {
                                sbi.append(copy().stringify((BaseComponent[])HOVER_GET_VALUE.invoke(component.getHoverEvent(), new Object[0])));
                            } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                        definitions.add(sbi.toString());
                    }
                    sb.append(definitions.stream().collect(Collectors.joining(" ")));
                    sb.append(')');
                } else {
                    appendFormatSuffix(sb, component);
                }
            }
        }
        return sb.toString();
    }

    private StringBuilder stringify(List<Content> contents) {
        StringBuilder sb = new StringBuilder();
        for (Content content : contents) {
            if (content instanceof Text) {
                Object value = ((Text)content).getValue();
                if (value instanceof BaseComponent[]) {
                    sb.append(stringify((BaseComponent[])value));
                    continue;
                }
                sb.append(value);
                continue;
            }
            if (content instanceof Entity) {
                Entity contentEntity = (Entity)content;
                sb.append(contentEntity.getId());
                if (contentEntity.getType() != null)
                    sb.append(":").append(contentEntity.getType());
                if (contentEntity.getName() != null)
                    sb.append(" ").append(stringify(new BaseComponent[] { contentEntity.getName() }));
                continue;
            }
            if (content instanceof Item) {
                Item contentItem = (Item)content;
                sb.append(contentItem.getId());
                if (contentItem.getCount() > 0)
                    sb.append("*").append(contentItem.getCount());
                if (contentItem.getTag() != null)
                    sb.append(" ").append(contentItem.getTag().getNbt());
            }
        }
        return sb;
    }

    private void appendText(StringBuilder sb, BaseComponent component) {
        if (component instanceof TextComponent) {
            sb.append(((TextComponent)component).getText());
        } else {
            throw new UnsupportedOperationException("Cannot stringify " + component.getClass().getTypeName() + " yet! Only TextComponents are supported right now. Sorry. :(");
        }
    }

    private void appendColor(StringBuilder sb, ChatColor color) {
        if (this.color != color) {
            this.color = color;
            if (useLegacyColors()) {
                sb.append(colorChar()).append(color.toString().substring(1));
            } else {
                sb.append(colorChar()).append(color.getName()).append(colorChar());
            }
        }
    }

    private void appendFormat(StringBuilder sb, BaseComponent component) {
        Set<ChatColor> formats = Util.getFormats(component, true);
        if (!formats.containsAll(this.formats)) {
            if (useLegacyFormatting()) {
                sb.append(colorChar()).append(ChatColor.RESET.toString().charAt(1));
            } else {
                Deque<ChatColor> formatDeque = new ArrayDeque<>(this.formats);
                while (!formatDeque.isEmpty()) {
                    ChatColor format = formatDeque.pollLast();
                    if (!formats.contains(format))
                        sb.append(MineDown.getFormatString(format));
                }
            }
        } else {
            formats.removeAll(this.formats);
        }
        for (ChatColor format : formats) {
            if (useLegacyFormatting()) {
                sb.append(colorChar()).append(format.toString().charAt(1));
                continue;
            }
            sb.append(MineDown.getFormatString(format));
        }
        this.formats.clear();
        this.formats.addAll(formats);
    }

    private void appendFormatSuffix(StringBuilder sb, BaseComponent component) {
        if (!useLegacyFormatting()) {
            Set<ChatColor> formats = Util.getFormats(component, true);
            for (ChatColor format : formats)
                sb.append(MineDown.getFormatString(format));
            this.formats.removeAll(formats);
        }
    }

    public MineDownStringifier copy() {
        return (new MineDownStringifier()).copy(this);
    }

    public MineDownStringifier copy(MineDownStringifier from) {
        MineDownStringifier copy = new MineDownStringifier();
        useLegacyColors(from.useLegacyColors());
        useLegacyFormatting(from.useLegacyFormatting());
        preferSimpleEvents(from.preferSimpleEvents());
        formattingInEventDefinition(from.formattingInEventDefinition());
        colorInEventDefinition(from.colorInEventDefinition());
        colorChar(from.colorChar());
        return copy;
    }

    public boolean useLegacyColors() {
        return this.useLegacyColors;
    }

    public MineDownStringifier useLegacyColors(boolean useLegacyColors) {
        this.useLegacyColors = useLegacyColors;
        return this;
    }

    public boolean useLegacyFormatting() {
        return this.useLegacyFormatting;
    }

    public MineDownStringifier useLegacyFormatting(boolean useLegacyFormatting) {
        this.useLegacyFormatting = useLegacyFormatting;
        return this;
    }

    public boolean preferSimpleEvents() {
        return this.preferSimpleEvents;
    }

    public MineDownStringifier preferSimpleEvents(boolean preferSimpleEvents) {
        this.preferSimpleEvents = preferSimpleEvents;
        return this;
    }

    public boolean colorInEventDefinition() {
        return this.colorInEventDefinition;
    }

    public MineDownStringifier colorInEventDefinition(boolean colorInEventDefinition) {
        this.colorInEventDefinition = colorInEventDefinition;
        return this;
    }

    public boolean formattingInEventDefinition() {
        return this.formattingInEventDefinition;
    }

    public MineDownStringifier formattingInEventDefinition(boolean formattingInEventDefinition) {
        this.formattingInEventDefinition = formattingInEventDefinition;
        return this;
    }

    public char colorChar() {
        return this.colorChar;
    }

    public MineDownStringifier colorChar(char colorChar) {
        this.colorChar = colorChar;
        return this;
    }
}
