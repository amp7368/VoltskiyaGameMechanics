package voltskiya.apple.game_mechanics.tmw.tmw_world.temperature;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class PlayerTemperature {
    private final UUID uuid;
    public double temperature = 0;
    public double wetness = 0;

    public PlayerTemperature(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void saveThreaded() {
        new Thread(this::save).start();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(PlayerTemperatureDatabase.playerTemperaturesFolder, uuid.toString())))) {
            new Gson().toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
