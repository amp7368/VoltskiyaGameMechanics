package voltskiya.apple.game_mechanics.tmw.tmw_world.mobs;

import org.bukkit.Bukkit;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.sql.SpawnPercentages;

import java.util.Map;

import static voltskiya.apple.game_mechanics.tmw.sql.MobSqlStorage.getRegen;

public class MobRegen implements Runnable {
    private static final long REGEN_INTERVAL = 20;
    private static boolean regen = true;

    public MobRegen() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 5 * 20);

    }

    public static boolean pause() {
        regen = !regen;
        return regen;
    }

    @Override
    public void run() {
        if (regen) {
            getRegen(this::spawn);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, REGEN_INTERVAL);
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
