package com.tamshep.spigot.regen.serialize.extensions;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import com.tamshep.spigot.regen.serialize.SerializedEntity;
import com.tamshep.spigot.regen.serialize.SerializedObject;

import java.util.HashMap;
import java.util.Map;


public class ExtendEntityArmorStand extends SerializedEntity {

    Map<String, Object> poses = new HashMap<String, Object>();
    ItemStack mainHand = null;
    ItemStack offHand = null;

    public ExtendEntityArmorStand() {
    }

    /**
     * Constructor to create a ExtendEntityArmorStand from an Entity.
     *
     * @param <T>    extends Entity
     * @param entity the Entity to store.
     */
    public <T extends Entity> ExtendEntityArmorStand(T entity) {

        super(entity);

        ArmorStand stand = (ArmorStand) entity;

        if (stand.getPose() != Pose.STANDING) {

            poses.put("body", toVector(stand.getBodyPose()));
            poses.put("head", toVector(stand.getHeadPose()));
            poses.put("leftarm", toVector(stand.getLeftArmPose()));
            poses.put("leftleg", toVector(stand.getLeftLegPose()));
            poses.put("rightarm", toVector(stand.getRightArmPose()));
            poses.put("rightleg", toVector(stand.getRightLegPose()));
        }

        if (stand.getEquipment() != null) {

            mainHand = stand.getEquipment().getItemInMainHand();
            offHand = stand.getEquipment().getItemInOffHand();

            int count = 0;
            for (ItemStack itemStack : stand.getEquipment().getArmorContents()) {
                if (itemStack != null) {
                    this.items.add(itemStack);
                    if (itemStack.getType() == Material.AIR) count++;
                }
            }
            /*
             * If we only added AIR we have no inventory.
             */
            if (count == this.items.size())
                this.items.clear();
        }
    }

    @Override
    public Entity regen() {

        ArmorStand spawn = null;

        try {
            spawn = (ArmorStand) super.regen();

            if (hasInventory())
                spawn.getEquipment().setArmorContents(getInventory());

            if (!poses.isEmpty()) {
                spawn.setBodyPose(toEulerAngle((Vector) poses.get("body")));
                spawn.setHeadPose(toEulerAngle((Vector) poses.get("head")));
                spawn.setLeftArmPose(toEulerAngle((Vector) poses.get("leftarm")));
                spawn.setLeftLegPose(toEulerAngle((Vector) poses.get("leftleg")));
                spawn.setRightArmPose(toEulerAngle((Vector) poses.get("rightarm")));
                spawn.setRightLegPose(toEulerAngle((Vector) poses.get("rightleg")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return spawn;
    }

    @Override
    public ExtendEntityArmorStand clone() {

        ExtendEntityArmorStand clone = null;

        clone = (ExtendEntityArmorStand) super.clone();
        clone.poses.putAll(this.poses);
        clone.mainHand = this.mainHand.clone();
        clone.offHand = this.offHand.clone();

        return clone;
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> result = super.serialize();

        if (mainHand != null && mainHand.getType() != Material.AIR) result.put("mainhand", mainHand.serialize());
        if (offHand != null && offHand.getType() != Material.AIR) result.put("offhand", offHand.serialize());

        if (!poses.isEmpty()) result.put("pose", poses);

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

        super.deserialize(map);

        if (map.containsKey("pose")) this.poses = (castToMap(map.get("pose")));

        if (map.containsKey("mainhand")) this.mainHand = ItemStack.deserialize(castToMap(map.get("mainhand")));
        if (map.containsKey("offhand")) this.offHand = ItemStack.deserialize(castToMap(map.get("offhand")));

        return (Z) this;
    }

    private Vector toVector(EulerAngle data) {

        return new Vector(data.getX(), data.getY(), data.getZ());
    }

    private EulerAngle toEulerAngle(Vector data) {

        return new EulerAngle(data.getX(), data.getY(), data.getZ());
    }

}
