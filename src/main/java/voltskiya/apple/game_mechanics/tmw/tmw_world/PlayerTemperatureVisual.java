package voltskiya.apple.game_mechanics.tmw.tmw_world;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PlayerTemperatureVisual implements WatchTickable {

    private final Player player;
    private int tickCount = 0;
    private int freezeTicks = 0;

    public PlayerTemperatureVisual(Player player) {
        this.player = player;
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
        return 2;
    }
}
