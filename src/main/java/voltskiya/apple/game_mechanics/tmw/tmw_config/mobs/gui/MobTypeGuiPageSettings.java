package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobTypeDatabase;
import voltskiya.apple.game_mechanics.util.gui.InventoryGui;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiPageSimple;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.game_mechanics.util.minecraft.InventoryUtils;
import voltskiya.apple.game_mechanics.util.minecraft.NbtUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MobTypeGuiPageSettings extends InventoryGuiPageSimple {
    private final MobTypeGui mobTypeGui;
    private final MobType.MobTypeBuilder mob;
    private final InventoryGui callbackGui;

    public MobTypeGuiPageSettings(MobTypeGui mobTypeGui, MobType.MobTypeBuilder mob, InventoryGui callbackGui) {
        super(mobTypeGui);
        this.mobTypeGui = mobTypeGui;
        this.mob = mob;
        this.callbackGui = callbackGui;
        setSlot(new MobNameSlot(), 0);
        setSlot(new SaveSlot(), 4);
        setSlot(new InventoryGuiSlotGeneric((e) -> mobTypeGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
        setSlot(new InventoryGuiSlotGeneric((e) -> {
        }, InventoryUtils.makeItem(Material.APPLE, 1, "Is persistant", null)), 36);
        setSlot(new InventoryGuiSlotGeneric((e) -> {
        }, InventoryUtils.makeItem(Material.APPLE, 1, "Despawns after _ real hours", null)), 37);
        setSlot(new InventoryGuiSlotGeneric((e) -> {
        }, InventoryUtils.makeItem(Material.APPLE, 1, "Highest y level", null)), 44);
        setSlot(new InventoryGuiSlotGeneric((e) -> {
        }, InventoryUtils.makeItem(Material.APPLE, 1, "Spawn with line of sight", null)), 45);
        setSlot(new InventoryGuiSlotGeneric((e) -> {
        }, InventoryUtils.makeItem(Material.APPLE, 1, "Time to spawn", null)), 49);
        setSlot(new InventoryGuiSlotGeneric((e) -> {
        }, InventoryUtils.makeItem(Material.APPLE, 1, "Lowest y level", null)), 53);
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
                mob.setIcon(new MobType.MobTypeBuilder.MobIcon(name, material, lore, nbt, entityTypes.get()));

                setSlot(new InventoryGuiSlotGeneric(e -> {
                }, mob.getIconItem()), 0);
                update();
            }
        }
    }

    private class SaveSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
            MobTypeDatabase.addMob(mob.build());
            callbackGui.update(null);
            event.getWhoClicked().openInventory(callbackGui.getInventory());
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.LIME_CONCRETE, 1, "Save", Collections.singletonList("list of things needed before saving"));
        }
    }
}
