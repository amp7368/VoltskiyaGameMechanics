package voltskiya.apple.game_mechanics.temperature.chunks;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import voltskiya.apple.game_mechanics.temperature.biome.TemperatureAllBiomes;
import voltskiya.apple.game_mechanics.temperature.biome.TemperatureBiome;
import voltskiya.apple.game_mechanics.util.data_structures.Pair;
import voltskiya.apple.game_mechanics.util.minecraft.MaterialUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TemperatureChunk {
    private static final int BLOCKS_IN_A_CHUNK = 16;
    private final int chunkX;
    private final int chunkZ;
    private double heightVariation;
    private double averageHeight;
    private final Map<Material, Double> materials = new HashMap<>();
    private final Map<Biome, Double> biomes = new HashMap<>();
    private TemperatureBiome myGuessedBiome;

    public TemperatureChunk(Location locationToLoad, Map<Pair<Integer, Integer>, ChunkSnapshot> chunksLoaded) {
        this.chunkX = locationToLoad.getChunk().getX();
        this.chunkZ = locationToLoad.getChunk().getZ();
        if (load()) scan(chunksLoaded.values());
    }

    /**
     * scan nearby chunks to include in our calculation of ourselves
     *
     * @param realChunks the chunks nearby to include
     */
    private void scan(Collection<ChunkSnapshot> realChunks) {
        TopBlock[] topBlocks = new TopBlock[BLOCKS_IN_A_CHUNK * BLOCKS_IN_A_CHUNK * realChunks.size()];
        int ySum = 0;
        int i = 0;
        for (ChunkSnapshot realChunk : realChunks) {
            for (short xi = 0; xi < BLOCKS_IN_A_CHUNK; xi++) {
                for (short zi = 0; zi < BLOCKS_IN_A_CHUNK; zi++) {
                    Material topBlockMaterial;
                    int yi = realChunk.getHighestBlockYAt(xi, zi);
                    while (MaterialUtils.isTree(topBlockMaterial = realChunk.getBlockType(xi, yi--, zi))) ;
                    topBlocks[i] = new TopBlock(
                            topBlockMaterial,
                            yi,
                            realChunk.getBiome(xi, yi, zi)
                    );
                    ySum += yi;
                    i++;
                }
            }
        }
        // get the standard deviation
        int yMean = ySum / topBlocks.length;
        double sd = 0;
        for (i = 0; i < topBlocks.length; i++) {
            final int diff = topBlocks[i].y - yMean;
            sd += diff * diff;
            i++;
        }
        sd = Math.sqrt(sd / (BLOCKS_IN_A_CHUNK * BLOCKS_IN_A_CHUNK));
        final double maxY = yMean + sd * 2;
        final double minY = yMean + sd * 2;

        int blocksInThisChunk = 0;
        // if you aren't within 2 standard deviation, you are probably a random pillar
        for (i = 0; i < topBlocks.length; i++) {
            final TopBlock topBlock = topBlocks[i];
            if (topBlock.y < maxY && topBlock.y > minY) {
                blocksInThisChunk++;
                this.averageHeight += topBlock.y;
                materials.compute(topBlock.material, (m, perc) -> perc == null ? 1 : perc + 1);
                biomes.compute(topBlock.biome, (b, perc) -> perc == null ? 1 : perc + 1);
            }
        }
        this.averageHeight /= blocksInThisChunk;
        this.heightVariation = sd;
        for (Map.Entry<Biome, Double> entry : biomes.entrySet())
            entry.setValue(entry.getValue() / blocksInThisChunk);
        for (Map.Entry<Material, Double> entry : materials.entrySet())
            entry.setValue(entry.getValue() / blocksInThisChunk);
        this.myGuessedBiome = TemperatureAllBiomes.getBiome(this);
    }

    /**
     * @return true if this chunk should be scanned
     */
    private boolean load() {
        return false;
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
