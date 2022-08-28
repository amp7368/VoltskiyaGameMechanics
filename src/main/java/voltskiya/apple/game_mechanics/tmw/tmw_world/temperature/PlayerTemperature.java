package voltskiya.apple.game_mechanics.tmw.tmw_world.temperature;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.TemperatureEffect;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects.TemperatureEffectsDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_world.PlayerTemperatureVisual;

public class PlayerTemperature {

    private static final double MODIFIER = .06;
    private final UUID uuid;
    private final HashMap<UUID, Integer> previousEffects = new HashMap<>();
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
            new File(PlayerTemperatureDatabase.playerTemperaturesFolder, uuid.toString())))) {
            new Gson().toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double doWetTick(double finalWetness) {
        return this.wetness += finalWetness * MODIFIER - MODIFIER * this.wetness;
    }

    public void doTemperatureEffects(PlayerTemperatureVisual playerVisual) {
        if (this.temperature < -30) {
            playerVisual.setFreezeTicks((int) (Math.abs(this.temperature + 30)));
        }
        List<TemperatureEffect> effects = new ArrayList<>();
        double start = 0;
        boolean thisTemperature = false;
        if (this.temperature < 0) {
            for (TemperatureEffect effect : TemperatureEffectsDatabase.getAllCold()) {
                if (thisTemperature) {
                    if (effect.getTemperatureStart() == start) {
                        effects.add(effect);
                    } else {
                        break;
                    }
                } else if (effect.getTemperatureStart() > this.temperature) {
                    effects.add(effect);
                    start = effect.getTemperatureStart();
                    thisTemperature = true;
                }
            }
        } else {
            for (TemperatureEffect effect : TemperatureEffectsDatabase.getAllHot()) {
                if (thisTemperature) {
                    if (effect.getTemperatureStart() == start) {
                        effects.add(effect);
                    } else {
                        break;
                    }
                } else if (effect.getTemperatureStart() < this.temperature) {
                    effects.add(effect);
                    start = effect.getTemperatureStart();
                    thisTemperature = true;
                }
            }
        }
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            for (TemperatureEffect effect : effects) {
                Integer previous = previousEffects.getOrDefault(effect.getUUID(), 0);
                if (previous - Bukkit.getCurrentTick() > 500) {
                    previousEffects.remove(effect.getUUID());
                    previous = 0;
                }
                if (previous < Bukkit.getCurrentTick()) {
                    PotionEffect potionData = effect.getPotionData();
                    if (potionData != null) {
                        player.addPotionEffect(potionData);
                        previousEffects.put(effect.getUUID(),
                            Bukkit.getCurrentTick() + effect.getInterval());
                    }
                }
            }
        }
    }
}
