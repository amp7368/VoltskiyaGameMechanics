package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.util.minecraft.InventoryUtils;

import java.util.List;

public class MobTypeBuilder {
    private MobIcon icon;

    public MobTypeBuilder(MobType mobType) {
        this.icon = mobType.getIcon();
    }

    public MobTypeBuilder() {
        icon = null;
    }

    public void setIcon(MobIcon icon) {
        this.icon = icon;
    }

    public ItemStack getIconItem() {
        return icon == null ? InventoryUtils.makeItem(Material.LEVER, 1, "No spawn egg", null) : icon.toItem();
    }

    public MobType build() {
        return new MobType(icon);
    }

    public static class MobIcon {
        private final String name;
        private final Material material;
        private final List<String> lore;
        private final NBTTagCompound nbt;

        public MobIcon(String name, Material material, List<String> lore, NBTTagCompound nbt, EntityTypes<?> entityTypes) {
            this.name = name;
            this.material = material;
            this.lore = lore;
            this.nbt = nbt;
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
