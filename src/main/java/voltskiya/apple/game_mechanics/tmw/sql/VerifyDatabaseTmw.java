package voltskiya.apple.game_mechanics.tmw.sql;

import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.*;

public class VerifyDatabaseTmw {
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
                        %s       Z     SMALLINT,
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
                        %s            BIGINT NOT NULL,
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
            Decay.BLOCK_UID,
            Decay.X,
            Decay.Y,
            Decay.Z,
            WORLD_MY_UID,
            Decay.IS_DECAY,
            Decay.DAMAGE,
            Decay.CURRENT_MATERIAL,
            Decay.ORIGINAL_MATERIAL,
            Decay.X,
            Decay.Y,
            Decay.Z,
            WORLD_MY_UID,
            WORLD_MY_UID,
            TABLE_WORLD
    );

    public static SessionFactory sessionFactory;

    private static long currentMobMyUid;
    private static long currentChunkUid;
    private static int currentMaterialUid;
    private static long currentDecayBlockUid;

    static {
        DATABASE_FILENAME = new File(PluginTMW.get().getDataFolder().getPath(), "tmwDatabase.db");
    }


    public synchronized static void connect() {
        // A SessionFactory is set up once for an application!
        final Configuration cfg = new Configuration()
                .setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect")
                .setProperty("hibernate.connection.pool_size", "3")
                .setProperty("hibernate.current_session_context_class", "thread")
                .setProperty("hibernate.show_sql", "false")
                .setProperty("hibernate.format_sql", "false")
                .setProperty("hibernate.hbm2ddl.auto", "create")
                .setProperty("hibernate.connection.url", TmwDatabaseConfig.get().url)
                .setProperty("hibernate.connection.username", TmwDatabaseConfig.get().username)
                .setProperty("hibernate.connection.password", TmwDatabaseConfig.get().password);
        cfg.addAttributeConverter(new BiomeTypeConverter());
        cfg.addSqlFunction("rand", new SqlRandFunction());
        cfg.addAnnotatedClass(TmwStoredMob.class)
                .addAnnotatedClass(TmwMapContour.class)
                .addAnnotatedClass(TmwSpawnPercentagesResponse.class);

        sessionFactory = cfg.buildSessionFactory();
        verify();
    }

    private static void verify() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        currentMobMyUid = ((Number) session.createNativeQuery(String.format("SELECT COALESCE(max(%s)+1,0) FROM %s", SqlVariableNames.MOB_MY_UID, SqlVariableNames.TABLE_STORED_MOB)).getSingleResult()).longValue();
        currentChunkUid = ((Number) session.createNativeQuery(String.format("SELECT COALESCE(max(%s)+1,0) FROM %s", Contour.CHUNK_UID, Contour.TABLE_CONTOUR)).getSingleResult()).longValue();
        currentMaterialUid = ((Number) session.createNativeQuery(String.format("SELECT COALESCE(max(%s)+1,0) FROM %s", MATERIAL_MY_UID, TABLE_MATERIAL)).getSingleResult()).intValue();
        currentDecayBlockUid = ((Number) session.createNativeQuery(String.format("SELECT COALESCE(max(%s)+1,0) FROM %s", Decay.BLOCK_UID, Decay.TABLE_DECAY_BLOCK)).getSingleResult()).longValue();
        session.getTransaction().commit();
        session.close();
    }

    public static class SqlRandFunction implements SQLFunction {
        @Override
        public boolean hasArguments() {
            return false;
        }

        @Override
        public boolean hasParenthesesIfNoArguments() {
            return true;
        }

        @Override
        public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
            return BigIntegerType.INSTANCE;
        }

        @Override
        public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
            return " rand() ";
        }
    }

    private synchronized static void old() throws SQLException {
        Statement statement = null;
        statement.execute(BUILD_TABLE_CHUNK);
        statement.execute(BUILD_TABLE_WORLD);
        statement.execute(BUILD_TABLE_MATERIAL);
        statement.execute(BUILD_TABLE_CONTOUR);
        statement.execute(TABLE_STORED_MOBS);
        statement.execute(BUILD_TABLE_CHUNK_KILL);
        statement.execute(BUILD_TABLE_WEATHER);
        statement.execute(BUILD_TABLE_BLOCK);
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

    public synchronized static long getCurrentDecayBlockUid() {
        return currentDecayBlockUid++;
    }

    @Converter(autoApply = true)
    public static class BiomeTypeConverter implements AttributeConverter<BiomeType, Integer>, JavaTypeDescriptor<BiomeType> {
        @Override
        public Integer convertToDatabaseColumn(BiomeType attribute) {
            return attribute.getUid();
        }

        @Override
        public BiomeType convertToEntityAttribute(Integer dbData) {
            return BiomeTypeDatabase.get(dbData);
        }

        @Override
        public Class<BiomeType> getJavaTypeClass() {
            return BiomeType.class;
        }


        @Override
        public BiomeType fromString(String string) {
            return BiomeTypeDatabase.get(Integer.parseInt(string));
        }

        @Override
        public <X> X unwrap(BiomeType value, Class<X> type, WrapperOptions options) {
            return type.cast(value.getUid());
        }

        @Override
        public <X> BiomeType wrap(X value, WrapperOptions options) {
            return BiomeTypeDatabase.get((int) value);
        }
    }
}
