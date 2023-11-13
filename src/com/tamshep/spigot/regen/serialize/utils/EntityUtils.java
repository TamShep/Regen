package com.tamshep.spigot.regen.serialize.utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import com.tamshep.spigot.regen.serialize.SerializedEntity;
import com.tamshep.spigot.regen.serialize.extensions.ExtendEntityArmorStand;
import com.tamshep.spigot.regen.serialize.extensions.ExtendEntityItemFrame;
import com.tamshep.spigot.regen.serialize.extensions.ExtendEntityPainting;

import java.util.Map;


public class EntityUtils {

    /**
     * Deserialize an Entity based upon EntityType.
     *
     * @param entity the Entity to serialize.
     * @return Map containing the serialised data.
     */
    public static Map<String, Object> serializeByType(Entity entity) {

        switch (entity.getType()) {

            case ARMOR_STAND:

                return new ExtendEntityArmorStand(entity).serialize();

            case ITEM_FRAME:

                return new ExtendEntityItemFrame(entity).serialize();

            case PAINTING:

                return new ExtendEntityPainting(entity).serialize();

            default:

                return new SerializedEntity(entity).serialize();
        }
    }

    /**
     * Deserialize from a Map to a SerializedEntity based upon EntityType.
     *
     * @param <Z> a class which extends SerializedEntity
     * @param map the map to process.
     * @return a SerializedEntity extended class.
     */
    public static <Z extends SerializedEntity> Z deserializeByType(Map<?, ?> map) {

        EntityType type = EntityType.valueOf((String) map.get("type"));

        switch (type) {

            case ARMOR_STAND:

                return new ExtendEntityArmorStand().deserialize(map);

            case ITEM_FRAME:

                return new ExtendEntityItemFrame().deserialize(map);

            case PAINTING:

                return new ExtendEntityPainting().deserialize(map);

            default:

                return new SerializedEntity().deserialize(map);
        }
    }

}
