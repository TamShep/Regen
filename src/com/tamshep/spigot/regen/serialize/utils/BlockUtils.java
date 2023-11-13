package com.tamshep.spigot.regen.serialize.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import com.tamshep.spigot.regen.serialize.SerializedBlock;
import com.tamshep.spigot.regen.serialize.extensions.ExtendBlockBanner;
import com.tamshep.spigot.regen.serialize.extensions.ExtendBlockBeacon;
import com.tamshep.spigot.regen.serialize.extensions.ExtendBlockSign;

import java.util.Map;


public class BlockUtils {

    /**
     * Serialize a Block based upon Material type.
     *
     * @param block the Block to serialize.
     * @return Map containing the serialised data.
     */
    public static Map<String, Object> serializeByType(Block block) {

        Material type = block.getType();

        if (Tag.BANNERS.isTagged(type)) {

            return new ExtendBlockBanner(block).serialize();

        } else if (Tag.SIGNS.isTagged(type)) {

            return new ExtendBlockSign(block).serialize();

        } else {

            if (type == Material.BEACON) {
                return new ExtendBlockBeacon(block).serialize();
            }
            return new SerializedBlock(block).serialize();
        }
    }

    /**
     * Deserialize from a Map to a SerializedBlock based upon Material type.
     *
     * @param <Z> a class which extends SerializedBlock
     * @param map the map to process.
     * @throws IllegalArgumentException thrown from createBlockData.
     * @return a SerializedBlock extended class.
     */
    public static <Z extends SerializedBlock> Z deserializeByType(Map<?, ?> map) throws IllegalArgumentException {

        Material type = Bukkit.createBlockData((String) map.get("blockData")).getMaterial();

        if (Tag.BANNERS.isTagged(type)) {

            return new ExtendBlockBanner().deserialize(map);

        } else if (Tag.SIGNS.isTagged(type)) {

            return new ExtendBlockSign().deserialize(map);

        } else {

            if (type == Material.BEACON) {
                return new ExtendBlockBeacon().deserialize(map);
            }
            return new SerializedBlock().deserialize(map);
        }
    }

}
