package voltskiya.apple.game_mechanics.tmw.sql;

import voltskiya.apple.game_mechanics.tmw.PluginTMW;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.*;

public class TmwSqlVerifyDatabase {
    public static final Object syncDB = new Object();
    private static final File DATABASE_FILENAME;
    private static final String TABLE_STORED_MOBS = String.format("""
                    CREATE TABLE IF NOT EXISTS %s
                    (
                        %s     BIGINT PRIMARY KEY,
                        %s            SMALLINT,
                        %s            SMALLINT,
                        %s            SMALLINT,
                        %s TINYINT,
                        %s          TEXT,
                        %s TIMESTAMP
                    )
                    """,
            SqlVariableNames.TABLE_STORED_MOB,
            SqlVariableNames.MOB_MY_UID,
            SqlVariableNames.X,
            SqlVariableNames.Y,
            SqlVariableNames.Z,
            SqlVariableNames.WORLD_MY_UID,
            SqlVariableNames.NBT,
            SqlVariableNames.DESPAWN_TIME);
    private static final String BUILD_TABLE_WORLD = String.format("""
                    CREATE TABLE IF NOT EXISTS %s
                    (
                        %s VARCHAR(36) PRIMARY KEY UNIQUE NOT NULL,
                        %s    INTEGER UNIQUE                 NOT NULL
                    )""",
            TABLE_WORLD,
            WORLD_UUID,
            WORLD_MY_UID
    );
    private static final String BUILD_TABLE_CONTOUR = String.format("""
                    CREATE TABLE IF NOT EXISTS %s
                    (
                        %s            BIGINT  NOT NULL,
                        %s        INTEGER NOT NULL,
                        %s        INTEGER NOT NULL,
                        %s BIGINT,
                        %s BIGINT,
                        %s BIGINT,
                        %s BIGINT,
                        %s       INTEGER,
                        %s       INTEGER,
                        %s       INTEGER,
                        PRIMARY KEY (%s),
                        UNIQUE (%s,%s)
                    );""",
            Contour.TABLE_CONTOUR,
            Contour.CHUNK_UID,
            Contour.CHUNK_X,
            Contour.CHUNK_Z,
            Contour.BRIDGE_X_POS,
            Contour.BRIDGE_X_NEG,
            Contour.BRIDGE_Z_POS,
            Contour.BRIDGE_Z_NEG,
            Contour.MIDDLE_X,
            Contour.MIDDLE_Y,
            Contour.MIDDLE_Z,
            Contour.CHUNK_UID,
            Contour.CHUNK_X,
            Contour.CHUNK_Z
    );
    private static final String BUILD_TABLE_BIOME = String.format("""
                    CREATE TABLE IF NOT EXISTS %s
                    (
                        %s INTEGER NOT NULL PRIMARY KEY
                    );""",
            BiomeSql.TABLE_BIOME,
            BiomeSql.BIOME_UID
    );
    private static final String BUILD_TABLE_CHUNK = String.format("""
                    CREATE TABLE IF NOT EXISTS %s
                    (
                        %s            BIGINT  NOT NULL PRIMARY KEY,
                        %s      INTEGER NOT NULL,
                        %s DOUBLE  NOT NULL,
                        FOREIGN KEY (%s) REFERENCES %s (%s)
                    );""",
            ChunkSql.TABLE_CHUNK,
            ChunkSql.CHUNK_UID,
            ChunkSql.BIOME_GUESS_UID,
            ChunkSql.TEMPERATURE_MODIFIER,
            ChunkSql.BIOME_GUESS_UID,
            BiomeSql.TABLE_BIOME,
            BiomeSql.BIOME_UID
    );
    private static final String BUILD_TABLE_CHUNK_KILL = String.format("""
                    CREATE TABLE IF NOT EXISTS %s
                    (
                        %s BIGINT   NOT NULL,
                        %s TIMESTAMP NOT NULL,
                        PRIMARY KEY (%s, %s),
                        FOREIGN KEY (%s) REFERENCES %s
                    );""",
            Kills.TABLE_CHUNK_KILL,
            Contour.CHUNK_UID,
            Kills.TIME,
            Contour.CHUNK_UID,
            Kills.TIME,
            Contour.CHUNK_UID,
            Contour.TABLE_CONTOUR
    );
    public static Connection database;
    private static long currentMobMyUid;
    private static long currentChunkUid;

    static {
        DATABASE_FILENAME = new File(PluginTMW.get().getDataFolder().getPath(), "tmwDatabase.db");
    }

    public synchronized static void connect() throws ClassNotFoundException, SQLException {
        synchronized (syncDB) {
            Class.forName("org.sqlite.JDBC");
            database = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILENAME.getPath());
            verify();
        }
    }

    private synchronized static void verify() throws SQLException {
        Statement statement = database.createStatement();
        statement.execute(BUILD_TABLE_BIOME);
        statement.execute(BUILD_TABLE_CHUNK);
        statement.execute(BUILD_TABLE_WORLD);
        statement.execute(BUILD_TABLE_CONTOUR);
        statement.execute(BUILD_TABLE_CHUNK_KILL);
        currentMobMyUid = statement.executeQuery(String.format("SELECT max(%s)+1 FROM %s", SqlVariableNames.MOB_MY_UID, SqlVariableNames.TABLE_STORED_MOB)).getLong(1);
        currentChunkUid = statement.executeQuery(String.format("SELECT max(%s)+1 FROM %s", Contour.CHUNK_UID, Contour.TABLE_CONTOUR)).getLong(1);
        statement.close();
    }

    public synchronized static long getMobMyUid() {
        return currentMobMyUid++;
    }

    public synchronized static long getChunkUid() {
        return currentChunkUid++;
    }
}
