package voltskiya.apple.game_mechanics.tmw.tmw_world.mobs;

import org.bukkit.Bukkit;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;

import java.util.Map;

import static voltskiya.apple.game_mechanics.tmw.sql.MobSqlStorage.SpawnPercentages;
import static voltskiya.apple.game_mechanics.tmw.sql.MobSqlStorage.getRegen;

public class MobRegen implements Runnable {
    private static final long REGEN_INTERVAL = 20 * 60;

    public MobRegen() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, REGEN_INTERVAL / 4);
    }

    @Override
    public void run() {
        getRegen(this::spawn);
    }

    private void spawn(Map<Long, SpawnPercentages> mobCounts) {
        if (mobCounts != null) {
            mobCounts.values().forEach(SpawnPercentages::spawn);
            mobCounts.values().removeIf(SpawnPercentages::noMobsToSpawn);
            mobCounts.values().forEach(SpawnPercentages::spawnIRL);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, REGEN_INTERVAL);
    }
}
