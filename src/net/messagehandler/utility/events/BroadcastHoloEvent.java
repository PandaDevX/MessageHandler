package net.messagehandler.utility.events;

import net.messagehandler.utility.Utility;
import net.messagehandler.utility.holo.broadcast.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BroadcastHoloEvent extends Event implements Cancellable {
    private List<String> message;
    private long death = 0;
    private static final HandlerList handlerList = new HandlerList();
    private Player recipient;
    private Hologram hologram;
    private boolean isCancelled;
    private final Location loc;
    private String sender;


    public BroadcastHoloEvent(String sender, Player recipient) {
        this.sender = sender;
        this.recipient = recipient;
        this.loc = recipient.getLocation().add(recipient.getLocation().getDirection().multiply(4));
        this.isCancelled = false;
        this.message = new ArrayList<>();
    }
    public BroadcastHoloEvent(Player recipient) {
        this.sender = "System";
        this.recipient = recipient;
        this.loc = recipient.getLocation().add(recipient.getLocation().getDirection().multiply(4));
        this.isCancelled = false;
        this.message = new ArrayList<>();
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public void setMessage(int line, String msg) {
        this.getMessage().set(line, msg);
    }

    public void setMessage(List<String> list) {
        this.message = list;
    }

    public void setMessage(String msg) {
        this.message = Arrays.asList(msg.split(","));
    }

    public List<String> getMessage() {
        return Utility.colorizeList(this.message, recipient);
    }

    public void createHologram() {
        hologram = new Hologram(loc);
        Collections.reverse(getMessage());
        for(int i = 0; i < getMessage().size(); i++) {
            hologram.updateLine(i, getMessage().get(i));
        }
    }

    public void removeHologram() {
        this.hologram.clear();
    }

    public int getSecondsBeforeDeath() {
        return (int) ((this.death - System.currentTimeMillis()) / 1000);
    }

    public long setDeathInterval(int seconds) {
        return this.death = System.currentTimeMillis() + (seconds * 1000);
    }

    public Player getRecipient() {
        return this.recipient;
    }

    public Hologram getHologram() {
        return this.hologram;
    }

    public Player getSender() {
        return Bukkit.getPlayer(sender);
    }

    public String getSenderString() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }


    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
