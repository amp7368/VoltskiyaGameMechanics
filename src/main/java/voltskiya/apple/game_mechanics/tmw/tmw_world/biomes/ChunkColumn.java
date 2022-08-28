package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;

import apple.utilities.util.NumberUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ChunkColumn {

    private final int x;
    private final int z;
    private List<ChunkTopBlock> topBlocks = new ArrayList<>(5);

    public ChunkColumn(int x, int z) {
        this.x = x;
        this.z = z;
    }

    private static boolean isBorder(int a, int b) {
        return NumberUtils.betweenInclusive(-1, a - b, 1);
    }

    public void addTopBlock(@NotNull ResourceLocation biome, int y) {
        topBlocks.add(new ChunkTopBlock(biome, y));
    }

    public void free() {
        topBlocks = null;
    }

    public boolean flatten(List<ScanWorldChunk.ChunkColumnFlattened> contours,
        ChunkColumn... others) {
        boolean didFlatten = false;
        for (ChunkTopBlock topBlock : topBlocks) {
            boolean didIFlatten = topBlock.flattenBlock(contours, this.x, this.z, others);
            if (didIFlatten) {
                didFlatten = true;
            }
        }
        return didFlatten;
    }

    public void doNextTo(ChunkColumn otherColumn, ScanBorder border) {
        for (ChunkTopBlock me : topBlocks) {
            if (me.flattened == null || me.flattened.getBorder(border) != null) {
                continue;
            }
            for (ChunkTopBlock other : otherColumn.topBlocks) {
                if (isBorder(me.y, other.y)) {
                    me.flattened.setBorder(other.flattened, border);
                    break;
                }
            }
        }
    }

    public static class ChunkTopBlock {

        private final ResourceLocation biome;
        private final int y;
        private ScanWorldChunk.ChunkColumnFlattened flattened = null;

        public ChunkTopBlock(ResourceLocation biome, int y) {
            this.biome = biome;
            this.y = y;
        }

        public boolean flattenBlock(List<ScanWorldChunk.ChunkColumnFlattened> contours, int x,
            int z, ChunkColumn... others) {
            boolean didFlatten = false;
            for (ChunkColumn otherColumn : others) {
                if (otherColumn == null) {
                    continue;
                }
                for (ChunkTopBlock other : otherColumn.topBlocks) {
                    if (isBorder(other.y, this.y)) {
                        boolean didIFlatten = flattenBlock(contours, other, x, z);
                        if (didIFlatten) {
                            didFlatten = true;
                        }
                    }
                }
            }
            return didFlatten;
        }

        private boolean flattenBlock(List<ScanWorldChunk.ChunkColumnFlattened> contours,
            ChunkTopBlock other, int x, int z) {
            if (other.flattened == null) {
                ScanWorldChunk.ChunkColumnFlattened flattened;
                if (this.flattened == null) {
                    flattened = new ScanWorldChunk.ChunkColumnFlattened(x, this.y, z);
                    contours.add(flattened);
                    flattened.addBiome(biome);
                    this.flattened = flattened;
                } else {
                    flattened = this.flattened.getRefer();
                }
                other.flattened = flattened;
                flattened.addBiome(other.biome);
            } else {
                ScanWorldChunk.ChunkColumnFlattened flattened = other.flattened.getRefer();
                if (this.flattened == null) {
                    flattened.addBiome(this.biome);
                } else if (this.flattened.getRefer() == flattened) {
                    return false;
                } else {
                    contours.remove(this.flattened.getRefer());
                    flattened.join(this.flattened);
                }
                this.flattened = flattened;
            }
            return true;
        }
    }

}
