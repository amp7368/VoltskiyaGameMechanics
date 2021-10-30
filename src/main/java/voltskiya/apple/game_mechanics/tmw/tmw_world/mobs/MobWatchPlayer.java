package voltskiya.apple.game_mechanics.tmw.tmw_world.mobs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.tmw.sql.MobSqlStorage;
import voltskiya.apple.game_mechanics.tmw.sql.TmwStoredMob;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayer;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchTickable;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.SimpleWorldDatabase;

import java.util.*;

import static voltskiya.apple.game_mechanics.deleteme_later.chunks.TemperatureChunk.BLOCKS_IN_A_CHUNK;

public class MobWatchPlayer implements WatchTickable {
    private static final int CHUNK_SIGHT = 6;
    //todo change interval
    private static final int CHECK_INTERVAL = 60;

    private static final HashSet<Long> mobsToBeRemoved = new HashSet<>();
    private final Player player;
    private int tickCount;

    public MobWatchPlayer(Player player, WatchPlayer watchPlayer) {
        this.player = player;
    }

    public static synchronized void spawnMobs(List<TmwStoredMob> mobsToSpawn) {
        List<Long> mobsToRemove = new ArrayList<>();
        for (TmwStoredMob storedMob : mobsToSpawn) {
            if (mobsToBeRemoved.contains(storedMob.uid)) continue;
            MobType mobType = MobTypeDatabase.getMob(storedMob.uniqueName);
            Optional<EntityTypes<?>> entityTypes = EntityTypes.a(mobType.getEnitityNbt());
            if (entityTypes.isPresent()) {
                Entity entity = entityTypes.get().a(storedMob.getNmsWorld());
                mobsToRemove.add(storedMob.uid);
                mobsToBeRemoved.add(storedMob.uid);
                if (entity != null) {
                    storedMob.getNmsWorld().addAllEntitiesSafely(entity);
                    entity.load(mobType.getEnitityNbt());
                    entity.addScoreboardTag(TmwStoredMob.getTag(storedMob.uniqueName));
                    entity.teleportAndSync(storedMob.x, storedMob.y, storedMob.z);
                    System.out.printf("summon mob %s at %s %d %d %d\n", storedMob.uniqueName, storedMob.getWorld().getName(), storedMob.x, storedMob.y, storedMob.z);
                }
            }
        }
        MobSqlStorage.removeMobs(mobsToRemove);
    }

    public static synchronized void removeMobToBeRemoved(Collection<Long> id) {
        mobsToBeRemoved.removeAll(id);
    }

    @Override
    public void run() {
        if (!this.player.isOnline()) {
            // player left the game. just let this die and tell the listener that spawned us that we're dead
            WatchPlayerListener.get().leave(player.getUniqueId());
            return;
        }
        final Location playerLocation = player.getLocation();
        final Chunk centerChunk = playerLocation.getChunk();
        int x = centerChunk.getX();
        int z = centerChunk.getZ();
        int lowerX = x - CHUNK_SIGHT;
        int upperX = x + CHUNK_SIGHT;
        int lowerZ = z - CHUNK_SIGHT;
        int upperZ = z + CHUNK_SIGHT;
        MobSqlStorage.getMobs(
                lowerX * BLOCKS_IN_A_CHUNK,
                (1 + upperX) * BLOCKS_IN_A_CHUNK,
                lowerZ * BLOCKS_IN_A_CHUNK,
                (1 + upperZ) * BLOCKS_IN_A_CHUNK,
                SimpleWorldDatabase.getWorld(playerLocation.getWorld().getUID()),
                MobWatchPlayer::spawnMobs
        );
    }

    @Override
    public int getTickCount() {
        return this.tickCount;
    }

    @Override
    public void setTickCount(int i) {
        this.tickCount = i;
    }

    @Override
    public int getTicksPerRun() {
        return CHECK_INTERVAL;
    }
}
