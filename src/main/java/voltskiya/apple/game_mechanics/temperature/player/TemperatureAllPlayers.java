package voltskiya.apple.game_mechanics.temperature.player;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class TemperatureAllPlayers {
    private static Set<TemperaturePlayer> players = new HashSet<>();

    public synchronized static void tickFromBukkit() {
        new Thread(TemperatureAllPlayers::tick).start();
    }

    private synchronized static void tick() {
        for (TemperaturePlayer player : players) {
            player.tick();
        }
    }

    public synchronized static void addPlayer(Player player) {
        players.add(new TemperaturePlayer(player));
    }

    public static void removePlayer(Player player) {
    }
}
