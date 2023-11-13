package com.tamshep.spigot.regen.serialize;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * A representation of a Block we can
 * serialize/deserialize and use to
 * replace a block in the World.
 *
 * @author ElgarL
 */
public class SerializedBlock extends SerializedObject {

    protected BlockData blockData = null;

    public SerializedBlock() {
    }

    /**
     * Constructor to create a SerializedBlock from a Block.
     *
     * @param <T>   extends Block
     * @param block the Block to store.
     */
    public <T extends Block> SerializedBlock(T block) {

        BlockState state = block.getState();
        this.loc = state.getLocation();
        this.blockData = state.getBlockData();

        /*
         * Read Inventories.
         */
        Inventory container = null;

        switch (state.getType()) {
            case LECTERN:
                container = ((Lectern) state).getSnapshotInventory();
            case JUKEBOX:
                ItemStack disc = ((Jukebox) state).getRecord();
                if (disc != null) this.items.add(disc);
            default:
                if (state instanceof Container) {
                    container = ((Container) state).getSnapshotInventory();
                }
        }
        /*
         * Store this Inventory eliminating null.
         */
        if ((container != null) && !container.isEmpty()) {

            for (ItemStack itemStack : container.getContents()) {
                if (itemStack != null)
                    this.items.add(itemStack);
            }
        }
    }

    @Override
    public BlockState regen() {

        Block block = getLocation().getBlock();
        BlockState state = block.getState();

        if (block.getType() != this.getType()) {

            state.setType(this.getType());
            state.setBlockData(this.getBlockData());

            /*
             * Change the block with physics
             * if conditions are met.
             */
            state.update(true, applyPhysics(block));

            // Refresh our snapshot.
            state = state.getBlock().getState();

            /*
             * Refill Inventories.
             */
            if (this.hasInventory()) {

                Inventory container = null;

                switch (state.getType()) {
                    case LECTERN:
                        container = ((Lectern) state).getInventory();
                    case JUKEBOX:
                        ((Jukebox) state).setRecord(this.items.get(0));
                        state.update(true, false);
                    default:
                        if (state instanceof Container) {
                            container = ((Container) state).getInventory();
                        }
                }

                if (container != null) container.setContents(this.getInventory());
            }
        }
        return state.getBlock().getState();
    }

    @Override
    public SerializedBlock clone() {

        SerializedBlock clone;

        clone = (SerializedBlock) super.clone();
        clone.blockData = this.blockData.clone();

        return clone;
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> result = super.serialize();

        result.put("blockData", blockData.getAsString());

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

        super.deserialize(map);

        this.blockData = Bukkit.createBlockData((String) map.get("blockData"));

        return (Z) this;
    }

    /**
     * @return the Material for this Block.
     */
    public Material getType() {

        return this.blockData.getMaterial();
    }

    /**
     * @return the BlockData for this Block.
     */
    public BlockData getBlockData() {

        return this.blockData;
    }

    /**
     * Determine whether we need to apply
     * physics when replacing this block.
     *
     * @param block the Block we are replacing.
     * @return true if applying physics.
     */
    private boolean applyPhysics(Block block) {

        if (Tag.FENCES.isTagged(getType())) return true;
        return block.isLiquid();
    }

}
