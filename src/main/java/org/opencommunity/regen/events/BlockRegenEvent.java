package org.opencommunity.regen.events;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * Fired before a group of Blocks are destroyed due to an explosion.
 * <p>
 * Remove blocks from this event to prevent them being damaged.
 * Any remaining blocks will not drop items and will be regenerated.
 * <p>
 * If we cancel this event the explosion will still happen and blocks
 * will drop as items; however, no regeneration will occur.
 *
 * @author ElgarL
 */
public class BlockRegenEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final List<Block> blockList;
    private boolean cancel;

    /**
     * Constructor for a new BlockList regen event.
     *
     * @param blockList a List of Blocks.
     */
    public BlockRegenEvent(List<Block> blockList) {

        this.blockList = blockList;
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
     * Returns the list of blocks that
     * will be or have been removed
     * by an explosion event.
     *
     * @return All blown-up blocks
     */
    public List<Block> blockList() {
        return this.blockList;
    }

    @Override
    public HandlerList getHandlers() {

        return handlers;
    }

}
