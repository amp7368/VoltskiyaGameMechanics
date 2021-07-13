package voltskiya.apple.game_mechanics.decay.world;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.decay.sql.DecaySqlStorage;

import java.util.List;

public class WatchBlockBreak implements Listener {
    public WatchBlockBreak() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockDestroyEvent event) {
        @NotNull Block blockBroken = event.getBlock();
        DecaySqlStorage.insertPlaceUpdate(new DecaySqlStorage.BlockUpdate(
                blockBroken.getType(),
                event.getNewState().getMaterial(),
                blockBroken.getX(),
                blockBroken.getY(),
                blockBroken.getZ(),
                blockBroken.getWorld().getUID()
        ));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        @NotNull Block blockBroken = event.getBlock();
        DecaySqlStorage.insertPlaceUpdate(new DecaySqlStorage.BlockUpdate(
                blockBroken.getType(),
                null,
                blockBroken.getX(),
                blockBroken.getY(),
                blockBroken.getZ(),
                blockBroken.getWorld().getUID()
        ));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockExplodeEvent event) {
        @NotNull List<Block> blockBroken = event.blockList();
        for (Block block : blockBroken) {
            DecaySqlStorage.insertPlaceUpdate(new DecaySqlStorage.BlockUpdate(
                    block.getType(),
                    null,
                    block.getX(),
                    block.getY(),
                    block.getZ(),
                    block.getWorld().getUID()
            ));
        }
        Block block = event.getBlock();
        DecaySqlStorage.insertPlaceUpdate(new DecaySqlStorage.BlockUpdate(
                block.getType(),
                null,
                block.getX(),
                block.getY(),
                block.getZ(),
                block.getWorld().getUID()
        ));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockPlaceEvent event) {
        @NotNull Block blockPlaced = event.getBlock();
        DecaySqlStorage.insertPlaceUpdate(new DecaySqlStorage.BlockUpdate(
                event.getBlockReplacedState().getType(),
                blockPlaced.getType(),
                blockPlaced.getX(),
                blockPlaced.getY(),
                blockPlaced.getZ(),
                blockPlaced.getWorld().getUID()
        ));
    }
}
