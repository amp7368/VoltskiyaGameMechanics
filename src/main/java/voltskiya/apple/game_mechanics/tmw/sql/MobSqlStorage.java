package voltskiya.apple.game_mechanics.tmw.sql;

import apple.utilities.request.keyed.AppleRequestOnConflict;
import apple.utilities.request.keyed.lazy.AppleRequestLazyService;
import org.bukkit.Bukkit;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.function.Consumer;

import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.*;

public class MobSqlStorage extends AppleRequestLazyService<Boolean> {
    private static final int MOB_COUNT_PER_TICK = 1;

    private static final MobSqlStorage instance = new MobSqlStorage();
    public static final HashSet<Long> mobsToBeRemoved = new HashSet<>();
    private static final Object mobsToBeInsertedSync = new Object();
    private static List<TmwStoredMob> mobsToBeInserted = new ArrayList<>();

    public static void insertMobs(List<TmwStoredMob> mobs) {
        if (mobs.isEmpty()) return;
        synchronized (mobsToBeInsertedSync) {
            mobsToBeInserted.addAll(mobs);
        }
        instance.queue("insert_mobs", () -> {
            insertThreaded();
            return false;
        }, AppleRequestOnConflict.ADD());
    }

    private static void insertThreaded() {
        Session session = VerifyDatabaseTmw.sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        List<TmwStoredMob> tempMobs;
        synchronized (mobsToBeInsertedSync) {
            tempMobs = mobsToBeInserted;
            mobsToBeInserted = new ArrayList<>();
        }
        for (TmwStoredMob mob : tempMobs) {
            synchronized (mobsToBeRemoved) {
                try {
                    if (!mobsToBeRemoved.remove(mob.uid))
                        session.saveOrUpdate(mob);
                } catch (PersistenceException ignored) {
                }
            }
        }
        try {
            transaction.commit();
            session.close();
        } catch (
                PersistenceException ignored) {
        }

    }

    public static void getMobs(int lowerX, int upperX, int lowerZ, int upperZ, int worldMyUid, Consumer<List<TmwStoredMob>> callback) {
        instance.queue("get_mobs", () -> {
            List<TmwStoredMob> finalAnswer = getMobThreaded(lowerX, upperX, lowerZ, upperZ, worldMyUid);
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> callback.accept(finalAnswer));
            return true;
        }, AppleRequestOnConflict.ADD());
    }

    private static List<TmwStoredMob> getMobThreaded(int lowerX, int upperX, int lowerZ, int upperZ, int worldMyUid) {
        Session session = VerifyDatabaseTmw.sessionFactory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<TmwStoredMob> query = criteriaBuilder.createQuery(TmwStoredMob.class);
        Root<TmwStoredMob> mobRoot = query.from(TmwStoredMob.class);
        query.where(criteriaBuilder.and(
                criteriaBuilder.between(mobRoot.get(X), lowerX, upperX),
                criteriaBuilder.between(mobRoot.get(Z), lowerZ, upperZ),
                criteriaBuilder.equal(mobRoot.get("myWorldUid"), worldMyUid)
        ));
        List<TmwStoredMob> result = session.createQuery(query).getResultList();
        session.close();
        return result;
    }

    public static void removeMobs(Collection<Long> mobsToDelete) {
        instance.queue("remove_mobs", () -> {
            removeMobsThreaded(mobsToDelete);
            return true;
        }, AppleRequestOnConflict.ADD());
    }


    public static void removeMobsThreaded(Collection<Long> mobsToDelete) {
        if (mobsToDelete.isEmpty()) return;
        Session session = VerifyDatabaseTmw.sessionFactory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaDelete<TmwStoredMob> query = criteriaBuilder.createCriteriaDelete(TmwStoredMob.class);
        Root<TmwStoredMob> mobRoot = query.from(TmwStoredMob.class);
        Transaction transaction = session.beginTransaction();
        session.createQuery(query.where(mobRoot.in(mobsToDelete))).executeUpdate();
        transaction.commit();
        session.close();

        synchronized (mobsToBeRemoved) {
            mobsToBeRemoved.removeAll(mobsToDelete);
        }
    }

    public static void getRegen(Consumer<Map<Long, SpawnPercentages>> afterRun) {
        instance.queue("getRegen", () -> {
            afterRun.accept(getRegen());
            return true;
        }, (b) -> {
        }, AppleRequestOnConflict.ADD());

    }

    public static Map<Long, SpawnPercentages> getRegen() {
        Session session = VerifyDatabaseTmw.sessionFactory.openSession();
        String queryString = String.format("""
                        SELECT nearby.*, %s.%s, count(%s.%s) as mob_count
                        FROM (
                                 SELECT center.%s as cchunk_uid,
                                        %s.%s,
                                        center.%s,
                                        center.%s,
                                        center.%s,
                                        center.%s,
                                        center.%s,
                                        center.%s,
                                        bridge.%s as bridge_uid
                                 FROM (SELECT *
                                       FROM %s
                                       ORDER BY rand()
                                       LIMIT %d) center
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
                                          INNER JOIN %s bridge2
                                                     ON bridge2.%s
                                                         IN (
                                                             bridge.%s,
                                                             bridge.%s,
                                                             bridge.%s,
                                                             bridge.%s,
                                                             bridge.%s
                                                            )
                             ) nearby
                                 LEFT JOIN %s
                                            ON floor(%s.%s / 16) = nearby.%s AND floor(%s.%s / 16) = nearby.%s
                        GROUP BY %s.%s, nearby.cchunk_uid
                        ORDER BY rand()
                        LIMIT 5
                        %n""",
                TABLE_STORED_MOB,
                MOB_UNIQUE_NAME,
                TABLE_STORED_MOB,
                MOB_MY_UID,
                Contour.CHUNK_UID,
                ChunkSql.TABLE_CHUNK,
                ChunkSql.BIOME_GUESS_UID,
                WORLD_MY_UID,
                Contour.CHUNK_X,
                Contour.CHUNK_Z,
                Contour.MIDDLE_X,
                Contour.MIDDLE_Y,
                Contour.MIDDLE_Z,
                Contour.CHUNK_UID,
                Contour.TABLE_CONTOUR,
                MOB_COUNT_PER_TICK,
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
        );
        @SuppressWarnings("unchecked") NativeQuery<TmwSpawnPercentagesResponse> nativeQuery = session.createNativeQuery(queryString, "tmw_spawn_percentages");
        List<TmwSpawnPercentagesResponse> results = nativeQuery.getResultList();
        HashMap<Long, SpawnPercentages> resultsInMap = new HashMap<>();
        for (TmwSpawnPercentagesResponse s : results) {
            resultsInMap.compute(s.chunkUid, (k, spawnPercentages) -> {
                if (spawnPercentages == null) {
                    if (s.biome == null) return null; //shouldn't happen in production
                    return new SpawnPercentages(s.biome, s.chunkX, s.chunkZ, s.mobName, s.worldMyUid, s.middleX, s.middleY, s.middleZ, s.mobCount);
                } else {
                    spawnPercentages.add(s.mobName, s.mobCount);
                    return spawnPercentages;
                }
            });
        }
        session.close();
        return resultsInMap;
    }

    @Override
    public int getRequestsPerTimeUnit() {
        return 80;
    }

    @Override
    public int getTimeUnitMillis() {
        return 0;
    }

    @Override
    public int getSafeGuardBuffer() {
        return 0;
    }

    @Override
    public int getLazinessMillis() {
        return 200;
    }

}
