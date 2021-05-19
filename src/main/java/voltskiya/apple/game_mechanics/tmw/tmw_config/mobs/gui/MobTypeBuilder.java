package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MobTypeBuilder {
    private MobIcon icon = null;

    public void setIcon(MobIcon icon) {
        this.icon = icon;
    }

    public ItemStack getIconItem() {
        return icon == null ? new ItemStack(Material.LEVER) : icon.toItem();
    }

    public static class MobIcon {
        private String name;
        private Material material;
        private List<String> lore;
        private NBTTagCompound nbt;
        private EntityTypes<?> entityTypes;

        public MobIcon(String name, Material material, List<String> lore, NBTTagCompound nbt, EntityTypes<?> entityTypes) {
            System.out.println(name);
            this.name = name;
            this.material = material;
            this.lore = lore;
            this.nbt = nbt;
            this.entityTypes = entityTypes;
        }

        public ItemStack toItem() {
            ItemStack item = new ItemStack(material);
            item.getItemMeta().setDisplayName(name);
            item.getItemMeta().setLore(lore);
            return item;
        }
    }
}
