package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs;

import com.google.gson.*;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import voltskiya.apple.game_mechanics.util.minecraft.InventoryUtils;

import java.lang.reflect.Type;
import java.util.List;

public class MobType {
    private final MobTypeBuilder.MobIcon icon;

    public MobType(MobTypeBuilder builder) {
        this.icon = builder.icon;
    }

    public ItemStack toItem() {
        return icon.toItem();
    }

    public MobTypeBuilder toBuilder() {
        return new MobTypeBuilder(this);
    }

    public String getName() {
        return icon.getName();
    }

    @Override
    public int hashCode() {
        return this.icon.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MobType && this.icon.getName().equals(((MobType) obj).getName());
    }

    @Override
    public String toString() {
        return this.icon.getName();
    }

    public static class MobTypeSerializer implements JsonSerializer<MobType> {
        @Override
        public JsonElement serialize(MobType mobType, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(mobType.icon.getName());
        }
    }

    public static class MobTypeDeSerializer implements JsonDeserializer<MobType> {
        @Override
        public MobType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return MobTypeDatabase.getMob(jsonElement.getAsString());
        }
    }

    public static class MobTypeBuilder {
        private MobIcon icon;

        public MobTypeBuilder(MobType real) {
            this.icon = real.icon;
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
            return new MobType(this);
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
}
