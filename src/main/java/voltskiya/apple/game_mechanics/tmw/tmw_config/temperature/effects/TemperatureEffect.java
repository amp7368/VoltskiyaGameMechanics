package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Collections;
import java.util.UUID;

public class TemperatureEffect {
    private String uuid;
    private double temperatureStart;
    private String potionEffectType;
    private int level;
    private int interval;
    private int lasting;

    public TemperatureEffect(TemperatureEffectBuilder builder) {
        this.uuid = builder.uuid.toString();
        this.temperatureStart = builder.temperatureStart;
        this.potionEffectType = builder.potionEffectType.getName();
        this.level = builder.level;
        this.interval = builder.interval;
        this.lasting = builder.lasting;
    }

    public TemperatureEffect() {

    }

    public UUID getUUID() {
        return UUID.fromString(uuid);
    }

    private PotionEffectType getPotionEffectType() {
        return PotionEffectType.getByName(potionEffectType);
    }

    public TemperatureEffectBuilder toBuilder() {
        return new TemperatureEffectBuilder(this);
    }

    public ItemStack toItem() {
        return InventoryUtils.makeItem(Material.POTION, 1, potionEffectType,
                Collections.singletonList(
                        String.format("Start: %.1f", temperatureStart)
                )
        );
    }

    public static class TemperatureEffectBuilder {
        private UUID uuid = UUID.randomUUID();
        private double temperatureStart = 0;
        private PotionEffectType potionEffectType = null;
        private int level = 0;
        private int interval = 0;
        private int lasting = 300;

        public TemperatureEffectBuilder(TemperatureEffect real) {
            this.uuid = real.getUUID();
            this.temperatureStart = real.temperatureStart;
            this.potionEffectType = real.getPotionEffectType();
            this.level = real.level;
            this.interval = real.interval;
            this.lasting = real.lasting;
        }

        public TemperatureEffectBuilder() {

        }

        public TemperatureEffect build() {
            return new TemperatureEffect(this);
        }

        public double getTemperatureStart() {
            return temperatureStart;
        }

        public void incrementTemperatureStart(double temperatureStart) {
            this.temperatureStart += temperatureStart;
        }

        public int getLevel() {
            return level;
        }

        public void incrementLevel(int level) {
            this.level += level;
        }

        public int getInterval() {
            return interval;
        }

        public void incrementInterval(int interval) {
            this.interval += interval;
        }

        public int getLasting() {
            return lasting;
        }

        public PotionEffectType getPotionEffectType() {
            return potionEffectType;
        }

        public void incrementLasting(int lasting) {
            this.lasting += lasting;
        }

        public void setPotionType(PotionEffectType potionType) {
            this.potionEffectType = potionType;
        }
    }
}
