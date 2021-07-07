package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class BiomeType {
    private final HashMap<MobType, Integer> mobs;
    private final BiomeTypeBuilder.BiomeIcon icon;
    private final int highestY;
    private final int lowestY;
    private final double heightVariance;
    private final double typicalY;
    private final int spawnRate;
    private final HashMap<Material, Double> materials;
    private final HashMap<Biome, Double> biomes;
    private final int importanceOfBlocks;
    private final int importanceOfHeightVariance;
    private final int importanceOfBiomes;
    private final boolean isYBoundsMatters;
    private final HashMap<TemperatureTime, TemperatureInfo> dailyTemperatures;
    private final WindInfo windInfo;
    private int biomeUid;

    public BiomeType(BiomeTypeBuilder builder) {
        this.icon = builder.icon;
        this.highestY = builder.highestY;
        this.lowestY = builder.lowestY;
        this.heightVariance = builder.heightVariance;
        this.typicalY = builder.typicalY;
        this.spawnRate = builder.spawnRate;
        this.materials = builder.materials;
        this.biomes = builder.biomes;
        this.importanceOfHeightVariance = builder.importanceOfHeightVariance;
        this.importanceOfBlocks = builder.importanceOfBlocks;
        this.importanceOfBiomes = builder.importanceOfBiomes;
        this.isYBoundsMatters = builder.yBoundsToggle;
        this.mobs = builder.mobs;
        this.dailyTemperatures = builder.dailyTemperatures;
        this.windInfo = builder.windInfo;
        this.biomeUid = builder.biomeUid <= 0 ? BiomeTypeDatabase.getCurrentBiomeUid() : builder.biomeUid;
    }

    public ItemStack toItem() {
        return icon.toItem();
    }

    public BiomeTypeBuilder toBuilder() {
        return new BiomeTypeBuilder(this);
    }

    public String getName() {
        return icon.getName();
    }

    /**
     * make a guess of whether the parameters fit this biomeType.
     *
     * @param heightVariance
     * @param typicalY
     * @param materials
     * @param biomes
     * @return a number between 0 and 1. the closer to 0, the less likely this is to be the biome. the inverse for 1
     */
    public double guess(double heightVariance, double typicalY, Map<Material, Double> materials, Map<Biome, Double> biomes) {
        // this will be incorrect 0.3% of the time
        // if the typicalY is way off, say this isn't happening
        if (this.isYBoundsMatters && (typicalY + this.heightVariance * 3 < this.typicalY || typicalY - this.heightVariance * 3 > this.typicalY)) {
            return 0;
        }

        // percent error of height variation
        // a number between 0 and 1
        double heightVarianceScore = 1 - (Math.abs(this.heightVariance - heightVariance) / (this.heightVariance + heightVariance));

        // percent error of materials
        // a number between 0 and 1
        double materialsScore = 1;
        for (Material material : materials.keySet())
            materialsScore -= Math.abs(materials.get(material) - this.materials.getOrDefault(material, 0d));

        // percent error of biomes
        // a number between 0 and 1
        double biomesScore = 1;
        for (Biome biome : biomes.keySet())
            biomesScore -= Math.abs(biomes.get(biome) - this.biomes.getOrDefault(biome, 0d));

        // convert the scores to the scaled score based on the settings of this biome
        heightVarianceScore *= this.importanceOfHeightVariance;
        materialsScore *= this.importanceOfBlocks;
        biomesScore *= this.importanceOfBiomes;
        int importanceTotal = this.importanceOfBiomes + this.importanceOfBlocks + this.importanceOfHeightVariance;
        heightVarianceScore /= importanceTotal;
        materialsScore /= importanceTotal;
        biomesScore /= importanceTotal;
        return heightVarianceScore + materialsScore + biomesScore;
    }

    @Override
    public String toString() {
        return icon == null ? null : icon.getName();
    }

    public int getTypicalTempNow(long time) {
        final TemperatureInfo temperatureInfo = dailyTemperatures.get(TemperatureTime.getTime(time));
        return temperatureInfo == null ? -100 : temperatureInfo.degrees; // null should be impossible
    }

    public double getWind() {
        return (windInfo.kphMax + windInfo.kphMin) / 2d;
    }

    public int getUid() {
        return biomeUid;
    }

    public void validateUid() {
        if (this.biomeUid <= 0) this.biomeUid = BiomeTypeDatabase.getCurrentBiomeUid();
    }

    public double getUsefulSpawnRate() {
        return spawnRate / 10d;
    }

    public Map<MobType, Integer> getSpawns() {
        return this.mobs;
    }

    public Map<MobType, Double> getSpawnPercentages() {
        double mobCount = 0;
        for (Integer mob : this.mobs.values()) {
            mobCount += mob;
        }
        Map<MobType, Double> percentages = new HashMap<>();
        for (Map.Entry<MobType, Integer> mob : this.mobs.entrySet()) {
            percentages.put(mob.getKey(), mob.getValue() / mobCount);
        }
        return percentages;
    }

    public boolean isCorrectBiome(Map<Biome, Double> biomes) {
        for (Map.Entry<Biome, Double> myBiome : this.biomes.entrySet()) {
            if (myBiome.getValue() > .25 && biomes.getOrDefault(myBiome.getKey(), 0d) > .25) return true;
        }
        return false;
    }

    public enum TemperatureTime {
        MORNING,
        NOON,
        EVENING,
        MIDNIGHT;

        public static TemperatureTime getTime(long time) {
            time += 6000;
            time %= 24000;
            if (time < 3000) return MIDNIGHT;
            else if (time < 9000) return MORNING;
            else if (time < 16000) return NOON;
            else return EVENING;
        }
    }

    public static class BiomeTypeBuilder {
        public HashMap<MobType, Integer> mobs = new HashMap<>();
        public int biomeUid = -1;
        private BiomeTypeBuilderRegisterBlocks registerBlocks = null;
        private BiomeIcon icon;
        private int highestY = -1;
        private int lowestY = -1;
        private double heightVariance = -1;
        private double typicalY = -1;
        private int spawnRate = 0;
        private int importanceOfBlocks = 1;
        private int importanceOfHeightVariance = 1;
        private int importanceOfBiomes = 1;
        private HashMap<Material, Double> materials;
        private HashMap<Biome, Double> biomes;
        private boolean yBoundsToggle = true;
        private HashMap<TemperatureTime, TemperatureInfo> dailyTemperatures = new HashMap<>();
        private WindInfo windInfo;

        public BiomeTypeBuilder(BiomeType real) {
            this.icon = real.icon;
            this.highestY = real.highestY;
            this.lowestY = real.lowestY;
            this.heightVariance = real.heightVariance;
            this.typicalY = real.typicalY;
            this.spawnRate = real.spawnRate;
            this.materials = real.materials;
            this.biomes = real.biomes;
            this.importanceOfHeightVariance = real.importanceOfHeightVariance;
            this.importanceOfBlocks = real.importanceOfBlocks;
            this.importanceOfBiomes = real.importanceOfBiomes;
            this.yBoundsToggle = real.isYBoundsMatters;
            this.mobs = real.mobs;
            this.dailyTemperatures = real.dailyTemperatures;
            this.windInfo = real.windInfo;
            this.biomeUid = real.biomeUid;
            if (this.windInfo == null) this.windInfo = new WindInfo();
            if (this.dailyTemperatures == null) this.dailyTemperatures = new HashMap<>();
            for (TemperatureTime time : TemperatureTime.values())
                this.dailyTemperatures.putIfAbsent(time, new TemperatureInfo());

        }

        public BiomeTypeBuilder() {
            icon = null;
            this.windInfo = new WindInfo();
            for (TemperatureTime time : TemperatureTime.values())
                this.dailyTemperatures.putIfAbsent(time, new TemperatureInfo());
        }

        public void setIcon(BiomeIcon icon) {
            this.icon = icon;
        }

        public ItemStack getIconItem() {
            return icon == null ? InventoryUtils.makeItem(Material.LEVER, 1, "No spawn egg", null) : icon.toItem();
        }

        public BiomeType build() {
            return new BiomeType(this);
        }

        public int getHighestY() {
            return highestY;
        }

        public int getLowestY() {
            return lowestY;
        }

        public double getHeightVariance() {
            return heightVariance;
        }

        public double getTypicalY() {
            return typicalY;
        }

        public int getSpawnRate() {
            return spawnRate;
        }

        public BiomeTypeBuilderRegisterBlocks getRegisterBlocks() {
            return registerBlocks;
        }

        public void setRegisterBlocks(BiomeTypeBuilderRegisterBlocks registerBlocks) {
            this.registerBlocks = registerBlocks;
        }

        public void incrementBlocksImportance(int change) {
            this.importanceOfBlocks += change;
            this.importanceOfBlocks = Math.max(0, this.importanceOfBlocks);
        }

        public void incrementBiomesImportance(int change) {
            this.importanceOfBiomes += change;
            this.importanceOfBiomes = Math.max(0, this.importanceOfBiomes);
        }

        public void incrementHeightImportance(int change) {
            this.importanceOfHeightVariance += change;
            this.importanceOfHeightVariance = Math.max(0, this.importanceOfHeightVariance);
        }

        public int getBlocksImportanceCount() {
            return importanceOfBlocks;
        }

        public double getBlocksImportancePerc() {
            return ((double) importanceOfBlocks) / (this.importanceOfBlocks + this.importanceOfBiomes + this.importanceOfHeightVariance);
        }

        public int getBiomesImportanceCount() {
            return importanceOfBiomes;
        }

        public double getBiomesImportancePerc() {
            return ((double) importanceOfBiomes) / (this.importanceOfBlocks + this.importanceOfBiomes + this.importanceOfHeightVariance);
        }

        public int getHeightImportanceCount() {
            return importanceOfHeightVariance;
        }

        public double getHeightImportancePerc() {
            return ((double) importanceOfHeightVariance) / (this.importanceOfBlocks + this.importanceOfBiomes + this.importanceOfHeightVariance);
        }

        public void updateFromRegisterBlocks() {
            this.registerBlocks.setShouldStop();
            BiomeTypeBuilderRegisterBlocks.BlocksInfo blocksInfo = this.registerBlocks.computeFromCurrent();
            if (blocksInfo == null) {
                PluginTMW.get().log(Level.WARNING, "The player didn't register any blocks for the biome");
                return;
            }

            this.lowestY = blocksInfo.getLowestHeight();
            this.highestY = blocksInfo.getHighestHeight();
            this.typicalY = blocksInfo.getAverageHeight();
            this.heightVariance = blocksInfo.getHeightVariation();
            this.materials = new HashMap<>(blocksInfo.getMaterials());
            this.biomes = new HashMap<>(blocksInfo.getBiomes());
            this.registerBlocks = null;
        }

        public Map<Material, Double> getMaterials() {
            return materials;
        }

        @Override
        public int hashCode() {
            return icon.name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof BiomeType && icon.name.equals(((BiomeType) obj).icon.name);
        }

        public void toggleYBounds() {
            this.yBoundsToggle = !this.yBoundsToggle;
        }

        public boolean getYBounds() {
            return this.yBoundsToggle;
        }

        public HashMap<MobType, Integer> getMobs() {
            return mobs;
        }

        public Integer getMob(MobType mob) {
            return mobs.get(mob);
        }

        public void incrementMob(MobType mob, int change) {
            this.mobs.computeIfPresent(mob, (m, count) -> count + change);
        }

        public void addMob(MobType mob) {
            this.mobs.putIfAbsent(mob, 1);
        }

        public HashMap<TemperatureTime, TemperatureInfo> getDailyTemperatures() {
            return this.dailyTemperatures;
        }

        public WindInfo getWind() {
            return this.windInfo;
        }

        @Nullable
        public String getName() {
            return icon == null ? null : icon.getName();
        }

        public void incrementSpawnRate(int i) {
            this.spawnRate += i;
        }

        public void removeMob(MobType mob) {
            this.mobs.remove(mob);
        }

        public boolean isBuildable() {
            return this.biomes != null;
        }

        public static class BiomeIcon {
            private final String name;
            private final Material material;
            private final List<String> lore;

            public BiomeIcon(String name, Material material, List<String> lore) {
                this.name = name;
                this.material = material;
                this.lore = lore;
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
                return name;
            }
        }


    }

    public static class TemperatureInfo {
        public int degrees = 0;
    }

    public static class WindInfo {
        public int kphMin = 0;
        public int kphMax = 0;
    }
}
