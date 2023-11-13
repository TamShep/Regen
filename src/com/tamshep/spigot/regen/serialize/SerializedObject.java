package com.tamshep.spigot.regen.serialize;

import org.bukkit.*;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class SerializedObject implements Cloneable {

    protected List<ItemStack> items = new ArrayList<>();
    protected Location loc = null;

    public SerializedObject() {
    }

    /**
     * Attempt to place the block or Entity
     * this represents back into the world.
     *
     * @return the Entity or BlockState this
     * created, or null if it failed
     */
    public abstract Object regen();

    @Override
    public SerializedObject clone() {

        SerializedObject clone = null;
        try {
            clone = (SerializedObject) super.clone();
            clone.loc = this.loc.clone();
            clone.items.addAll(this.items);

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    /**
     * Convert this Object to a {@literal Map<String, Object>} in
     * Preparation for saving or processing back into
     * an identical Object
     *
     * @return a {@literal Map<String, Object>}
     * containing the serialised data of this Object.
     */
    public Map<String, Object> serialize() {

        Map<String, Object> result = new HashMap<>();

        result.put("location", loc.serialize());

        if (hasInventory()) {
            List<Map<String, Object>> inventory = new ArrayList<>();

            /*
             * Add individually to eliminate
             * the class entry in the final map.
             */
            for (ItemStack itemStack : this.items) {
                inventory.add(itemStack.serialize());
            }
            result.put("inventory", inventory);
        }

        return result;
    }

    /**
     * Converts a serialised Map representation
     * of this class back into a usable Object.
     *
     * @param <Z> extends SerializedObject.
     * @param map a {@literal Map<?, ?>} to convert.
     * @throws IllegalArgumentException if the World doesn't exist.
     * @return a deserialized SerializedObject.
     */
    @SuppressWarnings("unchecked")
    public <Z extends SerializedObject> Z deserialize(Map<?, ?> map) {

        this.loc = Location.deserialize(castToMap(map.get("location")));

        if (map.containsKey("inventory")) {

            List<Map<String, Object>> inventory = (List<Map<String, Object>>) map.get("inventory");

            for (Map<String, Object> item : inventory) {
                ItemStack stack = ItemStack.deserialize(item);
                this.items.add(stack);
            }
        }

        return (Z) this;
    }

    /**
     * @return true if this Object has an Inventory
     */
    public Boolean hasInventory() {

        return this.items != null && !this.items.isEmpty();
    }

    /**
     * @return an array of the Inventory contents.
     */
    public ItemStack[] getInventory() {

        return this.items.toArray(new ItemStack[0]);
    }

    /**
     * @return the Location for this Object.
     */
    public Location getLocation() {

        return this.loc;
    }

    /**
     * @param location to set for this Object
     */
    public void setLocation(Location location) {

        this.loc = location;
    }

    /**
     * If loading from file the Maps in the data
     * will be stored as a MemorySection not Maps.
     *
     * @param entry MemorySection or Map to check.
     * @return Map containing the serialised data.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> castToMap(Object entry) {

        if (entry instanceof MemorySection) {
            return ((MemorySection) entry).getValues(true);
        } else {
            return (Map<String, Object>) entry;
        }
    }
}
