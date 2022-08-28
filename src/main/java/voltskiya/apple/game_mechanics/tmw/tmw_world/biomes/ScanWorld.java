package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;


import apple.nms.decoding.world.DecodeBiome;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;
import voltskiya.apple.game_mechanics.tmw.sql.BiomeSqlStorage;
import voltskiya.apple.game_mechanics.tmw.sql.TmwChunkEntity;
import voltskiya.apple.game_mechanics.tmw.sql.TmwMapContour;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.SimpleWorldDatabase;

public class ScanWorld {

    private final World world;
    private final int worldMyUid;
    private final int xCorner1;
    private final int zCorner1;
    private final int layerLength;
    private final int totalLayers;
    private final List<Double> tps = new ArrayList<>();
    private final ForkJoinPool workerService = new ForkJoinPool(
        ((int) (Runtime.getRuntime().availableProcessors() * 1.5)));
    private final int buildHeight;
    ScanWorldConfig scanningConfig = ScanWorldConfig.get();
    private ScanWorldChunk[] layer1;
    private ScanWorldChunk[] layer2;
    private ScanWorldChunk[] layer3;
    private int ziLayer = 0;
    private int xiLayer = 0;
    private int limit = 0;
    private double limitShouldBe = ScanWorldConfig.get().limitShouldBe;
    private ScanningState state = ScanningState.SCAN;
    private boolean isWaitingForLastPhase = false;

    public ScanWorld(World world, int xCorner1, int zCorner1, int xCorner2, int zCorner2) {
        this.world = world;
        this.buildHeight = world.getMaxHeight();
        this.xCorner1 = Math.min(xCorner1, xCorner2) / 16;
        this.zCorner1 = Math.min(zCorner1, zCorner2) / 16;
        int xCorner21 = Math.max(xCorner1, xCorner2) / 16;
        int zCorner21 = Math.max(zCorner1, zCorner2) / 16;
        this.layerLength = xCorner21 - this.xCorner1 + 1;
        this.totalLayers = zCorner21 - this.zCorner1 + 1;
        this.layer1 = new ScanWorldChunk[layerLength];
        this.layer2 = new ScanWorldChunk[layerLength];
        this.layer3 = new ScanWorldChunk[layerLength];
        this.worldMyUid = SimpleWorldDatabase.getWorld(world.getUID());
    }

    public void scanAll() {
        setLimitFromTps();
        PluginTMW.get()
            .log(Level.INFO, "state: %s, work/tick: %.2f, Completion: %.1f%%", state.toString(),
                limitShouldBe, ziLayer / (double) totalLayers * 100);
        while (limit != 0) {
            runSelf();
        }
        if (state == ScanningState.DONE_DONE) {
            finish();
            return;
        }
        VoltskiyaPlugin.get()
            .scheduleSyncDelayedTask(this::scanAll, scanningConfig.ticksPerScheduleWork);
    }

    private void finish() {
        for (int i = 0; i < 7; i++) {
            PluginTMW.get().log(Level.INFO, "DONE DONE SCANNING");
        }
    }

    private void runSelf() {
        if (isWaitingForLastPhase) {
            this.isWaitingForLastPhase = !workerService.isQuiescent();
            if (isWaitingForLastPhase) {
                limit = 0;
                return;
            }
        }
        switch (state) {
            case SCAN -> scanLayer();
            case CONSOLIDATE -> consolidateLayer();
            case INSERT -> insertLayer();
            case FINALIZE -> shiftLayer();
            case DONE_DONE -> limit = 0;
        }
    }

    private void setLimitFromTps() {
        double nowTps = Bukkit.getTPS()[0];
        this.tps.add(nowTps);

        while (this.tps.size() > scanningConfig.getWatchTpsSize()) {
            this.tps.remove(0);
        }
        double oldTps = this.tps.get(0);
        if (nowTps > scanningConfig.maxTps || oldTps < nowTps) {
            incrementLimit(1);
        } else if (oldTps > nowTps) {
            incrementLimit(-1);
        }
        limit = (int) limitShouldBe;
        if (Math.random() < limitShouldBe - limit) {
            limit++;
        }
    }

    private void incrementLimit(double i) {
        limitShouldBe += i / scanningConfig.timeWatchTpsFor * scanningConfig.limitVariation;
    }

    private void incrementInsideLoop() {
        xiLayer = 0;
        state = state.next();
        isWaitingForLastPhase = true;
    }

    private void insertLayer() {
        if (reachedCapacity()) {
            return;
        }
        int z = ziLayer + zCorner1;
        List<TmwMapContour> chunks = new ArrayList<>();
        List<TmwChunkEntity> biomes = new ArrayList<>();
        for (; xiLayer < layerLength; xiLayer++) {
            ScanWorldChunk chunk = layer3[xiLayer];
            chunk.setChunkUID();
            chunks.addAll(chunk.toSaveableChunk(worldMyUid, xiLayer + xCorner1, z));
            biomes.addAll(chunk.toSaveableBiome());
            if (--limit == 0) {
                break;
            }
        }
        addTask(() -> {
            BiomeSqlStorage.insertContour(chunks);
            BiomeSqlStorage.insertBiomes(biomes);
        });
        if (limit != 0) {
            incrementInsideLoop();
        }
    }

    private void shiftLayer() {
        for (ScanWorldChunk scanWorldChunk : layer1) {
            if (scanWorldChunk != null) {
                scanWorldChunk.free();
            }
        }
        layer1 = layer2;
        layer2 = layer3;
        layer3 = new ScanWorldChunk[layerLength];
        if (++ziLayer == totalLayers) {
            state = ScanningState.DONE_DONE;
        } else {
            incrementInsideLoop();
        }
    }

    private void consolidateLayer() {
        if (reachedCapacity()) {
            return;
        }
        for (; xiLayer < layerLength; xiLayer++) {
            ScanWorldChunk me = layer2[xiLayer];
            if (me == null) {
                continue;
            }
            ScanWorldChunk zPos = layer3[xiLayer];
            ScanWorldChunk zNeg = layer1[xiLayer];
            ScanWorldChunk xPos = xiLayer + 1 == layerLength ? null : layer2[xiLayer + 1];
            ScanWorldChunk xNeg = xiLayer == 0 ? null : layer2[xiLayer - 1];
            addTask(() -> me.cleanUp(zPos, zNeg, xPos, xNeg));
            if (--limit == 0) {
                return;
            }
        }
        incrementInsideLoop();
    }

    private void addTask(Runnable runnable) {
        workerService.submit(runnable);
    }

    private void scanLayer() {
        if (reachedCapacity()) {
            return;
        }
        int z = ziLayer + zCorner1;
        for (; xiLayer < layerLength; xiLayer++) {
            scanChunk(z, xiLayer);
            if (--limit == 0) {
                return;
            }
        }
        incrementInsideLoop();
    }

    private boolean reachedCapacity() {
        return limit <= 0 || workerService.getQueuedTaskCount() >= scanningConfig.maxExecutorSize;
    }

    private void scanChunk(int z, int xi) {
        int x = xi + xCorner1;
        ChunkSnapshot chunkToScan = world.getChunkAt(x, z).getChunkSnapshot(false, true, false);
        ScanWorldChunk scannedChunk;
        layer3[xi] = scannedChunk = new ScanWorldChunk();
        addTask(() -> scanChunk(chunkToScan, scannedChunk, x, z));
    }

    private void scanChunk(ChunkSnapshot chunkToScan, ScanWorldChunk scannedChunk, int x, int z) {
        for (int xii = 0; xii < 16; xii++) {
            for (int zii = 0; zii < 16; zii++) {
                ChunkColumn column = scannedChunk.columns[xii][zii] = new ChunkColumn(xii, zii);
                scanColumn(chunkToScan, column, xii, zii, x + xii, z + zii);
            }
        }
        scannedChunk.calculate();
    }

    private void scanColumn(ChunkSnapshot chunkToScan, ChunkColumn column, int xi, int zi, int x,
        int z) {
        boolean isLastBlockAir = true;
        for (int yi = 0; yi < buildHeight; yi++) {
            Material block = chunkToScan.getBlockType(xi, yi, zi);
            boolean isThisBlockAir = block.isAir();
            if (!isLastBlockAir && isThisBlockAir) {
                @Nullable ResourceLocation biome = DecodeBiome.getBiomeKey(this.world, x, yi, z)
                    .location();
                if (biome != null) {
                    column.addTopBlock(biome, yi - 1);
                }
            }
            isLastBlockAir = isThisBlockAir;
        }
    }

    private enum ScanningState {
        SCAN,
        CONSOLIDATE,
        INSERT,
        FINALIZE,
        WAITING,
        DONE_DONE;
        private static final int LAST_INDEX = values().length - 2;

        public ScanningState next() {
            int index = ordinal() + 1;
            if (index == LAST_INDEX) {
                index = 0;
            }
            return values()[index];
        }
    }
}
