package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import com.mysql.jdbc.UpdatableResultSet;
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

import java.util.Arrays;
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
        setSlot(new IsPersistentSlot(), 36);
        setSlot(new DespawnsAfterSlot(), 37);
        setSlot(new IsSpawnWithLineOfSight(), 45);
        setSlot(new TimeToSpawnSlot(), 49);
        setSlot(new HighestYValue(), 44);
        setSlot(new LowestYValue(), 53);
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

    private class IsPersistentSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
            mob.togglePersistent();
            update();
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.COBWEB, 1, "Is persistent?", Collections.singletonList(mob.isPersistent() ? "Yes" : "No"));
        }
    }

    private class DespawnsAfterSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (event.getClick().isShiftClick()) {
                if (event.getClick().isLeftClick()) {
                    mob.changeDespawnsAfterHours(.1);
                } else {
                    mob.changeDespawnsAfterHours(-.1);
                }
            } else {
                if (event.getClick().isLeftClick()) {
                    mob.changeDespawnsAfterHours(1);
                } else {
                    mob.changeDespawnsAfterHours(-1);
                }
            }
            update();
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.WITHER_ROSE, 1, String.format("Despawns after %.1f real life hours", mob.getDespawnsAfterHours()), Arrays.asList(
                    "Shift left click: +0.1",
                    "Normal left click: +1",
                    "Shift right click: -0.1",
                    "Normal right click: -1"
            ));
        }
    }

    private class IsSpawnWithLineOfSight implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
            mob.toggleSpawnWithLineOfSight();
            update();
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.END_ROD, 1, "Is spawn with line of sight?", Collections.singletonList(mob.isSpawnWithLineOfSight() ? "Yes" : "No"));
        }
    }


    private class TimeToSpawnSlot implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {

        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.APPLE, 1, (String) null, null);
        }
    }

    private class HighestYValue implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (event.getClick().isShiftClick()) {
                if (event.getClick().isLeftClick()) {
                    mob.changeHighestYLevel(5);
                } else {
                    mob.changeHighestYLevel(-5);
                }
            } else {
                if (event.getClick().isLeftClick()) {
                    mob.changeHighestYLevel(1);
                } else {
                    mob.changeHighestYLevel(-1);
                }
            }
            update();
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.GRASS_BLOCK, 1, String.format("Highest Y Level: %d", mob.getHighestYLevel()), Arrays.asList(
                    "Shift left click: +5",
                    "Normal left click: +1",
                    "Shift right click: -5",
                    "Normal right click: -1"
            ));
        }
    }

    private class LowestYValue implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (event.getClick().isShiftClick()) {
                if (event.getClick().isLeftClick()) {
                    mob.changeLowestYLevel(5);
                } else {
                    mob.changeLowestYLevel(-5);
                }
            } else {
                if (event.getClick().isLeftClick()) {
                    mob.changeLowestYLevel(1);
                } else {
                    mob.changeLowestYLevel(-1);
                }
            }
            update();
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.BEDROCK, 1, String.format("Lowest Y Level: %d", mob.getLowestYLevel()), Arrays.asList(
                    "Shift left click: +5",
                    "Normal left click: +1",
                    "Shift right click: -5",
                    "Normal right click: -1"
            ));
        }
    }
}
