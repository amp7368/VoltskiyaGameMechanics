package voltskiya.apple.game_mechanics.tmw.sql;

import apple.utilities.request.AppleRequestService;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class BiomeSqlStorage extends AppleRequestService {
    public static void insertContour(List<TmwMapContour> chunks) {
        try (Session session = VerifyDatabaseTmw.sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            for (TmwMapContour chunk : chunks) {
                session.saveOrUpdate(chunk);
            }
            transaction.commit();
        }
    }

    public static void insertBiomes(List<TmwChunkEntity> biomes) {
        try (Session session = VerifyDatabaseTmw.sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            for (TmwChunkEntity chunk : biomes) {
                session.saveOrUpdate(chunk);
            }
            transaction.commit();
        }
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
