package voltskiya.apple.game_mechanics.tmw.tmw_world.temperature;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TempBlockType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TemperatureBlocksDatabase;
import voltskiya.apple.utilities.util.DistanceUtils;
import voltskiya.apple.utilities.util.data_structures.Triple;
import voltskiya.apple.utilities.util.minecraft.MaterialUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TemperatureChecks {
    public static final int SOURCES_DISTANCE = 7;
    private static final int MAX_DEPTH_INSIDENESS = 12;
    public static final double INSIDE_MAX_SCANABLE = Math.pow(MAX_DEPTH_INSIDENESS, 3) / 2;

    /**
     * @param location the location to check around
     * @return a value between 0 and 1
     * 0 represents outside completely, 1 represents inside completely
     */
    public static double insideness(Location location) {
        location.getWorld();
        Set<Triple<Integer, Integer, Integer>> alreadyScanned = new HashSet<>();
        Collection<Triple<Integer, Integer, Integer>> edges = new HashSet<>();
        int count = scanInsideness(location, alreadyScanned, MAX_DEPTH_INSIDENESS / 2, edges);
        count -= edges.size();
        alreadyScanned.removeAll(edges);
        for (Triple<Integer, Integer, Integer> edge : edges) {
            count += scanInsideness(location.clone().set(edge.getX(), edge.getY(), edge.getZ()),
                    alreadyScanned,
                    MAX_DEPTH_INSIDENESS / 2,
                    new HashSet<>());
        }
        return 1 - Math.min(1, Math.max(0, -.2 + count / INSIDE_MAX_SCANABLE));
    }

    private static int scanInsideness(Location location, Set<Triple<Integer, Integer, Integer>> alreadyScanned, int depthToScan, Collection<Triple<Integer, Integer, Integer>> edges) {
        @NotNull Block block = location.getBlock();
        final Triple<Integer, Integer, Integer> coords = new Triple<>(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (alreadyScanned.add(coords)) {
            if (MaterialUtils.isPassable(block.getType())) {
                if (depthToScan == 0) {
                    edges.add(coords);
                    return 1;
                } else {
                    // recurse
                    int airCounter = 1;
                    Location clone = location.clone();
                    clone.setY(location.getY() + 1);
                    airCounter += scanInsideness(clone, alreadyScanned, depthToScan - 1, edges);
                    clone = location.clone();
                    clone.setX(location.getX() + 1);
                    airCounter += scanInsideness(clone, alreadyScanned, depthToScan - 1, edges);
                    clone = location.clone();
                    clone.setX(location.getX() - 1);
                    airCounter += scanInsideness(clone, alreadyScanned, depthToScan - 1, edges);
                    clone = location.clone();
                    clone.setZ(location.getZ() + 1);
                    airCounter += scanInsideness(clone, alreadyScanned, depthToScan - 1, edges);
                    clone = location.clone();
                    clone.setZ(location.getZ() - 1);
                    airCounter += scanInsideness(clone, alreadyScanned, depthToScan - 1, edges);
                    // dont do y - 1
                    return airCounter;
                }
            } else {
                return 0;
            }
        }
        return 0;
    }

    /**
     * @param location the location to check around
     * @return any double as a temperature value considering all heat sources
     */
    public static double sources(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        final World world = location.getWorld();
        double total = 0;
        for (int xi = -SOURCES_DISTANCE; xi <= SOURCES_DISTANCE; xi++) {
            for (int yi = -SOURCES_DISTANCE; yi <= SOURCES_DISTANCE; yi++) {
                for (int zi = -SOURCES_DISTANCE; zi <= SOURCES_DISTANCE; zi++) {
                    @NotNull Block block = world.getBlockAt(x + xi, y + yi, z + zi);
                    @Nullable TempBlockType temp = TemperatureBlocksDatabase.get(block.getType());
                    if (temp != null) {
                        double distance = DistanceUtils.magnitude(xi, yi, zi);
                        double blockTemp = temp.getTemperature();
                        total += calculateBlockTemp(distance, blockTemp);
                    }
                }
            }
        }
        double fin = Math.pow(Math.abs(total), 1 / 2f);
        if (total < 0) fin = -fin;
        return fin;
    }

    private static double calculateBlockTemp(double distance, double blockTemp) {
        return Math.sqrt(blockTemp * blockTemp / Math.sqrt(distance + 1));
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
