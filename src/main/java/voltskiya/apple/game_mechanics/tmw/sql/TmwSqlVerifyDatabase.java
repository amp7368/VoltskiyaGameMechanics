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
    private static final String BUILD_TABLE_WEATHER = String.format("""
                    CREATE TABLE IF NOT EXISTS %s
                    (
                        %s           BIGINT  NOT NULL,
                        %s         INTEGER NOT NULL,
                        %s              FLOAT   NOT NULL,
                        %s     FLOAT   NOT NULL,
                        %s FLOAT   NOT NULL,
                       \s
                        PRIMARY KEY (%s, %s)
                                
                    )""",
            Weather.TABLE_WEATHER,
            ChunkSql.CHUNK_UID,
            Weather.WEATHER_UID,
            Weather.IMPACT,
            Weather.IMPACT_VELOCITY,
            Weather.IMPACT_ACCELERATION,
            ChunkSql.CHUNK_UID,
            Weather.WEATHER_UID
    );
    private static final String TABLE_STORED_MOBS = String.format("""
                                    CREATE TABLE IF NOT EXISTS %s
                    (
                        %s     BIGINT PRIMARY KEY,
                        %s            SMALLINT,
                        %s            SMALLINT,
                        %s            SMALLINT,
                        %s TINYINT,
                        %s VARCHAR(60),
                        %s TIMESTAMP
                    )
                    """,
            SqlVariableNames.TABLE_STORED_MOB,
            SqlVariableNames.MOB_MY_UID,
            SqlVariableNames.X,
            SqlVariableNames.Y,
            SqlVariableNames.Z,
            SqlVariableNames.WORLD_MY_UID,
            SqlVariableNames.MOB_UNIQUE_NAME,
            SqlVariableNames.DESPAWN_TIME);
    private static final String BUILD_TABLE_MATERIAL = String.format("""
            CREATE TABLE IF NOT EXISTS %s
            (
                %s        VARCHAR(50) NOT NULL,
                %s INTEGER     NOT NULL PRIMARY KEY
            )""", TABLE_MATERIAL, MATERIAL, MATERIAL_MY_UID);
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
    private static final String BUILD_TABLE_CHUNK = String.format("""
                    CREATE TABLE IF NOT EXISTS %s
                    (
                        %s            BIGINT  NOT NULL PRIMARY KEY,
                        %s      INTEGER NOT NULL,
                        %s      INTEGER NOT NULL
                    );""",
            ChunkSql.TABLE_CHUNK,
            ChunkSql.CHUNK_UID,
            ChunkSql.BIOME_GUESS_UID,
            WORLD_MY_UID
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
    private static final String BUILD_TABLE_BLOCK = String.format("""
                    CREATE TABLE IF NOT EXISTS %s
                    (
                        %s            INTEGER NOT NULL,
                        %s            INTEGER NOT NULL,
                        %s            INTEGER NOT NULL,
                        %s INTEGER NOT NULL,
                        %s      BOOLEAN NOT NULL,
                        %s        FLOAT   NOT NULL,
                        %s INTEGER,
                        %s INTEGER,
                        PRIMARY KEY (%s, %s, %s, %s),
                        FOREIGN KEY (%s) REFERENCES %s
                    );""",
            Decay.TABLE_DECAY_BLOCK,
            Decay.X,
            Decay.Y,
            Decay.Z,
            WORLD_MY_UID,
            Decay.IS_DECAY,
            Decay.DAMAGE,
            Decay.NEW_MATERIAL,
            Decay.ORIGINAL_MATERIAL,
            Decay.X,
            Decay.Y,
            Decay.Z,
            WORLD_MY_UID,
            WORLD_MY_UID,
            TABLE_WORLD
    );

    public static Connection database;
    private static long currentMobMyUid;
    private static long currentChunkUid;
    private static int currentMaterialUid;

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
        statement.execute(BUILD_TABLE_CHUNK);
        statement.execute(BUILD_TABLE_WORLD);
        statement.execute(BUILD_TABLE_MATERIAL);
        statement.execute(BUILD_TABLE_CONTOUR);
        statement.execute(TABLE_STORED_MOBS);
        statement.execute(BUILD_TABLE_CHUNK_KILL);
        statement.execute(BUILD_TABLE_WEATHER);
        statement.execute(BUILD_TABLE_BLOCK);
        currentMobMyUid = statement.executeQuery(String.format("SELECT max(%s)+1 FROM %s", SqlVariableNames.MOB_MY_UID, SqlVariableNames.TABLE_STORED_MOB)).getLong(1);
        currentChunkUid = statement.executeQuery(String.format("SELECT max(%s)+1 FROM %s", Contour.CHUNK_UID, Contour.TABLE_CONTOUR)).getLong(1);
        currentMaterialUid = statement.executeQuery(String.format("SELECT max(%s)+1 FROM %s", MATERIAL_MY_UID, TABLE_MATERIAL)).getInt(1);
        statement.close();
    }

    public synchronized static long getMobMyUid() {
        return currentMobMyUid++;
    }

    public synchronized static long getChunkUid() {
        return currentChunkUid++;
    }

    public synchronized static int getMaterialUid() {
        return currentMaterialUid++;
    }
}
