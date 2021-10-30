package voltskiya.apple.game_mechanics.tmw.tmw_world;

import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.tmw.tmw_world.biomes.BiomeWatchPlayer;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobWatchPlayer;
import voltskiya.apple.game_mechanics.tmw.tmw_world.temperature.PlayerTemperature;
import voltskiya.apple.game_mechanics.tmw.tmw_world.temperature.PlayerTemperatureDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_world.temperature.TemperatureWatchPlayer;

public class WatchPlayer {
    private final Player player;
    private final BiomeWatchPlayer biomeWatch;
    private final MobWatchPlayer mobWatch;
    private final TemperatureWatchPlayer temperatureWatch;
    private final PlayerTemperature playerInfo;
    private final PlayerTemperatureVisual playerVisual;

    public WatchPlayer(Player player) {
        this.playerInfo = PlayerTemperatureDatabase.get(player.getUniqueId());
        this.player = player;
        this.playerVisual = new PlayerTemperatureVisual(player);
        this.biomeWatch = new BiomeWatchPlayer(player, this);
        this.mobWatch = new MobWatchPlayer(player, this);
        this.temperatureWatch = new TemperatureWatchPlayer(player, this.playerVisual, this);
        // for watching the specifics of a chunk in our DB
//        this.chunkWatch = new ChunkWatchPlayer(player,this);
    }

    public Player getPlayer() {
        return player;
    }

    public BiomeWatchPlayer getBiomeWatch() {
        return biomeWatch;
    }

    public MobWatchPlayer getMobWatch() {
        return mobWatch;
    }

    public TemperatureWatchPlayer getTemperatureWatch() {
        return temperatureWatch;
    }

    public PlayerTemperature getPlayerInfo() {
        return playerInfo;
    }

    public void scheduleTicks() {
        this.playerVisual.scheduleTicks();
        this.biomeWatch.scheduleTicks();
        this.mobWatch.scheduleTicks();
        this.temperatureWatch.scheduleTicks();
    }
}
