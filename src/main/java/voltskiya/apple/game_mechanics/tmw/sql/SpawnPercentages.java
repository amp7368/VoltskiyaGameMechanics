package voltskiya.apple.game_mechanics.tmw.sql;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.SimpleWorldDatabase;
import voltskiya.apple.utilities.util.minecraft.MaterialUtils;

import java.util.*;

import static voltskiya.apple.game_mechanics.deleteme_later.chunks.TemperatureChunk.BLOCKS_IN_A_CHUNK;
import static voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks.scanNearby;

public final class SpawnPercentages {
    private @NotNull BiomeType biome;
    private int chunkX;
    private int chunkZ;
    private int worldMyUid;
    private int middleX;
    private int middleY;
    private int middleZ;
    private HashMap<String, Integer> mobNames = new HashMap<>();
    private double mobCount = 0;
    private ArrayList<MobType> mobsToSpawn = new ArrayList<>();

    public SpawnPercentages(
            @NotNull BiomeType biome,
            int chunkX,
            int chunkZ,
            @Nullable String mobName,
            int worldMyUid, int middleX, int middleY, int middleZ, int mobCount) {
        this.biome = biome;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.worldMyUid = worldMyUid;
        this.middleX = middleX;
        this.middleY = middleY;
        this.middleZ = middleZ;
        if (mobName != null)
            this.mobNames.put(mobName, mobCount);
        this.mobCount += mobCount;
    }

    public void add(String mobName, int mobCount) {
        this.mobNames.put(mobName, mobCount);
        this.mobCount += mobCount;
    }

    public void spawn() {
        @Nullable UUID worldUUID = SimpleWorldDatabase.getWorld(worldMyUid);
        if (worldUUID == null) {
            return;
        } else {
            @Nullable World world = Bukkit.getWorld(worldUUID);
            if (world == null) {
                return;
            } else if (world.isChunkLoaded(chunkX, chunkZ)) {
                return;
            }
        }

        while (!this.mobCountHigh()) {
            Map<MobType, Double> shouldBeMobSpawns = biome.getSpawnPercentages();
            Map<String, Double> mobSpawns = new HashMap<>();
            for (Map.Entry<String, Integer> mob : this.mobNames.entrySet())
                mobSpawns.put(mob.getKey(), mob.getValue() / this.mobCount);

            double totalPerc = 0;
            Map<MobType, Double> shouldBe = new HashMap<>();
            for (Map.Entry<MobType, Double> shouldBeMob : shouldBeMobSpawns.entrySet()) {
                double shouldBeNess = shouldBeMob.getValue() - mobSpawns.getOrDefault(shouldBeMob.getKey().getName(), 0d);
                shouldBe.put(shouldBeMob.getKey(), shouldBeNess);
                totalPerc += shouldBeNess;
            }
            // randomly choose a mob but with correct distribution
            totalPerc *= Math.random();
            boolean wasSet = false;
            for (Map.Entry<MobType, Double> mob : shouldBe.entrySet()) {
                totalPerc -= mob.getValue();
                if (totalPerc < 0) {
                    this.mobCount += mob.getKey().getMeanGroup();
                   if (!this.mobsToSpawn.contains(mob.getKey())) this.mobsToSpawn.add(mob.getKey());
                    wasSet = true;
                    break;
                }
            }
            // just as a failsafe to make sure progress is made
            if (!wasSet) this.mobCount++;
        }

    }

    public boolean mobCountHigh() {
        return this.biome.getUsefulSpawnRate() < this.mobCount;
    }

    public boolean noMobsToSpawn() {
        return this.mobsToSpawn.isEmpty();
    }

    public void spawnIRL() {
        @Nullable UUID worldUUID = SimpleWorldDatabase.getWorld(worldMyUid);
        if (worldUUID != null) {
            @Nullable World world = Bukkit.getWorld(worldUUID);
            if (world != null) {
                @NotNull Chunk chunk = world.getChunkAt(chunkX, chunkZ);
                this.spawnIRL(chunk);
            }
        }
    }

    private void spawnIRL(Chunk chunk) {
        @NotNull Block block = chunk.getBlock(middleX, middleY, middleZ);
        int y = middleY;
        if (MaterialUtils.isPassable(block.getType())) {
            do {
                block = chunk.getBlock(middleX, --y, middleZ);
            } while (MaterialUtils.isPassable(block.getType()));
            y++;
        } else {
            do {
                block = chunk.getBlock(middleX, ++y, middleZ);
            } while (!MaterialUtils.isPassable(block.getType()));
        }
        List<BiomeTypeBuilderRegisterBlocks.TopBlock> topBlocks = scanNearby(chunk,
                new HashSet<>(),
                middleX,
                y + 1,
                middleZ
        );
        Collections.shuffle(topBlocks);
        List<TmwStoredMob> mobsToSave = new ArrayList<>();
        for (MobType mobType : mobsToSpawn) {
            for (int i = 0; i < topBlocks.size(); i++) {
                final BiomeTypeBuilderRegisterBlocks.TopBlock topBlock = topBlocks.get(i);
                if (mobType.canSpawn(topBlock)) {
                    final int group = mobType.getGroup();
                    for (int mobCount = 0; mobCount < group; mobCount++) {
                        final TmwStoredMob storedMob = new TmwStoredMob(topBlock.x() + chunkX * BLOCKS_IN_A_CHUNK, topBlock.y() + 1, topBlock.z() + chunkZ * BLOCKS_IN_A_CHUNK, worldMyUid, mobType);
                        mobsToSave.add(storedMob);
                        System.out.printf("spawn %s %d, %d, %d\n", storedMob.uniqueName, storedMob.x, storedMob.y, storedMob.z);
                    }
                    topBlocks.remove(i);
                    break;
                }
            }
        }
        MobSqlStorage.insertMobs(mobsToSave);
    }
}
