package voltskiya.apple.game_mechanics.tmw.tmw_world.mobs;

import static voltskiya.apple.game_mechanics.tmw.PluginTMW.BLOCKS_IN_A_CHUNK;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;
import voltskiya.apple.game_mechanics.tmw.TmwWatchConfig;
import voltskiya.apple.game_mechanics.tmw.sql.MobSqlStorage;
import voltskiya.apple.game_mechanics.tmw.sql.TmwStoredMob;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayer;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchTickable;
import voltskiya.apple.game_mechanics.tmw.tmw_world.util.SimpleWorldDatabase;

public class MobWatchPlayer implements WatchTickable {

    private static final int CHUNK_SIGHT = 6;

    private final Player player;
    private int tickCount;

    public MobWatchPlayer(Player player, WatchPlayer watchPlayer) {
        this.player = player;
    }

    public static synchronized void spawnMobs(List<TmwStoredMob> mobsToSpawn) {
        List<Long> mobsToRemove = new ArrayList<>();
        for (TmwStoredMob storedMob : mobsToSpawn) {
            synchronized (MobSqlStorage.mobsToBeRemoved) {
                if (MobSqlStorage.mobsToBeRemoved.contains(storedMob.uid)) {
                    continue;
                }
            }
            MobType mobType = MobTypeDatabase.getMob(storedMob.uniqueName);
            mobType.spawn(storedMob.getLocation());
            mobsToRemove.add(storedMob.uid);
            if (TmwWatchConfig.get().consoleOutput.showSummonMob) {
                PluginTMW.get().logger().info("summon mob %s at %s %d %d %d", storedMob.uniqueName,
                    storedMob.getWorld().getName(), storedMob.x, storedMob.y, storedMob.z);
            }
        }
        MobSqlStorage.removeMobs(mobsToRemove);
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
        MobSqlStorage.getMobs(lowerX * BLOCKS_IN_A_CHUNK, (1 + upperX) * BLOCKS_IN_A_CHUNK,
            lowerZ * BLOCKS_IN_A_CHUNK, (1 + upperZ) * BLOCKS_IN_A_CHUNK,
            SimpleWorldDatabase.getWorld(playerLocation.getWorld().getUID()),
            MobWatchPlayer::spawnMobs);
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
        return TmwWatchConfig.getCheckInterval().mobWatchPlayer;
    }
}
