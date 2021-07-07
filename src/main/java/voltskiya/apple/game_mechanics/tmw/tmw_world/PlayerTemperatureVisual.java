package voltskiya.apple.game_mechanics.tmw.tmw_world;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;

public class PlayerTemperatureVisual implements Runnable {
    private Player player;
    private int freezeTicks = 0;

    public PlayerTemperatureVisual(Player player) {
        this.player = player;
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 1);
    }

    public void setFreezeTicks(int freezeTicks) {
        this.freezeTicks = freezeTicks;
    }

    @Override
    public void run() {
        if (!this.player.isOnline()) {
            // player left the game. just let this die and tell the listener that spawned us that we're dead
            WatchPlayerListener.get().leave(this.player.getUniqueId());
            return;
        }
        if (player.getGameMode() != GameMode.SURVIVAL) {
            this.player.setFreezeTicks(0);
        } else if (this.freezeTicks != 0) {
            this.player.setFreezeTicks(Math.max(this.player.getFreezeTicks(), this.freezeTicks));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, 1);
    }
}
