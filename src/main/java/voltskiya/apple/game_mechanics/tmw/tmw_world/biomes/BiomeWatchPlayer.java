package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;

import apple.nms.decoding.world.DecodeBiome;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.TmwWatchConfig;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayer;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchTickable;

public class BiomeWatchPlayer implements WatchTickable {

    private final Player player;
    @Nullable
    private BiomeType currentGuess;
    private int tickCount;

    public BiomeWatchPlayer(Player player, WatchPlayer watchPlayer) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!this.player.isOnline()) {
            WatchPlayerListener.get().leave(this.player.getUniqueId());
            return;
        }
        final Location location = player.getLocation();

        ResourceLocation minecraft = DecodeBiome.getBiomeKey(location.getWorld(),
            location.getBlockX(), location.getBlockY(), location.getBlockZ()).location();
        if (minecraft != null) {
            currentGuess = BiomeTypeDatabase.getBiome(minecraft);
        }
    }

    @Nullable
    public BiomeType getCurrentGuess() {
        return this.currentGuess;
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
        return TmwWatchConfig.getCheckInterval().biomeWatchPlayer;
    }
}
