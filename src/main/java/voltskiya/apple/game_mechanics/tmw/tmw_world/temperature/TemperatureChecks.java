package voltskiya.apple.game_mechanics.tmw.tmw_world.temperature;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TemperatureChecks {
    /**
     * @param location the location to check around
     * @return a value between 0 and 1
     * 0 represents outside completely, 1 represents inside completely
     */
    public static double insideness(Location location) {
        return 0;
    }

    /**
     * @param location the location to check around
     * @return any double as a temperature value considering all heat sources
     */
    public static double sources(Location location) {
        return 0;
    }

    /**
     * @param location the location to check around
     * @return the wind of the chunk regardless of whether we are inside
     */
    public static double wind(Location location) {
        return 0;
    }

    public static ClothingTemperature clothing(Player player) {
        @Nullable EntityEquipment equipment = player.getEquipment();
        @NotNull ItemStack[] armor = equipment == null ? new ItemStack[0] : equipment.getArmorContents();
        @NotNull ItemStack mainHand = player.getInventory().getItemInMainHand();
        @NotNull ItemStack offHand = player.getInventory().getItemInOffHand();

        final ClothingTemperature clothing = new ClothingTemperature();
        for (ItemStack item : armor) {
            clothing.addArmor(item);
        }

        return clothing;
    }

    public static class ClothingTemperature {
        private double windProtection = 0;
        private double wetProtection = 0;
        private double wetness = 0;
        private double heatResistance = 0;
        private double coldResistance = 0;

        public double getWindProtection() {
            return windProtection;
        }

        public double getWetProtection() {
            return wetProtection;
        }

        public double getWetness() {
            return wetness;
        }

        public double getHeatResistance() {
            return heatResistance;
        }

        public double getColdResistance() {
            return coldResistance;
        }

        private void addArmor(@Nullable ItemStack item) {
            if (item == null) return;
            @NotNull PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        }

        public double resistWind(double wind) {
            return wind;
        }

        public double resistTemp(double feelsLikeOutside) {
            return feelsLikeOutside;
        }
    }
}
