package voltskiya.apple.game_mechanics.deleteme_later.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.deleteme_later.biome.TemperatureBiome;


public class TemperaturePlayer {
    private static final int BIOME_TICKS_INTERVAL = 100;
    private final Player player;
    private double currentTemperature;
    private double currentWetness;
    private int lastBiomeCheck = 0;
    private TemperatureBiome biome = null;
    private int currentTick = 0;

    /**
     * does a lot of the synchronizing to make sure im not doing any simultaneous multithreading in this object,
     * while still being able to use this object from two different threads
     */
    private boolean isInTick = false;
    private Location currentLocation;

    public TemperaturePlayer(Player player) {
        this.player = player;
        this.currentTemperature = 0;
        this.currentWetness = 0;
        load();
    }


    /**
     * does the player tick
     * **Should never be called from bukkit thread**
     */
    public synchronized void tick() {
        if (this.isInTick) return;
        this.isInTick = true;
        this.checkBiome();
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::bukkitTick);
    }

    /**
     * does everything that needs to be done on the main thread
     */
    private void bukkitTick() {
        TemperatureDisplay.display(this.player, this.currentTemperature);
        this.currentLocation = this.player.getLocation().clone();
        this.isInTick = false;
    }

    private void checkBiome() {
        boolean shouldCheckBiome;
        shouldCheckBiome = currentTick - this.lastBiomeCheck > BIOME_TICKS_INTERVAL;
        if (shouldCheckBiome) {
            this.lastBiomeCheck = currentTick;
        }
//        if (shouldCheckBiome) {
//            this.biome = new GetChunksRequest(currentLocation).getBiome();
//        }
    }

    /**
     * loads the player from the database
     */
    private void load() {

    }


    @Override
    public synchronized int hashCode() {
        return player.getUniqueId().hashCode();
    }


    @Override
    public synchronized boolean equals(Object obj) {
        return obj instanceof Player && player.getUniqueId().equals(((Player) obj).getUniqueId());
    }
}
