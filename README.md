# Regen
A one of a kind explosion regeneration plugin for PaperMC.

Automatically regenerate block damage from explosions. Asynchronous regen queue management.
Fully automated and supports any plug-in to take control to manage what is restored and what is left destroyed.

In your onEnable() method add
```java
final PluginManager pluginManager = getServer().getPluginManager();
if (pluginManager.getPlugin("Regen") != null)
	pluginManager.registerEvents(new RegenPluginListener(), instance);
```

This sample class can be used in your plugin to intercept Regen events and take control at a granular level.

```java
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.palmergames.spigot.regen.events.BlockRegenEvent;
import com.palmergames.spigot.regen.events.EntityRegenEvent;

/**
 * @author ElgarL
 *
 */
public class RegenPluginListener implements Listener {
	
	/**
	 * 
	 */
	public RegenPluginListener() {

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockRegenEvent(BlockRegenEvent event) {
	
		/*
		 * Remove blocks from this event
		 * to prevent them being damaged.
		 * Any remaining blocks will not
		 * drop items and will be regenerated.
		 */
		// example - event.blockList().removeIf(block -> ( database.hasPlot(block.getLocation()) );
		/*
		 * If we cancel this event the explosion will
		 * still happen and blocks will drop as items
		 * however, no regen will occur.
		 */
		// event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityRegenEvent(EntityRegenEvent event) {
		
		/*
		 * If we cancel this event the entity that
		 * was destroyed will not be regenerated.
		 * It will however drop any block and
		 * inventory it normally would.
		 */
		// event.setCancelled(true);
	}
}
```
