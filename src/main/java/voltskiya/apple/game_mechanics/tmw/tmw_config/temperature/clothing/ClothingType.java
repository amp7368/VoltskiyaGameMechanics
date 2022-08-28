package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing;

import apple.mc.utilities.item.material.ArmorType;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ClothingType {

    private final ClothingTypeBuilder.ClothingIcon icon;
    private final ArmorType clothingPlace;
    private final double windProtection;
    private final double wetProtection;
    private final double heatResistance;
    private final double coldResistance;

    public ClothingType(ClothingTypeBuilder builder) {
        this.icon = builder.icon;
        this.clothingPlace = builder.clothingPlace;
        this.windProtection = builder.windProtection;
        this.wetProtection = builder.wetProtection;
        this.heatResistance = builder.heatResistance;
        this.coldResistance = builder.coldResistance;
    }

    public static String getName(ItemStack item) {
        return getName(item.getType(), "");
    }

    public static String getName(Material material, String name) {
        return material.name() + ":" + name;
    }

    public String getName() {
        return this.icon.getName();
    }

    public ArmorType getClothingPlace() {
        return clothingPlace;
    }

    public double getWindProtection() {
        return windProtection;
    }

    public double getWetProtection() {
        return wetProtection;
    }

    public double getHeatResistance() {
        return heatResistance;
    }

    public double getColdResistance() {
        return coldResistance;
    }

    public ItemStack toItem() {
        return this.icon.toItem();
    }

    public ClothingTypeBuilder toBuilder() {
        return new ClothingTypeBuilder(this);
    }

    public static class ClothingTypeBuilder {

        private ClothingTypeBuilder.ClothingIcon icon = null;
        private ArmorType clothingPlace = null;
        private double windProtection = 0;
        private double wetProtection = 0;
        private double heatResistance = 0;
        private double coldResistance = 0;

        public ClothingTypeBuilder(ClothingType real) {
            this.icon = real.icon;
            this.clothingPlace = real.clothingPlace;
            this.windProtection = real.windProtection;
            this.wetProtection = real.wetProtection;
            this.heatResistance = real.heatResistance;
            this.coldResistance = real.coldResistance;
        }

        public ClothingTypeBuilder() {

        }

        public double getWindProtection() {
            return windProtection;
        }

        public void incrementWindProtection(double windProtection) {
            this.windProtection += windProtection;
        }

        public double getWetProtection() {
            return wetProtection;
        }

        public void incrementWetProtection(double wetProtection) {
            this.wetProtection += wetProtection;
        }

        public double getHeatResistance() {
            return heatResistance;
        }

        public void incrementHeatResistance(double heatResistance) {
            this.heatResistance += heatResistance;
        }

        public double getColdResistance() {
            return coldResistance;
        }

        public void incrementColdResistance(double coldResistance) {
            this.coldResistance += coldResistance;
        }

        public ClothingType build() {
            return new ClothingType(this);
        }

        public boolean canBuild() {
            return this.clothingPlace != null;
        }

        public void setIcon(ItemStack currentItem, ArmorType armorType) {
            this.icon = new ClothingIcon(currentItem);
            this.clothingPlace = armorType;
        }

        public ItemStack toItem() {
            return icon == null ? new ItemStack(Material.AIR) : icon.toItem();
        }

        public static class ClothingIcon {

            private final String name;
            private final Material material;
            private final List<String> lore;

            public ClothingIcon(String name, Material material, List<String> lore) {
                this.name = name;
                this.material = material;
                this.lore = lore;
            }

            public ClothingIcon(ItemStack item) {
                final ItemMeta itemMeta = item.getItemMeta();
                this.name = itemMeta.getDisplayName();
                this.material = item.getType();
                this.lore = itemMeta.getLore();
            }

            public ItemStack toItem() {
                ItemStack item = new ItemStack(material);
                final ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(name);
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                return item;
            }

            public String getName() {
                return ClothingType.getName(material, name);
            }

        }
    }
}
