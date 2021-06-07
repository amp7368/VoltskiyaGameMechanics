package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.util.data_structures.Pair;

import java.util.*;

import static voltskiya.apple.game_mechanics.temperature.chunks.TemperatureChunk.BLOCKS_IN_A_CHUNK;
import static voltskiya.apple.game_mechanics.temperature.chunks.TemperatureChunk.BUILD_HEIGHT;

public class BiomeTypeBuilderRegisterBlocks implements Runnable {
    private final Player player;
    private final BiomeTypeBuilder biomeBuilder;
    private boolean shouldStop = false;
    private final Set<Pair<Integer, Integer>> chunksScanned = new HashSet<>();
    private final List<TopBlock> topBlocks = new ArrayList<>();

    public BiomeTypeBuilderRegisterBlocks(Player player, BiomeTypeBuilder biomeBuilder) {
        this.player = player;
        this.biomeBuilder = biomeBuilder;
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this);
    }

    @Override
    public void run() {
        System.out.println("run");
        if (shouldStop) return;
        @NotNull Chunk chunk = this.player.getLocation().getChunk();
        Pair<Integer, Integer> coords = new Pair<>(chunk.getX(), chunk.getZ());
        // if we didn't already scan it
        if (chunksScanned.add(coords)) {
            // scan the chunk
            Set<Pair<Integer, Integer>> blocksScanned = new HashSet<>();
            scanNearby(chunk,
                    blocksScanned,
                    player.getLocation().getBlockX() - chunk.getX() * BLOCKS_IN_A_CHUNK,
                    player.getLocation().getBlockY(),
                    player.getLocation().getBlockZ() - chunk.getZ() * BLOCKS_IN_A_CHUNK
            );
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this,20);
    }

    private void scanNearby(Chunk chunk, Set<Pair<Integer, Integer>> blocksScanned, int x, int y, int z) {
        if (x >= 0 && x < BLOCKS_IN_A_CHUNK &&
                z >= 0 && z < BLOCKS_IN_A_CHUNK &&
                blocksScanned.add(new Pair<>(x, z))) {
            // if the block hasn't already been added
            // scan the block and walk to nearby blocks
            Block block = chunk.getBlock(x, y, z);
            boolean shouldScanNext = false;
            if (block.getType().isAir()) {
                // go down
                while (y > 0 && (block = chunk.getBlock(x, --y, z)).getType().isAir()) ;

                if (y >= 0) {
                    topBlocks.add(new TopBlock(block.getType(), y, block.getBiome()));
                    shouldScanNext = true;
                }
            } else {
                // go up
                while (y < BUILD_HEIGHT && !(block = chunk.getBlock(x, ++y, z)).getType().isAir()) ;

                if(y<BUILD_HEIGHT){
                    topBlocks.add(new TopBlock(block.getType(), y - 1, block.getBiome()));
                    shouldScanNext = true;
                }
            }
            if (shouldScanNext) {
                scanNearby(chunk, blocksScanned, x - 1, y, z);
                scanNearby(chunk, blocksScanned, x + 1, y, z);
                scanNearby(chunk, blocksScanned, x, y, z - 1);
                scanNearby(chunk, blocksScanned, x, y, z + 1);
            }
        }
    }

    public void setShouldStop() {
        shouldStop = true;
    }

    @Nullable
    public BlocksInfo compute() {
        if(topBlocks.isEmpty()) return null;
        Map<Material, Double> materials = new HashMap<>();
        Map<Biome, Double> biomes = new HashMap<>();
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
        int yMean = ySum / topBlocks.size();
        double sd = 0;
        for (int i = 0; i < topBlocks.size(); i++) {
            final int diff = topBlocks.get(i).y - yMean;
            sd += diff * diff;
            i++;
        }
        sd = Math.sqrt(sd / (BLOCKS_IN_A_CHUNK * BLOCKS_IN_A_CHUNK));
        final double maxY = yMean + sd * 2;
        final double minY = yMean - sd * 2;

        int blocksInThisChunk = 0;
        // if you aren't within 2 standard deviation, you are probably a random pillar
        for (final TopBlock topBlock : topBlocks) {
            if (topBlock.y <= maxY && topBlock.y >= minY) {
                blocksInThisChunk++;
                averageHeight += topBlock.y;
                materials.compute(topBlock.material, (m, perc) -> perc == null ? 1 : perc + 1);
                biomes.compute(topBlock.biome, (b, perc) -> perc == null ? 1 : perc + 1);
            }
        }
        averageHeight /= blocksInThisChunk;
        double heightVariation = sd;
        for (Map.Entry<Biome, Double> entry : biomes.entrySet())
            entry.setValue(entry.getValue() / blocksInThisChunk);
        for (Map.Entry<Material, Double> entry : materials.entrySet())
            entry.setValue(entry.getValue() / blocksInThisChunk);
        return new BlocksInfo(heightVariation, averageHeight, materials, biomes, lowestHeight, highestHeight);
    }

    public static class BlocksInfo {
        private final double heightVariation;
        private final int averageHeight;
        private final Map<Material, Double> materials;
        private final Map<Biome, Double> biomes;
        private final int lowestHeight;
        private final int highestHeight;

        public BlocksInfo(double heightVariation, int averageHeight, Map<Material, Double> materials, Map<Biome, Double> biomes, int lowestHeight, int highestHeight) {
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

    private static class TopBlock {
        private Material material;
        private int y;
        private Biome biome;

        public TopBlock(Material material, int y, Biome biome) {
            this.material = material;
            this.y = y;
            this.biome = biome;
        }
    }
}
