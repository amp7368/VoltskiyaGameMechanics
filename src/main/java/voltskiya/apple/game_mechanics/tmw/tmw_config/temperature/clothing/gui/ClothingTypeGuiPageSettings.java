package voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.ClothingDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.temperature.clothing.ClothingType;
import voltskiya.apple.utilities.util.gui.InventoryGui;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageSimple;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.minecraft.ArmorType;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;
import voltskiya.apple.utilities.util.minecraft.MaterialUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

public class ClothingTypeGuiPageSettings extends InventoryGuiPageSimple {
    private final ClothingType.ClothingTypeBuilder clothing;

    public ClothingTypeGuiPageSettings(TMWGui tmwGui, ClothingTypeGui clothingTypeGui, ClothingType.ClothingTypeBuilder clothing) {
        super(clothingTypeGui);
        this.clothing = clothing;
        setSlot(new InventoryGuiSlotGeneric(
                e -> {
                    if (clothing.canBuild()) {
                        ClothingDatabase.addClothing(clothing.build());
                        tmwGui.update(null);
                        e.getWhoClicked().openInventory(tmwGui.getInventory());
                    }
                }, InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Save", null)
        ), 4);
        setSlot(new IncrementSlot(clothing::incrementWindProtection, .1, 1, Material.REDSTONE, "Increase"), 10);
        setSlot(new IncrementSlot(clothing::incrementWindProtection, -.1, -1, Material.GUNPOWDER, "Decrease"), 11);
        setSlot(new IncrementSlot(clothing::incrementWetProtection, .1, 1, Material.REDSTONE, "Increase"), 19);
        setSlot(new IncrementSlot(clothing::incrementWetProtection, -.1, -1, Material.GUNPOWDER, "Decrease"), 20);
        setSlot(new IncrementSlot(clothing::incrementColdResistance, .1, 1, Material.REDSTONE, "Increase"), 28);
        setSlot(new IncrementSlot(clothing::incrementColdResistance, -.1, -1, Material.GUNPOWDER, "Decrease"), 29);
        setSlot(new IncrementSlot(clothing::incrementHeatResistance, .1, 1, Material.REDSTONE, "Increase"), 37);
        setSlot(new IncrementSlot(clothing::incrementHeatResistance, -.1, -1, Material.GUNPOWDER, "Decrease"), 38);
        setSlots();
    }

    private void setSlots() {
        setSlot(new InventoryGuiSlotGeneric(
                e -> {
                },
                clothing.toItem()
        ), 0);
        setSlot(new InventoryGuiSlotGeneric(
                e -> {
                }, InventoryUtils.makeItem(Material.BLACK_STAINED_GLASS_PANE, 1, "Wind protection",
                Collections.singletonList(String.valueOf(this.clothing.getWindProtection())))
        ), 9);
        setSlot(new InventoryGuiSlotGeneric(
                e -> {
                }, InventoryUtils.makeItem(Material.WET_SPONGE, 1, "Wet protection",
                Collections.singletonList(String.valueOf(this.clothing.getWetProtection())))
        ), 18);
        setSlot(new InventoryGuiSlotGeneric(
                e -> {
                }, InventoryUtils.makeItem(Material.CAMPFIRE, 1, "Cold resistance",
                Collections.singletonList(String.valueOf(this.clothing.getColdResistance())))
        ), 27);
        setSlot(new InventoryGuiSlotGeneric(
                e -> {
                }, InventoryUtils.makeItem(Material.SOUL_CAMPFIRE, 1, "Heat resistance",
                Collections.singletonList(String.valueOf(this.clothing.getHeatResistance())))
        ), 36);
    }

    @Override
    public void fillInventory() {
        setSlots();
        super.fillInventory();
    }

    @Override
    public String getName() {
        return "Clothing Type";
    }

    @Override
    public void dealWithPlayerInventoryClick(InventoryClickEvent event) {
        final ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null) {
            ArmorType armorType = MaterialUtils.getArmorType(currentItem.getType());
            if (armorType != null) {
                clothing.setIcon(currentItem, armorType);
            }
        }
        update();
        super.dealWithPlayerInventoryClick(event);
    }

    @Override
    public int size() {
        return 54;
    }

    private class IncrementSlot implements InventoryGui.InventoryGuiSlot {
        private final Consumer<Double> increment;
        private final double left;
        private final double right;
        private final Material material;
        private final String name;

        public IncrementSlot(Consumer<Double> increment, double left, double right, Material material, String name) {
            this.increment = increment;
            this.left = left;
            this.right = right;
            this.material = material;
            this.name = name;
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (event.getClick().isLeftClick()) {
                increment.accept(left);
            } else {
                increment.accept(right);
            }
            update();
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(material, 1, name,
                    Arrays.asList(
                            String.format("Left click - %s by %.1f", name, left),
                            String.format("Right click - %s by %.1f", name, right)
                    )
            );
        }
    }
}
