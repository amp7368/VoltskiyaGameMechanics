package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import voltskiya.apple.game_mechanics.tmw.sql.TmwChunkEntity;
import voltskiya.apple.game_mechanics.tmw.sql.TmwMapContour;
import voltskiya.apple.game_mechanics.tmw.sql.VerifyDatabaseTmw;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeUIDDatabase;

public class ScanWorldChunk {

    public static final int CHUNK_LENGTH = 16;
    public ChunkColumn[][] columns = new ChunkColumn[CHUNK_LENGTH][CHUNK_LENGTH];
    private ChunkColumn[] north = new ChunkColumn[CHUNK_LENGTH];
    private ChunkColumn[] south = new ChunkColumn[CHUNK_LENGTH];
    private ChunkColumn[] east = new ChunkColumn[CHUNK_LENGTH];
    private ChunkColumn[] west = new ChunkColumn[CHUNK_LENGTH];
    private List<ChunkColumnFlattened> contours = new ArrayList<>();

    public ScanWorldChunk() {
    }

    public void setChunkUID() {
        for (ChunkColumnFlattened contour : contours) {
            contour.setChunkUID();
        }
    }

    public List<TmwMapContour> toSaveableChunk(int worldMyUid, int x, int z) {
        List<TmwMapContour> chunks = new ArrayList<>();
        for (ChunkColumnFlattened contour : contours) {
            if (contour.getRefer() == contour) {
                chunks.add(contour.toSaveableContour(worldMyUid, x, z));
            }
        }
        return chunks;
    }

    public List<TmwChunkEntity> toSaveableBiome() {
        List<TmwChunkEntity> chunks = new ArrayList<>();
        for (ChunkColumnFlattened contour : contours) {
            chunks.add(contour.toSaveableBiome());
        }
        return chunks;
    }

    public void cleanUp(@Nullable ScanWorldChunk zPos,
        @Nullable ScanWorldChunk zNeg,
        @Nullable ScanWorldChunk xPos,
        @Nullable ScanWorldChunk xNeg) {
        if (zPos != null) {
            doNextTo(north, zPos.south, ScanBorder.NORTH);
        }
        if (zNeg != null) {
            doNextTo(south, zNeg.north, ScanBorder.SOUTH);
        }
        if (xPos != null) {
            doNextTo(east, xPos.west, ScanBorder.EAST);
        }
        if (xNeg != null) {
            doNextTo(west, xNeg.east, ScanBorder.WEST);
        }
    }

    private void doNextTo(ChunkColumn[] row1, ChunkColumn[] row2, ScanBorder border) {
        for (int i = 0; i < CHUNK_LENGTH; i++) {
            row1[i].doNextTo(row2[i], border);
        }
    }

    public void free() {
        north = null;
        south = null;
        east = null;
        west = null;
        contours = null;
    }


    public void calculate() {
        while (flatten())
            ;
        freeInner();
    }

    private void freeInner() {
        int length = CHUNK_LENGTH - 1;
//        int lengthInner = length - 1;
//        for (int xi = 1; xi < lengthInner; xi++) {
//            for (int zi = 1; zi < lengthInner; zi++) {
//                columns[xi][zi].free();
//                columns[xi][zi] = null;
//            }
//        }todo
        for (int xi = 0; xi < CHUNK_LENGTH; xi++) {
            for (int zi = 0; zi < CHUNK_LENGTH; zi++) {
                if (xi == 0) {
                    west[zi] = columns[xi][zi];
                } else if (xi == length) {
                    east[zi] = columns[xi][zi];
                }
                if (zi == 0) {
                    south[xi] = columns[xi][zi];
                } else if (zi == length) {
                    north[xi] = columns[xi][zi];
                } else {
                    zi = length - 1; // skip to next possible check
                }
            }
        }
//        columns = null;
    }

    private boolean flatten() {
        boolean shouldContinue = false;
        for (int xi = 0; xi < CHUNK_LENGTH; xi++) {
            for (int zi = 0; zi < CHUNK_LENGTH; zi++) {
                ChunkColumn zPos = zi + 1 == CHUNK_LENGTH ? null : columns[xi][zi + 1];
                ChunkColumn zNeg = zi == 0 ? null : columns[xi][zi - 1];
                ChunkColumn xPos = xi + 1 == CHUNK_LENGTH ? null : columns[xi + 1][zi];
                ChunkColumn xNeg = xi == 0 ? null : columns[xi - 1][zi];
                ChunkColumn me = columns[xi][zi];
                boolean didFlatten = me.flatten(contours, zPos, zNeg, xPos, xNeg);
                if (didFlatten) {
                    shouldContinue = true;
                }
            }
        }
        return shouldContinue;
    }


    public static class ChunkColumnFlattened {

        private final int middleX;
        private final int middleY;
        private final int middleZ;
        private Map<ResourceLocation, Integer> biomes = new HashMap<>();
        private ChunkColumnFlattened refer = null;
        private ChunkColumnFlattened north = null;
        private ChunkColumnFlattened south = null;
        private ChunkColumnFlattened east = null;
        private ChunkColumnFlattened west = null;
        private long chunkUID;
        private int biomeUID;

        public ChunkColumnFlattened(int middleX, int middleY, int middleZ) {
            this.middleX = middleX;
            this.middleY = middleY;
            this.middleZ = middleZ;
        }

        private static Long getChunkUid(ChunkColumnFlattened chunk) {
            if (chunk != null) {
                System.out.println(chunk.chunkUID);
            }
            return chunk == null ? null : chunk.chunkUID;
        }

        public void addBiome(ResourceLocation biome) {
            getRefer().addBiome_(biome);
        }

        private void addBiome_(ResourceLocation biome) {
            biomes.compute(biome, (b, i) -> i == null ? 1 : i + 1);
        }

        public void join(ChunkColumnFlattened flattened) {
            getRefer().join_(flattened.getRefer());
        }

        private void join_(ChunkColumnFlattened flattened) {
            for (Map.Entry<ResourceLocation, Integer> biome : flattened.biomes.entrySet()) {
                biomes.compute(biome.getKey(),
                    (b, i) -> i == null ? biome.getValue() : i + biome.getValue());
            }
            flattened.biomes = null;
            flattened.referTo(getRefer());
        }

        public ChunkColumnFlattened getRefer() {
            return this.refer == null ? this : this.refer.getRefer();
        }

        private void referTo(ChunkColumnFlattened refer) {
            this.refer = refer;
        }

        public void setBorder(ChunkColumnFlattened other, ScanBorder border) {
            getRefer().setBorder_(other.getRefer(), border);
        }

        private void setBorder_(ChunkColumnFlattened other, ScanBorder border) {
            switch (border) {
                case NORTH -> {
                    this.north = other;
                    other.south = this;
                }
                case SOUTH -> {
                    this.south = other;
                    other.north = this;
                }
                case EAST -> {
                    this.east = other;
                    other.west = this;
                }
                case WEST -> {
                    this.west = other;
                    other.east = this;
                }
            }
        }

        public ChunkColumnFlattened getBorder(ScanBorder border) {
            return getRefer().getBorder_(border);

        }

        private ChunkColumnFlattened getBorder_(ScanBorder border) {
            return switch (border) {
                case NORTH -> this.north;
                case SOUTH -> this.south;
                case EAST -> this.east;
                case WEST -> this.west;
            };
        }

        public TmwMapContour toSaveableContour(int worldMyUid, int x, int z) {
            ChunkColumnFlattened border = getBorder(ScanBorder.EAST);
            Long e = getChunkUid(border);
            Long w = getChunkUid(getBorder(ScanBorder.WEST));
            Long n = getChunkUid(getBorder(ScanBorder.NORTH));
            Long s = getChunkUid(getBorder(ScanBorder.SOUTH));
            System.out.printf("%s, %s,%s,%s,%s\n", border, e, w, n, s);
            return new TmwMapContour(chunkUID, worldMyUid, x, z, e, w, n, s, middleX, middleY,
                middleZ);
        }

        public void setChunkUID() {
            getRefer().setChunkUID_();
        }

        public void setChunkUID_() {
            this.chunkUID = VerifyDatabaseTmw.getChunkUid();
            int maxBiomeVal = 0;
            ResourceLocation maxBiome = null;
            for (Map.Entry<ResourceLocation, Integer> biome : biomes.entrySet()) {
                if (biome.getValue() > maxBiomeVal || maxBiome == null) {
                    maxBiomeVal = biome.getValue();
                    maxBiome = biome.getKey();
                }
            }
            this.biomeUID = BiomeUIDDatabase.get().getBiome(maxBiome);
        }

        public TmwChunkEntity toSaveableBiome() {
            return new TmwChunkEntity(chunkUID, biomeUID);
        }
    }
}
