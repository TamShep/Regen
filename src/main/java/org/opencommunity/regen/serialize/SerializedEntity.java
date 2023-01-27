package org.opencommunity.regen.serialize;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Map;


public class SerializedEntity extends SerializedObject {

    protected EntityType type = null;
    protected BlockFace facing = null;
    protected String customName = null;

    public SerializedEntity() {
    }

    /**
     * Constructor to create a SerializedEntity from an Entity.
     *
     * @param <T>    extends Entity.
     * @param entity the Entity to store.
     */
    public <T extends Entity> SerializedEntity(T entity) {

        this.loc = entity.getLocation();

        this.type = entity.getType();
        this.facing = entity.getFacing();
        this.customName = entity.getCustomName();
    }

    @Override
    public Entity regen() {

        Entity spawn = null;

        try {
            spawn = this.loc.getWorld().spawnEntity(loc, type);
            spawn.setCustomName(customName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return spawn;
    }

    @Override
    public SerializedEntity clone() {

        SerializedEntity clone = null;

        clone = (SerializedEntity) super.clone();
        clone.type = this.type;
        clone.facing = this.facing;
        clone.customName = this.customName;

        return clone;
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> result = super.serialize();

        result.put("type", type.name());
        if (facing != null) result.put("facing", facing.name());
        if (customName != null) result.put("name", customName);

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

        SerializedEntity result = super.deserialize(map);

        result.type = EntityType.valueOf((String) map.get("type"));
        if (map.containsKey("facing")) this.facing = BlockFace.valueOf((String) map.get("facing"));
        if (map.containsKey("name")) this.customName = (String) map.get("name");

        return (Z) result;
    }

    /**
     * @return the type
     */
    public EntityType getType() {

        return type;
    }

}
