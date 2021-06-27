package voltskiya.apple.game_mechanics.tmw.sql;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.SqlWorldGet;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.*;

public class MobSqlStorage {
    public static void insertMob(List<StoredMob> mobs) {
        new Thread(() -> {
            try {
                insertThreaded(mobs);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

    private static void insertThreaded(List<StoredMob> mobs) throws SQLException {
        synchronized (TmwSqlVerifyDatabase.syncDB) {
            TmwSqlVerifyDatabase.database.setAutoCommit(false);
            for (StoredMob mob : mobs) {
                PreparedStatement statement = TmwSqlVerifyDatabase.database.prepareStatement(String.format("""
                                INSERT INTO stored_mob (%s, %s, %s, %s, %s, %s, %s)
                                VALUES (%d,%d,%d,%d,%d,?,%d)
                                ON CONFLICT DO NOTHING;""",
                        MOB_MY_UID,
                        X,
                        Y,
                        Z,
                        WORLD_MY_UID,
                        NBT,
                        DESPAWN_TIME,
                        mob.uid,
                        mob.x,
                        mob.y,
                        mob.z,
                        mob.myWorldUid,
                        mob.despawnTime
                ));
                statement.setString(1, mob.nbt.asString());
                statement.execute();
                statement.close();
            }
            TmwSqlVerifyDatabase.database.commit();
            TmwSqlVerifyDatabase.database.setAutoCommit(true);
        }
    }

    public static void getMobs(int lowerX, int upperX, int lowerZ, int upperZ, Consumer<List<StoredMob>> callback) {
        new Thread(() -> {
            List<StoredMob> answer;
            try {
                answer = getMobThreaded(lowerX, upperX, lowerZ, upperZ);
            } catch (SQLException throwables) {
                answer = null;
            }
            List<StoredMob> finalAnswer = answer;
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), () -> callback.accept(finalAnswer));
        }).start();
    }

    private static List<StoredMob> getMobThreaded(int lowerX, int upperX, int lowerZ, int upperZ) throws SQLException {
        synchronized (TmwSqlVerifyDatabase.syncDB) {
            Statement statement = TmwSqlVerifyDatabase.database.createStatement();
            ResultSet response = statement.executeQuery(String.format("""
                            SELECT *
                            FROM %s
                            WHERE %s BETWEEN %d AND %d
                              AND %s BETWEEN %d AND %d""",
                    TABLE_STORED_MOB,
                    X, lowerX, upperX,
                    Z, lowerZ, upperZ
                    )
            );
            List<StoredMob> mobs = new ArrayList<>();
            List<Long> mobsToDelete = new ArrayList<>();
            while (response.next()) {
                try {
                    mobs.add(new StoredMob(response.getLong(MOB_MY_UID), response.getInt(X), response.getInt(Y), response.getInt(Z), response.getInt(WORLD_MY_UID), response.getString(NBT), response.getTimestamp(DESPAWN_TIME)));
                } catch (CommandSyntaxException e) {
                    mobsToDelete.add(response.getLong(MOB_MY_UID));
                    e.printStackTrace();
                }
            }
            removeMobs(mobsToDelete);
            statement.close();
            return mobs;
        }
    }

    public static void removeMobs(Collection<Long> mobsToDelete) throws SQLException {
        if (mobsToDelete.isEmpty()) return;
        synchronized (TmwSqlVerifyDatabase.syncDB) {
            Statement statement = TmwSqlVerifyDatabase.database.createStatement();
            List<String> whereClauses = new ArrayList<>();
            for (Long mob : mobsToDelete) {
                whereClauses.add(MOB_MY_UID + " = " + mob + " ");
            }
            statement.execute(String.format("DELETE FROM %s WHERE %s", TABLE_STORED_MOB, String.join(" OR ", whereClauses)));
            statement.close();
        }
    }

    public static class StoredMob {
        public long uid;
        public int x;
        public int y;
        public int z;
        public UUID worldUUID;
        public int myWorldUid;
        public NBTTagCompound nbt;
        public long despawnTime;

        public StoredMob(int x, int y, int z, UUID worldUUID, NBTTagCompound nbt, long despawnTime) throws SQLException {
            this.uid = TmwSqlVerifyDatabase.getMobMyUid();
            this.x = x;
            this.y = y;
            this.z = z;
            this.worldUUID = worldUUID;
            this.myWorldUid = SqlWorldGet.getMyWorldUid(worldUUID);
            this.nbt = nbt;
            this.despawnTime = despawnTime;
        }

        public StoredMob(long uid, int x, int y, int z, int myWorldUid, String nbt, Timestamp despawnTime) throws CommandSyntaxException {
            this.uid = uid;
            this.x = x;
            this.y = y;
            this.z = z;
            this.myWorldUid = myWorldUid;
            this.worldUUID = SqlWorldGet.getWorldUUID(myWorldUid);
            this.nbt = MojangsonParser.parse(nbt);
            this.despawnTime = despawnTime.getTime();
        }

        public WorldServer getNmsWorld() {
            @Nullable World world = getWorld();
            if (world == null) return null;
            return ((CraftWorld) world).getHandle();
        }

        @Nullable
        public World getWorld() {
            return Bukkit.getWorld(worldUUID);
        }
    }
}
