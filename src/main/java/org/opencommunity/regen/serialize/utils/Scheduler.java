package org.opencommunity.regen.serialize.utils;

import java.util.concurrent.TimeUnit;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.opencommunity.regen.Regen;

public class Scheduler {

    private Scheduler() {
        throw new IllegalStateException("Scheduler class");
    }

    public static void scheduleSyncDelayedTask(Regen plugin, Runnable task, Object regionData, int delay) {
        if (Regen.isFolia) {
            if (regionData instanceof Location) { // REGION
                Location location = (Location) regionData;
                if (delay == 0) {
                    Bukkit.getServer().getRegionScheduler().run(plugin, location, value -> task.run());
                }
                else {
                    Bukkit.getServer().getRegionScheduler().runDelayed(plugin, location, value -> task.run(), delay);
                }
            }
            else if (regionData instanceof Entity) { // ENTITY
                Entity entity = (Entity) regionData;
                if (delay == 0) {
                    entity.getScheduler().run(plugin, value -> task.run(), null);
                }
                else {
                    entity.getScheduler().runDelayed(plugin, value -> task.run(), null, delay);
                }
            }
            else { // GLOBAL
                if (delay == 0) {
                    Bukkit.getServer().getGlobalRegionScheduler().run(plugin, value -> task.run());
                }
                else {
                    Bukkit.getServer().getGlobalRegionScheduler().runDelayed(plugin, value -> task.run(), delay);
                }
            }
        }
        else { // BUKKIT
            if (delay == 0) {
                Bukkit.getScheduler().runTask(plugin, task);
            }
            else {
                Bukkit.getScheduler().runTaskLater(plugin, task, delay);
            }
        }
    }

    public static void scheduleAsyncDelayedTask(Regen plugin, Runnable task, long delay) {
        if (Regen.isFolia) {
            if (delay == 0) {
                Bukkit.getServer().getAsyncScheduler().runNow(plugin, value -> task.run());
            }
            else {
                Bukkit.getServer().getAsyncScheduler().runDelayed(plugin, value -> task.run(), (delay * 50L), TimeUnit.MILLISECONDS);
            }
        }
        else { // BUKKIT
            if (delay == 0) {
                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, task);
            }
            else {
                Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
            }
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
        if (Regen.isFolia) {
            Bukkit.getServer().getAsyncScheduler().runAtFixedRate(plugin, value -> task.run(), delay * 50L, frequency * 50L, TimeUnit.MILLISECONDS);
        } else {
            Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delay, frequency);
        }
    }
}