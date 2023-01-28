package org.opencommunity.regen.serialize.extensions;

import org.bukkit.Art;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;
import org.opencommunity.regen.serialize.SerializedEntity;
import org.opencommunity.regen.serialize.SerializedObject;

import java.util.Map;


public class ExtendEntityPainting extends SerializedEntity {

    Art art = null;

    public ExtendEntityPainting() {
    }

    /**
     * Constructor to create a ExtendEntityPainting from an Entity.
     *
     * @param <T>    extends Entity
     * @param entity the Entity to store.
     */
    public <T extends Entity> ExtendEntityPainting(T entity) {

        super(entity);

        Painting painting = (Painting) entity;

        this.art = painting.getArt();

        /*
         * Shift down half a block for Paintings with a Height of two.
         * Prevents the Painting re-spawning one block too high.
         */
        if (art.getBlockHeight() > 1)
            loc.add(0, -0.5, 0);

    }

    @Override
    public Entity regen() {

        Painting spawn = null;

        try {
            spawn = (Painting) super.regen();

            spawn.setArt(art, true);
            spawn.setFacingDirection(facing);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return spawn;
    }

    @Override
    public ExtendEntityPainting clone() {

        ExtendEntityPainting clone;

        clone = (ExtendEntityPainting) super.clone();
        clone.art = this.art;

        return clone;
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> result = super.serialize();

        if (facing != null) result.put("facing", facing.name());
        if (art != null) result.put("art", art.name());


        return result;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

        super.deserialize(map);

        if (map.containsKey("art")) this.art = Art.valueOf((String) map.get("art"));

        return (Z) this;
    }
}
