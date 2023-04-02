package org.opencommunity.regen;

import org.bukkit.*;
import org.bukkit.scheduler.BukkitTask;
import org.opencommunity.regen.serialize.SerializedBlock;
import org.opencommunity.regen.serialize.SerializedObject;
import org.opencommunity.regen.serialize.utils.Scheduler;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Holds the Queues for regeneration
 * and the Async Task used for processing.
 *
 * @author ElgarL
 */
public class TaskHolder {

    private final Queue<Entry<String, List<SerializedBlock>>> blockQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Entry<String, SerializedObject>> entityQueue = new ConcurrentLinkedQueue<>();

    private BukkitTask task = null;

    private final Regen plugin;
    private final FileHandler handler;
    private long frequency;
    private long delay;

    /**
     * Constructor for the Asynchronous TaskHolder.
     *
     * @param plugin Regen instance.
     */
    public TaskHolder(Regen plugin) {

        this.plugin = plugin;
        handler = this.plugin.getFileHandler();
        this.frequency = handler.frequency;
        this.delay = handler.delay;
    }

    /**
     * Add blocks to the queue for regeneration.
     *
     * @param key    A Unique ID to store this regeneration group under.
     * @param blocks A collection of blocks to regenerate.
     */
    public void addBlocks(String key, Collection<? extends SerializedBlock> blocks) {

        List<SerializedBlock> regen = new ArrayList<>(blocks);

        regen.sort(Comparator.comparingInt(block -> block.getLocation().getBlockY()));

        if (regen.size() > 0) {

            Entry<String, List<SerializedBlock>> entry = new AbstractMap.SimpleEntry<>(key, regen);

            blockQueue.add(entry);

            if (task == null)
                startMonitor();
        }
    }

    /**
     * Add this entity to the queue to be regenerated.
     *
     * @param key    A Unique ID to store this regeneration.
     * @param entity An entity to re-spawn.
     */
    public void addEntity(String key, SerializedObject entity) {

        if (entity != null) {

            Entry<String, SerializedObject> entry = new AbstractMap.SimpleEntry<>(key, entity);

            entityQueue.add(entry);

            if (task == null)
                startMonitor();
        }
    }

    private void startMonitor() {

        Scheduler.runTaskTimerAsynchronously(plugin, () -> {

            if (!blockQueue.isEmpty()) {

                int size = blockQueue.size();
                Entry<String, List<SerializedBlock>> entry;
                /*
                 * Pull every entry and regen one
                 * block from each list per pass.
                 */
                for (int i = 0; i < size; i++) {
                    entry = blockQueue.poll();

                    if (entry == null) {
                        // Handle the case when entry is null
                        continue;
                    }
                    List<SerializedBlock> blocks = entry.getValue();
                    SerializedObject block = blocks.remove(0);

                    Location loc = block.getLocation();
                    World world = loc.getWorld();
                    Material type = ((SerializedBlock) block).getType();
                    // Add block paste sound
                    world.playSound(loc, Sound.BLOCK_GRASS_PLACE, 1, 1);
                    // Add block break particles
                    world.playEffect(loc, Effect.STEP_SOUND, type);

                    Scheduler.runTask(plugin, block::regen);

                    if (blocks.size() > 0) {
                        /*
                         * Add the entry back onto the
                         * end of the queue as there
                         * are more blocks to regen.
                         */
                        entry.setValue(blocks);
                        blockQueue.add(entry);

                    } else {
                        //System.out.println("Finished: " + entry.getKey());
                        /*
                         * Delete any data files for this entry
                         * as we have finished its regen.
                         */
                        handler.deleteBlockData(entry.getKey());
                    }
                }
            } else if (!entityQueue.isEmpty()) {
                /*
                 * Regen entities one at a time.
                 */
                Entry<String, SerializedObject> entry = entityQueue.poll();

                Scheduler.runTask(plugin, () -> entry.getValue().regen());
                /*
                 * Delete any data files for this entry
                 * as we have finished its regen.
                 */
                handler.deleteEntityData(entry.getKey());
            }
        }, delay * 20, frequency);
    }

    /**
     * Call from onDisable() to stop the Asynchronous Task.
     */
    public void cancelTask() {

        if (task != null) task.cancel();
    }

}
