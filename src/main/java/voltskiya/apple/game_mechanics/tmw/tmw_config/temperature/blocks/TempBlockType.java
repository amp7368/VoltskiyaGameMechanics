package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

public class TempBlockType {
    private final double temperature;
    private final Material material;

    public TempBlockType(TempBlockTypeBuilder builder) {
        this.material = builder.material;
        this.temperature = builder.temperature;
    }

    public String getName() {
        return material.name();
    }

    public TempBlockTypeBuilder toBuilder() {
        return new TempBlockTypeBuilder(this);
    }

    public double getTemperature() {
        return this.temperature;
    }

    public ItemStack toItem() {
        return InventoryUtils.makeItem(material.isItem() ? material : Material.BLACK_CONCRETE, 1, material.name(), null);
    }

    public static class TempBlockTypeBuilder {
        private Material material = Material.AIR;
        private double temperature = 0;

        private TempBlockTypeBuilder(TempBlockType real) {
            this.material = real.material;
            this.temperature = real.temperature;
        }

        public TempBlockTypeBuilder() {
        }

        public TempBlockType build() {
            return new TempBlockType(this);
        }

        public void incrementTemp(int increment) {
            this.temperature += increment;
        }

        public void setType(Material material) {
            this.material = material;
        }

        public Material getMaterial() {
            return material;
        }

        public double getTemperature() {
            return temperature;
        }
    }
}
