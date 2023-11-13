package com.tamshep.spigot.regen.serialize.extensions;

import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import com.tamshep.spigot.regen.serialize.SerializedEntity;
import com.tamshep.spigot.regen.serialize.SerializedObject;

import java.util.Map;


public class ExtendEntityItemFrame extends SerializedEntity {

    Rotation rotation = null;

    public ExtendEntityItemFrame() {
    }

    /**
     * Constructor to create a ExtendEntityItemFrame from an Entity.
     *
     * @param <T>    extends Entity
     * @param entity the Entity to store.
     */
    public <T extends Entity> ExtendEntityItemFrame(T entity) {

        super(entity);

        ItemFrame frame = (ItemFrame) entity;

        rotation = frame.getRotation();
        customName = frame.getCustomName();

        this.items.add(frame.getItem());
    }

    @Override
    public Entity regen() {

        ItemFrame spawn = null;

        try {
            spawn = (ItemFrame) super.regen();

            spawn.setFacingDirection(facing, true);
            spawn.setRotation(rotation);

            if (customName != null) spawn.setCustomName(customName);

            if (hasInventory()) spawn.setItem(getInventory()[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return spawn;
    }

    @Override
    public ExtendEntityItemFrame clone() {

        ExtendEntityItemFrame clone = null;

        clone = (ExtendEntityItemFrame) super.clone();
        clone.rotation = this.rotation;

        return clone;
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> result = super.serialize();

        if (rotation != null) result.put("rotation", rotation.name());
        return result;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

        super.deserialize(map);

        if (map.containsKey("rotation")) this.rotation = Rotation.valueOf((String) map.get("rotation"));

        return (Z) this;
    }
}
