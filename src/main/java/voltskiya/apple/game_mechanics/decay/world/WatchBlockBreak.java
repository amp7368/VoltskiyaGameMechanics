package voltskiya.apple.game_mechanics.decay.world;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class WatchBlockBreak implements Listener {
    public WatchBlockBreak() {
//        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockEvent event) {
        @NotNull Block blockBroken = event.getBlock();

    }
}
