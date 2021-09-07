package voltskiya.apple.game_mechanics.decay.sql;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.sql.VerifyDatabaseTmw;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static voltskiya.apple.game_mechanics.tmw.sql.SqlVariableNames.*;

public class MaterialSqlStorage {
    private static final BiMap<Material, Integer> materialToMyUid = HashBiMap.create();

    /**
     * @param material the material to get the uid of
     * @return the uid corresponding to material or null if material is air
     * @throws SQLException probably never
     */
    @Nullable
    public static Integer get(Material material) throws SQLException {
        if (material == null || material.isAir()) return null;
        Integer result = materialToMyUid.get(material);
        if (result != null) return result;
        synchronized (VerifyDatabaseTmw.syncDB) {
            Statement statement = null;// TmwSqlVerifyDatabase.database.createStatement();

            ResultSet response = statement.executeQuery(String.format(
                    "SELECT %s FROM %s WHERE %s = '%s'",
                    MATERIAL_MY_UID, TABLE_MATERIAL, MATERIAL, material.getKey().getKey()
            ));
            if (response.isClosed()) {
                statement.execute(String.format("INSERT INTO %s (%s, %s) VALUES ('%s', %d)",
                        TABLE_MATERIAL,
                        MATERIAL,
                        MATERIAL_MY_UID,
                        material.getKey().getKey(),
                        result = VerifyDatabaseTmw.getMaterialUid()));
            } else {
                result = response.getInt(1);
            }
            statement.close();
            return result;
        }
    }

    public static Material get(int material) throws SQLException {
        Material result = materialToMyUid.inverse().get(material);
        if (result != null) return result;
        synchronized (VerifyDatabaseTmw.syncDB) {
            Statement statement = null;//TmwSqlVerifyDatabase.database.createStatement();
            result = Material.matchMaterial(statement.executeQuery(String.format(
                    "SELECT %s FROM %s WHERE %s = %d",
                    MATERIAL, TABLE_MATERIAL, MATERIAL_MY_UID, material
            )).getString(1));
            statement.close();
            return result;
        }
    }
}
