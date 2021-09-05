package voltskiya.apple.game_mechanics.tmw.sql;

import org.hibernate.Session;
import org.hibernate.Transaction;
import voltskiya.apple.game_mechanics.tmw.tmw_world.biomes.ScanWorldBiomes.ProcessedChunk;
import voltskiya.apple.utilities.util.data_structures.Triple;

import java.sql.SQLException;
import java.util.Collection;

import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.ChunkSql;
import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.Contour.*;
import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.WORLD_MY_UID;

public class BiomeSqlStorage {
    public static void insert(Collection<ProcessedChunk> processedChunks) {
        try {
            insertThreaded(processedChunks);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void insertThreaded(Collection<ProcessedChunk> processedChunks) throws SQLException {
        synchronized (TmwSqlVerifyDatabase.syncDB) {
            Session session = TmwSqlVerifyDatabase.sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            for (ProcessedChunk chunk : processedChunks) {
                session.createNativeQuery(
                        String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                                        "VALUES (%d, %d, %d, %s, %s, %s, %s, %d, %d, %d) " +
                                        "ON CONFLICT (%s, %s) DO NOTHING",
                                TABLE_CONTOUR,
                                CHUNK_UID,
                                CHUNK_X,
                                CHUNK_Z,
                                BRIDGE_X_POS,
                                BRIDGE_X_NEG,
                                BRIDGE_Z_POS,
                                BRIDGE_Z_NEG,
                                MIDDLE_X,
                                MIDDLE_Y,
                                MIDDLE_Z,
                                TmwSqlVerifyDatabase.getChunkUid(),
                                chunk.x(),
                                chunk.z(),
                                getChunkSql(chunk.getNeighborXPos().middle()),
                                getChunkSql(chunk.getNeighborXNeg().middle()),
                                getChunkSql(chunk.getNeighborZPos().middle()),
                                getChunkSql(chunk.getNeighborZNeg().middle()),
                                chunk.middle().getX(),
                                chunk.middle().getY(),
                                chunk.middle().getZ(),
                                CHUNK_X,
                                CHUNK_Z
                        )
                );
            }
            transaction.commit();
            // same thing, except replace into
            for (ProcessedChunk chunk : processedChunks) {
                session.createNativeQuery(
                        String.format("REPLACE INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                                        "VALUES (%d, %d, %d, %s, %s, %s, %s, %d, %d, %d)",
                                TABLE_CONTOUR,
                                CHUNK_UID,
                                CHUNK_X,
                                CHUNK_Z,
                                BRIDGE_X_POS,
                                BRIDGE_X_NEG,
                                BRIDGE_Z_POS,
                                BRIDGE_Z_NEG,
                                MIDDLE_X,
                                MIDDLE_Y,
                                MIDDLE_Z,
                                chunk.chunkUid = TmwSqlVerifyDatabase.getChunkUid(),
                                chunk.x(),
                                chunk.z(),
                                getChunkSql(chunk.getNeighborXPos().middle()),
                                getChunkSql(chunk.getNeighborXNeg().middle()),
                                getChunkSql(chunk.getNeighborZPos().middle()),
                                getChunkSql(chunk.getNeighborZNeg().middle()),
                                chunk.middle().getX(),
                                chunk.middle().getY(),
                                chunk.middle().getZ()
                        )
                );
            }
            transaction.commit();
            for (ProcessedChunk chunk : processedChunks) {
                session.createNativeQuery(String.format("INSERT INTO %s (%s, %s, %s)\n" +
                                "VALUES (%d, %d, %d)",
                        ChunkSql.TABLE_CHUNK,
                        ChunkSql.CHUNK_UID,
                        ChunkSql.BIOME_GUESS_UID,
                        WORLD_MY_UID,
                        chunk.chunkUid,
                        chunk.computedBiomeChunk().getGuessedBiomes().get(0).getKey().getUid(),
                        chunk.worldMyUid()
                ));
            }
            transaction.commit();
            session.close();
        }
    }

    private static String getChunkSql(Triple<Integer, Integer, Integer> coords) {
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
                MIDDLE_X,
                coords.getX(),
                MIDDLE_Y,
                coords.getY(),
                MIDDLE_Z,
                coords.getZ()
        );
    }
}
