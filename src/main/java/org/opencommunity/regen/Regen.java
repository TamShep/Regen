package org.opencommunity.regen;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Regen extends JavaPlugin {

    private final Regen instance;
    private final String version;

    private final FileHandler fileHandler;
    private final TaskHolder taskHolder;
    private final Detection detection;

    public Regen() {

        // Store our instance.
        instance = this;
        /*
         * Initialize everything we can here
         * but do NOTHING that attempts to load
         * any data, nor access any other plug-ins.
         */
        version = instance.getDescription().getVersion();
        fileHandler = new FileHandler(instance);
        taskHolder = new TaskHolder(instance);
        detection = new Detection(instance);
    }

    @Override
    public void onEnable() {

        /*
         * Executes after the worlds have initialized.
         *
         * Hook other plug-ins, register commands and
         * perform any remaining tasks to be fully
         * up and running.
         */
        final PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(detection, instance);

        fileHandler.loadConfig();
        fileHandler.loadBlocks();
        fileHandler.loadEntities();
    }

    @Override
    public void onDisable() {

        taskHolder.cancelTask();
    }

    /**
     * Handles saving, loading, decoding
     * and processing of data in preparation
     * of regeneration.
     *
     * @return the FileHandler
     */
    public FileHandler getFileHandler() {

        return fileHandler;
    }

    /**
     * Holds the Queues for regeneration and the
     * Asynchronous Task used for processing.
     *
     * @return the TaskHolder
     */
    protected TaskHolder getTaskHolder() {

        return taskHolder;
    }

    /**
     * Detects all explosions and process for
     * regeneration of destroyed entities and
     * Blocks.
     *
     * @return the Detection Listener
     */
    public Detection getDetection() {

        return detection;
    }

    public String getVersion() {

        return version;
    }

    protected String getFormattedName() {

        return "[" + getName() + "] ";
    }
}
