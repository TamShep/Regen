package org.opencommunity.regen.serialize.extensions;

import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.opencommunity.regen.serialize.SerializedBlock;
import org.opencommunity.regen.serialize.SerializedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ExtendBlockBanner extends SerializedBlock {

    private DyeColor baseColor = null;
    private List<Pattern> patterns = new ArrayList<Pattern>();

    public ExtendBlockBanner() {
    }

    /**
     * Constructor to create a ExtendBlockBanner from a Block.
     *
     * @param <T>   extends Block
     * @param block the Block to store.
     */
    public <T extends Block> ExtendBlockBanner(T block) {

        super(block);

        Banner state = (Banner) block.getState();

        this.baseColor = state.getBaseColor();
        this.patterns = state.getPatterns();
    }

    @Override
    public BlockState regen() {

        Banner state = (Banner) super.regen();

        state.setBaseColor(getBaseColor());
        state.setPatterns(getPatterns());

        state.update(true);

        return state.getBlock().getState();
    }

    @Override
    public ExtendBlockBanner clone() {

        ExtendBlockBanner clone = null;

        clone = (ExtendBlockBanner) super.clone();

        clone.baseColor = this.baseColor;
        clone.patterns.addAll(this.patterns);

        return clone;
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> result = super.serialize();

        if (getBaseColor() != null) result.put("colour", getBaseColor().name());
        if (!getPatterns().isEmpty()) result.put("patterns", getPatterns());

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

        super.deserialize(map);

        if (map.containsKey("colour")) this.baseColor = DyeColor.valueOf((String) map.get("colour"));
        if (map.containsKey("patterns")) this.patterns = (List<Pattern>) map.get("patterns");

        return (Z) this;
    }

    /**
     * @return the baseColor
     */
    public DyeColor getBaseColor() {

        return baseColor;
    }


    /**
     * @return the patterns
     */
    public List<Pattern> getPatterns() {

        return patterns;
    }

}
