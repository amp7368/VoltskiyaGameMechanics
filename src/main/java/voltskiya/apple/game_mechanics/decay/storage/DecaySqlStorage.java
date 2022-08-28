package voltskiya.apple.game_mechanics.decay.storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.sql.VerifyDatabaseTmw;

public class DecaySqlStorage {

    public static void insertPlaceUpdate(@NotNull Location location, DecayBlock placed) {
        synchronized (SaveDaemon.sync) {
            DecayBlock block = SaveDaemon.placeUpdates.get(location);
            if (block != null) {
                placed = new DecayBlock(block.getOldMaterial(), placed.getCurrentMaterial(),
                    placed.getX(), placed.getY(), placed.getZ(), placed.getWorld());
            }
            SaveDaemon.placeUpdates.put(location, placed);
        }
    }

    private static void placeBlocks(Collection<DecayBlock> blockUpdates) {
        Session session = VerifyDatabaseTmw.sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        for (DecayBlock block : blockUpdates) {
            if (!session.contains(block)) {
                session.saveOrUpdate(block);
            }
        }
        transaction.commit();
        session.close();
    }


    public static class SaveDaemon implements Runnable {

        private static final long SAVE_INTERVAL = 20 * 10;
        private static final Object sync = new Object();

        // this is a map of self references because I need the get() method to get the original material
        private static Map<Location, DecayBlock> placeUpdates = new HashMap<>();

        public SaveDaemon() {
            run();
        }

        private static void flush() {
            Map<Location, DecayBlock> placeUpdatesTemp;
            synchronized (sync) {
                placeUpdatesTemp = placeUpdates;
                placeUpdates = new HashMap<>();
            }
            placeBlocks(placeUpdatesTemp.values());
        }

        @Override
        public void run() {
            new Thread(SaveDaemon::flush).start();
            Bukkit.getScheduler()
                .scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, SAVE_INTERVAL);
        }
    }
}
