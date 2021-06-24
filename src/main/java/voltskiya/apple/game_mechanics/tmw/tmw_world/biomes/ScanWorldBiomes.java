package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks.TopBlock;
import voltskiya.apple.utilities.util.data_structures.Pair;
import voltskiya.apple.utilities.util.data_structures.Triple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static voltskiya.apple.game_mechanics.deleteme_later.chunks.TemperatureChunk.BLOCKS_IN_A_CHUNK;

public class ScanWorldBiomes {
    private final Location starterLocation;
    private final World world;
    private final List<Pair<Integer, Integer>> chunksToScan = new ArrayList<>();
    private final List<ComputedBiomeChunkWithBorders> pastComputedChunks = new ArrayList<>();
    private final List<ProcessedChunk> processedChunksToStoreInDB = new ArrayList<>();
    private double chunksPerStep = 1;
    private int chunksComputedThisStep;

    public ScanWorldBiomes(Location starterLocation, int x1, int z1, int x2, int z2) {
        this.starterLocation = starterLocation;
        this.world = starterLocation.getWorld();
        // start at the location and scan outwards 
        scan();
        HashSet<Pair<Integer, Integer>> alreadyChecked = new HashSet<>();
        for (int xi = starterLocation.getBlockX(); xi * 16 < x2; xi++) {
            for (int zi = starterLocation.getBlockZ(); zi * 16 < z2; zi++) {
                final Pair<Integer, Integer> chunkCoords = new Pair<>(xi, zi);
                if (alreadyChecked.add(chunkCoords)) {
                    chunksToScan.add(chunkCoords);
                }
            }
        }
        for (int xi = starterLocation.getBlockX(); xi * 16 > x1; xi--) {
            for (int zi = starterLocation.getBlockZ(); zi * 16 < z2; zi++) {
                final Pair<Integer, Integer> chunkCoords = new Pair<>(xi, zi);
                if (alreadyChecked.add(chunkCoords)) {
                    chunksToScan.add(chunkCoords);
                }
            }
        }
        for (int xi = starterLocation.getBlockX(); xi * 16 < x2; xi++) {
            for (int zi = starterLocation.getBlockZ(); zi * 16 > z1; zi--) {
                final Pair<Integer, Integer> chunkCoords = new Pair<>(xi, zi);
                if (alreadyChecked.add(chunkCoords)) {
                    chunksToScan.add(chunkCoords);
                }
            }
        }
        for (int xi = starterLocation.getBlockX(); xi * 16 > x1; xi--) {
            for (int zi = starterLocation.getBlockZ(); zi * 16 > z1; zi--) {
                final Pair<Integer, Integer> chunkCoords = new Pair<>(xi, zi);
                if (alreadyChecked.add(chunkCoords)) {
                    chunksToScan.add(chunkCoords);
                }
            }
        }
    }

    public void scan() {
        // determine if we should increase or decrease our chunk loading speed
        final double tps = Bukkit.getTPS()[0];
        if (tps > 19.75) {
            this.chunksPerStep++;
        } else if (tps < 19) {
            this.chunksPerStep--;
        }
        if (chunksPerStep == 0) {
            return;
        }

        this.chunksComputedThisStep = 0;
        List<PrepareChunks> chunksToProcess = new ArrayList<>();
        List<ComputedChunkProcessing> alreadyLoadedChunks = new ArrayList<>();
        while (this.chunksComputedThisStep < this.chunksPerStep && !this.chunksToScan.isEmpty()) {
            // determine what chunks need to be computed for this step
            chunksToProcess.add(determineRequiredChunks(alreadyLoadedChunks));
        }
        // process the chunksToProcess
        process(chunksToProcess);
        cleanUp();
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
                if (chunkComputed.getX() == chunkToProcess.middleX() && chunkComputed.getZ() == chunkToProcess.middleZ()) {
                    bridgeXPos = chunkComputed.bridgeXPos();
                    bridgeZPos = chunkComputed.bridgeZPos();
                    bridgeXNeg = chunkComputed.bridgeXNeg();
                    bridgeZNeg = chunkComputed.bridgeZNeg();
                    middle = chunkComputed.middle();
                }
            }
            this.processedChunksToStoreInDB.add(
                    new ProcessedChunk(
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

        // make sure the list of pastComputedChunks is as small as possible to conserve ram
        while (pastComputedChunks.size() > this.chunksPerStep * 9) pastComputedChunks.remove(0);

        // schedule ourselves again
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::scan, 1);
    }

    private PrepareChunks determineRequiredChunks(List<ComputedChunkProcessing> alreadyLoadedChunks) {
        List<ComputedChunkProcessing> chunksToBeComputed = new ArrayList<>();
        List<ComputedBiomeChunkWithBorders> chunksComputed = new ArrayList<>();
        System.out.println("determining chunks to be computed");
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
        return new PrepareChunks(chunksToBeComputed, chunksComputed, chunkToScanCoords.getKey(), chunkToScanCoords.getValue());
    }

    private record PrepareChunks(List<ComputedChunkProcessing> chunksToBeComputed,
                                 List<ComputedBiomeChunkWithBorders> chunksComputed,
                                 int middleX,
                                 int middleZ) {
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
                x = starterLocation.getBlockX();
                y = starterLocation.getBlockY();
                z = starterLocation.getBlockZ();
            } else {
                boolean success = false;
                for (ComputedBiomeChunkWithBorders other : chunksComputed) {
                    final Triple<Integer, Integer, Integer> bridge = other.getBridge(getX(), getZ());
                    if (bridge != null) {
                        x = bridge.getX();
                        y = bridge.getY();
                        z = bridge.getZ();
                        success = true;
                        break;
                    }
                }
                if (!success) return;
            }

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
                    bridgeXNeg = new Triple<>(getPreciseX(), topBlock.y(), topBlock.z());
                else if (topBlock.x() == BLOCKS_IN_A_CHUNK - 1 && bridgeXPos == null)
                    bridgeXPos = new Triple<>(topBlock.x(), topBlock.y(), topBlock.z());
                else if (topBlock.z() == 0 && bridgeZNeg == null)
                    bridgeZNeg = new Triple<>(topBlock.x(), topBlock.y(), getPreciseZ());
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

        public Triple<Integer, Integer, Integer> getBridge(int x, int z) {
            if (this.getX() == x) {
                if (this.getZ() - 1 == z) {
                    return bridgeZNeg;
                } else if (this.getZ() + 1 == z) {
                    return bridgeZPos;
                }
            } else if (this.getZ() == z) {
                if (this.getX() - 1 == x) {
                    return bridgeXNeg;
                } else if (this.getX() + 1 == x) {
                    return bridgeXPos;
                }
            }
            return null;
        }

        private int getX() {
            return x;
        }

        private int getZ() {
            return z;
        }
    }

    private record ProcessedChunk(ComputedBiomeChunk computedBiomeChunk, Triple<Integer, Integer, Integer> bridgeXPos,
                                  Triple<Integer, Integer, Integer> bridgeZPos,
                                  Triple<Integer, Integer, Integer> bridgeXNeg,
                                  Triple<Integer, Integer, Integer> bridgeZNeg,
                                  Triple<Integer, Integer, Integer> middle) {
    }
}
