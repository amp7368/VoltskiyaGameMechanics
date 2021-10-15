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
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.*;

public class VerifyDatabaseTmw {

    public static SessionFactory sessionFactory;

    private static long currentMobMyUid;
    private static long currentChunkUid;
    private static int currentMaterialUid;
    private static long currentDecayBlockUid;

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
