package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.util.gui.InventoryGui;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiPageSimple;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.game_mechanics.util.minecraft.NbtUtils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MobTypeGuiPageSettings extends InventoryGuiPageSimple {
    private MobTypeGui mobTypeGui;
    private MobTypeBuilder mob;

    public MobTypeGuiPageSettings(MobTypeGui mobTypeGui, MobTypeBuilder mob) {
        super(mobTypeGui);
        this.mobTypeGui = mobTypeGui;
        this.mob = mob;
        setSlot(new MobNameSlot(), 0);
    }

    @Override
    public String getName() {
        return "Mob settings";
    }

    @Override
    public int size() {
        return 54;
    }

    private class MobNameSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
            mobTypeGui.setTempInventory(new MobNamePage());
        }

        @Override
        public ItemStack getItem() {
            return mob.getIconItem();
        }

        private class MobNamePage extends InventoryGuiPageSimple {
            public MobNamePage() {
                super(mobTypeGui);
                setSlot(new InventoryGuiSlotGeneric(e -> {
                }, mob.getIconItem()), 0);
                setSlot(new InventoryGuiSlotGeneric(e -> {
                    mobTypeGui.setTempInventory(null);
                }, new ItemStack(Material.GREEN_TERRACOTTA)), 8);
            }

            @Override
            public String getName() {
                return "Insert item representation";
            }

            @Override
            public int size() {
                return 9;
            }

            @Override
            public void dealWithPlayerInventoryClick(InventoryClickEvent event) {
                @Nullable ItemStack item = event.getCurrentItem();
                if (item == null) return;
                // the material of the ItemStack
                Material material = item.getType();
                ItemMeta im = item.getItemMeta();

                // get the entity type in a very terrible way
                Optional<EntityTypes<?>> entityTypesFromMaterial = Optional.empty();
                try {
                    EntityType type = EntityType.valueOf(item.getType().getKey().getKey().toUpperCase().replace("_SPAWN_EGG", ""));
                    entityTypesFromMaterial = EntityTypes.a(type.getKey().getKey());
                } catch (IllegalArgumentException ignored) {
                }

                // get the name and lore of the item
                String name = im.getDisplayName();
                List<String> lore = im.getLore();

                // get the entityTag nbt
                net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
                NBTTagCompound nbt = nmsItem.save(new NBTTagCompound());
                nbt = nbt.hasKey(NbtUtils.ENTITY_TAG_NBT) ? nbt.getCompound(NbtUtils.ENTITY_TAG_NBT) : new NBTTagCompound();
                Optional<EntityTypes<?>> entityTypesFromNbt = EntityTypes.a(nbt);


                Optional<EntityTypes<?>> entityTypes = entityTypesFromNbt.isPresent() ? entityTypesFromNbt : entityTypesFromMaterial;
                if (entityTypes.isEmpty()) return;
                mob.setIcon(new MobTypeBuilder.MobIcon(name, material, lore, nbt, entityTypes.get()));

                setSlot(new InventoryGuiSlotGeneric(e -> {
                }, mob.getIconItem()), 0);
                update();
            }
        }
    }
}
