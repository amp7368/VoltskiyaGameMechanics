package voltskiya.apple.game_mechanics.tmw.sql;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import voltskiya.apple.game_mechanics.decay.storage.DecayBlock;
import voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.Contour;
import voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.Decay;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;

public class VerifyDatabaseTmw {

    public static SessionFactory sessionFactory;

    private static long currentMobMyUid;
    private static long currentChunkUid;
    private static long currentDecayBlockUid;

    public synchronized static void connect() {
        // A SessionFactory is set up once for an application!
        final Configuration cfg = new Configuration().setProperty(
                "hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
            .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect")
            .setProperty("hibernate.connection.pool_size", "100")
            .setProperty("hibernate.connection.autocommit", "false")
            .setProperty("hibernate.current_session_context_class", "thread")
            .setProperty("hibernate.show_sql", "false").setProperty("hibernate.format_sql", "false")
            .setProperty("hibernate.hbm2ddl.auto", "update")
            .setProperty("hibernate.connection.url", TmwDatabaseConfig.get().url)
            .setProperty("hibernate.connection.username", TmwDatabaseConfig.get().username)
            .setProperty("hibernate.connection.password", TmwDatabaseConfig.get().password);
        cfg.addAttributeConverter(new BiomeTypeConverter());
        cfg.addAnnotatedClass(TmwStoredMob.class).addAnnotatedClass(TmwChunkEntity.class)
            .addAnnotatedClass(TmwMapContour.class).addAnnotatedClass(DecayBlock.class)
            .addAnnotatedClass(TmwSpawnPercentagesResponse.class);

        sessionFactory = cfg.buildSessionFactory();
        verify();
    }

    private static void verify() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            currentMobMyUid = ((Number) session.createNativeQuery(
                String.format("SELECT COALESCE(max(%s)+1,0) FROM %s", SqlVariableNames.MOB_MY_UID,
                    SqlVariableNames.TABLE_STORED_MOB)).getSingleResult()).longValue();
        } catch (Exception e) {
            e.printStackTrace();
            currentMobMyUid = 1;
        }
        try {
            currentChunkUid = ((Number) session.createNativeQuery(
                String.format("SELECT COALESCE(max(%s)+1,0) FROM %s", Contour.CHUNK_UID,
                    Contour.TABLE_CONTOUR)).getSingleResult()).longValue();
        } catch (Exception e) {
            e.printStackTrace();
            currentChunkUid = 1;
        }
        try {
            currentDecayBlockUid = ((Number) session.createNativeQuery(
                String.format("SELECT COALESCE(max(%s)+1,0) FROM %s", Decay.BLOCK_UID,
                    Decay.TABLE_DECAY_BLOCK)).getSingleResult()).longValue();
        } catch (Exception e) {
            e.printStackTrace();
            currentDecayBlockUid = 1;
        }
        session.getTransaction().commit();
        session.close();
    }

    public synchronized static long getMobMyUid() {
        return currentMobMyUid++;
    }

    public synchronized static long getChunkUid() {
        return currentChunkUid++;
    }

    public synchronized static long getCurrentDecayBlockUid() {
        return currentDecayBlockUid++;
    }


    @Converter(autoApply = true)
    public static class BiomeTypeConverter implements AttributeConverter<BiomeType, Integer> {

        @Override
        public Integer convertToDatabaseColumn(BiomeType attribute) {
            return attribute.getUid();
        }

        @Override
        public BiomeType convertToEntityAttribute(Integer dbData) {
            return BiomeTypeDatabase.get(dbData);
        }
    }
}
