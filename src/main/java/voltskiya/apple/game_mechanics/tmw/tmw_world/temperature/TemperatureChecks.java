package voltskiya.apple.game_mechanics.tmw.tmw_world.temperature;

import apple.mc.utilities.data.region.XYZ;
import apple.mc.utilities.item.material.MaterialUtils;
import apple.mc.utilities.world.vector.VectorUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TempBlockType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.blocks.TemperatureBlocksDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.ClothingDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.ClothingType;

public class TemperatureChecks {

    public static final int SOURCES_DISTANCE = 7;
    private static final int MAX_DEPTH_INSIDENESS = 8;
    public static final double INSIDE_MAX_SCANABLE = Math.pow(MAX_DEPTH_INSIDENESS, 3) / 2;
    private static final double importanceWind = 0.3;
    private static final double importanceWet = 0.7;

    /**
     * @param location the location to check around
     * @return a value between 0 and 1 0 represents outside completely, 1 represents inside
     * completely
     */
    public static double insideness(Location location) {
        location.getWorld();
        Set<XYZ<Integer>> alreadyScanned = new HashSet<>();
        Collection<XYZ<Integer>> edges = new HashSet<>();
        int count = scanInsideness(location, alreadyScanned, MAX_DEPTH_INSIDENESS / 2, edges);
        count -= edges.size();
        alreadyScanned.removeAll(edges);
        for (XYZ<Integer> edge : edges) {
            count += scanInsideness(location.clone().set(edge.getX(), edge.getY(), edge.getZ()),
                alreadyScanned, MAX_DEPTH_INSIDENESS / 2, new HashSet<>());
        }
        return 1 - Math.min(1, Math.max(0, -.2 + count / INSIDE_MAX_SCANABLE));
    }

    private static int scanInsideness(Location location, Set<XYZ<Integer>> alreadyScanned,
        int depthToScan, Collection<XYZ<Integer>> edges) {
        @NotNull Block block = location.getBlock();
        final XYZ<Integer> coords = new XYZ<>(location.getBlockX(), location.getBlockY(),
            location.getBlockZ());
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
                        double distance = VectorUtils.magnitude(xi, yi, zi);
                        double blockTemp = temp.getTemperature();
                        total += calculateBlockTemp(distance, blockTemp);
                    }
                }
            }
        }
        double fin = Math.pow(Math.abs(total), 1 / 2f);
        if (total < 0) {
            fin = -fin;
        }
        return fin;
    }

    private static double calculateBlockTemp(double distance, double blockTemp) {
        return Math.sqrt(blockTemp * blockTemp / Math.sqrt(distance + 1));
    }

    /**
     * @param currentBiome the biome to check in
     * @param location     the location to check around
     * @return the wind of the chunk regardless of whether we are inside
     */
    public static double wind(@Nullable BiomeType currentBiome, Location location) {
        if (currentBiome == null) {
            return 0;
        }
        return currentBiome.getWind();
    }

    public static ClothingTemperature clothing(Player player) {
        @Nullable EntityEquipment equipment = player.getEquipment();
        @NotNull ItemStack[] armor =
            equipment == null ? new ItemStack[0] : equipment.getArmorContents();
        @NotNull ItemStack mainHand = player.getInventory().getItemInMainHand();
        @NotNull ItemStack offHand = player.getInventory().getItemInOffHand();

        final ClothingTemperature clothing = new ClothingTemperature();
        for (ItemStack item : armor) {
            clothing.addArmor(item);
        }

        return clothing;
    }

    /**
     * @param player the player to check
     * @return a positive value 0 means not wet, the higher the value the more wetness
     */
    public static double wetness(Player player) {
        return player.isInWaterOrRain() ? 50 : 0;
    }

    public static double fluidFactor(double wind, double wetness) {
        return Math.pow(importanceWind * Math.pow(Math.abs(wind), .25) + importanceWet * Math.pow(
            Math.abs(wetness), .25), 2);
    }

    public static class ClothingTemperature {

        private double windProtection = 0;
        private double wetProtection = 0;
        private double heatResistance = 0;
        private double coldResistance = 0;

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

        private void addArmor(@Nullable ItemStack item) {
            if (item == null) {
                return;
            }
            @NotNull PersistentDataContainer dataContainer = item.getItemMeta()
                .getPersistentDataContainer();
            @Nullable ClothingType clothing = ClothingDatabase.get(item);
            if (clothing != null) {
                this.windProtection += clothing.getWindProtection();
                this.wetProtection += clothing.getWetProtection();
                this.heatResistance += clothing.getHeatResistance();
                this.coldResistance += clothing.getColdResistance();
            }
        }

        public double resistTemp(double feelsLikeOutside) {
            double resistance = feelsLikeOutside < 0 ? this.coldResistance : this.heatResistance;
            double resisted = Math.pow(Math.abs(feelsLikeOutside),
                1 / Math.pow(resistance < 0 ? -1 / resistance : resistance + 1, .25));
            return feelsLikeOutside < 0 ? -resisted : resisted;
        }

        public double resistWet(double wetness) {
            double resistance = this.wetProtection;
            double resisted = Math.pow(Math.abs(wetness),
                1 / Math.pow(resistance < 0 ? -1 / resistance + 1 : resistance + 1, .25));
            return wetness < 0 ? 0 : resisted;
        }

        public double resistWind(double wind) {
            double resistance = this.windProtection;
            double resisted = Math.pow(Math.abs(wind),
                1 / Math.pow(resistance < 0 ? -1 / resistance + 1 : resistance + 1, .25));
            return wind < 0 ? 0 : resisted;
        }

    }
}
