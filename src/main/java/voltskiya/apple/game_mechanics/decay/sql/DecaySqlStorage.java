package voltskiya.apple.game_mechanics.decay.sql;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames;
import voltskiya.apple.game_mechanics.tmw.sql.TmwSqlVerifyDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.SqlWorldGet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.Decay.*;

public class DecaySqlStorage {
    public static void insertPlaceUpdate(BlockUpdate placed) {
        synchronized (SaveDaemon.sync) {
            BlockUpdate block = SaveDaemon.placeUpdates.get(placed);
            if (block != null) {
                placed = new BlockUpdate(block.oldMaterial, placed.newMaterial, placed.x, placed.y, placed.z, placed.world);
            }
            SaveDaemon.placeUpdates.put(placed, placed);
        }
    }

    private static void placeBlocks(Collection<BlockUpdate> blockUpdates) throws SQLException {
        synchronized (TmwSqlVerifyDatabase.syncDB) {
            Statement statement = TmwSqlVerifyDatabase.database.createStatement();
            for (BlockUpdate block : blockUpdates) {
                statement.addBatch(String.format("""
                                INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s)
                                VALUES (%d,%d,%d,%d,%b,%d,%s,%s)
                                ON CONFLICT (%s,%s,%s,%s)
                                    DO UPDATE SET %s = %s,
                                                  %s = 0;""",
                        TABLE_DECAY_BLOCK, X, Y, Z, SqlVariableNames.WORLD_MY_UID, IS_DECAY, DAMAGE, NEW_MATERIAL, ORIGINAL_MATERIAL,
                        block.x, block.y, block.z, block.myWorldUid, false, 0, block.getNewMaterialUidString(), block.getOldMaterialUidString(),
                        X, Y, Z, SqlVariableNames.WORLD_MY_UID, NEW_MATERIAL, block.getNewMaterialUidString(), DAMAGE
                ));
            }
            statement.executeBatch();
            statement.close();
        }
    }

    public static class SaveDaemon implements Runnable {
        private static final long SAVE_INTERVAL = 20 * 10;
        private static final Object sync = new Object();

        // this is a map of self references because I need the get() method to get the original material
        private static Map<BlockUpdate, BlockUpdate> placeUpdates = new HashMap<>();

        public SaveDaemon() {
            run();
        }

        private static void flush() {
            Map<BlockUpdate, BlockUpdate> placeUpdatesTemp;
            synchronized (sync) {
                placeUpdatesTemp = placeUpdates;
                placeUpdates = new HashMap<>();
            }
            try {
                for (BlockUpdate block : placeUpdatesTemp.keySet())
                    block.setMyUids();
                placeBlocks(placeUpdatesTemp.keySet());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        @Override
        public void run() {
            new Thread(SaveDaemon::flush).start();
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, SAVE_INTERVAL);
        }
    }

    public static final class BlockUpdate {
        private final Material oldMaterial;
        private final Material newMaterial;
        private final int x;
        private final int y;
        private final int z;
        private final UUID world;
        private int myWorldUid;
        private Integer oldMaterialUid;
        private Integer newMaterialUid;

        public BlockUpdate(Material oldMaterial, Material newMaterial, int x, int y, int z, UUID world) {
            this.oldMaterial = oldMaterial;
            this.newMaterial = newMaterial;
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world;
        }

        private void setMyUids() throws SQLException {
            this.oldMaterialUid = MaterialSqlStorage.get(oldMaterial);
            this.newMaterialUid = MaterialSqlStorage.get(newMaterial);
            this.myWorldUid = SqlWorldGet.getMyWorldUid(world);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BlockUpdate b) {
                return b.x == this.x && b.y == this.y && b.z == this.z && b.myWorldUid == this.myWorldUid;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (int) ((x + (long) y + z) % Integer.MAX_VALUE);
        }


        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public int z() {
            return z;
        }

        public int myWorldUid() {
            return myWorldUid;
        }

        public String getOldMaterialUidString() {
            return oldMaterialUid == null ? "null" : String.valueOf(oldMaterialUid);
        }

        public String getNewMaterialUidString() {
            return newMaterialUid == null ? "null" : String.valueOf(newMaterialUid);
        }
    }
}
