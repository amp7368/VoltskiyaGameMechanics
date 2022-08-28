package voltskiya.apple.game_mechanics.tmw.sql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.SimpleWorldDatabase;

@Entity(name = "stored_mob")
@Table(name = "stored_mob")
public class TmwStoredMob {

    private static final String VOLT_MOB = "volt.mob.";
    @Id
    @Column(name = SqlVariableNames.MOB_MY_UID)
    public long uid;

    @Column(name = SqlVariableNames.X)
    public int x;
    @Column(name = SqlVariableNames.Y)
    public int y;
    @Column(name = SqlVariableNames.Z)
    public int z;

    @Transient
    public UUID worldUUID = null;
    @Column(name = SqlVariableNames.WORLD_MY_UID)
    public int myWorldUid;
    @Column(name = SqlVariableNames.MOB_UNIQUE_NAME)
    public String uniqueName;
    @Column(name = SqlVariableNames.DESPAWN_TIME)
    public long despawnTime;

    public TmwStoredMob() {
    }

    public TmwStoredMob(int x, int y, int z, UUID worldUUID, String uniqueName, long despawnTime)
        throws SQLException {
        this.uid = VerifyDatabaseTmw.getMobMyUid();
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldUUID = worldUUID;
        this.myWorldUid = SimpleWorldDatabase.getWorld(worldUUID);
        this.uniqueName = uniqueName;

        this.despawnTime = despawnTime;
    }

    public TmwStoredMob(int x, int y, int z, int myWorldUid, MobType mobType) {
        this.uid = VerifyDatabaseTmw.getMobMyUid();
        this.x = x;
        this.y = y;
        this.z = z;
        this.myWorldUid = myWorldUid;
        this.worldUUID = SimpleWorldDatabase.getWorld(myWorldUid);
        this.uniqueName = mobType.getName();
        this.despawnTime = mobType.getDespawnAt();
    }

    @Nullable
    public static String getUniqueName(@NotNull Set<String> tags) {
        for (String tag : tags) {
            if (tag.startsWith(VOLT_MOB) && tag.length() > VOLT_MOB.length()) {
                return tag.substring(VOLT_MOB.length());
            }
        }
        return null;
    }

    public static String getTag(String uniqueName) {
        return VOLT_MOB + uniqueName;
    }

    public ServerLevel getNmsWorld() {
        @Nullable World world = getWorld();
        if (world == null) {
            return null;
        }
        return ((CraftWorld) world).getHandle();
    }

    @Nullable
    public World getWorld() {
        if (worldUUID == null) {
            this.worldUUID = SimpleWorldDatabase.getWorld(myWorldUid);
        }
        return Bukkit.getWorld(worldUUID);
    }

    public Location getLocation() {
        return new Location(getWorld(), x, y, z);
    }
}
