package org.opencommunity.regen.serialize.utils;

import org.bukkit.Bukkit;
import org.opencommunity.regen.Regen;

public class Scheduler {

    private Scheduler() {
        throw new IllegalStateException("Scheduler class");
    }

    public static void scheduleSyncDelayedTask(Regen plugin, Runnable task, Object regionData, int delay) {
        if (delay == 0) {
            Bukkit.getScheduler().runTask(plugin, task);
        }
        else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }

    public static void scheduleAsyncDelayedTask(Regen plugin, Runnable task, long delay) {
        if (delay == 0) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, task);
        }
        else {
            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
        }
    }

    public static void scheduleSyncDelayedTask(Regen plugin, Runnable task, int delay) {
        scheduleSyncDelayedTask(plugin, task, null, delay);
    }

    public static void runTask(Regen plugin, Runnable task) {
        scheduleSyncDelayedTask(plugin, task, null, 0);
    }

    public static void runTask(Regen plugin, Runnable task, Object regionData) {
        scheduleSyncDelayedTask(plugin, task, regionData, 0);
    }

    public static void runTaskAsynchronously(Regen plugin, Runnable task) {
        scheduleAsyncDelayedTask(plugin, task, 0);
    }

    public static void runTaskTimerAsynchronously(Regen plugin, Runnable task, long delay, long frequency) {
            Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delay, frequency);
    }
}