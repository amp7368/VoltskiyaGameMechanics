package voltskiya.apple.game_mechanics.tmw.tmw_world.mobs;

import java.util.Map;
import org.bukkit.Bukkit;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.TmwMobConfigDatabase;
import voltskiya.apple.game_mechanics.tmw.sql.MobSqlStorage;
import voltskiya.apple.game_mechanics.tmw.sql.SpawnPercentages;

public class MobRegen implements Runnable {

    public MobRegen() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 5 * 20);
    }


    @Override
    public void run() {
        VoltskiyaPlugin.get()
            .scheduleSyncDelayedTask(this, TmwMobConfigDatabase.get().getRegenInterval());
        if (TmwMobConfigDatabase.get().isSpawningMobs()) {
            MobSqlStorage.getRegen(this::spawn);
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
