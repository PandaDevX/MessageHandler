package net.messagehandler.utility.minedown;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Replacer {
    private static final boolean HAS_KEYBIND_SUPPORT = Util.hasClass("net.md_5.bungee.api.chat.KeybindComponent");

    private static final boolean HAS_HOVER_CONTENT_SUPPORT = Util.hasMethod(HoverEvent.class, "getContents", new Class[0]);

    private static final Method HOVER_GET_VALUE = Util.getMethod(HoverEvent.class, "getValue", new Class[0]);

    private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

    private static final Function<String, Pattern> PATTERN_CREATOR;

    static {
        PATTERN_CREATOR = (p -> Pattern.compile(p, 16));
    }

    private final Map<String, String> replacements = new LinkedHashMap<>();

    private final Map<String, BaseComponent[]> componentReplacements = (Map)new LinkedHashMap<>();

    private String placeholderPrefix = "%";

    private String placeholderSuffix = "%";

    private boolean ignorePlaceholderCase = true;

    public static String replaceIn(String message, String... replacements) {
        return (new Replacer()).replace(replacements).replaceIn(message);
    }

    public static BaseComponent[] replaceIn(BaseComponent[] message, String... replacements) {
        return (new Replacer()).replace(replacements).replaceIn(message);
    }

    public static BaseComponent[] replaceIn(BaseComponent[] message, String placeholder, BaseComponent... replacement) {
        return (new Replacer()).replace(placeholder, replacement).replaceIn(message);
    }

    public Replacer replace(String... replacements) {
        Util.validate((replacements.length % 2 == 0), "The replacement length has to be even, mapping i % 2 == 0 to the placeholder and i % 2 = 1 to the placeholder's value");
        Map<String, String> replacementMap = new LinkedHashMap<>();
        for (int i = 0; i + 1 < replacements.length; i += 2)
            replacementMap.put(replacements[i], replacements[i + 1]);
        return replace(replacementMap);
    }

    public Replacer replace(Map<String, ?> replacements) {
        if (replacements != null && !replacements.isEmpty()) {
            Object any = replacements.values().stream().filter(Objects::nonNull).findAny().orElse(null);
            if (any instanceof String) {
                replacements().putAll((Map<? extends String, ? extends String>) replacements);
            } else if (any != null && any.getClass().isArray() && BaseComponent.class.isAssignableFrom(any.getClass().getComponentType())) {
                componentReplacements().putAll((Map<? extends String, ? extends BaseComponent[]>) replacements);
            } else {
                for (Map.Entry<String, ?> entry : replacements.entrySet())
                    replacements().put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        return this;
    }

    public Replacer replace(String placeholder, BaseComponent... replacement) {
        componentReplacements().put(placeholder, replacement);
        return this;
    }

    public Replacer placeholderIndicator(String placeholderIndicator) {
        placeholderPrefix(placeholderIndicator);
        placeholderSuffix(placeholderIndicator);
        return this;
    }

    public BaseComponent[] replaceIn(BaseComponent... components) {
        return replaceIn(Arrays.asList(components));
    }

    public BaseComponent[] replaceIn(List<BaseComponent> components) {
        List<BaseComponent> returnList = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            BaseComponent component = ((BaseComponent)components.get(i)).duplicate();
            if (HAS_KEYBIND_SUPPORT && component instanceof KeybindComponent)
                ((KeybindComponent)component).setKeybind(replaceIn(((KeybindComponent)component).getKeybind()));
            if (component instanceof TextComponent)
                ((TextComponent)component).setText(replaceIn(((TextComponent)component).getText()));
            if (component instanceof TranslatableComponent) {
                ((TranslatableComponent)component).setTranslate(replaceIn(((TranslatableComponent)component).getTranslate()));
                ((TranslatableComponent)component).setWith(Arrays.asList(replaceIn(((TranslatableComponent)component).getWith())));
            }
            if (component.getClickEvent() != null)
                component.setClickEvent(new ClickEvent(component
                        .getClickEvent().getAction(),
                        replaceIn(component.getClickEvent().getValue())));
            if (component.getHoverEvent() != null)
                if (HAS_HOVER_CONTENT_SUPPORT) {
                    component.setHoverEvent(new HoverEvent(component
                            .getHoverEvent().getAction(),
                            replaceInContents(component.getHoverEvent().getContents())));
                } else if (HOVER_GET_VALUE != null) {
                    try {
                        component.setHoverEvent(new HoverEvent(component
                                .getHoverEvent().getAction(),
                                replaceIn((BaseComponent[])HOVER_GET_VALUE.invoke(component.getHoverEvent(), new Object[0]))));
                    } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            if (component.getExtra() != null)
                component.setExtra(Arrays.asList(replaceIn(component.getExtra())));
            List<BaseComponent> replacedComponents = new ArrayList<>();
            replacedComponents.add(component);
            for (Map.Entry<String, BaseComponent[]> replacement : componentReplacements().entrySet()) {
                List<BaseComponent> newReplacedComponents = new ArrayList<>();
                label72: for (BaseComponent replaceComponent : replacedComponents) {
                    if (replaceComponent instanceof TextComponent) {
                        TextComponent textComponent = (TextComponent)replaceComponent;
                        String placeHolder = placeholderPrefix() + (ignorePlaceholderCase() ? ((String)replacement.getKey()).toLowerCase(Locale.ROOT) : replacement.getKey()) + placeholderSuffix();
                        String text = ignorePlaceholderCase() ? textComponent.getText().toLowerCase(Locale.ROOT) : textComponent.getText();
                        int index = text.indexOf(placeHolder);
                        if (index > -1)
                            while (true) {
                                TextComponent startComponent = new TextComponent(textComponent);
                                if (index > 0) {
                                    startComponent.setText(textComponent.getText().substring(0, index));
                                } else {
                                    startComponent.setText("");
                                }
                                startComponent.setExtra(Arrays.asList((BaseComponent[])replacement.getValue()));
                                newReplacedComponents.add(startComponent);
                                if (index + placeHolder.length() < textComponent.getText().length()) {
                                    textComponent.setText(textComponent.getText().substring(index + placeHolder.length()));
                                } else {
                                    textComponent.setText("");
                                }
                                text = ignorePlaceholderCase() ? textComponent.getText().toLowerCase(Locale.ROOT) : textComponent.getText();
                                newReplacedComponents.add(textComponent);
                                if (!text.isEmpty()) {
                                    if ((index = text.indexOf(placeHolder)) <= -1)
                                        continue label72;
                                    continue;
                                }
                                continue label72;
                            }
                    }
                    newReplacedComponents.add(replaceComponent);
                }
                replacedComponents = newReplacedComponents;
            }
            returnList.addAll(replacedComponents);
        }
        return returnList.<BaseComponent>toArray(new BaseComponent[0]);
    }

    private List<Content> replaceInContents(List<Content> contents) {
        List<Content> replacedContents = new ArrayList<>();
        for (Content content : contents) {
            if (content instanceof Text) {
                Object value = ((Text)content).getValue();
                if (value instanceof BaseComponent[]) {
                    replacedContents.add(new Text(replaceIn((BaseComponent[])value)));
                    continue;
                }
                if (value instanceof String) {
                    replacedContents.add(new Text(replaceIn((String)value)));
                    continue;
                }
                throw new UnsupportedOperationException("Cannot replace in " + value.getClass() + "!");
            }
            if (content instanceof Entity) {
                String type;
                TextComponent textComponent = null;
                Entity entity = (Entity)content;
                String id = replaceIn(entity.getId());
                if (entity.getType() != null) {
                    type = replaceIn(entity.getType());
                } else {
                    type = "minecraft:pig";
                }
                BaseComponent name = null;
                if (entity.getName() != null)
                    textComponent = new TextComponent(replaceIn(TextComponent.toLegacyText(new BaseComponent[] { entity.getName() })));
                replacedContents.add(new Entity(type, id, (BaseComponent)textComponent));
                continue;
            }
            if (content instanceof Item) {
                Item item = (Item)content;
                String id = replaceIn(item.getId());
                ItemTag itemTag = (item.getTag() != null) ? ItemTag.ofNbt(replaceIn(item.getTag().getNbt())) : null;
                replacedContents.add(new Item(id, item.getCount(), itemTag));
                continue;
            }
            replacedContents.add(content);
        }
        return replacedContents;
    }

    public String replaceIn(String string) {
        for (Map.Entry<String, String> replacement : replacements().entrySet()) {
            String replValue = (replacement.getValue() != null) ? replacement.getValue() : "null";
            if (ignorePlaceholderCase()) {
                String str = placeholderPrefix() + ((String)replacement.getKey()).toLowerCase(Locale.ROOT) + placeholderSuffix();
                int nextStart = 0;
                int startIndex;
                while (nextStart < string.length() && (startIndex = string.toLowerCase(Locale.ROOT).indexOf(str, nextStart)) > -1) {
                    nextStart = startIndex + replValue.length();
                    string = string.substring(0, startIndex) + replValue + string.substring(startIndex + str.length());
                }
                continue;
            }
            String placeholder = placeholderPrefix() + (String)replacement.getKey() + placeholderSuffix();
            Pattern pattern = PATTERN_CACHE.computeIfAbsent(placeholder, PATTERN_CREATOR);
            string = pattern.matcher(string).replaceAll(Matcher.quoteReplacement(replValue));
        }
        return string;
    }

    public Replacer copy() {
        return (new Replacer()).copy(this);
    }

    public Replacer copy(Replacer from) {
        replacements().clear();
        replacements().putAll(from.replacements());
        componentReplacements().clear();
        componentReplacements().putAll((Map)from.componentReplacements());
        placeholderPrefix(from.placeholderPrefix());
        placeholderSuffix(from.placeholderSuffix());
        return this;
    }

    public Map<String, String> replacements() {
        return this.replacements;
    }

    public Map<String, BaseComponent[]> componentReplacements() {
        return this.componentReplacements;
    }

    public String placeholderPrefix() {
        return this.placeholderPrefix;
    }

    public Replacer placeholderPrefix(String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
        return this;
    }

    public String placeholderSuffix() {
        return this.placeholderSuffix;
    }

    public Replacer placeholderSuffix(String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
        return this;
    }

    public boolean ignorePlaceholderCase() {
        return this.ignorePlaceholderCase;
    }

    public Replacer ignorePlaceholderCase(boolean ignorePlaceholderCase) {
        this.ignorePlaceholderCase = ignorePlaceholderCase;
        return this;
    }
}
