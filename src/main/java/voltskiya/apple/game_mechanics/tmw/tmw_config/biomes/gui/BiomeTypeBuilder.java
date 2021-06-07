package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.util.minecraft.InventoryUtils;

import java.util.List;

public class BiomeTypeBuilder {
    private BiomeTypeBuilder.BiomeIcon icon;
    private int highestY = -1;
    private int lowestY = -1;
    private double heightVariance;
    private double typicalY;

    public BiomeTypeBuilder(BiomeType biomeType) {
        this.icon = biomeType.getIcon();
    }

    public BiomeTypeBuilder() {
        icon = null;
    }

    public void setIcon(BiomeTypeBuilder.BiomeIcon icon) {
        this.icon = icon;
    }

    public ItemStack getIconItem() {
        return icon == null ? InventoryUtils.makeItem(Material.LEVER, 1, "No spawn egg", null) : icon.toItem();
    }

    public BiomeType build() {
        return new BiomeType(icon);
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