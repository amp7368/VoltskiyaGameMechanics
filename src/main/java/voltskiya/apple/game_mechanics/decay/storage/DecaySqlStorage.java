package voltskiya.apple.game_mechanics.decay.storage;

import org.bukkit.Bukkit;
import org.hibernate.Session;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.sql.VerifyDatabaseTmw;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DecaySqlStorage {

    public static void insertPlaceUpdate(DecayBlock placed) {
        synchronized (SaveDaemon.sync) {
            DecayBlock block = SaveDaemon.placeUpdates.get(placed);
            if (block != null) {
                placed = new DecayBlock(block.getOldMaterial(), placed.getCurrentMaterial(), placed.getX(), placed.getY(), placed.getZ(), placed.getWorld());
            }
            SaveDaemon.placeUpdates.put(placed, placed);
        }
    }

    private static void placeBlocks(Collection<DecayBlock> blockUpdates) {
        Session session = VerifyDatabaseTmw.sessionFactory.openSession();
        session.beginTransaction();
        for (DecayBlock block : blockUpdates) {

        }
        session.getTransaction().commit();
    }


    public static class SaveDaemon implements Runnable {
        private static final long SAVE_INTERVAL = 20 * 10;
        private static final Object sync = new Object();

        // this is a map of self references because I need the get() method to get the original material
        private static Map<DecayBlock, DecayBlock> placeUpdates = new HashMap<>();

        public SaveDaemon() {
            run();
        }

        private static void flush() {
            Map<DecayBlock, DecayBlock> placeUpdatesTemp;
            synchronized (sync) {
                placeUpdatesTemp = placeUpdates;
                placeUpdates = new HashMap<>();
            }
            placeBlocks(placeUpdatesTemp.keySet());
        }

        @Override
        public void run() {
            new Thread(SaveDaemon::flush).start();
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, SAVE_INTERVAL);
        }
    }
}
