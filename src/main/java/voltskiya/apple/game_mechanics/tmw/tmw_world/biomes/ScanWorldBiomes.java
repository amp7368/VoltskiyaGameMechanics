package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.sql.BiomeSqlStorage;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks.TopBlock;
import voltskiya.apple.utilities.util.data_structures.Pair;
import voltskiya.apple.utilities.util.data_structures.Triple;

import java.util.*;

import static voltskiya.apple.game_mechanics.deleteme_later.chunks.TemperatureChunk.BLOCKS_IN_A_CHUNK;

public class ScanWorldBiomes {
    private final Location starterLocation;
    private final World world;
    private final List<Pair<Integer, Integer>> chunksToScan = new ArrayList<>();
    private final List<ComputedBiomeChunkWithBorders> pastComputedChunks = new ArrayList<>();
    private final Map<Pair<Integer, Integer>, ProcessedChunk> processedChunksToStoreInDB = new HashMap<>();
    private double chunksPerStep = 1;
    private int chunksComputedThisStep;

    public ScanWorldBiomes(Location starterLocation, int x1, int z1, int x2, int z2) {
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (z1 > z2) {
            int temp = z1;
            z1 = z2;
            z2 = temp;
        }
        this.starterLocation = starterLocation;
        this.world = starterLocation.getWorld();
        // start at the location and scan outwards 
        HashSet<Pair<Integer, Integer>> alreadyChecked = new HashSet<>();
        for (int xi = starterLocation.getBlockX() / 16; xi * 16 <= x2; xi++) {
            for (int zi = starterLocation.getBlockZ() / 16; zi * 16 <= z2; zi++) {
                final Pair<Integer, Integer> chunkCoords = new Pair<>(xi, zi);
                if (alreadyChecked.add(chunkCoords)) {
                    chunksToScan.add(chunkCoords);
                }
            }
        }
        for (int xi = starterLocation.getBlockX() / 16; xi * 16 >= x1; xi--) {
            for (int zi = starterLocation.getBlockZ() / 16; zi * 16 <= z2; zi++) {
                final Pair<Integer, Integer> chunkCoords = new Pair<>(xi, zi);
                if (alreadyChecked.add(chunkCoords)) {
                    chunksToScan.add(chunkCoords);
                }
            }
        }
        for (int xi = starterLocation.getBlockX() / 16; xi * 16 <= x2; xi++) {
            for (int zi = starterLocation.getBlockZ() / 16; zi * 16 >= z1; zi--) {
                final Pair<Integer, Integer> chunkCoords = new Pair<>(xi, zi);
                if (alreadyChecked.add(chunkCoords)) {
                    chunksToScan.add(chunkCoords);
                }
            }
        }
        for (int xi = starterLocation.getBlockX() / 16; xi * 16 >= x1; xi--) {
            for (int zi = starterLocation.getBlockZ() / 16; zi * 16 >= z1; zi--) {
                final Pair<Integer, Integer> chunkCoords = new Pair<>(xi, zi);
                if (alreadyChecked.add(chunkCoords)) {
                    chunksToScan.add(chunkCoords);
                }
            }
        }
        scan();
    }

    public void scan() {
        System.out.println("scan");
        // determine if we should increase or decrease our chunk loading speed
        final double tps = Bukkit.getTPS()[0];
        if (tps > 19.75) {
            this.chunksPerStep++;
        } else if (tps < 19) {
            this.chunksPerStep--;
        }
        if (chunksPerStep == 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::scan, 10);
            return;
        }
        if (this.chunksToScan.isEmpty()) {
            System.out.println("done scanning");
            return;
        }

        this.chunksComputedThisStep = 0;
        List<PrepareChunks> chunksToProcess = new ArrayList<>();
        List<ComputedChunkProcessing> alreadyLoadedChunks = new ArrayList<>();
        while (this.chunksComputedThisStep < this.chunksPerStep && !this.chunksToScan.isEmpty()) {
            // determine what chunks need to be computed for this step
            final PrepareChunks prepareChunks = determineRequiredChunks(alreadyLoadedChunks);
            if (prepareChunks == null) break;
            chunksToProcess.add(prepareChunks);
        }
        // process the chunksToProcess
        process(chunksToProcess);
        cleanUp();

        // schedule ourselves again
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::scan, 5);
    }

    private void process(List<PrepareChunks> chunksToProcess) {
        for (PrepareChunks chunkToProcess : chunksToProcess) {
            List<ComputedBiomeChunkWithBorders> chunksComputed = chunkToProcess.chunksComputed();
            List<ComputedChunkProcessing> chunksToBeComputed = chunkToProcess.chunksToBeComputed();
            // compute the chunksToBeComputed if possible
            // if it's not possible, tryAgain will become false because nothing was computed so nothing
            // will change to allow for more computations
            boolean tryAgain = true;
            while (tryAgain) {
                tryAgain = false;
                Iterator<ComputedChunkProcessing> chunkToBeComputedIterator = chunksToBeComputed.iterator();
                while (chunkToBeComputedIterator.hasNext()) {
                    ComputedChunkProcessing chunkToBeComputed = chunkToBeComputedIterator.next();
                    chunkToBeComputed.compute(this.starterLocation, chunksComputed);
                    if (chunkToBeComputed.isComputed()) {
                        chunksComputed.add(chunkToBeComputed.getComputed());
                        this.pastComputedChunks.add(chunkToBeComputed.getComputed());
                        chunkToBeComputedIterator.remove();
                        tryAgain = true;
                    }
                }
            }

            // as much computing as possible has happened
            // chunksToBeComputed should be empty or close to empty, so we'll ignore it
            // chunksComputed should contain a 3x3 of chunks around the preparedChunk
            List<TopBlock> allBlocks = new ArrayList<>();
            Triple<Integer, Integer, Integer> bridgeXPos = null, bridgeZPos = null, bridgeXNeg = null, bridgeZNeg = null, middle = null;
            for (ComputedBiomeChunkWithBorders chunkComputed : chunksComputed) {
                allBlocks.addAll(chunkComputed.topBlocks());
                if (chunkComputed.getX() == chunkToProcess.centerX() && chunkComputed.getZ() == chunkToProcess.centerZ()) {
                    bridgeXPos = chunkComputed.bridgeXPos();
                    bridgeZPos = chunkComputed.bridgeZPos();
                    bridgeXNeg = chunkComputed.bridgeXNeg();
                    bridgeZNeg = chunkComputed.bridgeZNeg();
                    middle = chunkComputed.middle();
                }
            }
            this.processedChunksToStoreInDB.put(new Pair<>(
                            chunkToProcess.centerX(),
                            chunkToProcess.centerZ()),
                    new ProcessedChunk(
                            chunkToProcess.centerX(),
                            chunkToProcess.centerZ(),
                            new ComputedBiomeChunk(BiomeTypeBuilderRegisterBlocks.compute(allBlocks)),
                            bridgeXPos,
                            bridgeZPos,
                            bridgeXNeg,
                            bridgeZNeg,
                            middle
                    )
            );
        }
    }

    private void cleanUp() {
        // store the computedChunksToStoreInDB in the DB
        List<ProcessedChunk> chunksDone = new ArrayList<>();
        List<Pair<Integer, Integer>> chunksDoneKey = new ArrayList<>();
        for (Map.Entry<Pair<Integer, Integer>, ProcessedChunk> chunk : processedChunksToStoreInDB.entrySet()) {
            if (chunk.getValue().checkNeighbors()) {
                chunksDone.add(chunk.getValue());
                chunksDoneKey.add(chunk.getKey());
            }
        }
        for (Pair<Integer, Integer> key : chunksDoneKey) processedChunksToStoreInDB.remove(key);

        if (!chunksDone.isEmpty())
            BiomeSqlStorage.insert(chunksDone);

        // make sure the list of pastComputedChunks is as small as possible to conserve ram
//        while (pastComputedChunks.size() > this.chunksPerStep * 9) pastComputedChunks.remove(0);

    }

    @Nullable
    private PrepareChunks determineRequiredChunks(List<ComputedChunkProcessing> alreadyLoadedChunks) {
        List<ComputedChunkProcessing> chunksToBeComputed = new ArrayList<>();
        List<ComputedBiomeChunkWithBorders> chunksComputed = new ArrayList<>();
        // read the chunks in the order that chunksToScan specifies
        Pair<Integer, Integer> chunkToScanCoords = chunksToScan.get(0);
        // the next two for loops don't add to the time complexity.
        // go over the nearby chunks

        for (int xi = -1; xi <= 1; xi++) {
            final int x = chunkToScanCoords.getKey() + xi;
            nextChunk:
            for (int zi = -1; zi <= 1; zi++) {
                final int z = chunkToScanCoords.getValue() + zi;

                // find the scanned chunk in our previously computed chunks
                for (ComputedBiomeChunkWithBorders computedChunk : pastComputedChunks) {
                    if (computedChunk.getX() == x && computedChunk.getZ() == z) {
                        chunksComputed.add(computedChunk);
                        continue nextChunk;
                    }
                }
                // find the scanned chunk in our previously computed chunks
                for (ComputedChunkProcessing loadedChunk : alreadyLoadedChunks) {
                    if (loadedChunk.getX() == x && loadedChunk.getZ() == z) {
                        chunksToBeComputed.add(loadedChunk);
                        continue nextChunk;
                    }
                }
                // or add the scanned chunk to the list required to compute
                chunksToBeComputed.add(new ComputedChunkProcessing(this.world.getChunkAt(x, z)));
                if (++this.chunksComputedThisStep >= this.chunksPerStep) return null;
            }
        }
        alreadyLoadedChunks.addAll(chunksToBeComputed);
        this.chunksToScan.remove(0);
        return new PrepareChunks(chunksToBeComputed, chunksComputed, chunkToScanCoords.getKey(), chunkToScanCoords.getValue());
    }

    private record PrepareChunks(List<ComputedChunkProcessing> chunksToBeComputed,
                                 List<ComputedBiomeChunkWithBorders> chunksComputed,
                                 int centerX,
                                 int centerZ) {
    }

    private static class ComputedChunkProcessing {
        private final Chunk chunk;
        private final int x;
        private final int z;
        private ComputedBiomeChunkWithBorders computed = null;

        public ComputedChunkProcessing(Chunk chunk) {
            this.chunk = chunk;
            this.x = this.chunk.getX();
            this.z = this.chunk.getZ();
        }

        public void compute(Location starterLocation, List<ComputedBiomeChunkWithBorders> chunksComputed) {
            if (isComputed()) return;
            // it's impossible for xyz to be what they are now by the time i use them,
            // but this is just to make the compiler happy
            int x = -1, y = -1, z = -1;
            if (starterLocation.getBlockX() / BLOCKS_IN_A_CHUNK == getX() && starterLocation.getBlockZ() / BLOCKS_IN_A_CHUNK == getZ()) {
                x = starterLocation.getBlockX() % 16;
                y = starterLocation.getBlockY();
                z = starterLocation.getBlockZ() % 16;
            } else {
                boolean success = false;
                for (ComputedBiomeChunkWithBorders other : chunksComputed) {
                    final Triple<Integer, Integer, Integer> bridge = other.getBridge(getX(), getZ());
                    if (bridge != null) {
                        x = bridge.getX() % 16;
                        y = bridge.getY();
                        z = bridge.getZ() % 16;
                        success = true;
                        break;
                    }
                }
                if (!success) return;
            }
            if (x < 0) x += 16;
            if (z < 0) z += 16;

            Triple<Integer, Integer, Integer> bridgeXPos = null;
            Triple<Integer, Integer, Integer> bridgeXNeg = null;
            Triple<Integer, Integer, Integer> bridgeZPos = null;
            Triple<Integer, Integer, Integer> bridgeZNeg = null;
            Triple<Integer, Integer, Integer> middle = null;
            // scan the top blocks
            List<TopBlock> topBlocks = BiomeTypeBuilderRegisterBlocks.scanNearby(this.chunk, new HashSet<>(), x, y, z);
            // find bridges
            for (TopBlock topBlock : topBlocks) {
                if (topBlock.x() == 0 && bridgeXNeg == null)
                    bridgeXNeg = new Triple<>(0, topBlock.y(), topBlock.z());
                else if (topBlock.x() == BLOCKS_IN_A_CHUNK - 1 && bridgeXPos == null)
                    bridgeXPos = new Triple<>(topBlock.x(), topBlock.y(), topBlock.z());
                else if (topBlock.z() == 0 && bridgeZNeg == null)
                    bridgeZNeg = new Triple<>(topBlock.x(), topBlock.y(), 0);
                else if (topBlock.z() == BLOCKS_IN_A_CHUNK - 1 && bridgeZPos == null)
                    bridgeZPos = new Triple<>(topBlock.x(), topBlock.y(), topBlock.z());
                else if (middle == null) middle = new Triple<>(topBlock.x(), topBlock.y(), topBlock.z());
            }
            // do computed
            this.computed = new ComputedBiomeChunkWithBorders(
                    this.getX(), this.getZ(), topBlocks, bridgeXPos, bridgeXNeg, bridgeZPos, bridgeZNeg, middle
            );
        }

        public boolean isComputed() {
            return computed != null;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        private int getPreciseX() {
            return getX() * BLOCKS_IN_A_CHUNK;
        }

        private int getPreciseZ() {
            return getZ() * BLOCKS_IN_A_CHUNK;
        }

        public ComputedBiomeChunkWithBorders getComputed() {
            return computed;
        }
    }

    private record ComputedBiomeChunkWithBorders(int x, int z,
                                                 List<TopBlock> topBlocks,
                                                 Triple<Integer, Integer, Integer> bridgeXPos,
                                                 Triple<Integer, Integer, Integer> bridgeXNeg,
                                                 Triple<Integer, Integer, Integer> bridgeZPos,
                                                 Triple<Integer, Integer, Integer> bridgeZNeg,
                                                 Triple<Integer, Integer, Integer> middle) {

        /**
         * @param x the chunk coords
         * @param z the chunk coords
         * @return the bridge on the other side of the chunk border
         */
        public Triple<Integer, Integer, Integer> getBridge(int x, int z) {
            int addToX = 0;
            int addToZ = 0;
            Triple<Integer, Integer, Integer> bridge = null;
            if (this.getX() == x) {
                if (this.getZ() - 1 == z) {
                    bridge = bridgeZNeg;
                    addToZ = -1;
                } else if (this.getZ() + 1 == z) {
                    bridge = bridgeZPos;
                    addToZ = 1;
                }
            } else if (this.getZ() == z) {
                if (this.getX() - 1 == x) {
                    bridge = bridgeXNeg;
                    addToX = -1;
                } else if (this.getX() + 1 == x) {
                    bridge = bridgeXPos;
                    addToX = 1;
                }
            }
            if (bridge != null) {
                bridge = new Triple<>(addToX + bridge.getX(), bridge.getY(), addToZ + bridge.getZ());
            }
            return bridge;
        }

        private int getX() {
            return x;
        }

        private int getZ() {
            return z;
        }
    }

    public class ProcessedChunk {
        private final int x;
        private final int z;
        private final ComputedBiomeChunk computedBiomeChunk;
        private final Triple<Integer, Integer, Integer> bridgeXPos;
        private final Triple<Integer, Integer, Integer> bridgeZPos;
        private final Triple<Integer, Integer, Integer> bridgeXNeg;
        private final Triple<Integer, Integer, Integer> bridgeZNeg;
        private final Triple<Integer, Integer, Integer> middle;
        private ProcessedChunk neighborXPos = null;
        private ProcessedChunk neighborZPos = null;
        private ProcessedChunk neighborXNeg = null;
        private ProcessedChunk neighborZNeg = null;

        public ProcessedChunk(int x,
                              int z,
                              ComputedBiomeChunk computedBiomeChunk,
                              Triple<Integer, Integer, Integer> bridgeXPos,
                              Triple<Integer, Integer, Integer> bridgeZPos,
                              Triple<Integer, Integer, Integer> bridgeXNeg,
                              Triple<Integer, Integer, Integer> bridgeZNeg,
                              Triple<Integer, Integer, Integer> middle
        ) {
            this.x = x;
            this.z = z;
            this.computedBiomeChunk = computedBiomeChunk;
            this.bridgeXPos = bridgeXPos;
            this.bridgeZPos = bridgeZPos;
            this.bridgeXNeg = bridgeXNeg;
            this.bridgeZNeg = bridgeZNeg;
            this.middle = middle;
        }

        public int x() {
            return x;
        }

        public int z() {
            return z;
        }

        public ComputedBiomeChunk computedBiomeChunk() {
            return computedBiomeChunk;
        }

        public Triple<Integer, Integer, Integer> bridgeXPos() {
            return bridgeXPos;
        }

        public Triple<Integer, Integer, Integer> bridgeZPos() {
            return bridgeZPos;
        }

        public Triple<Integer, Integer, Integer> bridgeXNeg() {
            return bridgeXNeg;
        }

        public Triple<Integer, Integer, Integer> bridgeZNeg() {
            return bridgeZNeg;
        }

        public Triple<Integer, Integer, Integer> middle() {
            return middle;
        }

        public ProcessedChunk getNeighborXPos() {
            return neighborXPos;
        }

        public ProcessedChunk getNeighborZPos() {
            return neighborZPos;
        }

        public ProcessedChunk getNeighborXNeg() {
            return neighborXNeg;
        }

        public ProcessedChunk getNeighborZNeg() {
            return neighborZNeg;
        }


        public boolean checkNeighbors() {
            if (this.neighborXPos == null) this.neighborXPos = processedChunksToStoreInDB.get(new Pair<>(x + 1, z));
            if (this.neighborZPos == null) this.neighborZPos = processedChunksToStoreInDB.get(new Pair<>(x, z + 1));
            if (this.neighborXNeg == null) this.neighborXNeg = processedChunksToStoreInDB.get(new Pair<>(x - 1, z));
            if (this.neighborZNeg == null) this.neighborZNeg = processedChunksToStoreInDB.get(new Pair<>(x, z - 1));
            if (this.neighborXPos != null) this.neighborXPos.neighborXNeg = this;
            if (this.neighborZPos != null) this.neighborZPos.neighborZNeg = this;
            if (this.neighborXNeg != null) this.neighborXNeg.neighborXPos = this;
            if (this.neighborZNeg != null) this.neighborZNeg.neighborZPos = this;
            return this.neighborXPos != null && this.neighborZPos != null && this.neighborXNeg != null && this.neighborZNeg != null;
        }
    }
}
