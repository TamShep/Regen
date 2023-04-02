package org.opencommunity.regen;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.opencommunity.regen.serialize.SerializedBlock;
import org.opencommunity.regen.serialize.utils.BlockUtils;
import org.opencommunity.regen.serialize.utils.EntityUtils;
import org.opencommunity.regen.serialize.utils.Scheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles saving, loading, decoding
 * and processing of data in preparation
 * of regeneration.
 *
 * @author ElgarL
 */
public class FileHandler {

    private final File blockSourceFolder;
    private final File entitySourceFolder;
    private final Regen plugin;
    private final Object decodeLock = new Object();
    private final Object fileLock = new Object();
    private FileConfiguration config;
    public long delay;
    public long frequency;
    public List<Material> skipMaterials;

    /**
     * Constructor for the FileHandler.
     *
     * @param plugin Regen instance.
     */
    public FileHandler(Regen plugin) {

        this.plugin = plugin;

        blockSourceFolder = new File(plugin.getDataFolder() + File.separator + "blocks");
        if (!blockSourceFolder.exists())
            blockSourceFolder.mkdirs();

        entitySourceFolder = new File(plugin.getDataFolder() + File.separator + "entities");
        if (!entitySourceFolder.exists())
            entitySourceFolder.mkdirs();
    }

    public void loadConfig() {

        plugin.saveDefaultConfig();
        /*
         * Ensure we load the config clean in case of a reload.
         */
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public void init() {
        this.delay = getDelay();
        this.frequency = getFrequency();
        this.skipMaterials = getSkipMaterials();
    }

    public Long getDelay() {

        return config.getLong("delay", 10L);
    }

    public Long getFrequency() {

        return config.getLong("frequency", 5L);
    }

    public List<Material> getSkipMaterials() {
        List<String> skipMaterialNames = config.getStringList("skipMaterials");
        if (skipMaterialNames == null) {
            throw new NullPointerException("The 'skipMaterials' list cannot be null.");
        }
        return skipMaterialNames.stream()
                .map(String::toUpperCase)
                .map(Material::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * Attempt to serialize an Entity and save to file.
     * Pass the serialized data to be regenerated.
     *
     * @param <T>    extends Entity
     * @param entity the Entity to process.
     */
    public <T extends Entity> void saveEntity(final T entity) {

        YamlConfiguration data = new YamlConfiguration();
        String uid = UUID.randomUUID().toString();

        data.createSection(uid, EntityUtils.serializeByType(entity));

        File file = new File(entitySourceFolder, uid + ".yml");
        saveData(data, file);

        // Schedule a regen.

        Scheduler.scheduleAsyncDelayedTask(plugin, () -> {
            decodeEntityData(data);
        }, delay * 20);

    }

    /**
     * Attempt to serialize a List of Blocks and save to file.
     * Pass the serialized data to be regenerated.
     *
     * @param <T>    extends Block
     * @param blocks the Blocks to process.
     */
    public <T extends Block> void saveBlocks(final List<T> blocks) {

        if (blocks == null || blocks.isEmpty()) return;

        YamlConfiguration data = new YamlConfiguration();
        List<Map<String, Object>> explosion = new ArrayList<>();

        blocks.forEach(block -> {
            explosion.add(BlockUtils.serializeByType(block));
        });

        String uid = UUID.randomUUID().toString();
        data.set(uid, explosion);


        File file = new File(blockSourceFolder, uid + ".yml");
        saveData(data, file);

        // Schedule a regen.

        Scheduler.scheduleAsyncDelayedTask(plugin, () -> {
            decodeBlockData(data);
        }, delay * 20);
    }

    /**
     * Load all entities from files
     * and begin deserializing.
     */
    public void loadEntities() {

        synchronized (fileLock) {
            for (String name : Objects.requireNonNull(entitySourceFolder.list())) {

                File file = new File(entitySourceFolder, name);

                if (!file.exists() || (file.length() == 0)) return;

                YamlConfiguration data = new YamlConfiguration();

                try {
                    data.load(file);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }

                // Decode the data

                decodeEntityData(data);
            }
        }
    }

    /**
     * Load all blocks from files
     * and begin deserializing.
     */
    public void loadBlocks() {

        synchronized (fileLock) {
            for (String name : Objects.requireNonNull(blockSourceFolder.list())) {

                File file = new File(blockSourceFolder, name);

                if (!file.exists() || (file.length() == 0)) continue;

                YamlConfiguration data = new YamlConfiguration();

                try {
                    data.load(file);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }

                // Decode the data

                decodeBlockData(data);
            }
        }
    }

    /**
     * Deserialize data back into SerializedBlock Objects
     * and pass for regeneration.
     *
     * @param data
     */
    private void decodeBlockData(YamlConfiguration data) {

        synchronized (decodeLock) {
            try {
                for (String key : data.getKeys(false)) {

                    List<? extends SerializedBlock> blocks = new ArrayList<>();

                    List<Map<?, ?>> mapList = data.getMapList(key);

                    for (Map<?, ?> map : mapList) {

                        blocks.add(BlockUtils.deserializeByType(map));
                    }

                    if (!blocks.isEmpty()) plugin.getTaskHolder().addBlocks(key, blocks);
                }

            } catch (Exception e) {
                System.out.println("Failed Decoding Blocks to respawn.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Deserialize data back into a SerializedEntity Object
     * and pass for regeneration.
     *
     * @param data
     */
    private void decodeEntityData(YamlConfiguration data) {

        synchronized (decodeLock) {
            try {
                for (String key : data.getKeys(false)) {

                    Map<String, Object> map = Objects.requireNonNull(data.getConfigurationSection(key)).getValues(true);

                    plugin.getTaskHolder().addEntity(key, EntityUtils.deserializeByType(map));
                }

            } catch (Exception e) {
                System.out.println("Failed Decoding Entities to respawn.");
                e.printStackTrace();
            }
        }
    }

    private void saveData(FileConfiguration data, File file) {

        Scheduler.runTaskAsynchronously(plugin, () -> {
            synchronized (fileLock) {
                try {
                    data.save(file);
                } catch (IOException e) {
                    System.err.println("Saving data error.");
                    e.printStackTrace();
                }
            }
        });
    }

    protected void deleteBlockData(String uid) {

        Scheduler.runTaskAsynchronously(plugin, () -> {
            synchronized (fileLock) {
                File file = new File(blockSourceFolder, uid + ".yml");

                if (!file.exists()) return;

                file.delete();
            }
        });
    }

    protected void deleteEntityData(String uid) {

        Scheduler.runTaskAsynchronously(plugin, () -> {
            synchronized (fileLock) {
                File file = new File(entitySourceFolder, uid + ".yml");

                if (!file.exists()) return;

                file.delete();
            }
        });
    }
}
