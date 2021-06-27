package voltskiya.apple.game_mechanics.tmw.tmw_world.mobs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.sql.MobSqlStorage;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayer;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static voltskiya.apple.game_mechanics.deleteme_later.chunks.TemperatureChunk.BLOCKS_IN_A_CHUNK;

public class MobWatchPlayer implements Runnable {
    private static final int CHUNK_SIGHT = 7;
    //todo change interval
    private static final long CHECK_INTERVAL = 60;
    private final Player player;

    public MobWatchPlayer(Player player, WatchPlayer watchPlayer) {
        this.player = player;
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this);
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
                upperX * BLOCKS_IN_A_CHUNK,
                (1 + lowerZ) * BLOCKS_IN_A_CHUNK,
                (1 + upperZ) * BLOCKS_IN_A_CHUNK,
                this::spawnMobs
        );
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, CHECK_INTERVAL);
    }

    public void spawnMobs(List<MobSqlStorage.StoredMob> mobsToSpawn) {
        List<Long> mobsToRemove = new ArrayList<>();
        for (MobSqlStorage.StoredMob mob : mobsToSpawn) {
            Optional<EntityTypes<?>> entityTypes = EntityTypes.a(mob.nbt);
            if (entityTypes.isPresent()) {
                Entity entity = entityTypes.get().a(mob.getNmsWorld());
                if (entity != null) {
                    entity.load(mob.nbt);
                    System.out.printf("%d, %d, %d, %s\n", mob.x, mob.y, mob.z, mob.getNmsWorld());
                    mob.getNmsWorld().addAllEntitiesSafely(entity);
                    entity.teleportAndSync(mob.x, mob.y, mob.z);
                    mobsToRemove.add(mob.uid);
                    System.out.println("spawned");
                }
            }
        }
        try {
            MobSqlStorage.removeMobs(mobsToRemove);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
