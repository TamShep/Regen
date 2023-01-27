package org.opencommunity.regen.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * If this event is cancelled the entity that was destroyed will not be regenerated.
 * It will however drop any block and inventory it normally would.
 *
 * @author ElgarL
 */
public class EntityRegenEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Entity entity;
    private boolean cancel;

    /**
     * Constructor for an Entity regen event.
     *
     * @param entity the Entity that was destroyed.
     */
    public EntityRegenEvent(Entity entity) {

        this.entity = entity;
        this.cancel = false;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Returns the Entity that
     * will be or has been removed
     * by an explosion event.
     *
     * @return blown-up Entity
     */
    public Entity entity() {
        return this.entity;
    }

    @Override
    public HandlerList getHandlers() {

        return handlers;
    }

}

