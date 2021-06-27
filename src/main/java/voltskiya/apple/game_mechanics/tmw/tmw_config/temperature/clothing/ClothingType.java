package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ClothingType {
    private ClothingTypeBuilder.ClothingIcon icon;
    private ClothingPlace clothingPlace;

    public String getName() {
        return this.icon.name;
    }

    private enum ClothingPlace {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }

    public static class ClothingTypeBuilder {


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
                return name;
            }
        }
    }
}
