package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import static voltskiya.apple.game_mechanics.tmw.PluginTMW.BLOCKS_IN_A_CHUNK;

import apple.mc.utilities.item.material.MaterialUtils;
import apple.utilities.structures.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;

public class BiomeTypeBuilderRegisterBlocks implements Runnable {

    private static final int BLOCKS_TO_COUNT_DOWN = 3;
    private static int BUILD_HEIGHT = 0;
    private final Player player;
    private final Set<Pair<Integer, Integer>> chunksScanned = new HashSet<>();
    private final List<TopBlock> topBlocks = new ArrayList<>();
    private boolean shouldStop = false;

    public BiomeTypeBuilderRegisterBlocks(Player player) {
        this.player = player;
        BUILD_HEIGHT = this.player.getWorld().getMaxHeight();
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this);
    }

    @Nullable
    public static BlocksInfo compute(Chunk chunk, Set<Pair<Integer, Integer>> blocksScanned, int x,
        int y, int z) {
        return compute(scanNearby(chunk, blocksScanned, x, y, z));
    }

    public static List<TopBlock> scanNearby(Chunk chunk, Set<Pair<Integer, Integer>> blocksScanned,
        int x, int y, int z) {
        List<TopBlock> topBlocksLocal = new ArrayList<>();
        if (y >= BUILD_HEIGHT || y < 0) {
            return topBlocksLocal;
        }
        if (x >= 0 && x < BLOCKS_IN_A_CHUNK && z >= 0 && z < BLOCKS_IN_A_CHUNK && blocksScanned.add(
            new Pair<>(x, z))) {
            // if the block hasn't already been added
            // scan the block and walk to nearby blocks
            Block block = chunk.getBlock(x, y, z);
            boolean shouldScanNext = false;
            if (isIgnored(block.getType())) {
                // go down
                while (--y > 0 && isIgnored((block = chunk.getBlock(x, y, z)).getType()))
                    ;

                if (y >= 0) {
                    // register the top blocks (key letter block's')
                    Block lowerBlock = block;

                    for (int yi = 0;
                        yi < BLOCKS_TO_COUNT_DOWN && !lowerBlock.getType().isAir() && y - yi >= 0;
                        yi++) {
                        topBlocksLocal.add(new TopBlock(lowerBlock.getType(), yi == 0, x, y, z,
                            lowerBlock.getBiome()));
                        lowerBlock = chunk.getBlock(x, y, z);
                    }
                    shouldScanNext = true;
                }
            } else {
                // go up
                while (++y < BUILD_HEIGHT && !isIgnored(chunk.getBlock(x, y, z).getType()))
                    ;

                if (y < BUILD_HEIGHT) {
                    block = chunk.getBlock(x, y - 1, z);
                    // register the top blocks (key letter block's')
                    Block lowerBlock = block;

                    for (int yi = 0; yi < BLOCKS_TO_COUNT_DOWN && !isIgnored(lowerBlock.getType())
                        && y - yi >= 0; yi++) {
                        topBlocksLocal.add(new TopBlock(lowerBlock.getType(), yi == 0, x, y, z,
                            lowerBlock.getBiome()));
                        lowerBlock = chunk.getBlock(x, y, z);
                    }
                    shouldScanNext = true;
                }
            }
            if (shouldScanNext) {
                topBlocksLocal.addAll(scanNearby(chunk, blocksScanned, x - 1, y, z));
                topBlocksLocal.addAll(scanNearby(chunk, blocksScanned, x + 1, y, z));
                topBlocksLocal.addAll(scanNearby(chunk, blocksScanned, x, y, z - 1));
                topBlocksLocal.addAll(scanNearby(chunk, blocksScanned, x, y, z + 1));
            }
        }
        return topBlocksLocal;
    }

    private static boolean isIgnored(Material type) {
        return type.isAir() || type == Material.WATER || type == Material.LAVA
            || MaterialUtils.isTree(type);
    }

    @Nullable
    public static BlocksInfo compute(List<TopBlock> topBlocks) {
        if (topBlocks.isEmpty()) {
            return null;
        }
        int ySum = 0;
        int averageHeight = 0;
        int lowestHeight = Integer.MAX_VALUE;
        int highestHeight = Integer.MIN_VALUE;
        for (TopBlock block : topBlocks) {
            ySum += block.y;
            lowestHeight = Math.min(lowestHeight, block.y);
            highestHeight = Math.max(highestHeight, block.y);
        }
        // get the standard deviation
        int topBlocksCount = topBlocks.size();
        int yMean = ySum / topBlocksCount;
        double sd = 0;
        for (TopBlock block : topBlocks) {
            final int diff = block.y - yMean;
            sd += diff * diff;
        }
        sd = Math.sqrt(sd / (BLOCKS_IN_A_CHUNK * BLOCKS_IN_A_CHUNK));
        final double maxY = yMean + sd * 2;
        final double minY = yMean - sd * 2;

        int blocksInThisChunk = 0;
        // if you aren't within 2 standard deviation, you are probably a random pillar
        Map<Material, Double> materials = new HashMap<>();
        Map<Biome, Double> biomes = new HashMap<>();
        for (TopBlock topBlock : topBlocks) {
            if (topBlock.y <= maxY && topBlock.y >= minY) {
                blocksInThisChunk++;
                averageHeight += topBlock.y;
                materials.compute(topBlock.material, (m, perc) -> perc == null ? 1 : perc + 1);
                biomes.compute(topBlock.biome, (b, perc) -> perc == null ? 1 : perc + 1);
            }
        }
        averageHeight /= blocksInThisChunk;
        double heightVariation = sd;
        for (Map.Entry<Biome, Double> entry : biomes.entrySet()) {
            entry.setValue(entry.getValue() / blocksInThisChunk);
        }
        for (Map.Entry<Material, Double> entry : materials.entrySet()) {
            entry.setValue(entry.getValue() / blocksInThisChunk);
        }
        return new BlocksInfo(heightVariation, averageHeight, materials, biomes, lowestHeight,
            highestHeight);
    }

    @Override
    public void run() {
        if (shouldStop) {
            return;
        }
        @NotNull Chunk chunk = this.player.getLocation().getChunk();
        Pair<Integer, Integer> coords = new Pair<>(chunk.getX(), chunk.getZ());
        // if we didn't already scan it
        if (chunksScanned.add(coords)) {
            // scan the chunk
            Set<Pair<Integer, Integer>> blocksScanned = new HashSet<>();
            topBlocks.addAll(scanNearby(chunk,
                blocksScanned,
                player.getLocation().getBlockX() - chunk.getX() * BLOCKS_IN_A_CHUNK,
                player.getLocation().getBlockY(),
                player.getLocation().getBlockZ() - chunk.getZ() * BLOCKS_IN_A_CHUNK
            ));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 20);
    }

    public void setShouldStop() {
        shouldStop = true;
    }

    @Nullable
    public BlocksInfo computeFromCurrent() {
        return compute(this.topBlocks);
    }

    public static class BlocksInfo {

        private final double heightVariation;
        private final int averageHeight;
        private final Map<Material, Double> materials;
        private final Map<Biome, Double> biomes;
        private final int lowestHeight;
        private final int highestHeight;

        public BlocksInfo(double heightVariation, int averageHeight,
            Map<Material, Double> materials, Map<Biome, Double> biomes, int lowestHeight,
            int highestHeight) {
            this.heightVariation = heightVariation;
            this.averageHeight = averageHeight;
            this.materials = materials;
            this.biomes = biomes;
            this.lowestHeight = lowestHeight;
            this.highestHeight = highestHeight;
        }

        public double getHeightVariation() {
            return heightVariation;
        }

        public int getAverageHeight() {
            return averageHeight;
        }

        public Map<Material, Double> getMaterials() {
            return materials;
        }

        public Map<Biome, Double> getBiomes() {
            return biomes;
        }

        public int getLowestHeight() {
            return lowestHeight;
        }

        public int getHighestHeight() {
            return highestHeight;
        }
    }

    public record TopBlock(Material material, boolean isSurface, int x, int y, int z, Biome biome) {

    }
}
