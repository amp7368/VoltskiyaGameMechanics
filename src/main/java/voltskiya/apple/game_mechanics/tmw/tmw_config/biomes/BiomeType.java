package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import voltskiya.apple.game_mechanics.tmw.PluginTMW;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks;
import voltskiya.apple.game_mechanics.util.minecraft.InventoryUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class BiomeType {
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
        if (typicalY + heightVariance * 3 < this.typicalY || typicalY - heightVariance * 3 > this.typicalY) return 0;

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

    public static class BiomeTypeBuilder {
        private BiomeTypeBuilderRegisterBlocks registerBlocks = null;
        private BiomeIcon icon;
        private int highestY = -1;
        private int lowestY = -1;
        private double heightVariance = -1;
        private double typicalY = -1;
        private int spawnRate = 0;
        private final int importanceOfBlocks = 1;
        private final int importanceOfHeightVariance = 1;
        private final int importanceOfBiomes = 1;
        private HashMap<Material, Double> materials;
        private HashMap<Biome, Double> biomes;

        public BiomeTypeBuilder(BiomeType real) {
            this.icon = real.icon;
            this.highestY = real.highestY;
            this.lowestY = real.lowestY;
            this.heightVariance = real.heightVariance;
            this.typicalY = real.typicalY;
            this.spawnRate = real.spawnRate;
            this.materials = real.materials;
            this.biomes = real.biomes;
        }

        public BiomeTypeBuilder() {
            icon = null;
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

        public void setHighestY(int highestY) {
            this.highestY = highestY;
        }

        public int getLowestY() {
            return lowestY;
        }

        public void setLowestY(int lowestY) {
            this.lowestY = lowestY;
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
}
