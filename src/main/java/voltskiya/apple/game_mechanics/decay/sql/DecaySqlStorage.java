package voltskiya.apple.game_mechanics.decay.sql;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames;
import voltskiya.apple.game_mechanics.tmw.sql.TmwSqlVerifyDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.SqlWorldGet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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
                                INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                                VALUES (%d,%d,%d,%d,%d,%b,%d,%s,%s)
                                ON CONFLICT (%s,%s,%s,%s)
                                    DO UPDATE SET %s = %s,
                                                  %s = 0;""",
                        TABLE_DECAY_BLOCK, BLOCK_UID, X, Y, Z, SqlVariableNames.WORLD_MY_UID, IS_DECAY, DAMAGE, NEW_MATERIAL, ORIGINAL_MATERIAL,
                        TmwSqlVerifyDatabase.getCurrentDecayBlockUid(), block.x, block.y, block.z, block.myWorldUid, false, 0, block.getNewMaterialUidString(), block.getOldMaterialUidString(),
                        X, Y, Z, SqlVariableNames.WORLD_MY_UID, NEW_MATERIAL, block.getNewMaterialUidString(), DAMAGE
                ));
            }
            statement.executeBatch();
            statement.close();
        }
    }

    public static void doDamageTick() throws SQLException {
        synchronized (TmwSqlVerifyDatabase.syncDB) {
            Statement statement = TmwSqlVerifyDatabase.database.createStatement();
            ResultSet response = statement.executeQuery(String.format("""
                    SELECT sum(nearby.damage) as nearby_damage, middle.*
                    FROM (
                             SELECT *
                             FROM decay_block
                             ORDER BY random()
                             LIMIT 1000
                         ) middle
                             INNER JOIN decay_block nearby
                                        ON middle.world_my_uid = nearby.world_my_uid
                                            AND nearby.x IN (middle.x - 1, middle.x + 1, middle.x - 2, middle.x + 2, middle.x)
                                            AND nearby.y IN (middle.y - 1, middle.y + 1, middle.y - 2, middle.y + 2, middle.y)
                                            AND nearby.z IN (middle.z - 1, middle.z + 1, middle.z - 2, middle.z + 2, middle.z)
                    GROUP BY (middle.block_uid)"""
            ));
            List<DamagedBlock> damagedBlocks = new ArrayList<>();
            while (response.next()) {
                damagedBlocks.add(new DamagedBlock(
                        response.getInt(DAMAGE),
                        response.getInt(ORIGINAL_MATERIAL),
                        response.getInt(NEW_MATERIAL),
                        response.getInt("nearby_damage"),
                        response.getLong(BLOCK_UID),
                        response.getInt(X),
                        response.getInt(Y),
                        response.getInt(Z)
                ));
            }
            for (DamagedBlock block : damagedBlocks) {
                block.doDamageTick();
                statement.addBatch(String.format(
                        "UPDATE %s SET %s = %d WHERE %s = %d",
                        TABLE_DECAY_BLOCK, DAMAGE, block.damage, BLOCK_UID, block.blockUid
                ));
            }
            statement.executeBatch();

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

    public static class BlockUpdate {
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

    private static class DamagedBlock {
        public static final double COUNT_NEARBY_BLOCKS = Math.pow(5, 3);
        private final int nearby_damage;
        private final long blockUid;
        private final int x;
        private final int y;
        private final int z;
        private int damage;
        private int newMaterialUid;
        private int oldMaterialUid;

        private DamagedBlock(int damage, int newMaterialUid, int oldMaterialUid, int nearby_damage, long blockUid, int x, int y, int z) {
            super();
            this.damage = damage;
            this.newMaterialUid = newMaterialUid;
            this.oldMaterialUid = oldMaterialUid;
            this.nearby_damage = nearby_damage;
            this.blockUid = blockUid;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void doDamageTick() {
            damage += 1 + damage / COUNT_NEARBY_BLOCKS;
            //TODO generator and decay blocks (snow buildup)
        }
    }
}
