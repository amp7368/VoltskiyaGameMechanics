package voltskiya.apple.game_mechanics.tmw.sql;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.SqlWorldGet;
import voltskiya.apple.utilities.util.minecraft.MaterialUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Consumer;

import static voltskiya.apple.game_mechanics.deleteme_later.chunks.TemperatureChunk.BLOCKS_IN_A_CHUNK;
import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.*;
import static voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks.TopBlock;
import static voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks.scanNearby;

public class MobSqlStorage {
    private static final double MOB_TYPE_VARIATION = .5;

    public static void insertMobs(List<StoredMob> mobs) {
        new Thread(() -> {
            try {
                insertThreaded(mobs);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

    private static void insertThreaded(List<StoredMob> mobs) throws SQLException {
        synchronized (TmwSqlVerifyDatabase.syncDB) {
            Statement statement = TmwSqlVerifyDatabase.database.createStatement();
            for (StoredMob mob : mobs) {
                statement.addBatch(String.format("""
                                INSERT INTO stored_mob (%s, %s, %s, %s, %s, %s, %s)
                                VALUES (%d,%d,%d,%d,%d,'%s',%d)
                                ON CONFLICT DO NOTHING;""",
                        MOB_MY_UID,
                        X,
                        Y,
                        Z,
                        WORLD_MY_UID,
                        MOB_UNIQUE_NAME,
                        DESPAWN_TIME,
                        mob.uid,
                        mob.x,
                        mob.y,
                        mob.z,
                        mob.myWorldUid,
                        mob.uniqueName,
                        mob.despawnTime
                ));
            }
            statement.executeBatch();
            statement.close();
        }
    }

    public static void getMobs(int lowerX, int upperX, int lowerZ, int upperZ, Consumer<List<StoredMob>> callback) {
        new Thread(() -> {
            List<StoredMob> answer;
            try {
                answer = getMobThreaded(lowerX, upperX, lowerZ, upperZ);
            } catch (SQLException throwables) {
                answer = null;
            }
            List<StoredMob> finalAnswer = answer;
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> callback.accept(finalAnswer));
        }).start();
    }

    private static List<StoredMob> getMobThreaded(int lowerX, int upperX, int lowerZ, int upperZ) throws SQLException {
        synchronized (TmwSqlVerifyDatabase.syncDB) {
            Statement statement = TmwSqlVerifyDatabase.database.createStatement();
            ResultSet response = statement.executeQuery(String.format("""
                            SELECT *
                            FROM %s
                            WHERE %s BETWEEN %d AND %d
                              AND %s BETWEEN %d AND %d""",
                    TABLE_STORED_MOB,
                    X, lowerX, upperX,
                    Z, lowerZ, upperZ
                    )
            );
            List<StoredMob> mobs = new ArrayList<>();
            List<Long> mobsToDelete = new ArrayList<>();
            while (response.next()) {
                try {
                    mobs.add(new StoredMob(response.getLong(MOB_MY_UID), response.getInt(X), response.getInt(Y), response.getInt(Z), response.getInt(WORLD_MY_UID), response.getString(MOB_UNIQUE_NAME), response.getTimestamp(DESPAWN_TIME)));
                } catch (CommandSyntaxException e) {
                    mobsToDelete.add(response.getLong(MOB_MY_UID));
                    e.printStackTrace();
                }
            }
            removeMobs(mobsToDelete);
            statement.close();
            return mobs;
        }
    }

    public static void removeMobs(Collection<Long> mobsToDelete) throws SQLException {
        if (mobsToDelete.isEmpty()) return;
        synchronized (TmwSqlVerifyDatabase.syncDB) {
            Statement statement = TmwSqlVerifyDatabase.database.createStatement();
            List<String> whereClauses = new ArrayList<>();
            for (Long mob : mobsToDelete) {
                whereClauses.add(MOB_MY_UID + " = " + mob + " ");
            }
            statement.execute(String.format("DELETE FROM %s WHERE %s", TABLE_STORED_MOB, String.join(" OR ", whereClauses)));
            statement.close();
        }
    }

    public static void getRegen(Consumer<Map<Long, SpawnPercentages>> afterRun) {
        try {
            afterRun.accept(getRegen());
        } catch (SQLException e) {
            e.printStackTrace();
            afterRun.accept(null);
        }
    }

    public static Map<Long, SpawnPercentages> getRegen() throws SQLException {
        Map<Long, SpawnPercentages> spawnPercentages = new HashMap<>();
        synchronized (TmwSqlVerifyDatabase.syncDB) {
            Statement statement = TmwSqlVerifyDatabase.database.createStatement();
            ResultSet response = statement.executeQuery(String.format("""
                            SELECT nearby.*, %s.%s, count(%s.%s) as mob_count
                            FROM (
                                     SELECT center.%s as cchunk_uid,
                                            %s.%s,
                                            %s.%s,
                                            center.%s,
                                            center.%s,
                                            center.%s,
                                            center.%s,
                                            center.%s,
                                            bridge.%s as bridge_uid
                                     FROM %s center
                                              INNER JOIN %s ON %s.%s = center.%s
                                              INNER JOIN %s bridge
                                                         ON bridge.%s
                                                             IN (
                                                                 center.%s,
                                                                 center.%s,
                                                                 center.%s,
                                                                 center.%s,
                                                                 center.%s
                                                                )
                                 ) nearby
                                     LEFT JOIN %s
                                                ON floor(%s.%s / 16) = nearby.%s AND floor(%s.%s / 16) = nearby.%s
                            GROUP BY %s.%s, nearby.cchunk_uid
                            ORDER BY random()
                            LIMIT 10
                            """,
                    TABLE_STORED_MOB,
                    MOB_UNIQUE_NAME,
                    TABLE_STORED_MOB,
                    MOB_MY_UID,
                    Contour.CHUNK_UID,
                    ChunkSql.TABLE_CHUNK,
                    ChunkSql.BIOME_GUESS_UID,
                    ChunkSql.TABLE_CHUNK,
                    WORLD_MY_UID,
                    Contour.CHUNK_X,
                    Contour.CHUNK_Z,
                    Contour.MIDDLE_X,
                    Contour.MIDDLE_Y,
                    Contour.MIDDLE_Z,
                    Contour.CHUNK_UID,
                    Contour.TABLE_CONTOUR,
                    ChunkSql.TABLE_CHUNK,
                    ChunkSql.TABLE_CHUNK,
                    ChunkSql.CHUNK_UID,
                    ChunkSql.CHUNK_UID,
                    Contour.TABLE_CONTOUR,
                    Contour.CHUNK_UID,
                    Contour.BRIDGE_X_NEG,
                    Contour.BRIDGE_X_POS,
                    Contour.BRIDGE_Z_NEG,
                    Contour.BRIDGE_Z_POS,
                    Contour.CHUNK_UID,
                    TABLE_STORED_MOB,
                    TABLE_STORED_MOB,
                    X,
                    Contour.CHUNK_X,
                    TABLE_STORED_MOB,
                    Z,
                    Contour.CHUNK_Z,
                    TABLE_STORED_MOB,
                    MOB_UNIQUE_NAME
            ));
            while (response.next()) {
                Long chunkUid = response.getLong("cchunk_uid");
                @Nullable BiomeType biome = BiomeTypeDatabase.get(response.getInt(ChunkSql.BIOME_GUESS_UID));
                if (biome == null) continue;
                int worldMyUid = response.getInt(WORLD_MY_UID);

                int middleX = response.getInt(Contour.MIDDLE_X);
                int middleY = response.getInt(Contour.MIDDLE_Y);
                int middleZ = response.getInt(Contour.MIDDLE_Z);

                int chunkX = response.getInt(Contour.CHUNK_X);
                int chunkZ = response.getInt(Contour.CHUNK_Z);
                String mobName = response.getString(MOB_UNIQUE_NAME);
                int mobCount = response.getInt("mob_count");

                spawnPercentages.compute(chunkUid, (uid, sP) -> {
                    if (sP == null) {
                        return new SpawnPercentages(
                                biome,
                                chunkX,
                                chunkZ,
                                mobName,
                                worldMyUid,
                                middleX, middleY, middleZ, mobCount
                        );
                    } else {
                        sP.add(mobName, mobCount);
                        return sP;
                    }
                });
            }
            statement.close();
        }
        return spawnPercentages;
    }

    public static final class SpawnPercentages {
        private final @NotNull BiomeType biome;
        private final int chunkX;
        private final int chunkZ;
        private final int worldMyUid;
        private final int middleX;
        private final int middleY;
        private final int middleZ;
        private final HashMap<String, Integer> mobNames = new HashMap<>();
        private double mobCount = 0;
        private final List<MobType> mobsToSpawn = new ArrayList<>();

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
            while (!this.mobCountHigh()) {
                Map<MobType, Double> shouldBeMobSpawns = biome.getSpawnPercentages();
                Map<String, Double> mobSpawns = new HashMap<>();
                for (Map.Entry<String, Integer> mob : this.mobNames.entrySet())
                    mobSpawns.put(mob.getKey(), mob.getValue() / this.mobCount);
                MobType bestMob = null;
                double mostPerc = Double.MIN_VALUE;
                for (Map.Entry<MobType, Double> shouldBeMob : shouldBeMobSpawns.entrySet()) {
                    if (shouldBeMob.getValue() * (1 - Math.random() * MOB_TYPE_VARIATION) - mobSpawns.getOrDefault(shouldBeMob.getKey().getName(), 0d) > mostPerc) {
                        bestMob = shouldBeMob.getKey();
                    }
                }
                if (bestMob == null) {
                    break;
                }
                this.mobCount += bestMob.getMeanGroup();
                this.mobsToSpawn.add(bestMob);
            }
        }

        public boolean mobCountHigh() {
            return this.biome.getUsefulSpawnRate() < this.mobCount;
        }

        public boolean noMobsToSpawn() {
            return this.mobsToSpawn.isEmpty();
        }

        public void spawnIRL() {
            @Nullable UUID worldUUID = SqlWorldGet.getWorldUUID(worldMyUid);
            if (worldUUID != null) {
                @Nullable World world = Bukkit.getWorld(worldUUID);
                if (world != null) {
                    if (!world.isChunkLoaded(chunkX, chunkZ)) {
                        world.getChunkAtAsync(chunkX, chunkZ).thenAccept(this::spawnIRL);
                    }
                }
            }
        }

        private void spawnIRL(Chunk chunk) {
            @NotNull Block block = chunk.getWorld().getBlockAt(middleX, middleY, middleZ);
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
            List<TopBlock> topBlocks = scanNearby(chunk,
                    new HashSet<>(),
                    middleX,
                    y - 1,
                    middleZ
            );
            Collections.shuffle(topBlocks);
            List<StoredMob> mobsToSave = new ArrayList<>();
            for (MobType mobType : mobsToSpawn) {
                for (int i = 0; i < topBlocks.size(); i++) {
                    final TopBlock topBlock = topBlocks.get(i);
                    if (mobType.canSpawn(topBlock)) {
                        final int group = mobType.getGroup();
                        for (int mobCount = 0; mobCount < group; mobCount++) {
                            System.out.println(topBlock);
                            final StoredMob storedMob = new StoredMob(topBlock.x() + chunkX * BLOCKS_IN_A_CHUNK, topBlock.y() + 1, topBlock.z() + chunkZ * BLOCKS_IN_A_CHUNK, worldMyUid, mobType);
                            mobsToSave.add(storedMob);
                            System.out.printf("spawn %s %d, %d, %d\n", storedMob.uniqueName, storedMob.x, storedMob.y, storedMob.z);
                        }
                        topBlocks.remove(i);
                        break;
                    }
                }
            }
            insertMobs(mobsToSave);
        }
    }

    public static class StoredMob {
        private static final String VOLT_MOB = "volt.mob.";
        public long uid;
        public int x;
        public int y;
        public int z;
        public UUID worldUUID;
        public int myWorldUid;
        public String uniqueName;
        public long despawnTime;

        public StoredMob(int x, int y, int z, UUID worldUUID, String uniqueName, long despawnTime) throws SQLException {
            this.uid = TmwSqlVerifyDatabase.getMobMyUid();
            this.x = x;
            this.y = y;
            this.z = z;
            this.worldUUID = worldUUID;
            this.myWorldUid = SqlWorldGet.getMyWorldUid(worldUUID);
            this.uniqueName = uniqueName;

            this.despawnTime = despawnTime;
        }

        public StoredMob(long uid, int x, int y, int z, int myWorldUid, String uniqueName, Timestamp despawnTime) throws CommandSyntaxException {
            this.uid = uid;
            this.x = x;
            this.y = y;
            this.z = z;
            this.myWorldUid = myWorldUid;
            this.worldUUID = SqlWorldGet.getWorldUUID(myWorldUid);
            this.uniqueName = uniqueName;
            this.despawnTime = despawnTime.getTime();
        }

        public StoredMob(int x, int y, int z, int myWorldUid, MobType mobType) {
            this.uid = TmwSqlVerifyDatabase.getMobMyUid();
            this.x = x;
            this.y = y;
            this.z = z;
            this.myWorldUid = myWorldUid;
            this.worldUUID = SqlWorldGet.getWorldUUID(myWorldUid);
            this.uniqueName = mobType.getName();
            this.despawnTime = mobType.getDespawnAt();
        }

        @Nullable
        public static String getUniqueName(@NotNull Set<String> tags) {
            for (String tag : tags) {
                if (tag.startsWith(VOLT_MOB) && tag.length() > VOLT_MOB.length()) {
                    return tag.substring(VOLT_MOB.length());
                }
            }
            return null;
        }

        public WorldServer getNmsWorld() {
            @Nullable World world = getWorld();
            if (world == null) return null;
            return ((CraftWorld) world).getHandle();
        }

        @Nullable
        public World getWorld() {
            return Bukkit.getWorld(worldUUID);
        }
    }
}
