package voltskiya.apple.game_mechanics.tmw.tmw_world.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.sql.TmwSqlVerifyDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.*;

public class SqlWorldGet {
    private static final BiMap<UUID, Integer> realToMyWorld = HashBiMap.create();


    @Nullable
    public synchronized static UUID getWorldUUID(int myWorldUid) {
        UUID worldUUID = realToMyWorld.inverse().get(myWorldUid);
        if (worldUUID != null) return worldUUID;
        synchronized (TmwSqlVerifyDatabase.syncDB) {
            try {
                Statement statement = TmwSqlVerifyDatabase.database.createStatement();
                ResultSet response = statement.executeQuery(String.format("SELECT %s\n" +
                                "FROM %s\n" +
                                "WHERE %s = %d",
                        WORLD_UUID,
                        TABLE_WORLD,
                        WORLD_MY_UID,
                        myWorldUid
                ));
                if (response.isClosed()) {
                    return null;
                } else {
                    try {
                        worldUUID = UUID.fromString(response.getString(WORLD_UUID));
                        statement.close();
                        realToMyWorld.put(worldUUID, myWorldUid);
                        return worldUUID;
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }
    }

    public synchronized static int getMyWorldUid(UUID worldUUID) throws SQLException {
        Integer myWorldUid = realToMyWorld.get(worldUUID);
        if (myWorldUid != null) return myWorldUid;
        synchronized (TmwSqlVerifyDatabase.syncDB) {
            Statement statement = TmwSqlVerifyDatabase.database.createStatement();
            statement.execute(String.format("""
                            INSERT INTO %s (%s, %s)
                            VALUES ((SELECT ifnull(max(%s) + 1,0) FROM %s), '%s')
                            ON CONFLICT (%s) DO NOTHING""",
                    TABLE_WORLD,
                    WORLD_MY_UID,
                    WORLD_UUID,
                    WORLD_MY_UID,
                    TABLE_WORLD,
                    worldUUID.toString(),
                    WORLD_UUID
            ));
            ResultSet response = statement.executeQuery(String.format("""
                            SELECT %s
                            FROM %s
                            WHERE %s = '%s'""",
                    WORLD_MY_UID,
                    TABLE_WORLD,
                    WORLD_UUID,
                    worldUUID
            ));
            myWorldUid = response.isClosed() ? -1 : response.getInt(WORLD_MY_UID);
            statement.close();
            realToMyWorld.put(worldUUID, myWorldUid);
            return myWorldUid;
        }
    }
}
