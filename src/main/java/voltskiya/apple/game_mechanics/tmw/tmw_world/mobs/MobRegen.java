package voltskiya.apple.game_mechanics.tmw.tmw_world.mobs;

import org.bukkit.Bukkit;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.MobConfigDatabase;
import voltskiya.apple.game_mechanics.tmw.sql.SpawnPercentages;

import java.util.Map;

import static voltskiya.apple.game_mechanics.tmw.sql.MobSqlStorage.getRegen;

public class MobRegen implements Runnable {
    public MobRegen() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 5 * 20);
    }


    @Override
    public void run() {
        VoltskiyaPlugin.get().scheduleSyncDelayedTask(this, MobConfigDatabase.get().getRegenInterval());
        if (MobConfigDatabase.get().isSpawningMobs()) {
            getRegen(this::spawn);
        }
    }

    private void spawn(Map<Long, SpawnPercentages> mobCounts) {
        if (mobCounts != null) {
            mobCounts.values().forEach(SpawnPercentages::spawn);
            mobCounts.values().removeIf(SpawnPercentages::noMobsToSpawn);
            mobCounts.values().forEach(SpawnPercentages::spawnIRL);
        }
    }
}
