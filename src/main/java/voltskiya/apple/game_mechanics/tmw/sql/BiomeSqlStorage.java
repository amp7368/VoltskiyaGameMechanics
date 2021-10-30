package voltskiya.apple.game_mechanics.tmw.sql;

import apple.utilities.request.AppleRequestService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import voltskiya.apple.game_mechanics.tmw.tmw_world.biomes.ScanWorldBiomes.ProcessedChunk;

import java.util.Collection;

import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.ChunkSql;
import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.Contour.*;
import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.WORLD_MY_UID;

public class BiomeSqlStorage extends AppleRequestService {
    private static final BiomeSqlStorage instance = new BiomeSqlStorage();

    public static void insert(Collection<ProcessedChunk> processedChunks) {
        instance.queueVoid(() -> insertThreaded(processedChunks)
        );
    }

    private static void insertThreaded(Collection<ProcessedChunk> processedChunks) {
        Session session = VerifyDatabaseTmw.sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();
        for (ProcessedChunk chunk : processedChunks) {
            session.createNativeQuery(
                    String.format("REPLACE INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                                    "SELECT %d, %d, %d, %d, %s, %s, %s, %s, %d, %d, %d ",
                            TABLE_CONTOUR,
                            CHUNK_UID,
                            WORLD_MY_UID,
                            CHUNK_X,
                            CHUNK_Z,
                            BRIDGE_X_POS,
                            BRIDGE_X_NEG,
                            BRIDGE_Z_POS,
                            BRIDGE_Z_NEG,
                            MIDDLE_X,
                            MIDDLE_Y,
                            MIDDLE_Z,
                            chunk.chunkUid = VerifyDatabaseTmw.getChunkUid(),
                            chunk.worldMyUid(),
                            chunk.x(),
                            chunk.z(),
                            getChunkSql(chunk.getNeighborXPos()),
                            getChunkSql(chunk.getNeighborXNeg()),
                            getChunkSql(chunk.getNeighborZPos()),
                            getChunkSql(chunk.getNeighborZNeg()),
                            chunk.middle().getX(),
                            chunk.middle().getY(),
                            chunk.middle().getZ()
                    )
            );
        }
        transaction.commit();
        transaction = session.beginTransaction();
        // same thing, except replace into
        for (ProcessedChunk chunk : processedChunks) {
            session.createNativeQuery(
                    String.format("REPLACE INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                                    "SELECT %d, %d, %d, %d, %s, %s, %s, %s, %d, %d, %d ",
                            TABLE_CONTOUR,
                            CHUNK_UID,
                            WORLD_MY_UID,
                            CHUNK_X,
                            CHUNK_Z,
                            BRIDGE_X_POS,
                            BRIDGE_X_NEG,
                            BRIDGE_Z_POS,
                            BRIDGE_Z_NEG,
                            MIDDLE_X,
                            MIDDLE_Y,
                            MIDDLE_Z,
                            chunk.chunkUid,
                            chunk.worldMyUid(),
                            chunk.x(),
                            chunk.z(),
                            getChunkSql(chunk.getNeighborXPos()),
                            getChunkSql(chunk.getNeighborXNeg()),
                            getChunkSql(chunk.getNeighborZPos()),
                            getChunkSql(chunk.getNeighborZNeg()),
                            chunk.middle().getX(),
                            chunk.middle().getY(),
                            chunk.middle().getZ()
                    )
            ).executeUpdate();
        }
        transaction.commit();
        transaction = session.beginTransaction();
        for (ProcessedChunk chunk : processedChunks) {
            session.createNativeQuery(String.format("REPLACE INTO %s (%s, %s)\n" +
                            "VALUES (%d, %d)",
                    ChunkSql.TABLE_CHUNK,
                    ChunkSql.CHUNK_UID,
                    ChunkSql.BIOME_GUESS_UID,
                    chunk.chunkUid,
                    chunk.computedBiomeChunk().getGuessedBiomes().get(0).getKey().getUid()
            )).executeUpdate();
        }
        transaction.commit();
        session.close();
    }

    private static String getChunkSql(ProcessedChunk coords) {
        if (coords == null) return " null ";
        return String.format("""
                         (SELECT %s
                        FROM %s
                        WHERE %s = %d
                        AND %s = %d
                        AND %s = %d)
                         """,
                CHUNK_UID,
                TABLE_CONTOUR,
                WORLD_MY_UID,
                coords.worldMyUid(),
                CHUNK_X,
                coords.x(),
                CHUNK_Z,
                coords.z()
        );
    }

    @Override
    public int getRequestsPerTimeUnit() {
        return 10;
    }

    @Override
    public int getTimeUnitMillis() {
        return 0;
    }

    @Override
    public int getSafeGuardBuffer() {
        return 50;
    }
}
