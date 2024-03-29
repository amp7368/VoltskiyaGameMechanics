package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.effects;

import apple.mc.utilities.inventory.item.InventoryUtils;
import java.util.Collections;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

    public double getTemperatureStart() {
        return temperatureStart;
    }

    public int getLevel() {
        return level;
    }

    public int getInterval() {
        return interval;
    }

    public int getLasting() {
        return lasting;
    }

    public String getPotionEffect() {
        return potionEffectType;
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
        return InventoryUtils.get().makeItem(Material.POTION, 1, potionEffectType,
            Collections.singletonList(String.format("Start: %.1f", temperatureStart)));
    }

    public PotionEffect getPotionData() {
        PotionEffectType type = PotionEffectType.getByName(potionEffectType);
        return type == null ? null : new PotionEffect(type, lasting, level, true);
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
