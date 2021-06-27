package voltskiya.apple.game_mechanics.tmw.tmw_world.temperature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerTemperatureDatabase {
    public static final File playerTemperaturesFolder;
    private static final String PLAYER_TEMPERATURES_FOLDER = "playerTemperature";
    private static final HashMap<UUID, PlayerTemperature> playerTemperatures = new HashMap<>();
    private static Gson gson;
    private static PlayerTemperatureDatabase instance;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        // get the playerTemperatures from our db
        playerTemperaturesFolder = new File(PluginTMW.get().getDataFolder(), PLAYER_TEMPERATURES_FOLDER);
        playerTemperaturesFolder.mkdirs();
        final File[] files = playerTemperaturesFolder.listFiles();
        if (files != null)
            for (File file : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    PlayerTemperature temp = gson.fromJson(reader, PlayerTemperature.class);
                    playerTemperatures.put(temp.getUUID(), temp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

    }


    public synchronized static void addPlayerTemperature(PlayerTemperature playerTemperature) {
        playerTemperatures.put(playerTemperature.getUUID(), playerTemperature);
        playerTemperature.save();
    }

    private static void save() {
        for (PlayerTemperature playerTemperature : getAll()) {
            playerTemperature.save();
        }
    }

    public static List<PlayerTemperature> getAll() {
        return new ArrayList<>(playerTemperatures.values());
    }

    public static PlayerTemperature get(UUID uuid) {
        return playerTemperatures.computeIfAbsent(uuid, (u) -> new PlayerTemperature(uuid));
    }
}
