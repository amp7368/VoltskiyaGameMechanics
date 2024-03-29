package voltskiya.apple.game_mechanics.tmw.tmw_world;

import org.bukkit.Bukkit;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;

public interface WatchTickable extends Runnable {

    default void scheduleTicks() {
        int tickNow = getTickCount() % getTicksPerRun();
        if (tickNow == 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this);
        }
        setTickCount(tickNow + 1);
    }

    int getTickCount();

    void setTickCount(int i);

    int getTicksPerRun();
}
