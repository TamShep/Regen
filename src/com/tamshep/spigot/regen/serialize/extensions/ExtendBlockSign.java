package com.tamshep.spigot.regen.serialize.extensions;

import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import com.tamshep.spigot.regen.serialize.SerializedBlock;
import com.tamshep.spigot.regen.serialize.SerializedObject;

import java.util.Map;


public class ExtendBlockSign extends SerializedBlock {

    String[] lines = null;
    private DyeColor colour = null;

    public ExtendBlockSign() {
    }

    /**
     * Constructor to create a ExtendBlockSign from a Block.
     *
     * @param <T>   extends Block
     * @param block the Block to store.
     */
    public <T extends Block> ExtendBlockSign(T block) {

        super(block);

        Sign state = (Sign) block.getState();

        this.lines = state.getLines();
        this.colour = state.getColor();
    }

    @Override
    public BlockState regen() {

        Sign state = (Sign) super.regen();

        if (lines != null && lines.length > 0) {
            for (int i = 0; i < lines.length; i++) {
                state.setLine(i, lines[i]);
            }
        }

        if (colour != null) state.setColor(colour);

        state.update(true, false);

        return state.getBlock().getState();
    }

    @Override
    public ExtendBlockSign clone() {

        ExtendBlockSign clone = null;

        clone = (ExtendBlockSign) super.clone();
        clone.lines = this.lines.clone();
        clone.colour = this.colour;

        return clone;
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> result = super.serialize();

        if (this.lines != null && this.lines.length > 0) result.put("lines", lines);
        if (this.colour != null) result.put("colour", this.colour.name());

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

        super.deserialize(map);

        if (map.containsKey("lines")) this.lines = (String[]) map.get("lines");
        if (map.containsKey("colour")) this.colour = DyeColor.valueOf((String) map.get("colour"));

        return (Z) this;
    }
}
